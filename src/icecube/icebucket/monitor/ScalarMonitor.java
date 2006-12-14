/*
 * interface: ScalarMonitor
 *
 * Version $Id: ScalarMonitor.java,v 1.1 2005/04/18 19:31:01 patton Exp $
 *
 * Date: July 12 2004
 *
 * (c) 2004 IceCube Collaboration
 */

package icecube.icebucket.monitor;

/**
 * This interface defines the set of methods that can be used to monitor the
 * rate at which an integer scalar changes.
 *
 * @version $Id: ScalarMonitor.java,v 1.1 2005/04/18 19:31:01 patton Exp $
 * @author patton
 */
public interface ScalarMonitor
{

    // public static final member data

    /**
     * An implementation of this interface that does nothing.
     */
    public static final ScalarMonitor NULL_MONITOR = new ScalarMonitor()
    {
        public void dispose()
        {
        }

        public void measure(int count)
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
     * Called to add a measurement to this object.
     *
     * @param count the change in count since the last time this method was
     * called.
     */
    void measure(int count);

    /**
     * Reset this object so it behaves as if it was just created.
     */
    void reset();
}
