/*
 * class: ScalarPairedMonitor
 *
 * Version $Id: ScalarPairedMonitor.java,v 1.2 2005/09/22 11:12:54 patton Exp $
 *
 * Date: July 13 2004
 *
 * (c) 2004 IceCube Collaboration
 */

package icecube.icebucket.monitor;


/**
 * This class pairs two ScalarMonitors so they can both make measurements at
 * the same time.
 *
 * @author patton
 * @version $Id: ScalarPairedMonitor.java,v 1.1 2005/04/20 21:11:43 patton Exp
 *          $
 */
public class ScalarPairedMonitor
        implements ScalarMonitor
{

    // private instance member data

    /**
     * The two RateMonitors wrapped by this class.
     */
    private final ScalarMonitor monitorOne;
    private final ScalarMonitor monitorTwo;

    // constructors

    /**
     * Create an instance of this class.
     *
     * @param monitorOne one of the monitors to be used by this object.
     * @param monitorTwo the other monitor to be used by this object.
     */
    public ScalarPairedMonitor(ScalarMonitor monitorOne,
                               ScalarMonitor monitorTwo)
    {
        this.monitorOne = monitorOne;
        this.monitorTwo = monitorTwo;
    }

    // instance member method (alphabetic)

    public void dispose()
    {
        monitorOne.dispose();
        monitorTwo.dispose();
    }

    public void measure(int count)
    {
        monitorOne.measure(count);
        monitorTwo.measure(count);
    }

    public void reset()
    {
        monitorOne.reset();
        monitorTwo.reset();
    }
}