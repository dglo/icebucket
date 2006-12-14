/*
 * class: MarkableEventFlowMonitorImpl
 *
 * Version $Id: MarkableEventFlowMonitorImpl.java,v 1.1 2006/02/21 03:25:14 patton Exp $
 *
 * Date: February 20 2006
 *
 * (c) 2006 IceCube Collaboration
 */

package icecube.icebucket.monitor.simple;

import icecube.icebucket.monitor.MarkableEventFlowMonitor;
import icecube.icebucket.monitor.ScalarRateMonitor;
import icecube.icebucket.monitor.ScalarSumMonitor;
import icecube.icebucket.monitor.ScalarPairedMonitor;

/**
 * This class ...does what?
 *
 * @author patton
 * @version $Id: MarkableEventFlowMonitorImpl.java,v 1.1 2006/02/21 03:25:14 patton Exp $
 */
public class MarkableEventFlowMonitorImpl
        extends EventFlowMonitorImpl
        implements MarkableEventFlowMonitor
{

    // public static final member data

    // protected static final member data

    // static final member data

    // private static final member data

    // private static member data

    // private instance member data

    /**
     * The ScalarSumMonitor object used to look at the flow since mark.
     */
    private final ScalarSumMonitor flowSinceMark;

    /**
     * The ScalarSumMonitor object used to look at the size since mark.
     */
    private final ScalarSumMonitor sizeSinceMark;

    // constructors

    /**
     * Create an instance of this class.
     */
    public MarkableEventFlowMonitorImpl(ScalarRateMonitor flowRate,
                                        ScalarSumMonitor flowTotal,
                                        ScalarSumMonitor flowSinceMark,
                                        ScalarRateMonitor sizeRate,
                                        ScalarSumMonitor sizeTotal,
                                        ScalarSumMonitor sizeSinceMark)
    {
        super(flowRate,
              new ShadowSumMonitor(flowTotal,
                                   flowSinceMark),
              sizeRate,
              new ShadowSumMonitor(sizeTotal,
                                   sizeSinceMark));
        this.flowSinceMark = flowSinceMark;
        this.sizeSinceMark = sizeSinceMark;
    }

    // instance member method (alphabetic)

    public long getFlowSinceMark()
    {
        return flowSinceMark.getTotal();
    }

    public long getSizeSinceMark()
    {
        return sizeSinceMark.getTotal();
    }

    public void mark()
    {
        flowSinceMark.reset();
        sizeSinceMark.reset();
    }

    // static member methods (alphabetic)

    private static class ShadowSumMonitor
            extends ScalarPairedMonitor
            implements ScalarSumMonitor
    {
        ScalarSumMonitor primary;

        ShadowSumMonitor(ScalarSumMonitor primary,
                         ScalarSumMonitor secondary)
        {
            super(primary,
                  secondary);
            this.primary = primary;
        }

        public long getTotal()
        {
            return primary.getTotal();
        }
    }

    // Description of this object.
    // public String toString() {}

    // public static void main(String args[]) {}
}