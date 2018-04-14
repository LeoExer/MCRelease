package com.leo.metainf.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Leo on 2017/11/13.
 */
public class FileUtils {

	/**
	 * Create a new file if file is not exist
	 * @param file file
	 * @return true if created file success or file is exist, otherwise false
	 */
	public static boolean createNewFile(File file) {
		try {
			if(!file.exists()) {
				File parent = file.getParentFile();
				if(!parent.exists()) {
					if (!parent.mkdirs()) return false;
				}
				return file.createNewFile();
			} else {
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Delete file or dir
	 * @param filePath file path
	 */
	public static boolean delete(String filePath) {
		return delete(new File(filePath));
	}

	/**
	 * Delete file or dir
	 * @param file file
	 */
	public static boolean delete(File file) {
		if (file == null) {
			return false;
		}
		
		if (!file.exists()) {
			return false;
		}
		
		if (file.isFile()) {
			return file.delete();
		} else if (file.isDirectory()) {
			File[] childFiles = file.listFiles();
			if (childFiles == null || childFiles.length == 0) {
				return file.delete();
			}
			
			for(File child : childFiles) {
				delete(child);
			}
		}
		
		return file.delete();
	}
	
	/**
	 * Return the suffix of file
	 * @param filePath The path of file
	 * @return The suffix of file
	 */
	public static String getFileSuffix(String filePath) {
		return getFileSuffix(filePath);
	}
	
	/**
	 * Return the suffix of file
	 * @param file file
	 * @return The suffix of file
	 */
	public static String getFileSuffix(File file) {
		if (file == null) {
			return null;
		}
		
		String fileName = file.getName();
		int index = fileName.lastIndexOf(".");
		return fileName.substring(index + 1);
	}
	
	public static String getFileNameWithoutSuffix(String filePath) {
		return getFileNameWithoutSuffix(new File(filePath));
	}
	
	public static String getFileNameWithoutSuffix(File file) {
		String fileName = file.getName();
		int index = fileName.lastIndexOf(".");
		return fileName.substring(0, index);
	}
	
	public static String createDirByFileName(String filePath) {
		return createDirByFileName(new File(filePath));
	}
	
	public static String createDirByFileName(File file) {
		String fileName = file.getName();
		int index = fileName.lastIndexOf(".");
		if (index != -1) {
			fileName = fileName.substring(0, index);
		}
		
		File parent = file.getParentFile();
		if (!parent.exists()) {
			parent.mkdirs();
		}
		String newDirPath = parent.getAbsolutePath() + File.separator + fileName;
		new File(newDirPath).mkdirs();
		
		return newDirPath;
	}
	
	public static boolean copyFolder(String srcDirPath, String desDirPath) {
		return copyFolder(srcDirPath, desDirPath, true);
	}
	
	public static boolean copyFolder(String srcDirPath, String desDirPath, boolean overwritten) {
		return copyFolder(new File(srcDirPath), new File(desDirPath), overwritten);
	}
	
	public static boolean copyFolder(File srcDir, File desDir) {
		return copyFolder(srcDir, desDir, true);
	}
	
	public static boolean copyFolder(File srcDir, File desDir, boolean overwritten) {
		if (!srcDir.exists()) return false;
		if (!srcDir.isDirectory()) return false;
		if (!desDir.exists()) {
			desDir.mkdirs();
		}
		
		boolean result = false;
		String[] childFiles = srcDir.list();
		for (String childFilename : childFiles) {
			File srcChildFile = new File(srcDir.getAbsolutePath() + File.separator + childFilename);
			File desChildFile = new File(desDir.getAbsoluteFile() + File.separator + childFilename);
			if (srcChildFile.isFile()) {	
				result = copyFile(srcChildFile, desChildFile, overwritten);
				if (!result) break;
			} else if (srcChildFile.isDirectory()) {
				if (!desChildFile.exists()) {
					desChildFile.mkdirs();
				}
				result = copyFolder(srcChildFile, desChildFile, overwritten);
				if (!result) break;
			}
		}
		
		return result;
	}
	
	public static boolean copyFile(String srcFilePath, String desFilePath) {
		return copyFile(srcFilePath, desFilePath, true);
	}
	
	public static boolean copyFile(String srcFilePath, String desFilePath, boolean overwritten) {
		return copyFile(new File(srcFilePath), new File(desFilePath), overwritten);
	}
	
	public static boolean copyFile(File srcFile, File desFile) {
		return copyFile(srcFile, desFile, true);
	}
	
	public static boolean copyFile(File srcFile, File desFile, boolean overwritten) {
		if (!srcFile.exists()) return false;
		if (!srcFile.isFile()) return false;
		
		try {
			if (desFile.exists()) {
				if (overwritten) {
					if (!desFile.delete()) return false;
					if (!desFile.createNewFile()) return false;
				} else {
					return false;
				}
			} else {
				if (!desFile.getParentFile().exists()) {
					desFile.getParentFile().mkdirs();
				}
				if (!desFile.createNewFile()) return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			fis = openFileInputStream(srcFile);
			fos = openFileOutputStream(desFile);
			byte[] buff = new byte[8 * 1024];
			int len;
			while ((len = fis.read(buff)) != -1) {
				fos.write(buff, 0, len);
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(fis);
			IOUtils.closeQuietly(fos);
		}
		
		return false;
	}
	
	public static FileInputStream openFileInputStream(String filePath)
			throws IOException, FileNotFoundException {
		return openFileInputStream(new File(filePath));
	}
	
	public static FileInputStream openFileInputStream(File file) 
			throws IOException, FileNotFoundException {
		if (file.exists()) {
			if (file.isDirectory()) {
				throw new IOException(String.format("File [%s] is a directory", file.getAbsolutePath()));
			}
			if (!file.canRead()) {
				throw new IOException(String.format("File [%s] can not be read", file.getAbsolutePath()));
			}
		} else {
			throw new FileNotFoundException(String.format("File [%s] dose not exist", file.getAbsolutePath()));
		}
		
		return new FileInputStream(file);
	}
	
	public static FileOutputStream openFileOutputStream(String filePath) 
			throws IOException, FileNotFoundException{
		return openFileOutputStream(new File(filePath));
	}
	
	public static FileOutputStream openFileOutputStream(File file) 
			throws IOException, FileNotFoundException {
		if (file.exists()) {
			if (file.isDirectory()) {
				throw new IOException(String.format("File [%s] is a directory", file.getAbsolutePath()));
			}
			if (!file.canWrite()) {
				throw new IOException(String.format("File [%s] can not be written", file.getAbsolutePath()));
			}
		} else {
			throw new FileNotFoundException(String.format("File [%s] dose not exist", file.getAbsolutePath()));
		}
		
		return new FileOutputStream(file);
	}
}
