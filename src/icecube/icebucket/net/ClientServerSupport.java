/*
 * class: ClientServerSupport
 *
 * Version $Id: ClientServerSupport.java,v 1.13 2005/04/18 18:43:15 patton Exp $
 *
 * Date: August 16 2004
 *
 * (c) 2004 IceCube Collaboration
 */

package icecube.icebucket.net;

import org.apache.commons.logging.Log;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.Channel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * This class provide support for simple Client Server implementations.
 *
 * @author patton
 * @version $Id: ClientServerSupport.java,v 1.1 2004/08/18 22:50:26 patton Exp
 *          $
 */
public final class ClientServerSupport
{

    // public static final member data

    // protected static final member data

    // static final member data

    // private static final member data

    /**
     * The byte value of CR.
     */
    private static final byte CR = (byte) 13;

    /**
     * The return value for {@link ByteChannel#read(ByteBuffer)} when it
     * encounters an EOF.
     */
    private static final int EOF = -1;

    /**
     * The byte value of LF.
     */
    private static final byte LF = (byte) 10;

    /**
     * The difference between the lastByte and the new limit if a CR is the
     * last byte.
     */
    private static final int CR_DIFF = 2;

    /**
     * The difference between the lastByte and the new limit if a CR is not the
     * last byte.
     */
    private static final int NO_CR_DIFF = 3;

    /**
     * The identity to use if none is specified.
     */
    private static final String DEFAULT_IDENTITY = "";

    /**
     * The prefix to signal inbound traffic.
     */
    private static final String INBOUND_PREFIX = "<I>";

    /**
     * The prefix to signal outbound traffic.
     */
    private static final String OUTBOUND_PREFIX = "<O>";

    // private static member data

    // private instance member data

    /**
     * The byte[] used to read and write commands.
     */
    private final byte[] cmdByteArray;

    /**
     * The ByteBuffer used to read and write commands.
     */
    private final ByteBuffer cmdByteBuffer;

    /**
     * True if this object is printing messages to System.out.
     */
    private boolean eavesDropping;

    /**
     * The identity of the connection this object is supporting.
     */
    private String identity = DEFAULT_IDENTITY;

    /**
     * The log stream, if any, this object uses.
     */
    private Log log;

    /**
     * The ReadableByteChannel this object is using for input.
     */
    private final ReadableByteChannel readableByteChannel;

    /**
     * True while in the process of reading a message.
     */
    private boolean readingMessage;

    /**
     * The WritableByteChannel this object is using for output.
     */
    private final WritableByteChannel writableByteChannel;

    // constructors

    /**
     * Create an instance of this class.
     *
     * @param channel the ByteChannel to be used for input and output.
     * @param maximumCmdLength the maximum length allowed for a command line.
     */
    public ClientServerSupport(ByteChannel channel,
                               int maximumCmdLength)
    {
        this(channel,
             channel,
             maximumCmdLength);
    }

    /**
     * Create an instance of this class.
     *
     * @param readableChannel the ReadableByteChannel to be used for input.
     * @param writableChannel the WritableByteChannel to be used for output.
     * @param maximumCmdLength the maximum length allowed for a command line.
     */
    public ClientServerSupport(ReadableByteChannel readableChannel,
                               WritableByteChannel writableChannel,
                               int maximumCmdLength)
    {
        readableByteChannel = readableChannel;
        writableByteChannel = writableChannel;
        cmdByteArray = new byte[maximumCmdLength];
        cmdByteBuffer = ByteBuffer.wrap(cmdByteArray);
    }

    // instance member method (alphabetic)

    /**
     * Returns the Channel this object is using for input and output. If the
     * channels for input and output are not the same then is one of them is
     * null the other one is returned, otherwise null is returned.
     *
     * @return the Channel this object is using for input and output.
     */
    public Channel getChannel()
    {
        if (readableByteChannel == writableByteChannel) {
            return readableByteChannel;
        } else if (null == readableByteChannel) {
            return writableByteChannel;
        } else if (null == writableByteChannel) {
            return readableByteChannel;
        } else {
            return null;
        }
    }

    /**
     * Returns the identity of this session.
     *
     * @return the identity of this session.
     */
    public String getIdentity()
    {
        return identity;
    }

    /**
     * Returns the ReadableByteChannel this object is using for input.
     *
     * @return the ReadableByteChannel this object is using for input.
     */
    public ReadableByteChannel getReadableByteChannel()
    {
        return readableByteChannel;
    }

    /**
     * Returns the WritableByteChannel this object is using for output.
     *
     * @return the WritableByteChannel this object is using for output.
     */
    public WritableByteChannel getWritableByteChannel()
    {
        return writableByteChannel;
    }

