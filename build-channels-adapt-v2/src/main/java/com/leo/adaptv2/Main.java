package com.leo.adaptv2;

import com.leo.adaptv2.write.MCRelease;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        String apkFilePath = "/Users/wuleo/Desktop/app-v2.apk";
        MCRelease.addChannelToApk(new File(apkFilePath));
    }
}
