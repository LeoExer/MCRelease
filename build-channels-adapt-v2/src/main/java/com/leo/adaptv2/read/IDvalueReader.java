package com.leo.adaptv2.read;

import com.leo.adaptv2.ApkUtil;
import com.leo.adaptv2.SignatureNotFoundException;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by wuleo on 2018/4/15.
 */

public class IDvalueReader {

    public static String getString(final File apkFile, final int id) {
        final byte[] bytes = get(apkFile, id);
        if (bytes == null) {
            return "";
        }
        try {
            return new String(bytes, ApkUtil.DEFAULT_CHARSET);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static byte[] get(final File apkFile, final int id) {
        final Map<Integer, ByteBuffer> idValues = getAll(apkFile);
        if (idValues == null) {
            return null;
        }
        final ByteBuffer byteBuffer = idValues.get(id);
        if (byteBuffer == null) {
            return null;
        }
        return getBytes(byteBuffer);
    }

    private static byte[] getBytes(final ByteBuffer byteBuffer) {
        final byte[] array = byteBuffer.array();
        final int arrayOffset = byteBuffer.arrayOffset();
        return Arrays.copyOfRange(array, arrayOffset + byteBuffer.position(),
                arrayOffset + byteBuffer.limit());
    }

    private static Map<Integer, ByteBuffer> getAll(final File apkFile) {
        Map<Integer, ByteBuffer> idValues = null;
        try {
            RandomAccessFile randomAccessFile = null;
            FileChannel fileChannel = null;
            try {
                randomAccessFile = new RandomAccessFile(apkFile, "r");
                fileChannel = randomAccessFile.getChannel();
                final ByteBuffer apkSigningBlock2 = ApkUtil.findApkSigningBlock(fileChannel).getFirst(); // 获取签名块
                idValues = ApkUtil.findIdValues(apkSigningBlock2); // 获取键值对
            } catch (IOException ignore) {
            } finally {
                try {
                    if (fileChannel != null) {
                        fileChannel.close();
                    }
                } catch (IOException ignore) {
                }
                try {
                    if (randomAccessFile != null) {
                        randomAccessFile.close();
                    }
                } catch (IOException ignore) {
                }
            }
        } catch (SignatureNotFoundException ignore) {
        }

        return idValues;
    }

}
