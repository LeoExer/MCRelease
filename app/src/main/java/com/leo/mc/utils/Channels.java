package com.leo.mc.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.leo.adaptv2.ChannelProcessor;
import com.leo.adaptv2.read.IDvalueReader;
import com.leo.fc.FileCommentProcessor;
import com.leo.metainf.MetaInfProcessor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by wuleo on 2018/3/26.
 */

public class Channels {

    private static final String TAG = Channels.class.getSimpleName();

    /**
     * 获取Apk文件META-INF目录里的渠道号信息
     * @param context context
     * @return 如果渠道文件存在，返回渠道号；否则返回空字符串
     */
    public static String getChannelByMetaInf(Context context) {
        String srcApkPath = ApkUtils.getSrcApkPath(context);
        if (srcApkPath == null) return "";

        return MetaInfProcessor.getChannelByMetaInf(new File(srcApkPath));
    }

    public static String getChannelByFC(Context context) {
        String srcApkPath = ApkUtils.getSrcApkPath(context);
        if (srcApkPath == null) return "";

        String channelJson = FileCommentProcessor.readFileComment(new File(srcApkPath));
        Log.i(TAG, channelJson);

        String str = "";
        try {
            JSONObject json = new JSONObject(channelJson);
            String channel = json.getString("channel");
            String channelId = json.getString("channel_id");
            str = channel + "_" + channelId;
        } catch (JSONException ignore) {}

        return str;
    }

    public static String getChannelByIdValue(Context context) {
        String srcApkPath = ApkUtils.getSrcApkPath(context);
        if (srcApkPath == null) return "";

        String channelJson = ChannelProcessor.read(new File(srcApkPath));
        Log.i(TAG, channelJson);

        String str = "";
        try {
            JSONObject json = new JSONObject(channelJson);
            String channel = json.getString("channel");
            String channelId = json.getString("channel_id");
            str = channel + "_" + channelId;
        } catch (JSONException ignore) {}

        return str;
    }
}
