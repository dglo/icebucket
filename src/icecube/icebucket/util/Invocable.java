/*
 * interface: Invocable
 *
 * Version $Id: Invocable.java,v 1.1 2004/09/05 17:54:04 patton Exp $
 *
 * Date: August 25 2004
 *
 * (c) 2004 IceCube Collaboration
 */

package icecube.icebucket.util;

/**
 * This interface defines the methods which allow {@link Runnable} objects to
 * be executes, usually in a different {@link Thread}.
 *
 * @author patton
 * @version $Id: Invocable.java,v 1.1 2004/09/05 17:54:04 patton Exp $
 */
public interface Invocable
{

    // public static final member data

    // instance member method (alphabetic)

    /**
     * This schedules the specified {@link Runnable} to be invoked and then
     * waits for the Runnable to finished execution before returning.
     *
     * @param doRun the Runnable to schedule.
     */
    void invokeAndWait(Runnable doRun);

    /**
     * This schedules the specified {@link Runnable} to be invoked and then
     * returns.
     *
     * @param doRun the Runnable to schedule.
     */
    void invokeLater(Runnable doRun);

    // static member methods (alphabetic)

}
