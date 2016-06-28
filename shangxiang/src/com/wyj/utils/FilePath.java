package com.wyj.utils;

import java.io.File;
import java.util.StringTokenizer;

import android.os.Environment;

public class FilePath {

	// public static final String SDCARD = "/sdcard";
	public static final String SDCARD = Environment
			.getExternalStorageDirectory().toString(); // 动态获取SD卡路径
	public static final String IMAGE_FILE = "/shangxiang_files";
	public static final String ROOT_DIRECTORY = SDCARD + IMAGE_FILE + "/";

	public static final String PICTURE = SDCARD + IMAGE_FILE + "/.picture";

	public static final String PORTRAIT = SDCARD + IMAGE_FILE + "/.portrait";

	public static final String TEMP = SDCARD + IMAGE_FILE + "/temp";
	public static final String Cmsimage = SDCARD + IMAGE_FILE + "/imageloader";

	private static FilePath fp;

	private FilePath() {

	}

	public static FilePath getInstance() {
		if (fp == null) {
			fp = new FilePath();
		}
		return fp;
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public boolean isSDCardExist() {
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			return true;
		} else
			return false;
	}

	/**
	 * 
	 * 
	 * @param path
	 * @return
	 */
	public void isExists(String path) {

		StringTokenizer st = new StringTokenizer(path, "/");
		String path1 = st.nextToken() + "/";
		String path2 = path1;
		while (st.hasMoreTokens()) {
			path1 = st.nextToken() + "/";
			path2 += path1;
			File inbox = new File(path2);
			if (!inbox.exists())
				inbox.mkdir();
		}
	}

	public String getFileName(String url) {
		return MD5.getMD5(url);
	}

	/**
	 * 
	 * 
	 * @param delpath
	 *            String
	 * @return boolean
	 */
	public boolean deletefile(String delpath) {
		try {

			File file = new File(delpath);
			if (!file.isDirectory()) {
				file.delete();
			} else if (file.isDirectory()) {
				String[] filelist = file.list();
				for (int i = 0; i < filelist.length; i++) {
					File delfile = new File(delpath + "/" + filelist[i]);
					if (!delfile.isDirectory()) {
						delfile.delete();
					} else if (delfile.isDirectory()) {
						deletefile(delpath + "/" + filelist[i]);
					}
				}
				// file.delete();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

}
