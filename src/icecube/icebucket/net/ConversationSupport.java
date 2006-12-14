/*
 * class: ClientServerSupport
 *
 * Version $Id: ConversationSupport.java,v 1.5 2006/02/20 05:25:27 patton Exp $
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
public class ConversationSupport
{

    // public static final member data

    /**
     * The state where this object is doing nothing.
     */
    public static final int DOING_NOTHING = 1;

    /**
     * The state whree this object is in the process of reading a variable
     * length message.
     */
    public static final int READING_MESSAGE = DOING_NOTHING + 1;

    /**
     * The state where this object is reading a known length of data.
     */
    public static final int READING_KNOWN_LENGTH = READING_MESSAGE + 1;

    /**
     * The state where this object is reading a known length of data.
     */
    public static final int WRITING_KNOWN_LENGTH = READING_KNOWN_LENGTH + 1;

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
     * The number of bytes handled during the current I/O phase.
     */
    private long bytesHandled;

    /**
     * The number of bytes to be handled during the current I/O phase.
     */
    private int bytesToHandle;

    /**
     * True if an OutOfMemory error was caught during the last I/O.
     */
    private boolean caughtOutOfMemory;

    /**
     * The byte[] used to read and write commands.
     */
    private final byte[] cmdByteArray;

    /**
     * The ByteBuffer used to read and write commands.
     */
    private final ByteBuffer cmdByteBuffer;

    /**
     * True if this object is printing messages to logging system.
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
     * The Action to execute at the end of the current I/O phase.
     */
    private Action pendingAction;

    /**
     * The buffer currently in use for I/O.
     */
    private ByteBuffer[] pendingBuffers;

    /**
     * The Log message to output upon I/O completion if eavesdropping.
     */
    private String pendingLogMessage;

    /**
     * The ReadableByteChannel this object is using for input.
     */
    private final ReadableByteChannel readableByteChannel;

    /**
     * Array to hold single pending buffer.
     */
    private final ByteBuffer[] singleBuffer = new ByteBuffer[1];

    /**
     * The current state of I/O.
     */
    private int state = DOING_NOTHING;

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
    public ConversationSupport(ByteChannel channel,
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
    public ConversationSupport(ReadableByteChannel readableChannel,
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
     * Begins a 'read' phase in which the provided buffers will be sent out
     * over this objects ReadableByteChannel.
     *
     * @param buffers the buffers to be filled.
     * @param total the total number of bytes to be read.
     * @param logMessage the message to output when complete.
     * @param action the Action to execute when the read is complete.
     * @throws IOException
     */
    private void beginRead(ByteBuffer[] buffers,
                           int total,
                           String logMessage,
                           Action action)
            throws IOException
    {
        if (DOING_NOTHING != state) {
            throw new IllegalStateException("Already doing I/O");
        }

        state = READING_KNOWN_LENGTH;
        bytesHandled = 0L;

        pendingBuffers = buffers;
        bytesToHandle = total;
        pendingLogMessage = logMessage;
        pendingAction = action;

        fillPendingBuffers();
    }

    /**
     * Begins a 'write' phase of I/O in which the provided buffers will be sent
     * out over this objects WritableByteChannel.
     *
     * @param buffers the buffers to be emptied.
     * @param total the total number of bytes to be written.
     * @param logMessage the message to output when complete.
     * @param action the Action to execute when the write is complete.
     * @throws IOException
     */
    private void beginWrite(ByteBuffer[] buffers,
                            int total,
                            String logMessage,
                            Action action)
            throws IOException
    {
        if (DOING_NOTHING != state) {
            throw new IllegalStateException("Already doing I/O");
        }

        state = WRITING_KNOWN_LENGTH;
        bytesHandled = 0L;

        pendingBuffers = buffers;
        bytesToHandle = total;
        pendingLogMessage = logMessage;
        pendingAction = action;

        emptyPendingBuffers();
    }

    /**
     * Releases up any resources this object has been using.
     */
    public void dispose()
    {
    }

    /**
     * Makes one attempt to write out any pending data.
     *
     * @return true if successfully emptied pending buffers.
     * @throws IOException
     */
    public boolean emptyPendingBuffers()
            throws IOException
    {
        if (WRITING_KNOWN_LENGTH != state) {
            if (DOING_NOTHING == state) {
                return true;
            }
            throw new IllegalStateException("Not writing a known length of" +
                                            " data");
        }
        final GatheringByteChannel gatheringChannel =
                (GatheringByteChannel) getWritableByteChannel();

        // This try block helps avoid the following know bug:
        // http://bugs.sun.com/bugdatabase/view_bug.do;:YfiG?bug_id=4879883
        try {
            bytesHandled += gatheringChannel.write(pendingBuffers);
            caughtOutOfMemory = false;
        } catch (OutOfMemoryError e) {
            if (caughtOutOfMemory) {
                log.warn("Consecutive OutOfMemoryError during a write.");
            } else {
                log.warn("OutOfMemoryError during a write, now garbage" +
                         " collecting");
                System.gc();
                caughtOutOfMemory = true;
            }
        }

        if (bytesToHandle != bytesHandled) {
            return false;
        }
        if (isEavesDropping() &&
            (null != log) &&
            (null != pendingLogMessage)) {
            log.debug(pendingLogMessage);
        }
        state = DOING_NOTHING;

        if (null != pendingAction) {

            // Clear task before executing it.
            final Action action = pendingAction;
            pendingAction = null;
            action.execute();
        }

        return true;
    }

    /**
     * Makes one attempt to read in any pending data.
     *
     * @return true if successfully filled pending buffers.
     * @throws IOException
     */
    public boolean fillPendingBuffers()
            throws IOException
    {
        if (READING_KNOWN_LENGTH != state) {
            if (DOING_NOTHING == state) {
                return true;
            }
            throw new IllegalStateException("Not reading a known length of" +
                                            " data");
        }
        final ScatteringByteChannel scatteringChannel =
                (ScatteringByteChannel) getReadableByteChannel();

        // This try block helps avoid the following know bug:
        // http://bugs.sun.com/bugdatabase/view_bug.do;:YfiG?bug_id=4879883
        try {
            bytesHandled += scatteringChannel.read(pendingBuffers);
            caughtOutOfMemory = false;
        } catch (OutOfMemoryError e) {
            if (caughtOutOfMemory) {
                log.warn("Consecutive OutOfMemoryError during a read.");
            } else {
                log.warn("OutOfMemoryError during a read, now garbage" +
                         " collecting.");
                System.gc();
                caughtOutOfMemory = true;
            }
        }

        if (bytesToHandle != bytesHandled) {
            return false;
        }
        if (isEavesDropping() &&
            (null != log) &&
            (null != pendingLogMessage)) {
            log.debug(pendingLogMessage);
        }
        state = DOING_NOTHING;

        if (null != pendingAction) {

            // Clear task before executing it.
            final Action action = pendingAction;
            pendingAction = null;
            action.execute();
        }

        return true;
    }

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
     * Returns the current state of this object.
     *
     * @return the current state of this object.
     */
    public int getState()
    {
        return state;
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
        read(buffer,
             null);
    }

    /**
     * Read in the contents of this object's channel and puts it in the buffer.
     * The specified action is executed when the read is complete.
     *
     * @param buffer the buffer to fill.
     * @param action the Action to execute when the read is complete.
     * @throws IOException
     */
    public void read(ByteBuffer buffer,
                     Action action)
            throws IOException
    {
        singleBuffer[0] = buffer;
        beginRead(singleBuffer,
                  buffer.remaining(),
                  INBOUND_PREFIX +
                  identity +
                  ":*Read " +
                  buffer.remaining() +
                  " bytes of data.*",
                  action);
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
        read(buffers,
             null);
    }

    /**
     * Read in the contents of this object's channel and puts it in the set of
     * buffers. The specified action is executed when the read is complete.
     *
     * @param buffers the set of buffers to fill.
     * @param action the Action to execute when the read is complete.
     * @throws IOException
     */
    public void read(ByteBuffer[] buffers,
                     Action action)
            throws IOException
    {
        int total = 0;
        final int allBuffer = buffers.length;
        for (int buffer = 0;
             allBuffer != buffer;
             buffer++) {
            total += buffers[buffer].remaining();
        }

        beginRead(buffers,
                  total,
                  INBOUND_PREFIX +
                  identity +
                  ":*Read " +
                  total +
                  " bytes of data.*",
                  action);
    }

    /**
     * Attempts to read a message from this objects channel. A message is
     * terminated with a CRLF pair. If null is returned then the complete
     * message has not yet been received and this method should be called until
     * it does return a valid String. </p> This is different from the other I/O
     * routines as the size of the data is not known.
     *
     * @return a String containing the message, without the CRLF pair.
     * @throws IOException
     */
    public String readMessage()
            throws IOException
    {
        if (READING_MESSAGE != state) {
            if (DOING_NOTHING != state) {
                throw new IllegalStateException("Already doing I/O");
            }

            cmdByteBuffer.position(0).limit(2);
            state = READING_MESSAGE;
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
        state = DOING_NOTHING;
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
        write(buffer,
              null);
    }

    /**
     * Writes out the contents of the buffer to this objects channel. The
     * specified action is executed when the write is complete.
     *
     * @param buffer the buffer to write.
     * @param action the Action to execute when the write is complete.
     * @throws IOException
     */
    public void write(ByteBuffer buffer,
                      Action action)
            throws IOException
    {
        singleBuffer[0] = buffer;
        beginWrite(singleBuffer,
                   buffer.remaining(),
                   OUTBOUND_PREFIX +
                   identity +
                   ":*Written " +
                   buffer.remaining() +
                   " bytes of data.*",
                   action);
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
        write(buffers,
              null);
    }

    /**
     * Writes out the contents of a set of buffers to this objects channel. The
     * specified action is executed when the write is complete.
     *
     * @param buffers the set of buffers to write.
     * @param action the Action to execute when the write is complete.
     * @throws IOException
     */
    public void write(ByteBuffer[] buffers,
                      Action action)
            throws IOException
    {
        int total = 0;
        final int allBuffer = buffers.length;
        for (int buffer = 0;
             allBuffer != buffer;
             buffer++) {
            total += buffers[buffer].remaining();
        }

        beginWrite(buffers,
                   total,
                   OUTBOUND_PREFIX +
                   identity +
                   ":*Written " +
                   total +
                   " bytes of data.*",
                   action);
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
        writeMessage(message,
                     null);
    }

    /**
     * Attempts to write a message to this objects channel. A response is
     * terminated with a CRLF pair. The specified action is executed when the
     * write is complete.
     *
     * @param message a String containing the message, without the CRLF pair.
     * @param action the Action to execute when the write is complete.
     * @throws IOException
     */
    public void writeMessage(String message,
                             Action action)
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

        singleBuffer[0] = cmdByteBuffer;
        beginWrite(singleBuffer,
                   cmdByteBuffer.remaining(),
                   OUTBOUND_PREFIX +
                   identity +
                   ':' +
                   message,
                   action);
    }

    // static member methods (alphabetic)

    /**
     * INterface used to define n action to take after an I/O phase has
     * completed.
     */
    public interface Action
    {
        /**
         * Invoked when the I/O phse has completed. When this is called its
         * associated ConversationSupport object will be in the DOING_NOTHING
         * state and so a new I/O phase may be stated by this method.
         *
         * @throws IOException
         */
        void execute()
                throws IOException;
    }

    // Description of this object.
    // public String toString() {}

    // public static void main(String args[]) {}
}