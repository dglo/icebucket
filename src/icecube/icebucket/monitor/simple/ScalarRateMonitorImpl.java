/*
 * class: SimpleScalarRateMonitoronitor
 *
 * Version $Id: ScalarRateMonitorImpl.java,v 1.4 2006/02/03 15:20:55 patton Exp $
 *
 * Date: July 12 2004
 *
 * (c) 2004 IceCube Collaboration
 */

package icecube.icebucket.monitor.simple;

import icecube.icebucket.monitor.ScalarRateMonitor;

import java.util.Timer;
import java.util.TimerTask;

/**
 * This class calculates the rate a scalar changes by simply dividing the total
 * count of the scalar and dividing it by the time to took at accumulate that
 * count.
 * <p/>
 * The default time base for this class is ScalarMonitor.HZ, i.e. per second.
 *
 * @author patton
 * @version $Id: ScalarRateMonitorImpl.java,v 1.1 2005/04/20 21:05:55 patton
 *          Exp $
 */
public class ScalarRateMonitorImpl
        implements ScalarRateMonitor
{

    // public static final member data

    /**
     * The constant representing count per second.
     */
    public static final long HZ = 1000;

    // protected static final member data

    // static final member data

    // private static final member data

    /**
     * The default depth of the ring buffer used by this object.
     */
    private static final int DEFAULT_RING_BUFFER_DEPTH = 32;

    /**
     * The default time base value 1000 = Hz.
     */
    private static final long DEFAULT_TIME_BASE = HZ;

    // private static member data

    /**
     * The timer object used to produced internal "ticks".
     */
    private final Timer timer = new Timer(true);

    // private instance member data

    /**
     * The ring buffer containing the count changes.
     */
    private final int[] counts;

    /**
     * The ring buffer containing the delta timed of each count change.
     */
    private final long[] deltaTimes;

    /**
     * The depth of history this object maintains.
     */
    private final int depth;

    /**
     * The latest valid elements in the ring buffer.
     */
    private int head;

    /**
     * The float array containing the rate history of this object.
     */
    private final float[] history;

    /**
     * The index at which to store the next history element.
     */
    private int historyInsert;

    /**
     * The number of valid elements in the ring buffer.
     */
    private int length;

    /**
     * The number of extra elements in "history" to enable it to taken extra
     * rates without having to "shift".
     */
    private final int loadFactor;

    /**
     * The time of the previous invocation of {@link #measure}.
     */
    private long previousTime;

    /**
     * The depth of the ring buffer.
     */
    private final int ringBufferDepth;

    /**
     * The TimerTask that provides the "tick".
     */
    private final TimerTask tick;

    /**
     * The number of milliseconds between rate sampling.
     */
    private final long timeBase;

    /**
     * The current total count.
     */
    private int totalCount;

    /**
     * THe current total time interval.
     */
    private long totalTime;

    // constructors

    /**
     * Create an instance of this class.
     */
    public ScalarRateMonitorImpl()
    {
        this(0,
             0);
    }

    /**
     * Create an instance of this class. If either of the parameters are less
     * than or equal to zero, then the default values will be used.
     *
     * @param timeBase the number of milliseconds between rate sampling.
     * @param depth the depth of 'memory', in calls to {@link #measure}.
     */
    public ScalarRateMonitorImpl(long timeBase,
                                 int depth)
    {
        if (!(0 < timeBase)) {
            this.timeBase = DEFAULT_TIME_BASE;
        } else {
            this.timeBase = timeBase;
        }

        if (!(0 < depth)) {
            this.depth = DEFAULT_RING_BUFFER_DEPTH;
        } else {
            this.depth = depth;
        }
        ringBufferDepth = this.depth;
        deltaTimes = new long[ringBufferDepth];
        counts = new int[ringBufferDepth];

        loadFactor = this.depth;
        history = new float[this.depth + loadFactor];
        resetAttributes();
        tick = new Tick();
        timer.scheduleAtFixedRate(tick,
                                  this.timeBase,
                                  this.timeBase);
    }

    // instance member method (alphabetic)

    public synchronized void addCount(int count)
    {
        // Prepare to store new measurment
        final long now = System.currentTimeMillis();
        if (0 == previousTime) {
            // First measurement
            previousTime = now;
            return;
        }

        int nextHead = head + 1;
        if (ringBufferDepth == nextHead) {
            nextHead = 0;
        }

        // If necessary, drop old measurment from ring buffer
        if (ringBufferDepth == length) {
            int tail = nextHead - length;
            if (0 > tail) {
                tail += ringBufferDepth;
            }
            totalCount -= counts[tail];
            totalTime -= deltaTimes[tail];
            length--;
        }
        head = nextHead;

        // Add new measurment
        final long deltaTime = now - previousTime;
        totalCount += count;
        totalTime += deltaTime;
        counts[head] = count;
        deltaTimes[head] = deltaTime;
        length++;

        previousTime = now;
    }

    /**
     * Archives the current rate into the history.
     */
    private void archive()
    {
        if (history.length == historyInsert) {
            final int shift = depth - 1;
            System.arraycopy(history,
                             historyInsert - shift,
                             history,
                             0,
                             shift);
            historyInsert = shift;
        }
        history[historyInsert] = getRate();
        historyInsert++;
    }

    /**
     * Returns the depth of history this object maintains.
     *
     * @return the depth of history this object maintains.
     */
    public int getDepth()
    {
        return ringBufferDepth;
    }

    /**
     * Returns a float[] containing the historical rates measured by this
     * object. The time between entries is the timeBase of this object. The
     * zeroth element contains the oldest measurement, with more recent
     * measurements in increasing indicies. The length of the returned array is
     * the depth hat this object is using.
     *
     * @return a float[] containing the historical rates.
     * @see #getDepth()
     * @see #getTimeBase()
     */
    public float[] getHistory()
    {
        float[] result = new float[depth];
        final int start;
        if (depth > historyInsert) {
            start = 0;
        } else {
            start = historyInsert - depth;
        }
        System.arraycopy(history,
                         start,
                         result,
                         0,
                         depth);
        return result;
    }

    public synchronized float getRate()
    {
        if ((0 == length) ||
            (0 == totalTime)) {
            return (float) 0;
        }
        final float result = ((float) totalCount * (float) timeBase) /
                             ((float) totalTime);
        return result;
    }

    /**
     * Returns the number of milliseconds between rate sampling.
     *
     * @return the number of milliseconds between rate sampling.
     */
    public long getTimeBase()
    {
        return timeBase;
    }

    public void dispose()
    {
        tick.cancel();
        timer.cancel();
    }

    public void measure(int count)
    {
        addCount(count);
    }

    public synchronized void reset()
    {
        resetAttributes();
    }

    private void resetAttributes()
    {
        previousTime = 0;
        length = 0;
        head = ringBufferDepth - 1;
        totalCount = 0;
        totalTime = 0;
        final int finished = history.length;
        for (int index = 0;
                finished != index;
                index++) {
            history[index] = 0.0F;
        }
        historyInsert = 0;
    }

    // static member methods (alphabetic)

    private class Tick
            extends TimerTask
    {
        private Tick()
        {
        }

        public void run()
        {
            addCount(0);
            archive();
        }
    }

    // Description of this object.
    // public String toString() {}

    // public static void main(String args[]) {}
}