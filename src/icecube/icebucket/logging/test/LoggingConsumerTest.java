/*
 * class: LoggingConsumerTest
 *
 * Version $Id: LoggingConsumerTest.java,v 1.5 2005/06/09 09:30:49 patton Exp $
 *
 * Date: February 24 2003
 *
 * (c) 2003 LBNL
 */

package icecube.icebucket.logging.test;

import icecube.icebucket.logging.LoggingConsumer;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * This class defines the tests that any LoggingConsumer object should pass.
 *
 * @version $Id: LoggingConsumerTest.java,v 1.5 2005/06/09 09:30:49 patton Exp $
 * @author patton
 */
public class LoggingConsumerTest
        extends TestCase
{

    // public static final member data

    // protected static final member data

    // static final member data

    // private static final member data

    // private static member data

    // private instance member data

    /** The object being tested. */
    private LoggingConsumer testObject;

    // constructors

    /**
     * Constructs and instance of this test.
     *
     * @param name the name of the test.
     */
    public LoggingConsumerTest(String name)
    {
        super(name);
    }

    // instance member function (alphabetic)

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
     * @throws Exception is there is a problem.
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
    public static void testAddingVerify()
    {
    }

    // static member functions (alphabetic)

    /**
     * Create test suite for this class.
     *
     * @return the suite of tests declared in this class.
     */
    public static Test suite()
    {
        return new TestSuite(LoggingConsumerTest.class);
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
        junit.textui.TestRunner.run(suite());
    }
}
