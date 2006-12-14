/*
 * class: SimpleServerTest
 *
 * Version $Id: SimpleServerTest.java,v 1.6 2005/04/18 18:43:15 patton Exp $
 *
 * Date: August 26 2004
 *
 * (c) 2004 IceCube Collaboration
 */

package icecube.icebucket.net.test;

import icecube.icebucket.net.ServerWorker;
import icecube.icebucket.net.SimpleServer;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * This class defines the tests that any SimpleServer object should pass.
 *
 * @author patton
 * @version $Id: SimpleServerTest.java,v 1.6 2005/04/18 18:43:15 patton Exp $
 */
public class SimpleServerTest
        extends TestCase
{

    // public static final member data

    // protected static final member data

    // static final member data

    // private static final member data

    // private static member data

    /**
     * Number used to assign unique ports to each test that build a server.
     */
    private static final int nextPortNumber = 1234;

    // private instance member data

    /**
     * A channel which can connect to the test object.
     */
    private SocketChannel channel;

    /**
     * The port being used by the executing test.
     */
    private int port;

    /**
     * The object being tested.
     */
    private SimpleServer testObject;

    // constructors

    /**
     * Constructs and instance of this test.
     *
     * @param name the name of the test.
     */
    public SimpleServerTest(String name)
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
    protected void setUp()
            throws Exception
    {
        super.setUp();
        port = getPortNumber();
        testObject = SimpleServer.createSimpleServer(new MockServerWorker(),
                                                     port,
                                                     "SimpleServer");
        testObject.start();

        // Give Server time to establish itself.
        letServerReact();
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
        if (null != testObject) {
            testTerminate();
        }
        super.tearDown();
    }

    /**
     * Tests that a connection can be made to the server.
     *
     * @throws IOException
     */
    public void testConnection()
            throws IOException
    {
        channel = SocketChannel.open(new InetSocketAddress("localhost",
                                                           port));
        // Give Server time to react.
        letServerReact();
        assertEquals(1,
                     testObject.getConnectionCount());
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
        testObject = SimpleServer.createSimpleServer(new MockServerWorker(),
                                                     port,
                                                     "SimpleServer");

        // Add early terminate command
        testObject.terminate();

        // Start thread
        testObject.start();

        // check termination
        testObject.join();
        testObject = null;
    }

    /**
     * Test that the server terminates correctly.
     *
     * @throws InterruptedException
     */
    public void testTerminate()
            throws InterruptedException
    {
        testObject.terminate();
        testObject.join();
        testObject = null;
    }

    // static member methods (alphabetic)

    private static synchronized int getPortNumber()
    {
        final int result = nextPortNumber;
//        nextPortNumber += 1;
        return result;
    }

    private static void letServerReact()
    {
        try {
            Thread.sleep(20L);
        } catch (InterruptedException e) {
            // do nothing if interrupted.
        }
    }

    /**
     * Create test suite for this class.
     *
     * @return the suite of tests declared in this class.
     */
    public static Test suite()
    {
        return new TestSuite(SimpleServerTest.class);
    }

    // Description of this object.
    // public String toString() {}

    private static class MockServerWorker
            implements ServerWorker
    {
        private MockServerWorker()
        {
        }

        public SocketChannel acceptKey(SelectionKey key)
                throws IOException
        {

            final ServerSocketChannel serverChannel =
                    (ServerSocketChannel) key.channel();
            final SocketChannel connection = serverChannel.accept();

            connection.configureBlocking(false);
            final Selector selector = key.selector();
            connection.register(selector,
                                SelectionKey.OP_READ);
            return connection;
        }

        public void closeConnection(SocketChannel connection)
                throws IOException
        {
            final Socket socket = connection.socket();
            if (!socket.isClosed()) {
                socket.close();
            }
        }

        public boolean readKey(SelectionKey key)
        {
            return true;
        }
    }

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
