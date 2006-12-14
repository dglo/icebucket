package icecube.icebucket.net;

import java.io.IOException;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class that acts as the engine of a simple, single threaded, server
 * implementation using the NIO classes. This server can be halted, in which
 * case all clients connections are dropped, but service can be resumed at a
 * later time, i.e. the run method does not exit so any thread in which it is
 * runnning does not die. It can also finish, in which case after it halted the
 * run method exits.
 */
public class SimpleServer
        extends ServerSocketInvoker
{
    /**
     * List of open channels.
     */
    private final List openClients = new ArrayList();

    /**
     * True if the main processing loop should not be executed.
     */
    private boolean paused;

    /**
     * The object used to handler successful selects.
     */
    private Runnable selectHandler;

    /**
     * The Thread in which this object is running.
     */
    private Thread thread;

    /**
     * The SimpleServerWorker that this object uses.
     */
    private final SimpleServerWorker worker;

    // constructors

    /**
     * Create an instance of this class.
     *
     * @param worker the SimpleServerWorker object this object will use.
     * @param port the port on which the server will listen.
     */
    private SimpleServer(SimpleServerWorker worker,
                         int port)
    {
        super(port);
        this.worker = worker;
    }

    // instance member method (alphabetic)

    protected void afterLoop()
            throws IOException
    {
        closeAllClients();

        super.afterLoop();
    }

    /**
     * Close the connection to all open clients.
     *
     * @throws IOException
     */
    private void closeAllClients()
            throws IOException
    {
        final Iterator sockets;
        synchronized (openClients) {
            sockets = openClients.iterator();
        }
        while (sockets.hasNext()) {
            final Object socketObject = sockets.next();
            final Socket socket = (Socket) socketObject;
            worker.closeConnection(socket.getChannel());
            sockets.remove();
        }
    }

    /**
     * Returns the number of open connections.
     *
     * @return the number of open connections.
     */
    public int getConnectionCount()
    {
        synchronized (openClients) {
            return openClients.size();
        }
    }

    protected Runnable getSelectHandler()
    {
        if (null == selectHandler) {

            selectHandler = new Runnable()
            {
                public void run()
                {
                    // Set up for the next select.
                    invokeWhenReady(getSelectHandler());

                    // If paused do nothing.
                    if (isPaused()) {
                        return;
                    }

                    // Otherwise process all waiting selections.
                    final Iterator iterator =
                            getSelector().selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        final SelectionKey key =
                                (SelectionKey) iterator.next();
                        try {
                            if (key.isValid() && key.isAcceptable()) {
                                final SocketChannel connection =
                                        worker.acceptKey(key);
                                if (null != connection) {
                                    synchronized (openClients) {
                                        openClients.add(0,
                                                        connection.socket());
                                    }
                                }
                            }

                            if (key.isValid() && key.isReadable()) {
                                if (!worker.readKey(key)) {
                                    synchronized (openClients) {
                                        openClients.remove(((SocketChannel)
                                                key.channel()).socket());
                                    }
                                    return;
                                }
                            }

                            if (key.isValid() && key.isWritable()) {
                                if (!worker.writeKey(key)) {
                                    synchronized (openClients) {
                                        openClients.remove(((SocketChannel)
                                                key.channel()).socket());
                                    }
                                    return;
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            if ("Broken pipe".equals(e.getMessage())) {
                                key.cancel();
                            }
                        }

                        prepareNextSelect(key);
                        iterator.remove();
                    }
                }
            };
        }

        return selectHandler;

    }

    private void prepareNextSelect(SelectionKey key)
    {
        final int validOps = key.channel().validOps();
        final int interestedOps = key.interestOps();

        if (0 != (validOps & SelectionKey.OP_READ)) {
            final boolean reading =
                    0 != (interestedOps & SelectionKey.OP_READ);
            final boolean expectingRead = worker.isExpectingRead(key);
            if (reading && (!expectingRead)) {
                key.interestOps(key.interestOps() - SelectionKey.OP_READ);
            } else if ((!reading) && expectingRead) {
                key.interestOps(key.interestOps() + SelectionKey.OP_READ);
            }
        }

        if (0 != (validOps & SelectionKey.OP_WRITE)) {
            final boolean writing =
                    0 != (interestedOps & SelectionKey.OP_WRITE);
            final boolean expectingWrite = worker.isExpectingWrite(key);
            if (writing && (!expectingWrite)) {
                key.interestOps(key.interestOps() - SelectionKey.OP_WRITE);
            } else if ((!writing) && expectingWrite) {
                key.interestOps(key.interestOps() + SelectionKey.OP_WRITE);
            }
        }
    }

    /**
     * True if the main processing loop is not being executed.
     *
     * @return true if the main processing loop is not being executed.
     */
    public synchronized boolean isPaused()
    {
        return paused;
    }

    /**
     * Blocks until the thread executing this object completes.
     *
     * @throws InterruptedException
     */
    public void join()
            throws InterruptedException
    {
        thread.join();
    }

    /**
     * Signals that this object should pause. That means that it will not
     * process and more traffic until {@link #unpause} is called.
     *
     * @see #unpause()
     */
    public void pause()
    {
        invokeLater(new Runnable()
        {
            public void run()
            {
                setPaused(true);
            }
        });
    }

    /**
     * Set the paused flag. Setting this flag to true signals that the
     * processing loop should not be executed.
     *
     * @param paused the value to which the paused flag should be set.
     */
    private synchronized void setPaused(boolean paused)
    {
        this.paused = paused;
    }

    /**
     * Sets the thread in which this object will execute.
     *
     * @param thread the thread in which this object will execute.
     */
    private void setThread(Thread thread)
    {
        this.thread = thread;
    }

    /**
     * Starts the Thread containing this object.
     */
    public void start()
    {
        thread.start();
    }

    /**
     * Signals that this object should reumse processing. That means that it
     * will start to traffic again.
     *
     * @see #pause()
     */
    public void unpause()
    {
        invokeLater(new Runnable()
        {
            public void run()
            {
                setPaused(false);
            }
        });
    }

    // static member methods (alphabetic)

    /**
     * Creates an instance of the Server, executing in its own {@link Thread}.
     *
     * @param serverWorker the object to be used by the server as it
     * serverWorker.
     * @param serverPort the serverPort on which to establish ther server.
     * @param threadName the name of the thread running the server.
     * @return the SimpleServer created.
     */
    public static SimpleServer createSimpleServer(ServerWorker serverWorker,
                                                  int serverPort,
                                                  String threadName)
    {
        final SimpleServer result =
                new SimpleServer(new ServerWorkerWrapper(serverWorker),
                                 serverPort);
        final Thread thread = new Thread(result,
                                         threadName);
        result.setThread(thread);
        return result;
    }

    /**
     * Creates an instance of the Server, executing in its own {@link Thread}.
     *
     * @param serverWorker the object to be used by the server as it
     * serverWorker.
     * @param serverPort the serverPort on which to establish ther server.
     * @param threadName the name of the thread running the server.
     * @return the SimpleServer created.
     */
    public static SimpleServer createSimpleServer(SimpleServerWorker serverWorker,
                                                  int serverPort,
                                                  String threadName)
    {
        final SimpleServer result = new SimpleServer(serverWorker,
                                                     serverPort);
        final Thread thread = new Thread(result,
                                         threadName);
        result.setThread(thread);
        return result;
    }

    /**
     * This is a simple wrapper to provide backwards compatability with old
     * ServerWorker interface.
     */
    static class ServerWorkerWrapper
            implements SimpleServerWorker
    {
        ServerWorker worker;

        ServerWorkerWrapper(ServerWorker worker)
        {
            this.worker = worker;
        }

        public SocketChannel acceptKey(SelectionKey key)
                throws IOException
        {
            return worker.acceptKey(key);
        }

        public void closeConnection(SocketChannel connection)
                throws IOException
        {
            worker.closeConnection(connection);
        }

        public boolean isExpectingRead(SelectionKey key)
        {
            return true;
        }

        public boolean isExpectingWrite(SelectionKey key)
        {
            return false;
        }

        public boolean readKey(SelectionKey key)
                throws IOException
        {
            return worker.readKey(key);
        }

        public boolean writeKey(SelectionKey key)
                throws IOException
        {
            throw new UnsupportedOperationException("This should never be" +
                                                    " called");
        }
    }
}
