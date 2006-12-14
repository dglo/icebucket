package icecube.icebucket.net;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * This interface defines a base set of the methods that a worker used by a
 * SimpleServer must implement.
 */
public interface ServerWorker
{

    /**
     * This method either accepts the SocketChannel derived from the specified
     * SelectionKey, connecting and configuring it, or rejects it.
     *
     * @param key the key of the SocketChannel to accept.
     * @return the SocketChannel if the connection is accepted.
     * @throws IOException
     */
    SocketChannel acceptKey(SelectionKey key)
            throws IOException;

    /**
     * This method closes the specified connection.
     *
     * @param connection the SocketChannel to close.
     * @throws IOException
     */
    void closeConnection(SocketChannel connection)
            throws IOException;

    /**
     * This method reads and process the read data for the specified
     * connection.
     *
     * @param key the key representing the connection to read and process.
     * @return true is the key can continue to be read.
     * @throws IOException is there is an IO problem.
     */
    boolean readKey(SelectionKey key)
            throws IOException;
}
