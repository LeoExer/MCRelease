package com.leo.adaptv2.write;

import com.leo.adaptv2.ChannelProcessor;
import com.leo.adaptv2.utils.FileUtils;
import com.leo.adaptv2.utils.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Created by wuleo on 2018/4/15.
 */

public class MCRelease {

    private static final String BUILD_DIR = System.getProperty("user.dir")
            + File.separator + "build-channels-adapt-v2" + File.separator + "build"
            + File.separator + "output" + File.separator + "channels";

    public static void addChannelToApk(File apkFile) {
        if (apkFile == null) throw new NullPointerException("Apk file can not be null");

        Map<String, String> channels = getAllChannels();
        Set<String> channelSet = channels.keySet();
        String srcApkName = apkFile.getName().replace(".apk", "");

        InputStream in = null;
        OutputStream out = null;
        for (String channel : channelSet) {
            String channelId = channels.get(channel);
            String jsonStr = "{" +
                    "\"channel\":" + "\"" + channel + "\"," +
                    "\"channel_id\":" + "\"" + channelId + "\"" +
                    "}";
            try {
                File channelFile = new File(BUILD_DIR,
                        srcApkName + "_" +  channel + "_" + channelId + ".apk");
                if (channelFile.exists()) {
                    channelFile.delete();
                }
                FileUtils.createNewFile(channelFile);
                in = new FileInputStream(apkFile);
                out = new FileOutputStream(channelFile);
                copyApkFile(in, out);

                ChannelProcessor.write(channelFile, jsonStr);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IOUtils.closeQuietly(in);
                IOUtils.closeQuietly(out);
            }
        }
    }

    private static void copyApkFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[4 * 1024];
        int len;
        while ((len = in.read(buffer)) != -1) {
            out.write(buffer, 0, len);
        }
    }

    private static Map<String, String> getAllChannels() {
        Properties p = new Properties();
        InputStream in = null;
        try {
            in = new FileInputStream("channel.properties");
            p.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(in);
        }

        Map<String, String> channels = new HashMap<>();
        Enumeration<Object> keys = p.keys();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            channels.put(key, p.getProperty(key));
        }

        return channels;
    }
}