    /**
     * Returns true if this object should echo to System.out.
     *
     * @return true if this object should echo to System.out.
     */
    public boolean isEavesDropping()
    {
        return eavesDropping;
    }

    /**
     * Read in the contents of this object's channel and puts it in the
     * buffer.
     *
     * @param buffer the buffer to fill.
     * @throws IOException
     */
    public void read(ByteBuffer buffer)
            throws IOException
    {
        // This is a simple 'keep reading until done' alogrithm. It should
        // probably be improved for non-blocking channels!
        int read = 0;
        final int finished = buffer.remaining();

        boolean caught = false;
        while (finished != read) {

            // This try block helps avoid the following know bug:
            // http://bugs.sun.com/bugdatabase/view_bug.do;:YfiG?bug_id=4879883
            try {
                read += getReadableByteChannel().read(buffer);
                caught = false;
            } catch (OutOfMemoryError e) {
                if (caught) {
                    log.warn("Consecutive OutOfMemoryError during a read.");
                } else {
                    log.warn("OutOfMemoryError during a read, now garbage" +
                             " collecting.");
                    System.gc();
                    caught = true;
                }
            }
        }

        // Log the summary if requested.
        if (isEavesDropping() && (null != log)) {
            log.debug(INBOUND_PREFIX +
                      identity +
                      ":*Read " +
                      read +
                      " bytes of data.*");
        }
    }

    /**
     * Read in the contents of this object's channel and puts it in the set of
     * buffers.
     *
     * @param buffers the set of buffers to fill.
     * @throws IOException
     */
    public void read(ByteBuffer[] buffers)
            throws IOException
    {
        final ScatteringByteChannel scatteringChannel =
                (ScatteringByteChannel) getReadableByteChannel();

        // This is a simple 'keep reading until done' alogrithm. It should
        // probably be improved for non-blocking channels!
        long read = 0L;
        int finished = 0;
        final int allBuffer = buffers.length;
        for (int buffer = 0;
             allBuffer != buffer;
             buffer++) {
            finished += buffers[buffer].remaining();
        }

        boolean caught = false;
        while (finished != read) {

            // This try block helps avoid the following know bug:
            // http://bugs.sun.com/bugdatabase/view_bug.do;:YfiG?bug_id=4879883
            try {
                read += scatteringChannel.read(buffers);
                caught = false;
            } catch (OutOfMemoryError e) {
                if (caught) {
                    log.warn("Consecutive OutOfMemoryError during a read.");
                } else {
                    log.warn("OutOfMemoryError during a read, now garbage" +
                             " collecting.");
                    System.gc();
                    caught = true;
                }
            }
        }

        // Log the summary if requested.
        if (isEavesDropping() && (null != log)) {
            log.debug(OUTBOUND_PREFIX +
                      identity +
                      ":*Read " +
                      read +
                      " bytes of data.*");
        }
    }

    /**
     * Attempts to read a message from this objects channel. A message is
     * terminated with a CRLF pair. If null is returned then the complete
     * message has not yet been received and this method should be called until
     * it does return a valid String.
     *
     * @return a String containing the message, without the CRLF pair.
     * @throws IOException
     */
    public String readMessage()
            throws IOException
    {
        if (!readingMessage) {
            cmdByteBuffer.position(0).limit(2);
            readingMessage = true;
        }
        int lastByte = 0;
        int read = getReadableByteChannel().read(cmdByteBuffer);
        boolean done = false;
        while (0 != read &&
                !done) {
            if (EOF == read) {
                throw new EOFException("Inbound channel closed by peer.");
            }
            lastByte = cmdByteBuffer.position() - 1;
            done = (0 < lastByte &&
                    (CR == cmdByteBuffer.get(lastByte - 1) &&
                    LF == cmdByteBuffer.get(lastByte)));
            if (!done) {
                if (CR == cmdByteBuffer.get(lastByte)) {
                    cmdByteBuffer.limit(lastByte + CR_DIFF);
                } else {
                    cmdByteBuffer.limit(lastByte + NO_CR_DIFF);
                }
                read = getReadableByteChannel().read(cmdByteBuffer);
            }
        }

        if (!done) {
            return null;
        }

        final String message = new String(cmdByteArray,
                                          0,
                                          lastByte - 1);

        // Log the messaging if requested.
        if (isEavesDropping() && (null != log)) {
            log.debug(INBOUND_PREFIX +
                      identity +
                      ':' +
                      message);
        }
        readingMessage = false;
        return message;
    }

