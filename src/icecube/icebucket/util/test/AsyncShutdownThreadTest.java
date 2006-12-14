/*
 * class: AsyncShutdownThreadTest
 *
 * Version $Id: AsyncShutdownThreadTest.java,v 1.4 2005/06/09 09:30:28 patton Exp $
 *
 * Date: August 28 2004
 *
 * (c) 2004 IceCube Collaboration
 */

package icecube.icebucket.util.test;

import icecube.icebucket.util.AsyncShutdownThread;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * This class defines the tests that any AsyncShutdownThread object should
 * pass.
 *
 * @author patton
 * @version $Id: AsyncShutdownThreadTest.java,v 1.1 2004/08/29 00:57:09 patton
 *          Exp $
 */
public class AsyncShutdownThreadTest
        extends TestCase
{

    // public static final member data

    // protected static final member data

    // static final member data

    // private static final member data

    /**
     * Pause for thread to start.
     */
    private static final long STARTUP_PAUSE = 20L;

    // private static member data

    // private instance member data

    /**
     * The execption, if any, thrown by the this thread.
     */
    private Exception exception;

    /**
     * The object being tested.
     */
    private AsyncShutdownThread testObject;

    // constructors

    /**
     * Constructs and instance of this test.
     *
     * @param name the name of the test.
     */
    public AsyncShutdownThreadTest(String name)
    {
        super(name);
    }

    // instance member method (alphabetic)

    private synchronized Exception getException()
    {
        return exception;
    }

    private synchronized void setException(Exception exception)
    {
        this.exception = exception;
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
        setException(null);
        testObject = null;
        super.tearDown();
    }

    /**
     * Test that a double incocation works correctly.
     */
    public void testDoubleInvocation()
    {
        testShutdown();
        testObject.shutdown();
    }

    /**
     * Test that registration fails when necessary.
     *
     * @throws InterruptedException
     */
    public void testFailedRegistration()
            throws InterruptedException
    {
        testObject = new AsyncShutdownThread(new Runnable()
        {
            public void run()
            {
                new NoObject().execute();
            }
        });
        testObject.start();
        testObject.join();
        assertTrue(getException() instanceof NullPointerException);

        testObject = new AsyncShutdownThread(new Runnable()
        {
            public void run()
            {
                new NoMethod().execute();
            }
        });
        testObject.start();
        testObject.join();
        assertTrue(getException() instanceof NullPointerException);

        testObject = new AsyncShutdownThread(new Runnable()
        {
            public void run()
            {
                new NoSuchMethod().execute();
            }
        });
        testObject.start();
        testObject.join();
        assertTrue(getException() instanceof IllegalArgumentException);

        testObject = new AsyncShutdownThread(new Runnable()
        {
            public void run()
            {
                new PrivateMethod().execute();
            }
        });
        testObject.start();
        testObject.join();
        assertTrue(getException() instanceof IllegalArgumentException);

        testObject = new AsyncShutdownThread(new Runnable()
        {
            public void run()
            {
                new DefaultMethod().execute();
            }
        });
        testObject.start();
        testObject.join();
        assertTrue(getException() instanceof IllegalArgumentException);

        testObject = new AsyncShutdownThread(new Runnable()
        {
            public void run()
            {
                new ProtectedMethod().execute();
            }
        });
        testObject.start();
        testObject.join();
        assertTrue(getException() instanceof IllegalArgumentException);

        testObject = new AsyncShutdownThread(new Runnable()
        {
            public void run()
            {
                new DoubleRegistration().execute();
            }
        });
        testObject.start();
        testObject.join();
        assertTrue(getException() instanceof IllegalStateException);
    }

    /**
     * Test a non-conforming shutdown is correctly handled.
     */
    public void testNonConformingShutdown()
    {
        testObject = new AsyncShutdownThread(new Runnable()
        {
            public void run()
            {
                new NonConforming().execute();
            }
        });
        try {
            testObject.shutdown();
            fail("Should have thrown a NullPointerException");
        } catch (NullPointerException e) {
            // Should be here.
        }


        testObject = new AsyncShutdownThread(new Runnable()
        {
            public void run()
            {
                new NonConforming().execute();
            }
        });
        testObject.start();
        assertTrue(testObject.isAlive());
        // Give the thread a chance to startup.
        try {
            Thread.sleep(STARTUP_PAUSE);
        } catch (InterruptedException e) {
            // do nothing special
        }

        try {
            testObject.shutdown();
            fail("Should have thrown a NullPointerException");
        } catch (NullPointerException e) {
            // Should be here.
        }

        // Give the thread a chance to shutdown.
        try {
            Thread.sleep(STARTUP_PAUSE);
        } catch (InterruptedException e) {
            // do nothing special
        }

        assertTrue(testObject.isAlive());
        assertNull(getException());
    }

    /**
     * Test a pending shutdown is correctly handled.
     */
    public void testPendingShutdown()
    {
        testObject = new AsyncShutdownThread(new Runnable()
        {
            public void run()
            {
                try {
                    AsyncShutdownFixture.main(null);
                } catch (Exception e) {
                    setException(e);
                }
            }
        });
        testObject.setConforming(true);
        testObject.shutdown();
        testObject.start();

        // Give the thread a chance to shutdown.
        try {
            Thread.sleep(STARTUP_PAUSE);
        } catch (InterruptedException e) {
            // do nothing special
        }

        assertFalse(testObject.isAlive());
        assertNull(getException());
    }

    /**
     * Test the normal behavour of an application executing in the test
     * object.
     */
    public void testShutdown()
    {
        testObject = new AsyncShutdownThread(new Runnable()
        {
            public void run()
            {
                try {
                    AsyncShutdownFixture.main(null);
                } catch (Exception e) {
                    setException(e);
                }
            }
        });
        testObject.start();
        assertTrue(testObject.isAlive());
        // Give the thread a chance to startup.
        try {
            Thread.sleep(STARTUP_PAUSE);
        } catch (InterruptedException e) {
            // do nothing special
        }

        testObject.shutdown();

        // Give the thread a chance to shutdown.
        try {
            Thread.sleep(STARTUP_PAUSE);
        } catch (InterruptedException e) {
            // do nothing special
        }

        assertFalse(testObject.isAlive());
        assertNull(getException());
    }

    // static member methods (alphabetic)

    private class DefaultMethod
            extends AsyncShutdownFixture
    {
        private DefaultMethod()
        {
        }

        void shutdown()
        {
            terminate();
        }

        void execute()
        {
            try {
                AsyncShutdownThread.registerShutdown(this,
                                                     "shutdown");
                run();
            } catch (Exception e) {
                setException(e);
            }
        }
    }

    private class DoubleRegistration
            extends AsyncShutdownFixture
    {
        private DoubleRegistration()
        {
        }

        void execute()
        {
            try {
                AsyncShutdownThread.registerShutdown(this,
                                                     "terminate");
                AsyncShutdownThread.registerShutdown(this,
                                                     "terminate");
                run();
            } catch (Exception e) {
                setException(e);
            }
        }
    }

    private class NoMethod
            extends AsyncShutdownFixture
    {
        private NoMethod()
        {
        }

        void execute()
        {
            try {
                AsyncShutdownThread.registerShutdown(this,
                                                     null);
                run();
            } catch (Exception e) {
                setException(e);
            }
        }
    }

    private class NonConforming
            extends AsyncShutdownFixture
    {
        private NonConforming()
        {
        }

        void execute()
        {
            try {
                run();
            } catch (Exception e) {
                setException(e);
            }
        }
    }

    private class NoObject
            extends AsyncShutdownFixture
    {
        private NoObject()
        {
        }

        void execute()
        {
            try {
                AsyncShutdownThread.registerShutdown(null,
                                                     "terminate");
                run();
            } catch (Exception e) {
                setException(e);
            }
        }
    }

    private class NoSuchMethod
            extends AsyncShutdownFixture
    {
        private NoSuchMethod()
        {
        }

        void execute()
        {
            try {
                AsyncShutdownThread.registerShutdown(this,
                                                     "Another");
                run();
            } catch (Exception e) {
                setException(e);
            }
        }
    }

    private class PrivateMethod
            extends AsyncShutdownFixture
    {
        private PrivateMethod()
        {
        }

        private void shutdown()
        {
            terminate();
        }

        void execute()
        {
            try {
                AsyncShutdownThread.registerShutdown(this,
                                                     "shutdown");
                run();
            } catch (Exception e) {
                setException(e);
            }
        }
    }

    private class ProtectedMethod
            extends AsyncShutdownFixture
    {
        private ProtectedMethod()
        {
        }

        protected void shutdown()
        {
            terminate();
        }

        void execute()
        {
            try {
                AsyncShutdownThread.registerShutdown(this,
                                                     "shutdown");
                run();
            } catch (Exception e) {
                setException(e);
            }
        }
    }


    /**
     * Create test suite for this class.
     *
     * @return the suite of tests declared in this class.
     */
    public static Test suite()
    {
        return new TestSuite(AsyncShutdownThreadTest.class);
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
