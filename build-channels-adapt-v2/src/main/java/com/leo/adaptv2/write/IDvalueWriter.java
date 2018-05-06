package com.leo.adaptv2.write;

import com.leo.adaptv2.ApkUtil;
import com.leo.adaptv2.Pair;
import com.leo.adaptv2.SignatureNotFoundException;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.Map;
import java.util.Set;

/**
 * Created by wuleo on 2018/4/15.
 */

public class IDvalueWriter {

    public static void writeApkSigningBlock(final File apkFile, final Map<Integer, ByteBuffer> idValues) throws IOException, SignatureNotFoundException {
        RandomAccessFile fIn = null;
        FileChannel fileChannel = null;
        try {
            fIn = new RandomAccessFile(apkFile, "rw");
            fileChannel = fIn.getChannel();
            // 获取注释长度
            final long commentLength = ApkUtil.getCommentLength(fileChannel);
            // 获取核心目录偏移
            final long centralDirStartOffset = ApkUtil.findCentralDirStartOffset(fileChannel, commentLength);
            final Pair<ByteBuffer, Long> apkSigningBlockAndOffset
                    = ApkUtil.findApkSigningBlock(fileChannel, centralDirStartOffset); // 获取签名块
            final ByteBuffer oldApkSigningBlock = apkSigningBlockAndOffset.getFirst();
            final long apkSigningBlockOffset = apkSigningBlockAndOffset.getSecond();

            // 获取apk已有的ID-value
            final Map<Integer, ByteBuffer> originIdValues = ApkUtil.findIdValues(oldApkSigningBlock);
            // 查找Apk的签名信息，ID值固定为：0x7109871a
            final ByteBuffer apkSignatureSchemeV2Block = originIdValues.get(ApkUtil.APK_SIGNATURE_SCHEME_V2_BLOCK_ID);
            if (apkSignatureSchemeV2Block == null) {
                throw new IOException("No APK Signature Scheme v2 block in APK Signing Block");
            }

            // // 获取所有 ID-value
            final ApkSigningBlock apkSigningBlock = genApkSigningBlock(idValues, originIdValues);

            if (apkSigningBlockOffset != 0 && centralDirStartOffset != 0) {
                // 读取核心目录的内容
                fIn.seek(centralDirStartOffset);
                byte[] centralDirBytes;
                centralDirBytes = new byte[(int) (fileChannel.size() - centralDirStartOffset)];
                fIn.read(centralDirBytes);

                // 更新签名块
                fileChannel.position(apkSigningBlockOffset);
                // 写入新的签名块，返回的长度是不包含签名块头部的 Size of block（8字节）
                final long lengthExcludeHSOB = apkSigningBlock.writeApkSigningBlock(fIn);

                // 更新核心目录
                fIn.write(centralDirBytes);

                // 更新文件的总长度
                fIn.setLength(fIn.getFilePointer());

                // 更新 EOCD 所记录的核心目录的偏移
                // End of central directory record (EOCD)
                // Offset     Bytes     Description[23]
                // 0            4       End of central directory signature = 0x06054b50
                // 4            2       Number of this disk
                // 6            2       Disk where central directory starts
                // 8            2       Number of central directory records on this disk
                // 10           2       Total number of central directory records
                // 12           4       Size of central directory (bytes)
                // 16           4       Offset of start of central directory, relative to start of archive
                // 20           2       Comment length (n)
                // 22           n       Comment

                fIn.seek(fileChannel.size() - commentLength - 6);
                // 6 = 2(Comment length) + 4 (Offset of start of central directory, relative to start of archive)
                final ByteBuffer temp = ByteBuffer.allocate(4);
                temp.order(ByteOrder.LITTLE_ENDIAN);
                long oldSignBlockLength = centralDirStartOffset - apkSigningBlockOffset; // 旧签名块字节数
                long newSignBlockLength = lengthExcludeHSOB + 8; // 新签名块字节数, 8 = size of block in bytes (excluding this field) (uint64)
                long extraLength = newSignBlockLength - oldSignBlockLength;
                temp.putInt((int) (centralDirStartOffset + extraLength));
                temp.flip();
                fIn.write(temp.array());
            }
        } finally {
            if (fileChannel != null) {
                fileChannel.close();
            }
            if (fIn != null) {
                fIn.close();
            }
        }
    }

    private static ApkSigningBlock genApkSigningBlock(final Map<Integer, ByteBuffer> idValues,
                                           final Map<Integer, ByteBuffer> originIdValues) {
        // 把已有的和新增的 ID-value 添加到 payload 列表
        if (idValues != null && !idValues.isEmpty()) {
            originIdValues.putAll(idValues);
        }
        final ApkSigningBlock apkSigningBlock = new ApkSigningBlock();
        final Set<Map.Entry<Integer, ByteBuffer>> entrySet = originIdValues.entrySet();
        for (Map.Entry<Integer, ByteBuffer> entry : entrySet) {
            final ApkSigningPayload payload = new ApkSigningPayload(entry.getKey(), entry.getValue());
            apkSigningBlock.addPayload(payload);
        }

        return apkSigningBlock;
    }

}
