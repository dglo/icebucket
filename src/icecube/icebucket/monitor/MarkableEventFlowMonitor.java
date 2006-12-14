/*
 * interface: MarkableEventFlowMonitor
 *
 * Version $Id: MarkableEventFlowMonitor.java,v 1.1 2006/02/21 03:25:14 patton Exp $
 *
 * Date: February 20 2006
 *
 * (c) 2006 IceCube Collaboration
 */

package icecube.icebucket.monitor;

/**
 * This interface extends the EventFlowMonitor interface to allow for the flow
 * to be marked at intervals.
 *
 * @author patton
 * @version $Id: MarkableEventFlowMonitor.java,v 1.1 2006/02/21 03:25:14 patton Exp $
 */
public interface MarkableEventFlowMonitor
        extends EventFlowMonitor
{

    // public static final member data

    /**
     * An implementation of this interface that does nothing.
     */
    public static final MarkableEventFlowMonitor NULL_MONITOR =
            new MarkableEventFlowMonitor()
            {
                public void dispose()
                {
                }

                public float[] getFlowHistory()
                {
                    throw new UnsupportedOperationException(
                            "This is the NULL monitor");
                }

                public float getFlowRate()
                {
                    throw new UnsupportedOperationException(
                            "This is the NULL monitor");
                }

                public long getFlowTotal()
                {
                    throw new UnsupportedOperationException(
                            "This is the NULL monitor");
                }

                public float[] getSizeHistory()
                {
                    throw new UnsupportedOperationException(
                            "This is the NULL monitor");
                }

                public float getSizeRate()
                {
                    throw new UnsupportedOperationException(
                            "This is the NULL monitor");
                }

                public long getSizeTotal()
                {
                    throw new UnsupportedOperationException(
                            "This is the NULL monitor");
                }

                public void measure(int count,
                                    int size)
                {
                }

                public long getFlowSinceMark()
                {
                    throw new UnsupportedOperationException(
                            "This is the NULL monitor");
                }

                public long getSizeSinceMark()
                {
                    throw new UnsupportedOperationException(
                            "This is the NULL monitor");
                }

                public void mark()
                {
                }

                public void reset()
                {
                }
            };

    // instance member method (alphabetic)

    /**
     * Returns the number of objects since the last mark.
     *
     * @return the number of objects since the last mark.
     */
    long getFlowSinceMark();

    /**
     * Returns the size of objects since the last mark.
     *
     * @return the size of objects since the last mark.
     */
    long getSizeSinceMark();


    /**
     * Resets the 'since mark' counters.
     */
    void mark();

    // static member methods (alphabetic)

}
