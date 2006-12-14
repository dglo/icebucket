/*
 * class: ClientServerSupportTest
 *
 * Version $Id: ClientServerSupportTest.java,v 1.2 2005/04/18 18:43:15 patton Exp $
 *
 * Date: August 16 2004
 *
 * (c) 2004 IceCube Collaboration
 */

package icecube.icebucket.net.test;

import icecube.icebucket.net.ClientServerSupport;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.Pipe;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * This class defines the tests that any ClientServerSupport object should
 * pass.
 *
 * @author patton
 * @version $Id: ClientServerSupportTest.java,v 1.1 2004/08/18 22:50:26 patton
 *          Exp $
 */
public class ClientServerSupportTest
        extends TestCase
{

    // public static final member data

    // protected static final member data

    // static final member data

    // private static final member data

    /**
     * The maximum length of a command.
     */
    private static final int MAXIMUM_MESSAGE_LENGTH = 80;

    /**
     * The test massage to exchange.
     */
    private static final String TEST_MESSAGE = "Cheese Gromit, everyone" +
            " knows the moon is made of cheese.";

    // private static member data

    // private instance member data

    /**
     * The client object being tested.
     */
    private ClientServerSupport clientSupport;

    /**
     * The pipe bringing messages into the server.
     */
    private Pipe inboundPipe;

    /**
     * The pipe taking messages away from the server.
     */
    private Pipe outboundPipe;

    /**
     * The server object being tested.
     */
    private ClientServerSupport serverSupport;

    // constructors

    /**
     * Constructs and instance of this test.
     *
     * @param name the name of the test.
     */
    public ClientServerSupportTest(String name)
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
        inboundPipe = Pipe.open();
        final WritableByteChannel clientSink = inboundPipe.sink();
        final ReadableByteChannel serverSource = inboundPipe.source();
        outboundPipe = Pipe.open();
        final WritableByteChannel serverSink = outboundPipe.sink();
        final ReadableByteChannel clientSource = outboundPipe.source();
        clientSupport = new ClientServerSupport(new ByteChannel()
        {
            public int read(ByteBuffer buffer)
                    throws IOException
            {
                return clientSource.read(buffer);
            }

            public boolean isOpen()
            {
                return clientSink.isOpen() && clientSource.isOpen();
            }

            public void close()
                    throws IOException
            {
                clientSink.close();
                clientSource.close();
            }

            public int write(ByteBuffer buffer)
                    throws IOException
            {
                return clientSink.write(buffer);
            }
        },
                                                MAXIMUM_MESSAGE_LENGTH);
        serverSupport = new ClientServerSupport(new ByteChannel()
        {
            public int read(ByteBuffer buffer)
                    throws IOException
            {
                return serverSource.read(buffer);
            }

            public boolean isOpen()
            {
                return serverSink.isOpen() && serverSource.isOpen();
            }

            public void close()
                    throws IOException
            {
                serverSink.close();
                serverSource.close();
            }

            public int write(ByteBuffer buffer)
                    throws IOException
            {
                return serverSink.write(buffer);
            }
        },
                                                MAXIMUM_MESSAGE_LENGTH);
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
        serverSupport = null;
        clientSupport = null;
        super.tearDown();
    }

    /**
     * Test that a message can pass from client to server.
     *
     * @throws IOException is there is an IO problem.
     */
    public void testMessage()
            throws IOException
    {
        clientSupport.writeMessage(TEST_MESSAGE);
        assertEquals(TEST_MESSAGE,
                     serverSupport.readMessage());
    }

    // static member methods (alphabetic)

    /**
     * Create test suite for this class.
     *
     * @return the suite of tests declared in this class.
     */
    public static Test suite()
    {
        return new TestSuite(ClientServerSupportTest.class);
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