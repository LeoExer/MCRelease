package com.leo.fc;

import com.leo.fc.utils.DigitUtils;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        String apkFilePath = "/Users/wuleo/Desktop/app.apk";
        MCRelease.addChannelToApk(new File(apkFilePath));

        // 41 50 4B 20 53 69 67 20 42 6C 6F 63 6B 20 34 32
//        String magic = "APK Sig Block 42";
//        byte[] bytes = magic.getBytes();
////        for (byte b : bytes) {
////            System.out.println(DigitUtils.byte2Hex(b));
////        }
//        System.out.println(DigitUtils.byte2Hex(bytes));
    }
}
