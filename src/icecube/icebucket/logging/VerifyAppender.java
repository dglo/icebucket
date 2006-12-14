/*
 * class: VerifyAppender
 *
 * Version $Id: VerifyAppender.java,v 1.7 2005/06/09 09:30:49 patton Exp $
 *
 * Date: June 17 2002
 *
 * (c) 2002 LBNL
 */

package icecube.icebucket.logging;

import junit.framework.Assert;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class ...does what?
 *
 * @version $Id: VerifyAppender.java,v 1.7 2005/06/09 09:30:49 patton Exp $
 * @author patton
 */
public class VerifyAppender
        extends AppenderSkeleton
{

    // public static final member data

    // public static member functions (alphabetic)

    // public instance member function (ctor first then alphabetic)

    /**
     * Create an instance of this class with the specified List of Strings.
     *
     * @param strings a Collection of string which contain the expected
     * output.
     */
    public VerifyAppender(Collection strings)
    {
        this.strings = new ArrayList(strings);
        expectedCount = strings.size();
    }

    public void close()
    {
        if (success &&
                (expectedCount != count)) {
            success = false;
            Assert.fail("Only logged " +
                        count +
                        " messages, expected " +
                        strings.size());
        }
    }

    public boolean requiresLayout()
    {
        return false;
    }

    // protected static member functions (alphabetic)

    // protected instance member function (ctor first then alphabetic)

    protected void append(LoggingEvent event)
    {
        if (!success) {
            return;
        }

        if (expectedCount == count) {
            success = false;
            Assert.fail("More than the expected " +
                        strings.size() +
                        " messages have been logged.");
        }
        if (!event.getMessage().equals(strings.get(count))) {
            success = false;
            Assert.fail("Message was\n\t" +
                        event.getMessage() +
                        "\nexpected\n\t" +
                        strings.get(count));
        }
        count++;
    }

    // package static member functions (alphabetic)

    // package instance member function (ctor first then alphabetic)

    // private static member functions (alphabetic)

    // private instance member function (ctor first then alphabetic)

    /**
     * Create an instance of this class.
     * 
     * Default constructor is declared, but private, to stop accidental
     * creation of an instance of the class.
     */
    private VerifyAppender()
    {
    }

    // private static member data

    // private instance member data

    /** The List of expected messages. */
    private List strings;

    /** The expected number of messages to be logger. */
    private int expectedCount;

    /** The number of messages logged so far. */
    private int count;

    /** true if there have been no failures in logging. */
    private boolean success = true;
}
