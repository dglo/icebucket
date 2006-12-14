/*
 * class: ReadXmlTest
 *
 * Version $Id: ReadXmlTest.java,v 1.2 2006/11/12 03:51:47 patton Exp $
 *
 * Date: September 19 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.icebucket.jaxb.example.test;

import icecube.icebucket.jaxb.example.ReadXml;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * This class defines the tests that any ReadXml object should pass.
 *
 * @author patton
 * @version $Id: ReadXmlTest.java,v 1.2 2006/11/12 03:51:47 patton Exp $
 */
public class ReadXmlTest
        extends TestCase
{

    // public static final member data

    // protected static final member data

    // static final member data

    // private static final member data

    // private static member data

    // private instance member data

    /**
     * The object being tested.
     */
    private ReadXml testObject;

    // constructors

    /**
     * Constructs and instance of this test.
     *
     * @param name the name of the test.
     */
    public ReadXmlTest(String name)
    {
        super(name);
    }

    // instance member method (alphabetic)

    /**
     * Sets up the fixture, for example, open a network connection. This method
     * is called before a test is executed.
     *
     * @throws Exception if super class setUp fails.
     */
//    protected void setUp()
//            throws Exception
//    {
//        super.setUp();
//    }

    /**
     * Tears down the fixture, for example, close a network connection. This
     * method is called after a test is executed.
     *
     * @throws Exception if super class tearDown fails.
     */
    protected void tearDown()
            throws Exception
    {
        testObject = null;
        super.tearDown();
    }

    /**
     * Explanation of test.
     */
    public void testSomething()
    {
//        fail("No test has been specified for " + testObject.toString());
    }

    // static member methods (alphabetic)

    /**
     * Create test suite for this class.
     *
     * @return the suite of tests declared in this class.
     */
    public static Test suite()
    {
        return new TestSuite(ReadXmlTest.class);
    }

    // Description of this object.
    // public String toString() {}

    /**
     * Main routine which runs text test in standalone mode.
     *
     * @param args the arguments with which to execute this method.
     */
    public static void main(String[] args)
    {
        TestRunner.run(suite());
    }
}
