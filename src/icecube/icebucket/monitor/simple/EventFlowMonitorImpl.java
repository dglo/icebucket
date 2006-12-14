/*
 * class: SimpleEventFlowMonitornitor
 *
 * Version $Id: EventFlowMonitorImpl.java,v 1.3 2005/09/22 13:16:29 patton Exp $
 *
 * Date: December 16 2004
 *
 * (c) 2004 IceCube Collaboration
 */

package icecube.icebucket.monitor.simple;

import icecube.icebucket.monitor.EventFlowMonitor;
import icecube.icebucket.monitor.ScalarMonitor;
import icecube.icebucket.monitor.ScalarPairedMonitor;
import icecube.icebucket.monitor.ScalarRateMonitor;
import icecube.icebucket.monitor.ScalarSumMonitor;

/**
 * This class is a simple implementation of the {@link EventFlowMonitor}
 * interface.
 *
 * @author patton
 * @version $Id: EventFlowMonitorImpl.java,v 1.1 2005/04/20 21:07:34 patton Exp
 *          $
 */
public class EventFlowMonitorImpl
        implements EventFlowMonitor
{

    // public static final member data

    // protected static final member data

    // static final member data

    // private static final member data

    // private static member data

    // private instance member data

    /**
     * The ScalarMonitor object measuring object rate of flow.
     */
    private final ScalarRateMonitor flowRate;

    /**
     * The ScalarMonitor object measuring object rate of flow.
     */
    private final ScalarSumMonitor flowTotal;

    /**
     * The ScalarMonitor object used to look at the object flow of this
     * object.
     */
    private final ScalarMonitor flowMonitor;

    /**
     * The ScalarMonitor object measuring size rate of flow.
     */
    private final ScalarRateMonitor sizeRate;

    /**
     * The ScalarMonitor object measuring size total.
     */
    private final ScalarSumMonitor sizeTotal;

    /**
     * The ScalarMonitor object used to look at the size flow of this object.
     */
    private final ScalarMonitor sizeMonitor;

    /**
     * The ScalerMonitor that contains all the monitors in this object.
     */
    private final ScalarMonitor unifiedMonitor;

    // constructors

    /**
     * Create an instance of this class.
     */
    public EventFlowMonitorImpl(ScalarRateMonitor flowRate,
                                ScalarSumMonitor flowTotal,
                                ScalarRateMonitor sizeRate,
                                ScalarSumMonitor sizeTotal)
    {
        this.flowRate = flowRate;
        this.flowTotal = flowTotal;
        flowMonitor = new ScalarPairedMonitor(flowRate,
                                              flowTotal);
        this.sizeRate = sizeRate;
        this.sizeTotal = sizeTotal;
        sizeMonitor = new ScalarPairedMonitor(sizeRate,
                                              sizeTotal);
        unifiedMonitor = new ScalarPairedMonitor(flowMonitor,
                                                 sizeMonitor);
    }

    // instance member method (alphabetic)

    public void dispose()
    {
        unifiedMonitor.dispose();
    }

    public float[] getFlowHistory()
    {
        return flowRate.getHistory();
    }

    public float getFlowRate()
    {
        return flowRate.getRate();
    }

    public long getFlowTotal()
    {
        return flowTotal.getTotal();
    }

    public float[] getSizeHistory()
    {
        return sizeRate.getHistory();
    }

    public float getSizeRate()
    {
        return sizeRate.getRate();
    }

    public long getSizeTotal()
    {
        return sizeTotal.getTotal();
    }

    public void measure(int count,
                        int size)
    {
        flowMonitor.measure(count);
        sizeMonitor.measure(size);
    }

    public void reset()
    {
        unifiedMonitor.reset();
    }

    // static member methods (alphabetic)

    // Description of this object.
    // public String toString() {}

    // public static void main(String args[]) {}
}