/*
 * class: Ice3JmxTest
 *
 * Version $Id: Ice3JmxTest.java,v 1.3 2005/06/09 09:30:49 patton Exp $
 *
 * Date: July 14 2004
 *
 * (c) 2004 IceCube Collaboration
 */

package icecube.icebucket.jmx.test;

import icecube.icebucket.jmx.Ice3Jmx;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

/**
 * This class defines the tests that any Ice3Jmx object should pass.
 *
 * @author patton
 * @version $Id: Ice3JmxTest.java,v 1.3 2005/06/09 09:30:49 patton Exp $
 */
public class Ice3JmxTest
        extends TestCase {
    // private instance member data

    /**
     * The object being tested.
     */
    //private Ice3Jmx testObject;

    // constructors

    /**
     * Constructs and instance of this test.
     *
     * @param name the name of the test suite.
     */
    public Ice3JmxTest(String name) {
        super(name);
    }

    // instance member method (alphabetic)

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
     *
     * @throws Exception if there is a problem.
     */
//    protected void tearDown()
//            throws Exception {
//        testObject = null;
//        super.tearDown();
//    }

    /**
     * Test that the {@link Ice3Jmx#transformName} method functions correctly.
     */
    public static void testObjectNameTransform() {
        ObjectName original = null;
        ObjectName transformed = null;
        try {
            original = new ObjectName("fred:first=one,second=two");
            transformed =
                    new ObjectName("fred:first=three,second=two");
        } catch (MalformedObjectNameException e) {
            fail("Exception thrown: " + e.toString());
        }
        ObjectName result = Ice3Jmx.transformName(original,
                "first",
                "one");
        assertEquals(original,
                result);
        result = Ice3Jmx.transformName(original,
                "first",
                "three");
        assertEquals(transformed,
                result);
        try {
            Ice3Jmx.transformName(original,
                    "third",
                    "three");
            fail("IllegalArgumentException should have been thrown");
        } catch (IllegalArgumentException e) {
            // should get here
        }
    }

    // static member methods (alphabetic)

    /**
     * Create test suite for this class.
     *
     * @return the suite to be run.
     */
    public static Test suite() {
        return new TestSuite(Ice3JmxTest.class);
    }

    // Description of this object.
    // public String toString() {}

    /**
     * Main routine which runs text test in standalone mode.
     *
     * @param args the programs arguments.
     */
    public static void main(String[] args) {
        TestRunner.run(suite());
    }
}