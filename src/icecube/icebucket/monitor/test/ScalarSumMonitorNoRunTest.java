/*
 * class: ScalarSumMonitorNoRunTest
 *
 * Version $Id: ScalarSumMonitorNoRunTest.java,v 1.1 2005/04/20 21:03:01 patton Exp $
 *
 * Date: July 15 2004
 *
 * (c) 2004 IceCube Collaboration
 */

package icecube.icebucket.monitor.test;

import icecube.icebucket.monitor.ScalarSumMonitor;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * This class defines the tests that any ScalarSumMonitor object should pass.
 *
 * @version $Id: ScalarSumMonitorNoRunTest.java,v 1.1 2005/04/20 21:03:01 patton Exp $
 * @author patton
 */
public class ScalarSumMonitorNoRunTest
        extends TestCase
{

    // public static final member data

    // protected static final member data

    // static final member data

    // private static final member data

    private static final int[] COUNTS = new int[]{0,
                                                  100,
                                                  300,
                                                  200,
                                                  0,
                                                  150,
                                                  1150};

    // private static member data

    // private instance member data

    /** The object being tested. */
    private ScalarSumMonitor testObject;

    // constructors

    /**
     * Create an instance of this class.
     * Default constructor is declared, but private, to stop accidental
     * creation of an instance of the class.
     */
    private ScalarSumMonitorNoRunTest()
    {
        this(null);
    }

    /**
     * Constructs and instance of this test.
     * @param name
     */
    public ScalarSumMonitorNoRunTest(String name)
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
//        testObject = new ScalarSumMonitor();
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
     * Test normal accumulation.
     */
    public void testAccumulation()
    {
        int total = 0;
        final int finished = COUNTS.length;
        for (int i = 0;
             finished != i;
             i++) {
            testObject.measure(COUNTS[i]);
            total += COUNTS[i];
            assertEquals(total,
                         testObject.getTotal());
        }
    }

    /**
     * Test the most basic accumulation.
     */
    public void testSingleAccumulation()
    {
        testObject.measure(COUNTS[0]);
        assertEquals(COUNTS[0],
                     testObject.getTotal());
    }

    // static member methods (alphabetic)

    /**
     * Create test suite for this class.
     */
    public static Test suite()
    {
        return new TestSuite(ScalarSumMonitorNoRunTest.class);
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