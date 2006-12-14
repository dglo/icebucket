/*
 * class: SimpleScalarRateMonitoronitor
 *
 * Version $Id: ScalarFlowMonitorImpl.java,v 1.2 2005/09/22 11:12:55 patton Exp $
 *
 * Date: July 12 2004
 *
 * (c) 2004 IceCube Collaboration
 */

package icecube.icebucket.monitor.simple;

import icecube.icebucket.monitor.ScalarFlowMonitor;
import icecube.icebucket.monitor.ScalarMonitor;
import icecube.icebucket.monitor.ScalarPairedMonitor;
import icecube.icebucket.monitor.ScalarRateMonitor;
import icecube.icebucket.monitor.ScalarSumMonitor;

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
public class ScalarFlowMonitorImpl
        implements ScalarFlowMonitor
{

    // public static final member data

    // protected static final member data

    // static final member data

    // private static final member data

    // private static member data

    /**
     * The ScalarMonitor object measuring object rate of flow.
     */
    private final ScalarRateMonitor rate;

    /**
     * The ScalarMonitor object measuring object rate of flow.
     */
    private final ScalarSumMonitor total;

    /**
     * The ScalarMonitor object used to look at the object flow of this
     * object.
     */
    private final ScalarMonitor monitor;

    // constructors

    /**
     * Create an instance of this class.
     */
    public ScalarFlowMonitorImpl()
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
    public ScalarFlowMonitorImpl(long timeBase,
                                 int depth)
    {
        this.rate = new ScalarRateMonitorImpl(timeBase,
                                              depth);
        this.total = new ScalarSumMonitorImpl();
        monitor = new ScalarPairedMonitor(rate,
                                          total);
    }

    // instance member method (alphabetic)

    public void dispose()
    {
        monitor.reset();
    }

    public float[] getHistory()
    {
        return rate.getHistory();
    }

    public float getRate()
    {
        return rate.getRate();
    }

    public long getTotal()
    {
        return total.getTotal();
    }

    public void measure(int count)
    {
        monitor.measure(count);
    }

    public void reset()
    {
        monitor.reset();
    }

    // static member methods (alphabetic)

    // Description of this object.
    // public String toString() {}

    // public static void main(String args[]) {}
}