/**
 * class: Unsigned
 *
 * Version $Id: Unsigned.java,v 1.8 2005/06/09 09:30:33 patton Exp $
 *
 * (c) 2003 IceCube Collaboration
 *
 * @author David Hays
 * @author dehays@lbl.gov
 * Date: Feb 25, 2003
 * Time: 11:07:14 AM
 *
 * @author IceCube Project
 * @author Lawrence Berkeley National Laboratory
 * @author unsigned little-endian mods & other additions by Jacobsen
 *
 */

package icecube.icebucket.util;

import java.nio.ByteBuffer;

/**
 *  This is a utility class based on Ron Hitchens Unsigned class in his book
 *  on Java NIO.  It is used for handling the value conversions between the signed
 *  primitive data types available in Java and the unsigned data types used when
 *  creating DOM Application messages.
 *
 * For the moment this deals with bytes and unsigned bytes.  It may be necessary to
 * add support for other types such as Java long and ULONG.
 */
public class Unsigned
{
    private static final int ONE_BYTE_MASK = 0xFF;

    private static final int TWO_BYTE_MASK = 0xFFFF;

    private static final int THREE_BYTE_MASK = 0xFFFFFF;

    private static final int FOUR_BYTE_MASK = 0xFFFFFFFF;

    private static final int ONE_BYTE_SHIFT = 8;

    private static final int TWO_BYTE_SHIFT = ONE_BYTE_SHIFT * 2;

    private static final int ThREE_BYTE_SHIFT = ONE_BYTE_SHIFT * 3;

    private static final long INT_MASK_AS_LONG = 0xFFFFFFFFL;

    private static final long LONG_MASK_AS_LONG = 0xFFFFFFFFFFFFFFFFL;

    private Unsigned()
    {
    }

    // unsigned bytes

    public static short getUnsignedByte(ByteBuffer bb)
    {

        return ((short) ((int) bb.get() & ONE_BYTE_MASK));
    }

    public static void putUnsignedByte(ByteBuffer bb, int value)
    {

        bb.put((byte) (value & ONE_BYTE_MASK));
    }

    public static short getUnsignedByte(ByteBuffer bb, int position)
    {

        return ((short) (bb.get(position) & (short) ONE_BYTE_MASK));
    }

    public static void putUnsignedByte(ByteBuffer bb, int position, int value)
    {

        bb.put(position, (byte) (value & ONE_BYTE_MASK));
    }

    // unsigned shorts

    public static int getUnsignedShort(ByteBuffer bb)
    {

        return (bb.getShort() & TWO_BYTE_MASK);
    }

    public static void putUnsignedShort(ByteBuffer bb, int value)
    {

        bb.putShort((short) (value & TWO_BYTE_MASK));
    }

    public static int getUnsignedShort(ByteBuffer bb, int position)
    {

        return (bb.getShort(position) & TWO_BYTE_MASK);
    }

    public static int getUnsignedLEShort(ByteBuffer bb, int position)
    {
        return
                (((int) bb.get(position) << 0) & ONE_BYTE_MASK) |
                (((int) bb.get(position + 1) << 8) & TWO_BYTE_MASK);
    }

    public static int getUnsignedLEInt(ByteBuffer bb, int position)
    {
        return
                (((int) bb.get(position) << 0) & ONE_BYTE_MASK) |
                (((int) bb.get(position + 1) << ONE_BYTE_SHIFT) & TWO_BYTE_MASK) |
                (((int) bb.get(position + 2) << TWO_BYTE_SHIFT) & THREE_BYTE_MASK) |
                (((int) bb.get(position + 3) << ThREE_BYTE_SHIFT) & FOUR_BYTE_MASK);
    }


    public static void putUnsignedShort(ByteBuffer bb, int position,
                                        int value)
    {

        bb.putShort(position, (short) (value & TWO_BYTE_MASK));
    }

    // unsigned ints

    public static long getUnsignedInt(ByteBuffer bb)
    {

        return ((long) bb.getInt() & INT_MASK_AS_LONG);
    }

    public static void putUnsignedInt(ByteBuffer bb, long value)
    {

        bb.putInt((int) (value & INT_MASK_AS_LONG));
    }

    public static long getUnsignedInt(ByteBuffer bb, int position)
    {

        return ((long) bb.getInt(position) & INT_MASK_AS_LONG);
    }

    public static long getUnsignedLong(ByteBuffer bb, int position)
    {

        return ( bb.getLong(position) & LONG_MASK_AS_LONG);
    }

    public static void putUnsignedShort(ByteBuffer bb, int position, long value)
    {

        bb.putInt(position, (int) (value & INT_MASK_AS_LONG));
    }


}
