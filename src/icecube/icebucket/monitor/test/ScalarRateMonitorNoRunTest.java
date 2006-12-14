/*
 * class: ScalarRateMonitorNoRunTest
 *
 * Version $Id: ScalarRateMonitorNoRunTest.java,v 1.1 2005/04/20 20:52:45 patton Exp $
 *
 * Date: July 12 2004
 *
 * (c) 2004 IceCube Collaboration
 */

package icecube.icebucket.monitor.test;

import icecube.icebucket.monitor.ScalarRateMonitor;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * This class defines the tests that any ScalarMonitor object should pass.
 *
 * @version $Id: ScalarRateMonitorNoRunTest.java,v 1.1 2005/04/20 20:52:45 patton Exp $
 * @author patton
 */
public class ScalarRateMonitorNoRunTest
        extends TestCase
{

    // public static final member data

    // protected static final member data

    /** The changes in count used in tests. */
    protected static final int[] CHANGES = new int[]{0,
                                                     100,
                                                     300,
                                                     200,
                                                     0,
                                                     150,
                                                     1150};

    protected static final int[] WAIT_ONE = new int[]{0,
                                                      100,
                                                      100,
                                                      200,
                                                      200,
                                                      400,
                                                      300};
    protected static final int[] RATE_ONE = new int[]{0,
                                                      1000,
                                                      2000,
                                                      1500,
                                                      1000,
                                                      750,
                                                      1460};
    // static final member data

    // private static final member data

    /**
     * The allowed margin of variation for a test to pass.
     * (10% is very generous, on "better" systems this can be reduced
     * to 0.5%!)
     */
    private static final float ALLOWED_MARGIN = 0.1F;

    // private static member data

    // private instance member data

    /** The object being tested. */
    private ScalarRateMonitor testObject;

    // constructors

    /**
     * Constructs and instance of this test.
     * @param name
     */
    public ScalarRateMonitorNoRunTest(String name)
    {
        super(name);
    }

    // instance member method (alphabetic)

    protected synchronized void addCount(long pause,
                                         int count)
    {
        try {
            Thread.sleep(pause);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        testObject.measure(count);
    }

    protected synchronized void addCount(int wait,
                                         int count,
                                         float expectedRate)
    {
        addCount((long) wait,
                 count);
        assertEquals(expectedRate,
                     testObject.getRate(),
                     expectedRate * ALLOWED_MARGIN);

    }

    /**
     * Sets the object to be tested.
     *
     * @param testObject object to be tested.
     */
    protected void setRateMonitor(ScalarRateMonitor testObject)
    {
        this.testObject = testObject;
    }

    /**
     * Sets up the fixture, for example, open a network connection.
     * This method is called before a test is executed.
     */
//    protected void setUp()
//    {
//    }

    /**
     * Tears down the fixture, for example, close a network connection.
     * This method is called after a test is executed.
     */
    protected void tearDown()
    {
        testObject = null;
    }

    /**
     * Test that the basic rate calculation works for two sample points.
     */
    public void testBasicRate()
    {
        testObject.measure(CHANGES[0]);

        // wait one second and add 1000 Hz sample
        addCount(WAIT_ONE[1],
                 CHANGES[1],
                 (float) RATE_ONE[1]);

        // wait another second and add 3000 Hz, giving an average of 2000 Hz
        addCount(WAIT_ONE[2],
                 CHANGES[2],
                 (float) RATE_ONE[2]);
    }

    /**
     * Test that the default object can handle a memory of depth six.
     */
    public synchronized void testSixDeep()
    {
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
                 (float) RATE_ONE[finished]);
    }

    // static member methods (alphabetic)

    /**
     * Create test suite for this class.
     */
    public static Test suite()
    {
        return new TestSuite(ScalarRateMonitorNoRunTest.class);
    }

    // Description of this object.
    // public String toString() {}
}
