/*
 * class: RunnableInvokablele
 *
 * Version $Id: ThreadInvoker.java,v 1.1 2005/04/18 18:22:40 patton Exp $
 *
 * Date: August 23 2004
 *
 * (c) 2004 IceCube Collaboration
 */

package icecube.icebucket.util;


/**
 * This class is a simple thread worker class which implements the {@link
 * Invocable} interface. When running in its own thread this class executes any
 * Runnables queued up via the Invocable interface.
 *
 * @author patton
 * @version $Id: ThreadInvoker.java,v 1.1 2005/04/18 18:22:40 patton Exp $
 */
public class ThreadInvoker
        extends RunnableInvoker
{
    // private instance member data

    /**
     * True is an invocation has been added an should be executed.
     */
    private boolean pendingInvocation;

    // constructors

    /**
     * Create an instance of this class.
     */
    public ThreadInvoker()
    {
    }

    // instance member method (alphabetic)

    protected final synchronized void ready()
    {
        pendingInvocation = true;
        notifyAll();
    }

    protected final synchronized void waitUntilReady()
    {
        while (!pendingInvocation) {
            try {
                wait();
            } catch (InterruptedException e) {
                // do nothing special if interrupted
            }
        }
        pendingInvocation = false;
    }
}