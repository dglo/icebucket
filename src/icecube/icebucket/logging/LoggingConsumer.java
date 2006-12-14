/*
 * class: LoggingConsumer
 *
 * Version $Id: LoggingConsumer.java,v 1.7 2005/09/21 20:26:58 artur Exp $
 *
 * Date: February 24 2003
 *
 * (c) 2003 LBNL
 */

package icecube.icebucket.logging;

import org.apache.log4j.Level;
import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.xml.DOMConfigurator;

import java.util.Arrays;
import java.util.Enumeration;
import java.io.File;
import java.io.IOException;

/**
 * This class provides static methods to set up a default Appender for Log4j
 * output.
 *
 * @version $Id: LoggingConsumer.java,v 1.7 2005/09/21 20:26:58 artur Exp $
 * @author patton
 */
public class LoggingConsumer
{

    // public static final member data

    // protected static final member data

    // static final member data

    // private static final member data

    /** The default PatternLayout to use in the default appender. */
    private static final String DEFAULT_PATTERN = "%m%n";

    // private static member data

    // private instance member data

    /** The Log4j Appender which this object represents. */
    private Appender appender;

    // constructors

    /**
     * Create an instance of this class.
     * Default constructor is declared, but private, to stop accidental
     * creation of an instance of the class.
     */
    private LoggingConsumer()
    {
    }

    /**
     * Create an instance of this class repesenting the Appender specified.
     *
     * @param appender the Appender this object represents.
     */
    private LoggingConsumer(Appender appender)
    {
        this.appender = appender;
    }

    // instance member function (alphabetic)

    // static member functions (alphabetic)

    /**
     * Installs a consumer which can verify the logging messages match the
     * sequence supplied.
     *
     * @param messages the set of message against which to verify the logging
     * output.
     * @return the LoggingConsumer added.
     */
    public static LoggingConsumer addVerifableConsumer(String[] messages)
    {
        final Appender appender = new VerifyAppender(Arrays.asList(messages));
        final Logger root = Logger.getRootLogger();
        root.addAppender(appender);
        return new LoggingConsumer(appender);
    }

    /**
     * Installs a logging consumer, with default layout and
     * specified threshold level, if no
     * other comsumer is currently installed.
     *
     * @param level the level. The level should be one of these: (ERROR, WARN, INFO, DEBUG).
     */
    public static void installDefaultLogger(String level)
    {
        if (!checkLevel(level)){
            throw new IllegalArgumentException("Illegal value for level: " + level);
        }
        // If no other output has been set up write out to the console.
        Logger root = Logger.getRootLogger();
        root.removeAllAppenders();
	    ConsoleAppender appender = new ConsoleAppender();
	    appender.setThreshold(Level.toLevel(level));
	    root.addAppender(appender);
    }
     /**
     * Installs a file logging consumer, with default layout and
     * specified threshold level , if no
     * other comsumer is currently installed.
     *
     * @param level the level. The level should be one of these: (ERROR, WARN, INFO, DEBUG).
     * @param fileName the file name.
     */
    public static void installDefaultLogger(String level, String fileName) throws IOException
    {
        if (!checkLevel(level)){
            throw new IllegalArgumentException("Illegal value for level: " + level);
        }
        // If no other output has been set up write out to the console.
        Logger root = Logger.getRootLogger();
        root.removeAllAppenders();
	    FileAppender appender = new FileAppender(new PatternLayout(), fileName);
	    appender.setThreshold(Level.toLevel(level));
	    root.addAppender(appender);
    }

    private static boolean checkLevel(String level){
        if (level == null){
            return false;
        }
        if (level.equalsIgnoreCase("ERROR") || level.equalsIgnoreCase("WARN") ||
                level.equalsIgnoreCase("INFO") || level.equalsIgnoreCase("DEBUG")){
            return true;
        }
        return false;
    }

   /**
     * Installs a default logging consumer if no other consumer is currently
     * installed.
     */
    public static void installDefault()
    {
        installDefault(DEFAULT_PATTERN);
    }

    /**
     * Installs a default logging consumer, with the specified layout, if no
     * other comsumer is currently installed.
     *
     * @param pattern the pattern to use with the default consumer.
     */
    public static void installDefault(String pattern)
    {
        // If no other output has been set up write out to the console.
        final Logger root = Logger.getRootLogger();
        final Enumeration appenders = root.getAllAppenders();
        if (!appenders.hasMoreElements()) {
            root.addAppender(
                    new ConsoleAppender(
                            new PatternLayout(pattern)));
        }

    }
    /**
     * Installs a default logging consumer, to read attributes from
     * a configuration file(xml file).
     *
     * @param inFile the File to use for configuration.
     */
    public static void installDefault(File inFile)
    {
	//if file doesn't exist install the default
	if (! inFile.exists() ) {
	    installDefault();
	    return;
	}
	DOMConfigurator.configure(inFile.toString());
    }
    /**
     * Removes the specified LoggingConsumer after checking that it can verify
     * messages and that all messages it expects have been seen.
     *
     * @param consumer the verifiable consumer to be removed.
     */
    public static void removeVerifiableConsumer(LoggingConsumer consumer)
    {
        final Logger root = Logger.getRootLogger();
        final VerifyAppender verifyAppender = (VerifyAppender) consumer.appender;
        root.removeAppender(verifyAppender);
        verifyAppender.close();
    }

    // Description of this object.
    // public String toString() {}

    // public static void main(String args[]) {}
}
