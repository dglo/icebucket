/*
 * class: TrackingTest
 *
 * Version $Id: TrackingTest.java,v 1.2 2005/04/18 18:19:20 patton Exp $
 *
 * Date: October 26 2004
 *
 * (c) 2004 IceCube Collaboration
 */

package icecube.icebucket.logging.test;

import icecube.icebucket.logging.Tracking;
import icecube.icebucket.logging.VerifyAppender;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.apache.log4j.Logger;

import java.util.Arrays;

/**
 * This class defines the tests that any Tracking object should pass.
 *
 * @author patton
 * @version $Id: TrackingTest.java,v 1.2 2005/04/18 18:19:20 patton Exp $
 */
public class TrackingTest
        extends TestCase
{

    // public static final member data

    // protected static final member data

    // static final member data

    // private static final member data

    /**
     * The Logger used by this class.
     */
    private final Logger logger = Logger.getLogger(getClass());

    // private static member data

    // private instance member data

    /**
     * The object being tested.
     */
    private Tracking testObject;

    /**
     * The appender used to test the output.
     */
    private VerifyAppender appender;

    // constructors

    /**
     * Constructs and instance of this test.
     *
     * @param name the name of the test.
     */
    public TrackingTest(String name)
    {
        super(name);
    }

    // instance member method (alphabetic)

    private void prepareVersionLogging()
    {
        final Package pack = getClass().getPackage();
        final String[] output = new String[]{"The tag for \"" +
                pack.getSpecificationTitle() +
                "\" is \"" +
                pack.getSpecificationVersion() +
                '\"',
                                       "The build label for \"" +
                pack.getImplementationTitle() +
                "\" is \"" +
                pack.getImplementationVersion() +
                '\"'};
        appender = new VerifyAppender(Arrays.asList(output));
    }

    /**
     * Sets up the fixture, for example, open a network connection. This method
     * is called before a test is executed.
     *
     * @throws Exception if super class setUp fails.
     */
    protected void setUp()
            throws Exception
    {
        super.setUp();
    }

    /**
     * Tears down the fixture, for example, close a network connection. This
     * method is called after a test is executed.
     *
     * @throws Exception if super class tearDown fails.
     */
    protected void tearDown()
            throws Exception
    {
        final Logger root = Logger.getRootLogger();
        root.removeAppender(appender);
        testObject = null;
        super.tearDown();
    }

    /**
     * Explanation of test.
     */
    public void testLog4jVersionLogging()
    {
        prepareVersionLogging();
        final Logger root = Logger.getRootLogger();
        root.addAppender(appender);
        Tracking.logVersions(logger,
                             getClass());
    }

    // static member methods (alphabetic)

    /**
     * Create test suite for this class.
     *
     * @return the suite of tests declared in this class.
     */
    public static Test suite()
    {
        return new TestSuite(TrackingTest.class);
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
