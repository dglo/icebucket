/*
 * class: AsyncShutdownFixture
 *
 * Version $Id: AsyncShutdownFixture.java,v 1.4 2005/04/20 20:59:49 patton Exp $
 *
 * Date: August 28 2004
 *
 * (c) 2004 IceCube Collaboration
 */

package icecube.icebucket.util.test;


import icecube.icebucket.util.AsyncShutdownThread;

/**
 * This class is used to test that the AsyncShutdownThread class behaves
 * correctly. It can also be used as ablue print on how to write applications
 * that make use of this class.
 *
 * @author patton
 * @version $Id: AsyncShutdownFixture.java,v 1.1 2004/08/29 00:57:09 patton Exp
 *          $
 */
public class AsyncShutdownFixture
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
    protected AsyncShutdownFixture()
    {
    }

    // instance member method (alphabetic)

    /**
     * Returns true if this object has been signaled to finish.
     *
     * @return true if this object has been signaled to finish.
     */
    private synchronized boolean isFinished()
    {
        return finished;
    }

    /**
     * The main core of this object.
     */
    protected synchronized void run()
    {
        while (!isFinished()) {
            try {
                wait();
            } catch (InterruptedException e) {
                // do nothing special if interrupted
            }
        }
    }

    /**
     * Signals to this object that it should terminate as soon as possible.
     */
    public final synchronized void terminate()
    {
        finished = true;
        notifyAll();
    }

    // static member methods (alphabetic)

    /**
     * Runs an instance of this class as an executable.
     *
     * @param args parameters used to create instance of this class.
     */
    public static void main(String[] args)
    {
        final AsyncShutdownFixture fixture =
                new AsyncShutdownFixture();
        AsyncShutdownThread.registerShutdown(fixture,
                                             "terminate");
        fixture.run();
    }
}