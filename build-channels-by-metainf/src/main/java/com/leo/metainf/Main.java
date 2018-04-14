package com.leo.metainf;

import java.io.IOException;
import java.util.zip.ZipFile;

public class Main {

    public static void main(String[] args) throws IOException {
        String apkFilePath = "/Users/wuleo/Desktop/app.apk";
        ZipFile apkFile = new ZipFile(apkFilePath);
        MCRelease.addChannelToApk(apkFile);
    }
}
