/*
 * class: MarkableEventFlowMonitorAdapter
 *
 * Version $Id: MarkableEventFlowMonitorAdapter.java,v 1.1 2006/02/21 03:25:14 patton Exp $
 *
 * Date: February 20 2006
 *
 * (c) 2006 IceCube Collaboration
 */

package icecube.icebucket.monitor;

/**
 * This class enables EventFlowMonitor objects to function as MarkableEventFlowMonitor objects.
 *
 * @version $Id: MarkableEventFlowMonitorAdapter.java,v 1.1 2006/02/21 03:25:14 patton Exp $
 * @author patton
 */
public class MarkableEventFlowMonitorAdapter
    implements MarkableEventFlowMonitor
{

    // public static final member data

    // protected static final member data

    // static final member data

    // private static final member data

    // private static member data

    // private instance member data

    /**
     * The EventFlowMonitor being adapted.
     */
    private final EventFlowMonitor monitor;

    // constructors

    /**
     * Create an instance of this class.
     */
    public MarkableEventFlowMonitorAdapter(EventFlowMonitor monitor)
    {
        this.monitor = monitor;
    }

    // instance member method (alphabetic)

    public long getFlowSinceMark()
    {
        return monitor.getFlowTotal();
    }

    public long getSizeSinceMark()
    {
        return monitor.getSizeTotal();
    }

    public void mark()
    {
    }

    public void dispose()
    {
        monitor.dispose();
    }

    public float[] getFlowHistory()
    {
        return monitor.getFlowHistory();
    }

    public float getFlowRate()
    {
        return monitor.getFlowRate();
    }

    public long getFlowTotal()
    {
        return monitor.getFlowTotal();
    }

    public float[] getSizeHistory()
    {
        return monitor.getSizeHistory();
    }

    public float getSizeRate()
    {
        return monitor.getSizeRate();
    }

    public long getSizeTotal()
    {
        return monitor.getSizeTotal();
    }

    public void measure(int first,
                        int second)
    {
        monitor.measure(first,
                        second);
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