/*
 * interface: EventFlowMonitor
 *
 * Version $Id: EventFlowMonitor.java,v 1.3 2005/09/22 13:16:29 patton Exp $
 *
 * Date: December 16 2004
 *
 * (c) 2004 IceCube Collaboration
 */

package icecube.icebucket.monitor;



/**
 * This interface defines the set of methods that can be used to monitor the
 * flow of objects.
 *
 * @author patton
 * @version $Id: EventFlowMonitor.java,v 1.3 2005/09/22 13:16:29 patton Exp $
 */
public interface EventFlowMonitor
{

    // public static final member data

    /**
     * An implementation of this interface that does nothing.
     */
    public static final EventFlowMonitor NULL_MONITOR = new EventFlowMonitor()
    {
        public void dispose()
        {
        }

        public float[] getFlowHistory()
        {
            throw new UnsupportedOperationException("This is the NULL monitor");
        }

        public float getFlowRate()
        {
            throw new UnsupportedOperationException("This is the NULL monitor");
        }

        public long getFlowTotal()
        {
            throw new UnsupportedOperationException("This is the NULL monitor");
        }

        public float[] getSizeHistory()
        {
            throw new UnsupportedOperationException("This is the NULL monitor");
        }

        public float getSizeRate()
        {
            throw new UnsupportedOperationException("This is the NULL monitor");
        }

        public long getSizeTotal()
        {
            throw new UnsupportedOperationException("This is the NULL monitor");
        }

        public void measure(int count,
                            int size)
        {
        }

        public void reset()
        {
        }
    };

    // instance member method (alphabetic)

    /**
     * Tells this object is can release any resources it has been using.
     */
    void dispose();

    /**
     * Returns the history of the rate of flow of objects.
     *
     * @return the history of the rate of flow of objects.
     */
    float[] getFlowHistory();

    /**
     * Returns the current measured rate of flow of objects.
     *
     * @return the current measured rate of flow of objects.
     */
    float getFlowRate();

    /**
     * Returns the current total number of objects.
     *
     * @return the current total number of objects.
     */
    long getFlowTotal();

    /**
     * Returns the history of the rate of the size of objects.
     *
     * @return the history of the rate of the size of objects.
     */
    float[] getSizeHistory();

    /**
     * Returns the current measured rate of the size of objects.
     *
     * @return the current measured rate of the size of objects.
     */
    float getSizeRate();

    /**
     * Returns the current size number of objects.
     *
     * @return the current size number of objects.
     */
    long getSizeTotal();

    /**
     * Called to add a measurement to this object.
     *
     * @param first the change in count in the first scalar since the last time
     * this method was called.
     * @param second the change in count in the second scalar since the last
     * time this method was called.
     */
    void measure(int first,
                 int second);

    /**
     * Reset this object so it behaves as if it was just created.
     */
    void reset();
}
