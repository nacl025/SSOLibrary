package com.nationsky.oauthlibrary.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.util.EncodingUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import android.os.Environment;
import android.util.Xml;


public class FileUtil {
	private final static String TAG = "FileUtil";
	private final static String DEFAULT_SD_PATH = Environment
			.getExternalStorageDirectory().toString();
	private final static String ROOT_PATH = DEFAULT_SD_PATH + "/OAuth/";
    private final static String CONFIG_DIR_PATH = ROOT_PATH +"Config/";
    private final static String CONFIG_FILE_PATH = CONFIG_DIR_PATH + "config";
    private final static String TOKEN_FILE = "ACCESS";
    
    
    private final static String CONFIG_XML_ROOT = "config";
    private final static String CONFIG_XML_REMOTE = "remote";
    private final static String CONFIG_XML_REMOTE_KEY = "key";
    private final static String CONFIG_XML_REMOTE_ADDRESS = "address";
    
    
	public static List<String> readConfig(){
		File file = new File(CONFIG_FILE_PATH);
		try {
			if(!file.exists()){
				return null;
			}
			FileInputStream in = new FileInputStream(file);
			return getRemoteInfo(in);
		} catch (Exception e) {
			e.printStackTrace();
			LogUtil.e(TAG, e.getMessage());
		}		
		return null;
	}
	
	private static List<String> getRemoteInfo(InputStream xml) throws Exception {
		List<String> list = new ArrayList<String>();
		XmlPullParser pullParser = Xml.newPullParser();
		pullParser.setInput(xml, "UTF-8"); // 为Pull解释器设置要解析的XML数据
		int event = pullParser.getEventType();
		String address = "";
		while (event != XmlPullParser.END_DOCUMENT) {
			switch (event) {
			case XmlPullParser.START_DOCUMENT:
				break;
			case XmlPullParser.START_TAG:
				if (CONFIG_XML_REMOTE.equals(pullParser.getName())) {

				}
				if (CONFIG_XML_REMOTE_ADDRESS.equals(pullParser.getName())) {
					String tmp = pullParser.nextText();
					address = DesCryptUtil.decryption(tmp);
				}
				break;

			case XmlPullParser.END_TAG:
				if (CONFIG_XML_REMOTE.equals(pullParser.getName())) {
					list.add(address);
					address = "";
				}
				break;
			}
			event = pullParser.next();
		}
		return list;
	}
	
	public static void writeConfig(List<String> list) {
		if (list == null || list.size() == 0)
			return;
		File file = new File(CONFIG_FILE_PATH);		
		try {
			createParentDir(file);
			file.deleteOnExit();
			file.createNewFile();
			FileOutputStream out = new FileOutputStream(file);
			String str = getRemoteString(list);
			out.write(str.getBytes());
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			LogUtil.e(TAG, e);
		}
	}
	
	private static String getRemoteString(List<String> list) {
		StringWriter stringWriter = new StringWriter();
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlSerializer xmlSerializer = factory.newSerializer();
			xmlSerializer.setOutput(stringWriter);

			xmlSerializer.startDocument("utf-8", true);
			xmlSerializer.startTag(null, CONFIG_XML_ROOT);

			for (String address : list) {
				xmlSerializer.startTag(null, CONFIG_XML_REMOTE);
				
				xmlSerializer.startTag(null, CONFIG_XML_REMOTE_ADDRESS);
				String tmp = DesCryptUtil.encryption(address);
				xmlSerializer.text(tmp);
				xmlSerializer.endTag(null, CONFIG_XML_REMOTE_ADDRESS);
				
				xmlSerializer.endTag(null, CONFIG_XML_REMOTE);
			}
			xmlSerializer.endTag(null, CONFIG_XML_ROOT);
			xmlSerializer.endDocument();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringWriter.toString();
	}
	
	public static void createRemoteDir(String dirName){
		String dirPath= ROOT_PATH + dirName;
		File dir = new File(dirPath);
		if (!dir.exists()) {
			dir.mkdir();			
		}
	}

	public static boolean exitTokenFile(String dirName){
		String tokenPath = ROOT_PATH + dirName + "/" + TOKEN_FILE;
		File token = new File(tokenPath);
		return token.exists();
	}

    public static void writeTokenFile(String dirName, String tokenValue){
    	String tokenPath = ROOT_PATH + dirName + "/" + TOKEN_FILE;
		File token = new File(tokenPath);		
		try {
			createParentDir(token);
			if (token.exists())
				token.delete();
			token.createNewFile();
			FileOutputStream out = new FileOutputStream(token);
			String tmp = DesCryptUtil.encryption(tokenValue);
			out.write(tmp.getBytes());
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			LogUtil.e(TAG, e);
		}
    }

	public static String readTokenFile(String dirName) {
		String tokenValue = "";
		String tokenPath = ROOT_PATH + dirName + "/" + TOKEN_FILE;
		File token = new File(tokenPath);
		try {
			FileInputStream fin = new FileInputStream(tokenPath);
			int length = fin.available();
			byte[] buffer = new byte[length];
			fin.read(buffer);
			String tmp = EncodingUtils.getString(buffer, "UTF-8");
			tokenValue = DesCryptUtil.decryption(tmp);
			fin.close();
		} catch (Exception e) {
			e.printStackTrace();
			LogUtil.e(TAG, e);
		}
		return tokenValue;
	}

	public static void removeTokenFile(String dirName) {
		String tokenPath = ROOT_PATH + dirName + "/" + TOKEN_FILE;
		File token = new File(tokenPath);
		try {
			if (token.exists())
				token.delete();
		} catch (Exception e) {
			e.printStackTrace();
			LogUtil.e(TAG, e);
		}
	}

	private static void createParentDir(File file){
		File parentFile = file.getParentFile();	
		if(parentFile.exists()){
			return;
		}else {
			boolean result = parentFile.mkdirs();			
		}
	}
}
