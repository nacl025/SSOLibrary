package com.nationsky.ssolibrary;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import com.nationsky.ssolibrary.util.LogUtil;

import android.os.Environment;
import android.util.Xml;


public class FileHelper {
	private final String TAG = this.getClass().getName();
	private final String DEFAULT_SD_PATH = Environment
			.getExternalStorageDirectory().toString();
	private final String DEFAULT_PATH = DEFAULT_SD_PATH + "/SSO/";
    private final String CONFIG_DIR_PATH = DEFAULT_PATH +"Config/";
    private final String CONFIG_FILE_PATH = CONFIG_DIR_PATH + "config.xml";
    private final String TOKEN_FILE = "ACCESS";
    
    
    private final String CONFIG_XML_ROOT = "config";
    private final String CONFIG_XML_REMOTE = "remote";
    private final String CONFIG_XML_REMOTE_KEY = "key";
    private final String CONFIG_XML_REMOTE_ADDRESS = "address";
    
    
	private volatile static FileHelper singleton;
	
	public static FileHelper getInstance() {

		if (singleton == null) {
			synchronized (SSOHelper.class) {
				if (singleton == null) {
					singleton = new FileHelper();
				}
			}
		}
		return singleton;
	}

	private FileHelper() {
		File root_dir = new File(DEFAULT_PATH);
		if(!root_dir.exists()){
			root_dir.mkdir();
		}
		File config_dir = new File(DEFAULT_PATH);
		if(!config_dir.exists()){
			config_dir.mkdir();
		}
	}
    
	public List<RemoteInfo> readConfig(){
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
	
	private List<RemoteInfo> getRemoteInfo(InputStream xml) throws Exception {
		List<RemoteInfo> list = new ArrayList<RemoteInfo>();
		RemoteInfo remote = null;
		XmlPullParser pullParser = Xml.newPullParser();
		pullParser.setInput(xml, "UTF-8"); // 为Pull解释器设置要解析的XML数据
		int event = pullParser.getEventType();

		while (event != XmlPullParser.END_DOCUMENT) {

			switch (event) {
			case XmlPullParser.START_DOCUMENT:
				break;
			case XmlPullParser.START_TAG:
				if (CONFIG_XML_REMOTE.equals(pullParser.getName())) {
					remote = new RemoteInfo();
				}
				if (CONFIG_XML_REMOTE_KEY.equals(pullParser.getName())) {
					String key = pullParser.nextText();
					remote.key = key;
				}
				if (CONFIG_XML_REMOTE_ADDRESS.equals(pullParser.getName())) {
					String address = pullParser.nextText();
					remote.address = address;
				}
				break;

			case XmlPullParser.END_TAG:
				if (CONFIG_XML_REMOTE.equals(pullParser.getName())) {
					list.add(remote);
					remote = null;
				}
				break;
			}
			event = pullParser.next();
		}
		return list;
	}
	
	public void writeConfig(List<RemoteInfo> list) {
		if (list == null || list.size() == 0)
			return;
		File file = new File(CONFIG_FILE_PATH);
		try {
			file.deleteOnExit();
			file.createNewFile();
			FileOutputStream out = new FileOutputStream(file);
			String str = getRemoteString(list);
			out.write(str.getBytes());
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			LogUtil.e(TAG, e.getMessage());
		}
	}
	
	private String getRemoteString(List<RemoteInfo> list) {
		StringWriter stringWriter = new StringWriter();
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlSerializer xmlSerializer = factory.newSerializer();
			xmlSerializer.setOutput(stringWriter);

			xmlSerializer.startDocument("utf-8", true);
			xmlSerializer.startTag(null, CONFIG_XML_ROOT);

			for (RemoteInfo remoteInfo : list) {
				xmlSerializer.startTag(null, CONFIG_XML_REMOTE);
				
				xmlSerializer.startTag(null, CONFIG_XML_REMOTE_KEY);
				xmlSerializer.text(remoteInfo.key);
				xmlSerializer.endTag(null, CONFIG_XML_REMOTE_KEY);
				
				xmlSerializer.startTag(null, CONFIG_XML_REMOTE_ADDRESS);
				xmlSerializer.text(remoteInfo.address);
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
	
	public void createRemoteDir(String dirName){
		String dirPath= DEFAULT_PATH + dirName;
		File dir = new File(dirPath);
		if (!dir.exists()) {
			dir.mkdir();
		}
	}

	public boolean exitTokenFile(String dirName){
		String tokenPath = DEFAULT_PATH + dirName + "/" + TOKEN_FILE;
		File token = new File(tokenPath);
		return token.exists();
	}

}
