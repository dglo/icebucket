/*
 * class: Tracking
 *
 * Version $Id: Tracking.java,v 1.3 2005/04/18 18:19:20 patton Exp $
 *
 * Date: October 26 2004
 *
 * (c) 2004 IceCube Collaboration
 */

package icecube.icebucket.logging;

/**
 * This class logs information used to track software condifgurations.
 *
 * @author patton
 * @version $Id: Tracking.java,v 1.3 2005/04/18 18:19:20 patton Exp $
 */
public class Tracking
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
     *
     * Default constructor is declared, but private, to stop accidental
     * creation of an instance of the class.
     */
    private Tracking()
    {
    }

    // instance member method (alphabetic)

    // static member methods (alphabetic)

    /**
     * Prints the Versions of the Class to the logging system. This version
     * uses the Apache commons-logging system.
     *
     * @param log the Logger to use to log the Versions.
     * @param clazz the class whose detail will be logged.
     */
    public static void logVersions(Object log,
                                   Class clazz)
    {
        boolean hasCommonsLogging;
        try {
            clazz.getClassLoader().loadClass
                    ("org.apache.commons.logging.Log");
            hasCommonsLogging = true;
        } catch (ClassNotFoundException e) {
            hasCommonsLogging = false;
        }

        boolean hasLog4jLogging;
        try {
            clazz.getClassLoader().loadClass
                    ("org.apache.log4j.Logger");
            hasLog4jLogging = true;
        } catch (ClassNotFoundException e) {
            hasLog4jLogging = false;
        }

        boolean hasJbossLogging;
        try {
            clazz.getClassLoader().loadClass
                    ("org.jboss.logging.Logger");
            hasJbossLogging = true;
        } catch (ClassNotFoundException e) {
            hasJbossLogging = false;
        }

        final Package pack = clazz.getPackage();
        final String specificationString = "The tag for \"" +
                pack.getSpecificationTitle() +
                "\" is \"" +
                pack.getSpecificationVersion() +
                '\"';
        final String implementationString = "The build label for \"" +
                pack.getImplementationTitle() +
                "\" is \"" +
                pack.getImplementationVersion() +
                '\"';

        // The following should really be a if ... else if set of blocks but
        // this confuses the ClassLoader, so the following is a work around
        // for that.
        boolean logged = false;
        if (hasCommonsLogging &&
                (log instanceof org.apache.commons.logging.Log)) {
            final org.apache.commons.logging.Log output =
                    (org.apache.commons.logging.Log) log;
            output.info(specificationString);
            output.info(implementationString);
            logged = true;
        }

        if (!logged &&
                hasLog4jLogging &&
                (log instanceof org.apache.log4j.Logger)) {
            final org.apache.log4j.Logger output =
                    (org.apache.log4j.Logger) log;
            output.info(specificationString);
            output.info(implementationString);
            logged = true;

        }

        if (!logged &&
                hasJbossLogging &&
                (log instanceof org.jboss.logging.Logger)) {
            final org.jboss.logging.Logger output =
                    (org.jboss.logging.Logger) log;
            output.info(specificationString);
            output.info(implementationString);
            logged = true;
        }

        if (!logged) {
            System.out.println(specificationString);
            System.out.println(implementationString);
        }
    }

    // Description of this object.
    // public String toString() {}

    // public static void main(String args[]) {}
}