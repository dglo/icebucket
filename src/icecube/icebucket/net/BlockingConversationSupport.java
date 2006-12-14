/*
 * class: BlockingConversationSupport
 *
 * Version $Id: BlockingConversationSupport.java,v 1.3 2006/02/20 18:41:22 patton Exp $
 *
 * Date: February 14 2006
 *
 * (c) 2006 IceCube Collaboration
 */

package icecube.icebucket.net;

import java.io.IOException;
import java.nio.channels.ByteChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.WritableByteChannel;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This class modifies the behaviour of the ConversationSupport class so that
 * its I/O methods only return when they complete. </p> <strong>Note: Although
 * the methods of this class are effectively blocking methods, this class does
 * swicth its channels, if they are SelectableChannels, into non-blocking
 * mode!</strong>
 *
 * @author patton
 * @version $Id: BlockingConversationSupport.java,v 1.1 2006/02/14 20:51:15
 *          patton Exp $
 */
public class BlockingConversationSupport
        extends ConversationSupport
{

    // public static final member data

    // protected static final member data

    // static final member data

    // private static final member data

    /**
     * The default time delay between checking from completed I/O operations.
     */
    private static final long DEFAULT_RETRY_DELAY = 10L;

    // private static member data

    // private instance member data

    /**
     * The locking object used to block with non-selectable channels.
     */
    private final Object blockObject = new Object();

    /**
     * The SelectionKey used by the readableChannel.
     */
    private SelectionKey readKey;

    /**
     * The time delay between checking from completed I/O operations.
     */
    private long retryDelay;

    /**
     * The selection used to wait for I/O.
     */
    private Selector selector;

    /**
     * The timer use with non-selectable channels.
     */
    private Timer timer;

    /**
     * The SelectionKey used by the writableChannel.
     */
    private SelectionKey writeKey;

    // constructors

    /**
     * Create an instance of this class.
     *
     * @param channel the ByteChannel to be used for input and output.
     * @param maximumCmdLength the maximum length allowed for a command line.
     */
    public BlockingConversationSupport(ByteChannel channel,
                                       int maximumCmdLength)
            throws IOException
    {
        this(channel,
             maximumCmdLength,
             DEFAULT_RETRY_DELAY);
    }

    /**
     * Create an instance of this class.
     *
     * @param channel the ByteChannel to be used for input and output.
     * @param maximumCmdLength the maximum length allowed for a command line.
     */
    public BlockingConversationSupport(ByteChannel channel,
                                       int maximumCmdLength,
                                       long retryDelay)
            throws IOException
    {
        this(channel,
             channel,
             maximumCmdLength,
             retryDelay);
    }

    /**
     * Create an instance of this class.
     *
     * @param readableChannel the ReadableByteChannel to be used for input.
     * @param writableChannel the WritableByteChannel to be used for output.
     * @param maximumCmdLength the maximum length allowed for a command line.
     */
    public BlockingConversationSupport(ReadableByteChannel readableChannel,
                                       WritableByteChannel writableChannel,
                                       int maximumCmdLength)
            throws IOException
    {
        this(readableChannel,
             writableChannel,
             maximumCmdLength,
             DEFAULT_RETRY_DELAY);
    }

    /**
     * Create an instance of this class.
     *
     * @param readableChannel the ReadableByteChannel to be used for input.
     * @param writableChannel the WritableByteChannel to be used for output.
     * @param maximumCmdLength the maximum length allowed for a command line.
     */
    public BlockingConversationSupport(ReadableByteChannel readableChannel,
                                       WritableByteChannel writableChannel,
                                       int maximumCmdLength,
                                       long retryDelay)
            throws IOException
    {
        super(readableChannel,
              writableChannel,
              maximumCmdLength);
        if ((readableChannel instanceof SelectableChannel) ||
            (writableChannel instanceof SelectableChannel)) {
            selector = Selector.open();
            if (readableChannel instanceof SelectableChannel) {
                final SelectableChannel readChannel =
                        (SelectableChannel) readableChannel;
                readChannel.configureBlocking(false);
                readKey = readChannel.register(selector,
                                               0);
            }
            if (writableChannel instanceof SelectableChannel) {
                if (writableChannel == readableChannel) {
                    writeKey = readKey;
                } else {
                    final SelectableChannel writeChannel =
                            (SelectableChannel) writableChannel;
                    writeChannel.configureBlocking(false);
                    writeKey = writeChannel.register(selector,
                                                     0);
                }
            }
        }
        if ((null == readKey) ||
            (null == writeKey)) {
            timer = new Timer();
        }
        this.retryDelay = retryDelay;
    }

    // instance member method (alphabetic)

    public void dispose()
    {
        if (null != timer) {
            timer.cancel();
        }

        if (null != selector) {
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.dispose();
    }

    public boolean emptyPendingBuffers()
            throws IOException
    {
        if (null == writeKey) {
            while (!super.emptyPendingBuffers()) {
                if (null == selector) {
                    synchronized (blockObject) {
                        timer.schedule(new TimerTask()
                        {
                            public void run()
                            {
                                synchronized (blockObject) {
                                    blockObject.notifyAll();
                                }
                            }
                        },
                                       retryDelay);
                        try {
                            blockObject.wait();
                        } catch (InterruptedException e) {
                            // do nothing.
                        }
                    }
                } else {

                }
            }
        } else {
            writeKey.interestOps(SelectionKey.OP_WRITE);
            do {
                selector.select();
                final Iterator iterator = selector.selectedKeys().iterator();
                if (iterator.hasNext()) {
                    iterator.next();
                    iterator.remove();
                }
            } while (!super.emptyPendingBuffers());
            writeKey.interestOps(0);
        }
        return true;
    }

    public boolean fillPendingBuffers()
            throws IOException
    {
        if (null == readKey) {
            while (!super.fillPendingBuffers()) {
                synchronized (blockObject) {
                    timer.schedule(new TimerTask()
                    {
                        public void run()
                        {
                            synchronized (blockObject) {
                                blockObject.notifyAll();
                            }
                        }
                    },
                                   retryDelay);
                    try {
                        blockObject.wait();
                    } catch (InterruptedException e) {
                        // do nothing.
                    }
                }
            }
        } else {
            readKey.interestOps(SelectionKey.OP_READ);
            do {
                selector.select();
                final Iterator iterator = selector.selectedKeys().iterator();
                if (iterator.hasNext()) {
                    iterator.next();
                    iterator.remove();
                }
            } while (!super.fillPendingBuffers());
            readKey.interestOps(0);
        }
        return true;
    }

    protected void finalize()
            throws Throwable
    {
        timer.cancel();
        super.finalize();
    }

    public String readMessage()
            throws IOException
    {
        String message = null;
        if (null == writeKey) {
            message = super.readMessage();
            while (null == message) {
                synchronized (blockObject) {
                    timer.schedule(new TimerTask()
                    {
                        public void run()
                        {
                            synchronized (blockObject) {
                                blockObject.notifyAll();
                            }
                        }
                    },
                                   retryDelay);
                    try {
                        blockObject.wait();
                    } catch (InterruptedException e) {
                        // do nothing.
                    }
                }
                message = super.readMessage();
            }
        } else {
            readKey.interestOps(SelectionKey.OP_READ);
            do {
                selector.select();
                final Iterator iterator = selector.selectedKeys().iterator();
                if (iterator.hasNext()) {
                    iterator.next();
                    iterator.remove();
                    message = super.readMessage();
                }
            } while (null == message);
            readKey.interestOps(0);
        }
        return message;

    }

    // static member methods (alphabetic)

    // Description of this object.
    // public String toString() {}

    // public static void main(String args[]) {}
}