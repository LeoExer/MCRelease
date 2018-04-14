package com.leo.metainf.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * IO 工具类
 * Created by Leo on 2016/10/19
 */
public class IOUtils {

	public static void close(Closeable closeable) {
		if(closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
				throw new RuntimeException("IOException occurred. ", e);
			}
		}
	}
	
	public static void closeQuietly(Closeable closeable) {
		if(closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
				// ignored
			}
		}
	}
}
