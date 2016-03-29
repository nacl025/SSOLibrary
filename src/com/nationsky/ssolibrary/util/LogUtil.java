package com.nationsky.ssolibrary.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import android.os.Build;
import android.os.Environment;
import android.text.format.DateFormat;

public class LogUtil {
	
	private static final String TAG = LogUtil.class.getName();
	private static boolean LOG_SWITCH = true;
	private static final String DEFAULT_SD_PATH = Environment
			.getExternalStorageDirectory().toString();
	private static final String DEFAULT_PATH = DEFAULT_SD_PATH + "/SSO/";
	 public static final String LOG_FILE = DEFAULT_PATH + "Log/sso.log";

	public static void d(String tag, Throwable e) {
		if (LOG_SWITCH) {
			String content = exception2Str(e);
			android.util.Log.d(tag, content);
			writeLog(LOG_FILE, tag, content);
		}
	}

	public static void i(String tag, Throwable e) {
		if (LOG_SWITCH) {
			String content = exception2Str(e);
			android.util.Log.d(tag, content);
			writeLog(LOG_FILE, tag, content);
		}
	}

	public static void e(String tag, Throwable e) {
		if (LOG_SWITCH) {
			String content = exception2Str(e);
			android.util.Log.e(tag, content);
			writeLog(LOG_FILE, tag, content);
		}
	}

	private static String exception2Str(Throwable e) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintWriter pw = new PrintWriter(baos);
		e.printStackTrace(pw);
		pw.close();

		return baos.toString();
	}

	public static void d(String tag, String content) {
		if (LOG_SWITCH) {
			android.util.Log.d(tag, content);
			writeLog(LOG_FILE, tag, content);
		}
	}

	public static void i(String tag, String content) {
		if (LOG_SWITCH) {
			android.util.Log.i(tag, content);
			writeLog(LOG_FILE, tag, content);
		}
	}

	public static void e(String tag, String content) {
		if (LOG_SWITCH) {
			android.util.Log.e(tag, content);
			writeLog(LOG_FILE, tag, content);
		}
	}

	private static void writeLog(String filePath, String tag, String content) {
		try {
			if (Build.VERSION.SDK_INT > Build.VERSION_CODES.DONUT) {
				File log = new File(filePath);
				if (!log.getParentFile().exists())
					log.getParentFile().mkdirs();
				FileWriter out = new FileWriter(log, true);
				out.write("[" + getTodayDateTimeStr() + "] " + tag + " : " + content + "\n");
				out.close();
			}
		} catch (IOException e) {
		}
	}
	
	private static String getTodayDateTimeStr() {
		 return DateFormat.format("yyyy-MM-dd kk:mm:ss", new Date().getTime()).toString();
	}
}
