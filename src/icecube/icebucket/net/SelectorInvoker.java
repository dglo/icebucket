package icecube.icebucket.net;

import icecube.icebucket.util.RunnableInvoker;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

/**
 * This abstract class provides an implementation of the @link Invocable}
 * interface that runs in conjuction with a {@link Selector}. Subclasses can
 * reserve resources using the {@link #beforeLoop} method and release them
 * using the {@link #afterLoop} method. They should also use the beforeLoop
 * method to queue up the {@link Runnable} object to be executed when the
 * Selector has selected.
 */
public abstract class SelectorInvoker
        extends RunnableInvoker
{
    // private instance member data

    /**
     * The selector used to manage this objects channels.
     */
    private Selector selector;

    /**
     * True is there has been an attempt to wake the Selector.
     */
    private boolean woken;

    // constructors

    /**
     * Create an instance of this class.
     */
    protected SelectorInvoker()
    {
    }

    // instance member method (alphabetic)

    /**
     * This method is called after the Selector loop has exited and should
     * release up any resources allocated during {@link #beforeLoop}.
     *
     * @throws IOException
     * @see #beforeLoop()
     */
    protected abstract void afterLoop()
            throws IOException;

    /**
     * This method is called before the Selector loop has begin and should
     * allocate up any resources needed during the loop. It  also usually sets
     * up the {@link Runnable} to be executed after the first successful {@link
     * Selector#select()}.
     *
     * @throws IOException
     * @see #afterLoop()
     */
    protected abstract void beforeLoop()
            throws IOException;

    /**
     * Returns the Selector used by this object.
     *
     * @return the Selector used by this object.
     */
    protected final Selector getSelector()
    {
        if (null == selector) {
            try {
                selector = Selector.open();
            } catch (IOException e) {
                // End up returning null if can not be opened!
                e.printStackTrace();
            }
        }
        return selector;
    }

    protected final void ready()
    {
        getSelector().wakeup();
        woken = true;
    }

    public void run()
    {
        try {
            beforeLoop();

            super.run();

            afterLoop();

            // The following cleans up a known problem when using a
            // SeverSocket with a Selector.
            // see: http://forum.java.sun.com/thread.jsp?forum=31&thread=384019
            final Iterator iterator = getSelector().keys().iterator();
            while (iterator.hasNext()) {
                ((SelectionKey) iterator.next()).cancel();
            }
            getSelector().selectNow();
            // end of workaround.

            selector = null;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected final void waitUntilReady()
    {
        if (!woken) {
            try {
                getSelector().select();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        woken = false;
    }
}
