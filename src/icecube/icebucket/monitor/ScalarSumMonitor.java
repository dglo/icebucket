/*
 * class: ScalarSumMonitor
 *
 * Version $Id: ScalarSumMonitor.java,v 1.2 2005/09/22 11:12:54 patton Exp $
 *
 * Date: July 15 2004
 *
 * (c) 2004 IceCube Collaboration
 */

package icecube.icebucket.monitor;



/**
 * This interface extends the {@link ScalarMonitor} interface to enable access
 * to a meaurement of scalar's totsl over time.
 *
 * @author patton
 * @version $Id: ScalarSumMonitor.java,v 1.2 2005/09/22 11:12:54 patton Exp $
 */
public interface ScalarSumMonitor
        extends ScalarMonitor
{
// instance member method (alphabetic)

    /**
     * Returns the accumulated total of all measurements.
     *
     * @return the accumulated total of all measurements.
     */
    public long getTotal();
}