    /**
     * Sets the eavesdropping flag.
     *
     * @param eavesDropping true if this object should echo to System.out.
     */
    public void setEavesDropping(boolean eavesDropping)
    {
        this.eavesDropping = eavesDropping;
    }

    /**
     * Sets the identity of the connection this object is supporting.
     *
     * @param identity the identity of the connection.
     */
    public void setIdentity(String identity)
    {
        if (null == identity) {
            this.identity = DEFAULT_IDENTITY;
            return;
        }
        this.identity = identity;
    }

    /**
     * Set the log object this object should use.
     *
     * @param log the log object this object should use.
     */
    public void setLog(Log log)
    {
        this.log = log;
    }

    /**
     * Writes out the contents of the buffer to this objects channel.
     *
     * @param buffer the buffer to write.
     * @throws IOException
     */
    public void write(ByteBuffer buffer)
            throws IOException
    {
        final int written = writeWithoutSummary(buffer);

        // Log the summary if requested.
        if (isEavesDropping() && (null != log)) {
            log.debug(OUTBOUND_PREFIX +
                      identity +
                      ":*Written " +
                      written +
                      " bytes of data.*");
        }
    }

    /**
     * Writes out the contents of a set of buffers to this objects channel.
     *
     * @param buffers the set of buffers to write.
     * @throws IOException
     */
    public void write(ByteBuffer[] buffers)
            throws IOException
    {
        final GatheringByteChannel gatheringChannel =
                (GatheringByteChannel) getWritableByteChannel();

        // This is a simple 'keep writing until done' alogrithm. It should
        // probably be improved for non-blocking channels!
        long written = 0L;
        int finished = 0;
        final int allBuffer = buffers.length;
        for (int buffer = 0;
             allBuffer != buffer;
             buffer++) {
            finished += buffers[buffer].remaining();
        }

        boolean caught = false;
        while (finished != written) {

            // This try block helps avoid the following know bug:
            // http://bugs.sun.com/bugdatabase/view_bug.do;:YfiG?bug_id=4879883
            try {
                written += gatheringChannel.write(buffers);
                caught = false;
            } catch (OutOfMemoryError e) {
                if (caught) {
                    log.warn("Consecutive OutOfMemoryError during a write.");
                } else {
                    log.warn("OutOfMemoryError during a write, now garbage" +
                             " collecting");
                    System.gc();
                    caught = true;
                }
            }
        }

        // Log the summary if requested.
        if (isEavesDropping() && (null != log)) {
            log.debug(OUTBOUND_PREFIX +
                      identity +
                      ":*Written " +
                      written +
                      " bytes of data.*");
        }
    }

    /**
     * Attempts to write a message to this objects channel. A response is
     * terminated with a CRLF pair.
     *
     * @param message a String containing the message, without the CRLF pair.
     * @throws IOException
     */
    public void writeMessage(String message)
            throws IOException
    {
        final int requestLength = message.length();
        System.arraycopy(message.getBytes(),
                         0,
                         cmdByteArray,
                         0,
                         requestLength);
        cmdByteArray[requestLength] = CR;
        cmdByteArray[requestLength + 1] = LF;
        cmdByteBuffer.position(0).limit(requestLength + 2);

        writeWithoutSummary(cmdByteBuffer);

        // Log the messaging if requested.
        if (isEavesDropping() && (null != log)) {
            log.debug(OUTBOUND_PREFIX +
                      identity +
                      ':' +
                      message);
        }
    }

    /**
     * Writes out the contents of the buffer to this objects channel.
     *
     * @param buffer the buffer to write.
     * @return the number of bytes written
     * @throws IOException
     */
    private int writeWithoutSummary(ByteBuffer buffer)
            throws IOException
    {
        // This is a simple 'keep writing until done' alogrithm. It should
        // probably be improved for non-blocking channels!
        int written = 0;
        final int finished = buffer.remaining();
        boolean caught = false;
        while (finished != written) {

            // This try block helps avoid the following know bug:
            // http://bugs.sun.com/bugdatabase/view_bug.do;:YfiG?bug_id=4879883
            try {
                written += getWritableByteChannel().write(buffer);
                caught = false;
            } catch (OutOfMemoryError e) {
                if (caught) {
                    log.warn("Consecutive OutOfMemoryError during a write.");
                } else {
                    log.warn("OutOfMemoryError during a write, now garbage" +
                             " collecting");
                    System.gc();
                    caught = true;
                }
            }
        }

        return written;
    }

    // static member methods (alphabetic)

    // Description of this object.
    // public String toString() {}

    // public static void main(String args[]) {}
}