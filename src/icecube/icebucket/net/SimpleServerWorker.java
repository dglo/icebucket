package icecube.icebucket.net;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * This interface defines the full set of the methods that a worker used by a
 * SimpleServer must implement.
 *
 * Note: This interface does not have to handle a connectionKey as that is a
 * client side operation, not a server.
 */
public interface SimpleServerWorker
{

    /**
     * Either accepts the SocketChannel derived from the specified
     * SelectionKey, connecting and configuring it, or rejects it.
     *
     * @param key the key of the SocketChannel to accept.
     * @return the SocketChannel if the connection is accepted.
     * @throws IOException
     */
    SocketChannel acceptKey(SelectionKey key)
            throws IOException;

    /**
     * Closes the specified connection.
     *
     * @param connection the SocketChannel to close.
     * @throws IOException
     */
    void closeConnection(SocketChannel connection)
            throws IOException;

    /**
     * True if the specified key is expecting to read data.
     *
     * @return true if the specified key is expecting to read data.
     */
    boolean isExpectingRead(SelectionKey key);

    /**
     * True if the specified key has more data write.
     *
     * @return true if the specified key has more data write.
     */
    boolean isExpectingWrite(SelectionKey key);

    /**
     * Reads and process the read data from the specified connection.
     *
     * @param key the key representing the connection from which to read.
     * @return true is the key can continue to be used.
     * @throws IOException is there is an IO problem.
     */
    boolean readKey(SelectionKey key)
            throws IOException;

    /**
     * Writes pending data into the specified connection.
     *
     * @param key the key representing the connection from which to write.
     * @return true is the key can continue to be read.
     * @throws IOException is there is an IO problem.
     */
    boolean writeKey(SelectionKey key)
            throws IOException;
}
