/*
 * class: RunnableInvokablele
 *
 * Version $Id: Invoker.java,v 1.4 2005/04/20 21:12:24 patton Exp $
 *
 * Date: August 23 2004
 *
 * (c) 2004 IceCube Collaboration
 */

package icecube.icebucket.util;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * This abstract class provides an implementation of the {@link Invocable}
 * interface. It works by invoking the {@link #executeWhenReady} method which
 * waits, using the {@link #waitUntilReady} method to block execution. Once
 * that method returns, it then executes all Runnable objects that have been
 * specified by either the {@link #invokeWhenReady} method or either of the
 * Invocable methods.
 *
 * The implementations of both Invocable methods call the abstract @link
 * #ready} method to single to derived classes that one of these methods has
 * been called so that the implementation of the {@link #waitUntilReady} method
 * should no longer block. In contrast the invokeWhenReady method of this class
 * does not call the ready method so this can be used to queue Runnable objects
 * that should not cause the waitUntilReady method to return.
 *
 * @author patton
 * @version $Id: Invoker.java,v 1.4 2005/04/20 21:12:24 patton Exp $
 */
public abstract class Invoker
        implements Invocable
{
    // private instance member data

    /**
     * The List of Runnable instances to be invoked when appropriate.
     */
    private final List invocationStack =
            Collections.synchronizedList(new LinkedList());

    /**
     * The List of Runnable instances to be invoked after waiting for the next
     * ready.
     */
    private final List nextReadyStack =
            Collections.synchronizedList(new LinkedList());

    // constructors

    /**
     * Create an instance of this class.
     */
    protected Invoker()
    {
    }

    // instance member method (alphabetic)

    /**
     * This method blocks until it is signaled that there is at least one
     * {@link Runnable} ready to be executed and then executes all pending
     * Runnables.
     */
    protected final void executeWhenReady()
    {
        // Copy all Runnables queued for the next ready onto the stack.
        synchronized (invocationStack) {
            invocationStack.addAll(nextReadyStack);
            nextReadyStack.clear();
        }
        waitUntilReady();
        do {
        } while (executeNext());
    }

    /**
     * Executes the next {@link Runnable} if there is one. This method will
     * <em>note</em> execute Runnables place in this object by the {@link
     * #invokeWhenReady(Runnable)} method until this object has waited once
     * after that method was called.
     *
     * @return true if there is a next Runnable, and it is executed.
     */
    private boolean executeNext()
    {
        final Runnable doRun;
        synchronized (invocationStack) {

            if (0 == invocationStack.size()) {
                doRun = null;
            } else {

                doRun = (Runnable) invocationStack.remove(0);
            }
        }

        if (null == doRun) {
            return false;
        }

        try {
            doRun.run();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        return true;
    }

    public final void invokeAndWait(final Runnable doRun)
    {
        final Semaphore semaphore = new Semaphore();
        final Runnable runnable = new Runnable()
        {
            public void run()
            {
                try {
                    doRun.run();
                } finally {
                    synchronized (semaphore) {
                        semaphore.done = true;
                        semaphore.notifyAll();
                    }
                }
            }
        };
        synchronized (semaphore) {
            invokeLater(runnable);
            while (!semaphore.done) {
                try {
                    semaphore.wait();
                } catch (InterruptedException e) {
                    // loop until done - so do nothing when interrupted.
                }
            }
        }
    }

    /**
     * Queues the specified {@link Runnable} object to be invoked by this
     * object when it is next convenient.
     *
     * @param doRun the Runnable instance to invoke later.
     */
    public final void invokeLater(Runnable doRun)
    {
        synchronized (invocationStack) {
            invocationStack.add(doRun);
        }
        ready();
    }

    /**
     * Queues the specified {@link Runnable} object to be invoked by this
     * object when it <em>next</em> executes Runnables. If this is called by a
     * Runnable that is executing in this object, the specified Runnable will
     * not be queued to execute until after this object is waiting for the next
     * ready signal.
     *
     * @param doRun the {@link Runnable} object to queue.
     */
    protected final void invokeWhenReady(Runnable doRun)
    {
        // Note: this synchroizes on the invocationStrack to enable the
        // transfer in {@link executeWhenReady} to be efficient.
        synchronized (invocationStack) {
            nextReadyStack.add(doRun);
        }
    }

    /**
     * Signals to this class that there is a {@link Runnable} object that is
     * ready to be executed.
     *
     * @see #waitUntilReady
     */
    protected abstract void ready();

    /**
     * This method block until it is signalled that there is a  {@link
     * Runnable} object that is ready to be executed.
     *
     * @see #ready
     */
    protected abstract void waitUntilReady();

    // static member methods (alphabetic)

    private static class Semaphore
    {
        private boolean done;

        private Semaphore()
        {
        }
    }
}