/*
 * class: VerifyAppenderTest
 *
 * Version $Id: VerifyAppenderTest.java,v 1.4 2005/04/18 18:19:20 patton Exp $
 *
 * Date: June 17 2002
 *
 * (c) 2002 LBNL
 */

package icecube.icebucket.logging.test;

import icecube.icebucket.logging.VerifyAppender;
import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.log4j.Logger;

import java.util.Arrays;

/**
 * This class defines the tests needed for any VerifyAppender class.
 *
 * @author patton
 * @version $Id: VerifyAppenderTest.java,v 1.3 2004/07/15 16:51:24 patton Exp
 *          $
 */
public class VerifyAppenderTest
        extends TestCase
{

    // public static final member data

    // public static member functions (alphabetic)

    /**
     * Main routine which runs text test in standalone mode.
     *
     * @param args the arguments with which to execute this method.
     */
    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(suite());
    }

    /**
     * Create test suite for this class.
     *
     * @return the suite of tests declared in this class.
     */
    public static Test suite()
    {
        return new TestSuite(VerifyAppenderTest.class);
    }

    // public instance member function (ctor first then alphabetic)

    /**
     * Constructs and instance of this test.
     *
     * @param name the name of the test.
     */
    public VerifyAppenderTest(String name)
    {
        super(name);
    }

    /**
     * Test that the Appender fails "bad" output.
     */
    public void testBadOutput()
    {
        // Need flag as execption thrown by Logger is the same a that thrown
        // by Assert.fail.
        boolean success = true;
        try {
            getLogger().info(SECOND_STRING);
            success = false;
        } catch (AssertionFailedError e1) {
            // should be caught.
        }
        if (!success) {
            fail("Did not detect mismatched output");
        }
    }

    /**
     * Test that the Appender passes "good" output.
     */
    public void testGoodOutput()
    {
        getLogger().info(FIRST_STRING);
        getLogger().info(SECOND_STRING);
    }

    // protected static member functions (alphabetic)

    // protected instance member function (ctor first then alphabetic)

    /**
     * Sets up the fixture, for example, open a network connection. This method
     * is called before a test is executed.
     *
     * @throws Exception is there is a problem.
     */
    protected void setUp()
            throws Exception
    {
        super.setUp();
        final String[] output = new String[]{FIRST_STRING,
                                             SECOND_STRING};
        appender = new VerifyAppender(Arrays.asList(output));
        final Logger root = Logger.getRootLogger();
        root.addAppender(appender);
    }

    /**
     * Tears down the fixture, for example, close a network connection. This
     * method is called after a test is executed.
     * 
     * @throws Exception is there is a problem.
     */
    protected void tearDown()
            throws Exception
    {
        final Logger root = Logger.getRootLogger();
        root.removeAppender(appender);
        super.tearDown();
    }

    // package static member functions (alphabetic)

    // package instance member function (ctor first then alphabetic)

    // private static member functions (alphabetic)

    // private instance member function (ctor first then alphabetic)

    /**
     * Create an instance of this class. Default constructor is declared, but
     * private, to stop accidental creation of an instance of the class.
     */
    private VerifyAppenderTest()
    {
        super(null);
    }

    /**
     * Returns the logger use by this class.
     *
     * @return the logger use by this class.
     */
    private Logger getLogger()
    {
        return logger;
    }

    // private static member data

    /**
     * First string expected to be output.
     */
    private static final String FIRST_STRING = "First expected output";

    /**
     * Second string expected to be output.
     */
    private static final String SECOND_STRING = "Second expected output";

    // private instance member data
    /**
     * The object being tested.
     */
    private VerifyAppender appender;

    /**
     * The Logger used by this class.
     */
    private final Logger logger = Logger.getLogger(getClass());

}
