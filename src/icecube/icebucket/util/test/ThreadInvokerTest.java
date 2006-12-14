/*
 * class: ThreadInvokerTest
 *
 * Version $Id: ThreadInvokerTest.java,v 1.2 2005/06/09 09:29:13 patton Exp $
 *
 * Date: August 23 2004
 *
 * (c) 2004 IceCube Collaboration
 */

package icecube.icebucket.util.test;

import icecube.icebucket.util.ThreadInvoker;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * This class defines the tests that any RunnableInvoker object should pass.
 *
 * @author patton
 * @version $Id: ThreadInvokerTest.java,v 1.2 2005/06/09 09:29:13 patton Exp $
 */
public class ThreadInvokerTest
        extends TestCase
{

    // public static final member data

    // protected static final member data

    // static final member data

    // private static final member data

    /**
     * The value set by this thread.
     */
    private static final int THIS_THREAD_VALUE = 0;

    /**
     * The value set by a different thread.
     */
    private static final int OTHER_THREAD_VALUE = THIS_THREAD_VALUE + 1;

    /**
     * Pause for thread to start.
     */
    private static final long STARTUP_PAUSE = 20L;

    // private static member data

    // private instance member data

    /**
     * The object being tested.
     */
    private ThreadInvoker testObject;

    /**
     * The Thread running the testObject.
     */
    private Thread thread;

    /**
     * A test location to check multi-threaded access.
     */
    private int value;

    // constructors

    /**
     * Constructs and instance of this test.
     *
     * @param name the name of the test.
     */
    public ThreadInvokerTest(String name)
    {
        super(name);
    }

    // instance member method (alphabetic)

    private synchronized int getValue()
    {
        return value;
    }

    private synchronized void setValue(int value)
    {
        this.value = value;
    }

    /**
     * Sets up the fixture, for example, open a network connection.
     * This method is called before a test is executed.
     *
     * @throws Exception
     */
    protected void setUp()
            throws Exception
    {
        super.setUp();
        testObject = new ThreadInvoker();
        thread = new Thread(testObject);
        thread.start();

        // Make sure thread has started.
        try {
            Thread.sleep(STARTUP_PAUSE);
        } catch (InterruptedException e) {
            // do nothing
        }
    }

    /**
     * Tears down the fixture, for example, close a network connection. This
     * method is called after a test is executed.
     *
     * @throws Exception
     */
    protected void tearDown()
            throws Exception
    {
        if (null != thread) {
            testObject.terminate();
        }
        testObject = null;
        super.tearDown();
    }

    /**
     * Test that Runnables added before the thread starts still get invoked.
     *
     * @throws InterruptedException
     */
    public void testEarlyInvocation()
            throws InterruptedException
    {
        // terminate original testObject
        testTerminate();

        // Build new one
        testObject = new ThreadInvoker();
        thread = new Thread(testObject);

        // Add early terminate command
        testObject.terminate();

        // Start thread
        thread.start();

        // check termination
        thread.join();
        thread = null;
    }

    /**
     * Test that a RUnnable instance can be executed in another Thread.
     *
     * @throws InterruptedException
     */
    public void testExecutionLater()
            throws InterruptedException
    {
        setValue(THIS_THREAD_VALUE);
        assertEquals(THIS_THREAD_VALUE,
                     getValue());
        testObject.invokeLater(new Runnable()
        {
            public void run()
            {
                try {
                    Thread.sleep(STARTUP_PAUSE);
                } catch (InterruptedException e) {
                    // do nothing
                }
                setValue(OTHER_THREAD_VALUE);
            }
        });
        assertEquals(THIS_THREAD_VALUE,
                     getValue());
        testTerminate();
        assertEquals(OTHER_THREAD_VALUE,
                     getValue());
    }

    /**
     * Test that a RUnnable instance can be executed in another Thread.
     *
     * @throws InterruptedException
     */
    public void testExecutionWait()
            throws InterruptedException
    {
        setValue(THIS_THREAD_VALUE);
        assertEquals(THIS_THREAD_VALUE,
                     getValue());
        testObject.invokeAndWait(new Runnable()
        {
            public void run()
            {
                try {
                    Thread.sleep(STARTUP_PAUSE);
                } catch (InterruptedException e) {
                    // do nothing
                }
                setValue(OTHER_THREAD_VALUE);
            }
        });
        assertEquals(OTHER_THREAD_VALUE,
                     getValue());
        testTerminate();
        assertEquals(OTHER_THREAD_VALUE,
                     getValue());
    }

    /**
     * Test that a Thread running a RunnableInvoker termintes correctly.
     *
     * @throws InterruptedException
     */
    public void testTerminate()
            throws InterruptedException
    {
        testObject.terminate();
        thread.join();
        thread = null;
    }

    // static member methods (alphabetic)

    /**
      * Create test suite for this class.
      *
      * @return the suite of tests declared in this class.
      */
     public static Test suite()
    {
        return new TestSuite(ThreadInvokerTest.class);
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