/*
 * class: ScalarSumMonitor
 *
 * Version $Id: ScalarSumMonitorImpl.java,v 1.4 2005/11/21 18:14:25 patton Exp $
 *
 * Date: July 15 2004
 *
 * (c) 2004 IceCube Collaboration
 */

package icecube.icebucket.monitor.simple;

import icecube.icebucket.monitor.ScalarSumMonitor;

/**
 * This class is a simple implementation of the {@link ScalarSumMonitor}
 * interface.
 *
 * @version $Id: ScalarSumMonitorImpl.java,v 1.4 2005/11/21 18:14:25 patton Exp $
 * @author patton
 */
public class ScalarSumMonitorImpl
        implements ScalarSumMonitor
{
    // private instance member data

    /** The accumulated total of all measurements. */
    private long total;

    // constructors

    /**
     * Create an instance of this class.
     */
    public ScalarSumMonitorImpl()
    {
    }

    // instance member method (alphabetic)

    public void dispose()
    {
    }

    public synchronized void measure(int count)
    {
        total += count;
    }

    public synchronized void reset()
    {
        total = 0;
    }

    public synchronized long getTotal()
    {
        return total;
    }
}