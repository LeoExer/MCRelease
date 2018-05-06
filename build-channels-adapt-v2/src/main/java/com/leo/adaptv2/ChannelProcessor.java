package com.leo.adaptv2;

import com.leo.adaptv2.read.IDvalueReader;
import com.leo.adaptv2.write.IDvalueWriter;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by wuleo on 2018/4/15.
 */

public class ChannelProcessor {

    private static final String CHARSET = "utf-8";

    public static void write(final File apkFile, String string) {
        try {
            final byte[] bytes = string.getBytes(CHARSET);
            final ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length);
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            byteBuffer.put(bytes, 0, bytes.length);
            byteBuffer.flip(); // 重置position等状态

            Map<Integer, ByteBuffer> idValues = new LinkedHashMap<>();
            idValues.put(ApkUtil.APK_CHANNEL_BLOCK_ID, byteBuffer);

            IDvalueWriter.writeApkSigningBlock(apkFile, idValues);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SignatureNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String read(final File apkFile) {
        return IDvalueReader.getString(apkFile, ApkUtil.APK_CHANNEL_BLOCK_ID);
    }
}
