/*
 * class: JoinStreams
 *
 * Version $Id: JoinStreams.java,v 1.1 2006/04/07 00:45:59 patton Exp $
 *
 * Date: April 6 2006
 *
 * (c) 2006 IceCube Collaboration
 */

package icecube.icebucket.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This class transfers the output of one stream into the input of another.
 * This class is designed to run in its own thread. Here is an example usage in
 * the case when the method using this class should block until the streams are
 * closed, i.e. the transfer is complete.
 * <p/>
 * <pre>
 *     final Runnable transfer = new JoinStream(fromStream,
 *                                              toStream);
 *     final Thread transferThread = new Thread(transfer);
 *     transferThread.start();
 *     transferThread.join();
 * </pre>
 * Of course, the final statement can be ommited it the method using this class
 * should not block on the completion of the transfer.
 *
 * @author patton
 * @version $Id: JoinStreams.java,v 1.1 2006/04/07 00:45:59 patton Exp $
 */
public class JoinStreams
        implements Runnable
{

    // public static final member data

    // protected static final member data

    // static final member data

    // private static final member data

    /**
     * The size of the staging array.
     */
    private static final int TRANSFER_SIZE = 1024;

    // private static member data

    // private instance member data

    /**
     * True if the input stream is finished.
     */
    private boolean complete;

    /**
     * The InputStream providing data.
     */
    private final InputStream input;

    /**
     * The OutputStream consuming data.
     */
    private final OutputStream output;

    /**
     * The array used to tranfer data between streams.
     */
    private final byte[] transferArray = new byte[TRANSFER_SIZE];

    // constructors

    /**
     * Create an instance of this class.
     */
    JoinStreams(InputStream input,
                OutputStream output)
    {
        this.input = input;
        this.output = output;
    }

    // instance member method (alphabetic)

    public void run()
    {
        try {
            while (!complete) {
                final int count;
                count = input.read(transferArray);
                if (0 < count) {
                    output.write(transferArray,
                                 0,
                                 count);
                } else if (-1 == count) {
                    complete = true;
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    // do nothing if interrupted.
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // static member methods (alphabetic)

    // Description of this object.
    // public String toString() {}

    // public static void main(String args[]) {}
}