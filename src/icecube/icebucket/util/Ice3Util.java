/*
 * class: Ice3Util
 *
 * Version $Id: Ice3Util.java,v 1.1 2006/04/07 00:46:27 patton Exp $
 *
 * Date: April 6 2006
 *
 * (c) 2006 IceCube Collaboration
 */

package icecube.icebucket.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 * This class contains a number of static utility methods.
 *
 * @author patton
 * @version $Id: Ice3Util.java,v 1.1 2006/04/07 00:46:27 patton Exp $
 */
public class Ice3Util
{

    // public static final member data

    // protected static final member data

    // static final member data

    // private static final member data

    // private static member data

    // private instance member data

    // constructors

    /**
     * Create an instance of this class.
     */
    private Ice3Util()
    {
    }

    // instance member method (alphabetic)

    // static member methods (alphabetic)

    /**
     * Copies the contents of the specified resource into the specified File.
     * This method both openms and closed the destination File.
     *
     * @param clazz the class whose classpath contains the resource.
     * @param resource the name of the resource to copy
     * @param destination the File in which to copy the resource.
     * @throws InterruptedException
     * @throws IOException
     */
    public static void copyResourceToFile(Class clazz,
                                          String resource,
                                          File destination)
            throws InterruptedException, IOException
    {
        final URL resourceURL = clazz.getResource(resource);
        if (null == resourceURL) {
            throw new IllegalArgumentException("No such resource as \"" +
                                               resource +
                                               "\"");
        }
        final InputStream fromResource = resourceURL.openStream();
        final OutputStream toFile = new FileOutputStream(destination);
        final Runnable copyPipe = new JoinStreams(fromResource,
                                                  toFile);
        final Thread copyThread = new Thread(copyPipe,
                                             "copying resource " + resource);
        copyThread.start();
        copyThread.join();
        toFile.close();
    }

    // Description of this object.
    // public String toString() {}

    // public static void main(String args[]) {}
}