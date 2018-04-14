package com.leo.fc.utils;


public class DigitUtils {

    private static final char[] HEX_DIGIT = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f'
    };

    public static String byte2Hex(byte[] b) {
        if (b == null || b.length == 0) {
            return null;
        }

        int len = b.length;
        char[] ret = new char[len * 2];
        for (int i = 0, j = 0; i < len; i++) {
            ret[j++] = HEX_DIGIT[b[i] >> 4 & 0x0f];
            ret[j++] = HEX_DIGIT[b[i] & 0x0f];
        }
        return new String(ret);
    }

    public static byte[] short2Byte(short s) {
        byte[] ret = new byte[2];
        ret[0] = Integer.valueOf(s & 0xff).byteValue();
        ret[1] = Integer.valueOf((s >> 8) & 0xff).byteValue();
        return ret;
    }

    public static short byte2Short(byte[] buf) {
        return (short) (buf[0] + (buf[1] << 8));
    }

    public static int byte2Int(byte[] buf) {
        return (int) (buf[0] + (buf[1] << 8) + (buf[2] << 16) + (buf[3] << 24));
    }

    public static byte[] int2Byte(int i) {
        byte[] ret = new byte[4];
        ret[0] = (byte) (i & 0xff);
        ret[1] = (byte) ((i >> 8) & 0xff);
        ret[2] = (byte) ((i >> 16) & 0xff);
        ret[3] = (byte) ((i >> 24) & 0xff);
        return ret;
    }
}
