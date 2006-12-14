package icecube.icebucket.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class contains information about a given disk. THe {@link
 * #getUsage(String)} method is used to acquire this class, specifying a file
 * or directory on the device whose usage is required.
 * </p>
 * Currently supported paltform are "Linux" and "Mac OS X".
 *
 * @author patton
 * @version $Id: DiskUsage.java,v 1.1 2006/04/07 00:22:20 patton Exp $
 */
public class DiskUsage
{

    // public static final member data

    // protected static final member data

    // static final member data

    // private static final member data

    private static final String[] SUPPORTED_PLATFORMS = new String[]{
            "Linux",
            "Mac OS X"
    };

    /**
     * The command to execute.
     */
    private static final String[] DF_CMD_LINE = new String[]{"df",
            "-k",
            "."
    };

    /**
     * regex for processing results of 'df -k .' command.
     */
    private static final Pattern DF_PATTERN =
            Pattern.compile("([a-zA-Z0-9/]*)[ ]*([0-9]*)[ ]*" +
                            "([0-9]*)[ ]*([0-9]*)[ ]*" +
                            "([0-9]*)%[ ]*([a-zA-Z0-9/]*)");

    /**
     * The index in a match of DF_PATTERN that has the number of 1K blocks
     * available on the device.
     */
    private static final int AVAILABLE_INDEX = 4;

    /**
     * The index in a match of DF_PATTERN that has the number of 1K blocks on
     * the device.
     */
    private static final int BLOCKS_INDEX = 2;

    /**
     * The index in a match of DF_PATTERN that has the number of 1K blocks used
     * on the device.
     */
    private static final int USED_INDEX = 3;

    /**
     * The index in a match of DF_PATTERN that has the Volume where the device
     * is mounted.
     */
    private static final int VOLUME_INDEX = 6;

    // private static member data

    // private instance member data

    /**
     * The number of 1K blocks available on the device.
     */
    private final long available;

    /**
     * The number of 1K blocks on the device.
     */
    private final long blocks;

    /**
     * The number of 1K blocks used on the device.
     */
    private final long used;

    /**
     * The Volume where the device is mounted.
     */
    private final String volume;

    // constructors

    /**
     * Create an instance of this class.
     */
    private DiskUsage(String volume,
                      long blocks,
                      long used,
                      long available)
    {
        this.volume = volume;
        this.blocks = blocks;
        this.used = used;
        this.available = available;
    }

    // instance member method (alphabetic)

    /**
     * Returns the number of 1K blocks available on the device.
     *
     * @return the number of 1K blocks available on the device.
     */
    public long getAvailable()
    {
        return available;
    }

    /**
     * Returns the number of 1K blocks on the device.
     *
     * @return the number of 1K blocks on the device.
     */
    public long getBlocks()
    {
        return blocks;
    }

    /**
     * Returns the number of 1K blocks used on the device.
     *
     * @return the number of 1K blocks used on the device.
     */
    public long getUsed()
    {
        return used;
    }

    /**
     * Returns the Volume where the device is mounted.
     *
     * @return the Volume where the device is mounted.
     */
    public String getVolume()
    {
        return volume;
    }

    // static member methods (alphabetic)

    /**
     * Create a DiskUsage object for the specified directory or file. If this
     * is called on an unsupported platform it returns <code> null<code> as the
     * volume and -1 for all values.
     *
     * @param file the directory or file whose usage is required.
     * @return a DiskUsage object contains the disk usage of the specified
     *         directory or file.
     */
    public static DiskUsage getUsage(String file)
    {
        final String platform = System.getProperty("os.name");
        boolean supported = false;
        int index = 0;
        final int finished = SUPPORTED_PLATFORMS.length;
        while (!supported &&
               finished != index) {
            if (SUPPORTED_PLATFORMS[index].equals(platform)) {
                supported = true;
            }
            index++;
        }
        if (!supported) {
            return new DiskUsage(null,
                                 -1,
                                 -1,
                                 -1);
        }

        final Process process;
        try {
            process = Runtime.getRuntime().exec(DiskUsage.DF_CMD_LINE,
                                                null,
                                                new File(file));
            boolean complete = false;
            final InputStreamReader reader =
                    new InputStreamReader(process.getInputStream());
            final BufferedReader output = new BufferedReader(reader);
            final Matcher dfResult = DiskUsage.DF_PATTERN.matcher("");
            while (!complete) {
                final String line;
                line = output.readLine();
                dfResult.reset(line);
                if (null != line) {
                    if (dfResult.matches()) {
                        return new DiskUsage(
                                dfResult.group(DiskUsage.VOLUME_INDEX),
                                Long.parseLong(dfResult.group(
                                        DiskUsage.BLOCKS_INDEX)),
                                Long.parseLong(
                                        dfResult.group(DiskUsage.USED_INDEX)),
                                Long.parseLong(dfResult.group(
                                        DiskUsage.AVAILABLE_INDEX)));
                    }
                } else {
                    complete = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return null;

    }

    // Description of this object.
    // public String toString() {}

    // public static void main(String args[]) {}

}
