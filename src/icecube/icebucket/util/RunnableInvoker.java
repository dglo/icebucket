/*
 * class: RunnableInvokablele
 *
 * Version $Id: RunnableInvoker.java,v 1.7 2005/06/09 09:30:49 patton Exp $
 *
 * Date: August 23 2004
 *
 * (c) 2004 IceCube Collaboration
 */

package icecube.icebucket.util;


/**
 * This abstract class extends the {@link Invoker} class so that it can be run
 * in it own thread. An instance of a subclass of this class should be be used
 * during the construction of that {@link Thread} and then that Thread should
 * be started. This class requires is subclasses to implement the {@link
 * #ready}/{@link #waitUntilReady} mechanism needed by the Invoker superclass.
 *
 * @author patton
 * @version $Id: RunnableInvoker.java,v 1.7 2005/06/09 09:30:49 patton Exp $
 */
public abstract class RunnableInvoker
        extends Invoker
        implements Runnable
{
    // private instance member data

    /**
     * True if this object has been signaled to finish.
     */
    private boolean finished;

    // constructors

    /**
     * Create an instance of this class.
     */
    protected RunnableInvoker()
    {
    }

    // instance member method (alphabetic)

    /**
     * Returns true is the finished flag is set.
     *
     * @return true is the finished flag is set.
     */
    private synchronized boolean isFinished()
    {
        return finished;
    }

    public void run()
    {
        while (!isFinished()) {
            executeWhenReady();
        }
    }

    /**
     * Sets the finished flag to the specified value.
     *
     * @param finished the value to which the finished flag should be set.
     */
    private synchronized void setFinished(boolean finished)
    {
        this.finished = finished;
    }

    /**
     * Signals to this object that it should terminate as soon as possible.
     */
    public final void terminate()
    {
        invokeLater(new Runnable()
        {
            public void run()
            {
                setFinished(true);
            }
        });
    }
}