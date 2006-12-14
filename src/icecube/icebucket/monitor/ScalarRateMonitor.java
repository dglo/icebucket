/*
 * interface: ScalarRateMonitor
 *
 * Version $Id: ScalarRateMonitor.java,v 1.2 2005/09/22 11:12:54 patton Exp $
 *
 * Date: July 15 2004
 *
 * (c) 2004 IceCube Collaboration
 */

package icecube.icebucket.monitor;


/**
 * This interface extends the {@link ScalarMonitor} interface to enable access
 * to a meaurement of scalar's change over time.
 *
 * @author patton
 * @version $Id: ScalarRateMonitor.java,v 1.2 2005/09/22 11:12:54 patton Exp $
 */
public interface ScalarRateMonitor
        extends ScalarMonitor
{
    // instance member method (alphabetic)

    /**
     * Returns a float[] containing the historical rates measured by this
     * object. The zeroth element contains the oldest measurement, with more
     * recent measurements in increasing indicies.
     * <p/>
     * This method is optional and if not implemented will throw an
     * UnsupportedOperationException.
     *
     * @return a float[] containing the historical rates measured.
     */
    float[] getHistory();

    /**
     * Returns the current measured rate.
     *
     * @return the current measured rate.
     */
    float getRate();
}
