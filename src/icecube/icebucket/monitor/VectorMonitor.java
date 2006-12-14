/*
 * interface: ScalarMonitor
 *
 * Version $Id: VectorMonitor.java,v 1.3 2005/06/09 09:30:49 patton Exp $
 *
 * Date: July 12 2004
 *
 * (c) 2004 IceCube Collaboration
 */

package icecube.icebucket.monitor;

/**
 * This interface defines the set of methods that can be used to monitor the
 * rate at which an Array of integer scalar changes.
 *
 * @author patton
 * @version $Id: VectorMonitor.java,v 1.3 2005/06/09 09:30:49 patton Exp $
 */
public interface VectorMonitor
{

    // public static final member data

    /**
     * An implementation of this interface that does nothing.
     */
    public static final VectorMonitor NULL_MONITOR = new VectorMonitor()
    {
        private int dimension;

        public void dispose()
        {
        }

        public int getDimension()
        {
            return dimension;
        }

        public void measure(int[] counts)
        {
            if (0 == dimension) {
                dimension = counts.length;
            } else {
                if (dimension != counts.length) {
                    throw new IllegalArgumentException("The count array" +
                                                       " should have a" +
                                                       " length of " +
                                                       dimension +
                                                       " not " +
                                                       counts.length +
                                                       '.');
                }
            }
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
     * Returns the number of counts expected in a {@link #measure} call.
     *
     * @return the number of counts expected in a {@link #measure} call.
     */
    int getDimension();

    /**
     * Called to add a measurement to this object.
     *
     * @param counts the change in counts since the last time this method was
     * called.
     */
    void measure(int[] counts);

    /**
     * Reset this object so it behaves as if it was just created.
     */
    void reset();
}
