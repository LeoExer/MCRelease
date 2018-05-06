package com.leo.fc;

import com.leo.fc.utils.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

/**
 * File comment 数据格式：
 * [data][data_len][magic]
 *
 * [data] -- Json数据内容
 * [data_len] -- 数据内容长度，占2个字节
 * [magic] -- 魔数 "LEO"，占3个字节
 *
 * Created by wuleo on 2018/3/28.
 */

public class FileCommentProcessor {

    private static final String COMMENT_MAGIC = "LEO";
    private static final short BYTE_DATA_LEN = 2;
    private static final String CHARSET = "utf-8";

    public static void writeFileComment(File apkFile, String data) {
        if (apkFile == null) throw new NullPointerException("Apk file can not be null");
        if (!apkFile.exists()) throw new IllegalArgumentException("Apk file is not found");

        int length = data.length();
        if (length > Short.MAX_VALUE) throw new IllegalArgumentException("Size out of range: " + length);

        RandomAccessFile accessFile = null;
        try {
            accessFile = new RandomAccessFile(apkFile, "rw");
            long index = accessFile.length();
            index -= 2; // 2 = FCL
            accessFile.seek(index);

            short dataLen = (short) length;
            int tempLength = dataLen + BYTE_DATA_LEN + COMMENT_MAGIC.length();
            if (tempLength > Short.MAX_VALUE) throw new IllegalArgumentException("Size out of range: " + tempLength);

            short fcl = (short) tempLength;
            // Write FCL
            ByteBuffer byteBuffer = ByteBuffer.allocate(Short.BYTES);
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            byteBuffer.putShort(fcl);
            byteBuffer.flip();
            accessFile.write(byteBuffer.array());

            // Write data
            accessFile.write(data.getBytes(CHARSET));

            // Write data len
            byteBuffer = ByteBuffer.allocate(Short.BYTES);
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            byteBuffer.putShort(dataLen);
            byteBuffer.flip();
            accessFile.write(byteBuffer.array());

            // Write flag
            accessFile.write(COMMENT_MAGIC.getBytes(CHARSET));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(accessFile);
        }
    }

    public static String readFileComment(File apkFile) {
        if (apkFile == null) throw new NullPointerException("Apk file can not be null");
        if (!apkFile.exists()) throw new IllegalArgumentException("Apk file is not found");

        RandomAccessFile accessFile = null;
        try {
            accessFile = new RandomAccessFile(apkFile, "r");
            FileChannel fileChannel = accessFile.getChannel();
            long index = accessFile.length();

            // Read flag
            index -= COMMENT_MAGIC.length();
            fileChannel.position(index);
            ByteBuffer byteBuffer = ByteBuffer.allocate(COMMENT_MAGIC.length());
            fileChannel.read(byteBuffer);
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            if (!new String(byteBuffer.array(), CHARSET).equals(COMMENT_MAGIC)) {
                return "";
            }

            // Read dataLen
            index -= BYTE_DATA_LEN;
            fileChannel.position(index);
            byteBuffer = ByteBuffer.allocate(Short.BYTES);
            fileChannel.read(byteBuffer);
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            short dataLen = byteBuffer.getShort(0);

            // Read data
            index -= dataLen;
            fileChannel.position(index);
            byteBuffer = ByteBuffer.allocate(dataLen);
            fileChannel.read(byteBuffer);
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            return new String(byteBuffer.array(), CHARSET);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(accessFile);
        }
        return "";
    }
}
