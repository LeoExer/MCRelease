package com.leo.fc;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        String apkFilePath = "/Users/wuleo/Desktop/app.apk";
        MCRelease.addChannelToApk(new File(apkFilePath));
    }
}
