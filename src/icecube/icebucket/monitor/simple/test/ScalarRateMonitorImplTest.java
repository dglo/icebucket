/*
 * class: SimpleScalarRateMonitorTestorTest
 *
 * Version $Id: ScalarRateMonitorImplTest.java,v 1.2 2005/09/22 11:12:55 patton Exp $
 *
 * Date: July 12 2004
 *
 * (c) 2004 IceCube Collaboration
 */

package icecube.icebucket.monitor.simple.test;

import icecube.icebucket.monitor.simple.ScalarRateMonitorImpl;
import icecube.icebucket.monitor.test.ScalarRateMonitorNoRunTest;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * This class defines the tests that any ScalarRateMonitorImpl object should pass.
 *
 * @version $Id: ScalarRateMonitorImplTest.java,v 1.2 2005/09/22 11:12:55 patton Exp $
 * @author patton
 */
public class ScalarRateMonitorImplTest
        extends ScalarRateMonitorNoRunTest
{

    // public static final member data

    // protected static final member data

    // static final member data

    // private static final member data

    private static final int RATE_ONE_DEPTH_FIVE = 1500;


    // private static member data

    // private instance member data

    /** The object being tested. */
    private ScalarRateMonitorImpl testObject;

    // constructors

    /**
     * Constructs and instance of this test.
     * @param name
     */
    public ScalarRateMonitorImplTest(String name)
    {
        super(name);
    }

    // instance member method (alphabetic)

    /**
     * Sets up the fixture, for example, open a network connection.
     * This method is called before a test is executed.
     */
    protected void setUp()
    {
        setRateMonitor(new ScalarRateMonitorImpl());
    }

    /**
     * Tears down the fixture, for example, close a network connection.
     * This method is called after a test is executed.
     */
    protected void tearDown()
    {
        testObject = null;
    }

    /**
     * Test that the depth limit works correctly.
     */
    public synchronized void testLimitedDepth()
    {
        testObject = new ScalarRateMonitorImpl(0L,
                                               5);
        final int finished = WAIT_ONE.length - 1;
        testObject.measure(CHANGES[0]);
        for (int i = 1;
             finished != i;
             i++) {
            addCount((long) WAIT_ONE[i],
                     CHANGES[i]);
        }
        addCount(WAIT_ONE[finished],
                 CHANGES[finished],
                 (float) RATE_ONE_DEPTH_FIVE);
    }

    // static member methods (alphabetic)

    /**
     * Create test suite for this class.
     */
    public static Test suite()
    {
        return new TestSuite(ScalarRateMonitorImplTest.class);
    }

    // Description of this object.
    // public String toString() {}

    /**
     * Main routine which runs text test in standalone mode.
     */
    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(suite());
    }
}