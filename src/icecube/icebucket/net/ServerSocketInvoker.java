/*
 * class: ServerSocketInvoker *
 * Version $Id: ServerSocketInvoker.java,v 1.2 2005/04/18 18:43:15 patton Exp $
 *
 * Date: September 5 2004
 *
 * (c) 2004 IceCube Collaboration
 */

package icecube.icebucket.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

/**
 * This abstract class is the engine for a ServerSocket running in its own
 * thread.
 *
 * @author patton
 * @version $Id: ServerSocketInvoker.java,v 1.1 2005/04/18 18:27:07 patton Exp
 *          $
 */
public abstract class ServerSocketInvoker
        extends SelectorInvoker
{

    // public static final member data

    // protected static final member data

    // static final member data

    // private static final member data

    // private static member data

    // private instance member data

    /**
     * The port on which this object should listen.
     */
    private final int port;

    /**
     * The ServerSocket one which this object will listen for connections.
     */
    private ServerSocket serverSocket;

    // constructors

    /**
     * Create an instance of this class.
     *
     * @param port the port on which to establish the ServerSocket.
     */
    protected ServerSocketInvoker(int port)
    {
        this.port = port;
    }

    // instance member method (alphabetic)

    protected void afterLoop()
            throws IOException
    {
        if (!serverSocket.isClosed()) {
            serverSocket.close();
        }
    }

    protected void beforeLoop()
            throws IOException
    {
        final ServerSocketChannel serverChannel =
                ServerSocketChannel.open();
        serverSocket = serverChannel.socket();
        serverSocket.setReuseAddress(true);
        serverSocket.bind(new InetSocketAddress(getPort()));
        serverChannel.configureBlocking(false);
        serverChannel.register(getSelector(),
                               SelectionKey.OP_ACCEPT);
        invokeWhenReady(getSelectHandler());
    }

    /**
     * Returns the port used by this object.
     *
     * @return the port used by this object.
     */
    public int getPort()
    {
        return port;
    }

    /**
     * Returns the {@link Runnable} object that will be executed every time the
     * is a successful {@link Selector#select()}.
     *
     * @return the RUnnable to execute after a successful select.
     */
    protected abstract Runnable getSelectHandler();

    // static member methods (alphabetic)

    // Description of this object.
    // public String toString() {}

    // public static void main(String args[]) {}
}