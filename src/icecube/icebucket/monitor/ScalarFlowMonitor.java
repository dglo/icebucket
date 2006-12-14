/*
 * interface: EventFlowMonitor
 *
 * Version $Id: ScalarFlowMonitor.java,v 1.3 2005/09/22 11:12:54 patton Exp $
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
 * @version $Id: ScalarFlowMonitor.java,v 1.3 2005/09/22 11:12:54 patton Exp $
 */
public interface ScalarFlowMonitor
        extends ScalarSumMonitor, ScalarRateMonitor
{

    // public static final member data

    /**
     * An implementation of this interface that does nothing.
     */
    public static final ScalarFlowMonitor NULL_MONITOR =
            new ScalarFlowMonitor()
            {
                public void dispose()
                {
                }

                public float[] getHistory()
                {
                    throw new UnsupportedOperationException("This is the NULL monitor");
                }

                public float getRate()
                {
                    throw new UnsupportedOperationException("This is the NULL monitor");
                }

                public long getTotal()
                {
                    throw new UnsupportedOperationException("This is the NULL monitor");
                }

                public void measure(int count)
                {
                }

                public void reset()
                {
                }
            };
}
