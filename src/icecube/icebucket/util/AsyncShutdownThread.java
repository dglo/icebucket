/*
 * class: AsyncShutdownThread
 *
 * Version $Id: AsyncShutdownThread.java,v 1.7 2006/04/07 00:21:37 patton Exp $
 *
 * Date: August 28 2004
 *
 * (c) 2004 IceCube Collaboration
 */

package icecube.icebucket.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * This class extends the Thread class to provide a mechanism to shutdown the
 * object which is executing in that thread. Such an object must have a method
 * that takes no parameters and terminates the {@link Runnable#run()}  method
 * which is executing this this Thread. This method can the be registered
 *
 * @author patton
 * @version $Id: AsyncShutdownThread.java,v 1.2 2004/08/29 01:29:40 patton Exp
 *          $
 */
public class AsyncShutdownThread
        extends Thread
{

    // public static final member data

    // protected static final member data

    // static final member data

    // private static final member data

    // private static member data

    // private instance member data

    /**
     * True if the application is thoughts to be a conforming one.
     */
    private boolean conforming;

    /**
     * True if {@link #shutdown} has been called, but now method was registered
     * at that time.
     */
    private boolean shutdownPending;

    /**
     * True is the shutdown method can be executed. It can only be called once,
     * after it has been set up.
     */
    private boolean execute;

    /**
     * The method to invoke to shutdown the application.
     */
    private Method method;

    /**
     * The object on which this object should invoke the shutdown method.
     */
    private Object object;

    // constructors

    /**
     * Allocates a new Thread object. This constructor has the same effect as
     * createThread(null,  gname), where gname is a newly generated name.
     * Automatically generated names are of the form "Thread-"+n, where n is an
     * integer.
     */
    public AsyncShutdownThread()
    {
    }

    /**
     * Allocates a new Thread object. This constructor has the same effect as
     * Thread(target,  gname), where gname is a newly generated name.
     * Automatically generated names are of the form "Thread-"+n, where n is an
     * integer.
     *
     * @param target the object whose run method is called.
     */
    public AsyncShutdownThread(Runnable target)
    {
        super(target);
    }

    /**
     * Allocates a new Thread object. This constructor has the same effect as
     * Thread(null, name).
     *
     * @param name the name of the new thread.name.
     */
    public AsyncShutdownThread(String name)
    {
        super(name);
    }

    /**
     * Allocates a new Thread object. This constructor has the same effect as
     * Thread(target, name).
     *
     * @param target the object whose run method is called.
     * @param name the name of the new thread.
     */
    public AsyncShutdownThread(Runnable target,
                               String name)
    {
        super(target,
              name);
    }

    // instance member method (alphabetic)

    /**
     * Returns true if the object has already, or will in the future, register
     * a shutdown method.
     *
     * @return true if the thread is dealing, or expects to deal, with a
     *         conforming object.
     */
    public synchronized boolean isConforming()
    {
        return conforming;
    }

    /**
     * Sets the conforming flag. This flag is true if the {@link Runnable}is
     * thought to be a conforming object. Setting this flag to be true means
     * that a {@link NullPointerException} will not be thrown if there is an
     * attempt to invoke {@link #shutdown} and no method has been registered.
     * In that situation the shutdown method will be executed as soon as it is
     * registered. The registration of a shutdown method automatially sets this
     * flag.
     *
     * @param conforming the value to which the conforming flag should be set.
     */
    public synchronized void setConforming(boolean conforming)
    {
        this.conforming = conforming;
    }

    /**
     * Set the object and method to use when shutting down.
     *
     * @param object the object on which to invoke the shutdown method.
     * @param method the method to invoke to shutdown the application.
     */
    private synchronized void setShutdown(Object object,
                                                Method method)
    {
        if (shutdownSet()) {
            throw new IllegalStateException
                    ("An \"object\" and \"method\" have already been" +
                     " registered for this Thread.");
        }
        this.object = object;
        this.method = method;
        execute = true;
        conforming = true;
        if (shutdownPending) {
            shutdown();
        }
    }

    /**
     * This method requests that the code running in this thread terminate,
     * thus causing this Thread to terminate.
     */
    public final synchronized void shutdown()
    {
        if (!execute) {

            // If shutdown is set, then it must have been run, so do nothing.
            if (shutdownSet()) {
                return;
            }

            // Otherwise prepare to shutdown once the shutdown method has been
            // registered.
            shutdownPending = true;
            if (!conforming) {
                throw new NullPointerException
                        ("No shutdown method is currently registered");
            }
            return;
        }

        try {
            method.invoke(object,
                          (Object[]) null);
            execute = false;
        } catch (IllegalAccessException e) {
            // should never get here as access is checked at registration.
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e.getCause());
        }
    }

    /**
     * This method requests that the code running in this thread terminate,
     * thus causing this Thread to terminate. This method waits until this
     * thread has terminated. Therefore it <em>must</en> be invoked from a
     * different thread.
     *
     * @throws InterruptedException if intrrupted while waiting.
     */
    public void shutdownAndWait()
            throws InterruptedException
    {
        shutdown();
        join();
    }

    /**
     * Returns true if the shutdown parameters have already been set.
     *
     * @return true if the shutdown parameters have already been set.
     */
    private synchronized boolean shutdownSet()
    {
        return null != method && null != object;
    }

    // static member methods (alphabetic)

    /**
     * This method is used by an application to register the method that should
     * be called when it is time to shutdown.
     *
     * @param object the object on which the method should be invoked.
     * @param methodName the name of the methods to invoke. This should be a
     * method that has no parameters.
     */
    public static void registerShutdown(Object object,
                                        String methodName)
    {
        if (null == object ||
                null == methodName) {
            throw new NullPointerException
                    ("Neither \"object\" nor \"method\" can be 'null'.");
        }
        final Thread currentThread = Thread.currentThread();

        // If this has not being called in a AsynchronousShutdownThread
        // then it can not do anything, so don't.
        if (!(currentThread instanceof AsyncShutdownThread)) {
            return;
        }

        final Method method;
        try {
            method = object.getClass().getMethod(methodName,
                                                 (Class[]) null);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException
                    ("The specified object has no public method \"" +
                     methodName +
                     "\'.");
        }

        final AsyncShutdownThread asyncShutdownThread =
                (AsyncShutdownThread) currentThread;
        if (!Modifier.isPublic(method.getModifiers())) {
            throw new IllegalArgumentException('\"' +
                                               methodName +
                                               " is not a public method.");
        }

        asyncShutdownThread.setShutdown(object,
                                        method);
    }

    // Description of this object.
    // public String toString() {}

    // public static void main(String args[]) {}
}