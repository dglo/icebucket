/*
 * class: ServerSocketTest
 *
 * Version $Id: ServerSocketTest.java,v 1.3 2005/04/18 18:43:15 patton Exp $
 *
 * Date: August 31 2004
 *
 * (c) 2004 IceCube Collaboration
 */

package icecube.icebucket.net.test;

import icecube.icebucket.logging.LoggingConsumer;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * This class defines the tests that the {@link ServerSocket} class should
 * pass.
 *
 * @author patton
 * @version $Id: ServerSocketTest.java,v 1.3 2005/04/18 18:43:15 patton Exp $
 */
public class ServerSocketTest
        extends TestCase
{

    // public static final member data

    // protected static final member data

    // static final member data

    // private static final member data

    // private static member data

    /**
     * The logging interface used to output messages.
     */
    private static final Log log =
            LogFactory.getLog(ServerSocketTest.class);

    /**
     * The next port on which to open a server socket.
     */
    private static final int nextPort = 1234;

    // private instance member data

    /**
     * The Sokcet acting as a client.
     */
    private Socket client;

    /**
     * The execption, if any, thrown by the second thread.
     */
    private Exception exception;

    /**
     * The port being used by the current test.
     */
    private int port;

    /**
     * The object being tested.
     */
    private ServerSocket testObject;

    /**
     * The second thread used by tests.
     */
    private Thread thread;

    // constructors

    /**
     * Constructs and instance of this test.
     *
     * @param name the name of the test.
     */
    public ServerSocketTest(String name)
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
        LoggingConsumer.installDefault();
        port = getPortForTest();
        log.debug("port = " + port);
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
     * Test the basic operation of a ServerSocket.
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public void testBasicAccept()
            throws IOException,
                   InterruptedException
    {
        final ServerSocketChannel channel = ServerSocketChannel.open();
        testObject = channel.socket();
        testObject.bind(new InetSocketAddress(port));

        thread = new Thread(new Runnable()
        {
            public void run()
            {
                try {
                    channel.configureBlocking(true);
                    final SocketChannel acceptedClient = channel.accept();
                    log.debug("Accepted Client");
                    acceptedClient.close();
                    log.debug("Closed Client");
                    channel.close();
                } catch (IOException e) {
                    setException(e);
                }
            }
        });
        thread.start();

        client = new Socket("localhost",
                            port);

        thread.join();
        assertException(getException());
    }

    /**
     * Test whether the server socker porperly closes.
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public void testBasicServerClosing()
            throws IOException,
                   InterruptedException
    {
        testBasicAccept();
        testBasicAccept();
    }

    /**
     * Test the selector operation of a ServerSocket.
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public void testNonBlockingAccept()
            throws IOException,
                   InterruptedException
    {
        final ServerSocketChannel channel = ServerSocketChannel.open();
        testObject = channel.socket();
        testObject.bind(new InetSocketAddress(port));

        thread = new Thread(new Runnable()
        {
            public void run()
            {
                try {
                    channel.configureBlocking(false);
                    SocketChannel acceptedClient = null;
                    while (null == acceptedClient) {
                        acceptedClient = channel.accept();
                    }
                    log.debug("Accepted Client");
                    acceptedClient.close();
                    log.debug("Closed Client");
                    channel.close();
                } catch (IOException e) {
                    setException(e);
                }
            }
        });
        thread.start();

        client = new Socket("localhost",
                            port);

        thread.join();
        assertException(getException());
    }

    /**
     * Test whether the server socker porperly closes.
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public void testNonBlockingClosing()
            throws IOException,
                   InterruptedException
    {
        testNonBlockingAccept();
        testNonBlockingAccept();
    }

    /**
     * Test the selector operation of a ServerSocket.
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public void testSelectorAccept()
            throws IOException,
                   InterruptedException
    {
        final ServerSocketChannel channel = ServerSocketChannel.open();
        testObject = channel.socket();
        testObject.bind(new InetSocketAddress(port));

        thread = new Thread(new Runnable()
        {
            public void run()
            {
                try {
                    final Selector selector = Selector.open();
                    channel.configureBlocking(false);
                    final SelectionKey key =
                            channel.register(selector,
                                             SelectionKey.OP_ACCEPT);
                    SocketChannel acceptedClient = null;
                    while (null == acceptedClient) {
                        selector.select();
                        log.debug("select returned.");
                        acceptedClient = channel.accept();
                    }
                    log.debug("Accepted Client");
                    acceptedClient.close();
                    log.debug("Closed Client");
                    channel.close();

                    // The following cleans up a known problem when using a
                    // SeverSocket with a Selector.
                    // see: http://forum.java.sun.com/thread.jsp?forum=31&thread=384019
                    key.cancel();
                    selector.selectNow();
                    // end of workaround.

                    selector.close();
                } catch (ClosedChannelException e) {
                    // Should not get here.
                } catch (IOException e) {

                    // If this is the Mac OSX bug, ignore it.
                    if ("Mac OS X".equals(System.getProperty("os.name")) &&
                            "Bad file descriptor".equals(e.getMessage())) {
                        // do nothing
                    } else {
                        setException(e);
                    }
                }
            }
        });
        thread.start();

        client = new Socket("localhost",
                            port);

        thread.join();
        assertException(getException());
    }

    /**
     * Test whether the server socker porperly closes.
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public void testSelectorClosing()
            throws IOException,
                   InterruptedException
    {
        testSelectorAccept();
        testSelectorAccept();
    }

    // static member methods (alphabetic)

    // instance member method (alphabetic)

    private static void assertException(Exception e)
    {
        if (null != e) {
            assertNull(e.toString(),
                       e);
        }

    }

    /**
     * Returns the port on which to run this current test.
     *
     * @return the port on which to run this current test.
     */
    private static int getPortForTest()
    {
        return nextPort;
    }


    /**
     * Create test suite for this class.
     *
     * @return the suite of tests declared in this class.
     */
    public static Test suite()
    {
        return new TestSuite(ServerSocketTest.class);
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
