package com.nationsky.ssolibrary.net;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;

import com.nationsky.ssolibrary.util.LogUtil;

public class HttpURLConnectionHelper {

	private final String TAG = this.getClass().getName();
	private static int httpConnectionConnectTimeout = 30000;
	private static final int httpConnectionReadTimeout = 60000;
	private static final int NET_BUFFER_SIZE = 512;
	private static final int NET_MAX_SIZE = 1024 * 1024 * 10;
	
	// 请求数据
	private byte[] requestbytes;
	// 回应数据
	private byte[] responsebytes;
	// 传送是否完成标志
	private boolean finish = false;
	// 传送是否成功标志
	private boolean success = false;
	// 是否结束联网标志
	private boolean cancel;
	// 服务器地址
	private URL url;
	// GET or POST
	private String requestMethod = "GET";

	// 设置传送数据
	public void setRequestBytes(byte[] requestbytes) {
		LogUtil.i("Request", "setRequestBytes_Len:" + requestbytes.length);
		this.requestbytes = requestbytes;
	}

	// 设置服务器地址
	public void setUrl(String urlString) {
		URL url = null;
		try {
			url = new URL(urlString);
		} catch (MalformedURLException e) {
			LogUtil.e(TAG, e.getMessage());
		}
		this.url = url;
	}

	// 联网时，设置服务器地址。
	public void setUrl(URL url) {
		this.url = url;
	}

	// 设置请求方式
	public void setRequestMethod(String requestMethod) {
		this.requestMethod = requestMethod;
	}

	// 重置传输状态
	public void reset() {
		this.finish = false;
		this.success = false;
		this.cancel = false;
	}
	
	// 发送请求
	public synchronized void sendRequest(Context context) {
		HttpURLConnection httpCon = null;
		LogUtil.i(TAG, "url#" + url.toString());
		try {
			httpCon = (HttpURLConnection) url.openConnection();
			LogUtil.i(TAG, "1.1#start");
			initHttpRequestHeader(httpCon);
			LogUtil.i(TAG, "1.2#initHttpRequestHeader");
			sendData2Server(httpCon);
			LogUtil.i(TAG, "1.3#sendData2Server");
			int responseCode = httpCon.getResponseCode();
			LogUtil.i(TAG, "1.4#responseCode:" + responseCode);
			responsebytes = this.getDataFromServer(httpCon, responseCode);
			this.success = true; // 传输成功
		} catch (Exception e) {
			e.printStackTrace();
			LogUtil.e(TAG, "sendRequest error:" + e.getMessage());
			LogUtil.e(TAG, e.getStackTrace().toString());
			printNetworkState(context);
			this.success = false; // 传输失败
		} finally {
			this.finish = true; // 传输完成，无论成功与失败。
			if (httpCon != null) {
				httpCon.disconnect(); // 释放http连接。
				httpCon = null;
			}
			notify();
		}
	}

	// 设置Http请求的Header字段的值
	private void initHttpRequestHeader(HttpURLConnection httpCon) throws ProtocolException {
		httpCon.setRequestProperty("Accept", "*/*");
		httpCon.setRequestProperty("Connection", "keep-alive");
	    httpCon.setRequestProperty("Content-type", "UTF-8");
		httpCon.setRequestMethod(requestMethod);
		httpCon.setConnectTimeout(httpConnectionConnectTimeout);
		httpCon.setReadTimeout(httpConnectionReadTimeout);
		
	}
	
	private void initHttpGetRequestHeader(HttpURLConnection httpCon) throws ProtocolException {
		httpCon.setRequestMethod(requestMethod);
		httpCon.setConnectTimeout(httpConnectionConnectTimeout);
		httpCon.setReadTimeout(httpConnectionReadTimeout);
		httpCon.setRequestProperty("Accept", "*/*");
		httpCon.setRequestProperty("Accept-Language", "zh-CN");  
		httpCon.setRequestProperty("Charset", "UTF-8"); 
		
	}
	
	private void sendData2Server(HttpURLConnection httpCon) throws IOException {
		// 判断是否有数据需要发送到服务器，如果需要发送数据的话，判读用户此时是否已经取消本次联网。
		if (requestbytes != null && !this.cancel) {
			OutputStream os = httpCon.getOutputStream();
			LogUtil.i(TAG, "1.2.1");
			ByteArrayInputStream bin = new ByteArrayInputStream(requestbytes);
			LogUtil.i(TAG, "1.2.2");
			byte buffer[] = new byte[NET_BUFFER_SIZE];
			int bytesRead = 0;
			while ((bytesRead = bin.read(buffer, 0, NET_BUFFER_SIZE)) != -1) {
				if (this.cancel) { // 在向服务器发送数据时，判断用户此时是否取消联网。
					break;
				}
				os.write(buffer, 0, bytesRead);
				os.flush();
			}
			LogUtil.i(TAG, "1.2.3");
			bin.close();
			os.close();
		}
	}

	private byte[] getDataFromServer(HttpURLConnection httpCon, int responseCode)
			throws Exception {
		if (!this.cancel) { // 判断是否在从服务器获得数据之前，用户已经取消本次联网请求。
			ByteArrayOutputStream bao = new ByteArrayOutputStream();
			InputStream is;
			if (responseCode == 200) {// 特别注意，如果responseCode不为200执行httpCon.getInputStream()异常
				is = new BufferedInputStream(httpCon.getInputStream());
			} else {
				is = new BufferedInputStream(httpCon.getErrorStream());
			}
			long totalcount = httpCon.getContentLength();
			LogUtil.i(TAG, "responseLen#" + totalcount);
			int bytesRead = 0;
			int sum = 0;
			byte buffer[] = new byte[NET_BUFFER_SIZE];
			while ((bytesRead = is.read(buffer, 0, NET_BUFFER_SIZE)) != -1) {
				if (this.cancel) { // 判断是否在从服务器读取数据的时候，用户取消本次联网请求。
					break;
				}
				bao.write(buffer, 0, bytesRead);
				sum += bytesRead;
				if (sum >= NET_MAX_SIZE) {
					break;
				}
			}
			is.close();

			byte[] responseBytes = bao.toByteArray();
			StringBuffer strbuffer = new StringBuffer();
			if (responseBytes.length > 0)
				for (int i = 0; i < 16; i++) {
					strbuffer.append("[" + responseBytes[i] + "] ");
				}
			LogUtil.i(TAG, "1.6#response_header:" + strbuffer.toString());
			LogUtil.i(TAG, "1.7#reponse_content:" + bao.toString());
			return bao.toByteArray();
		} else {
			return null;
		}
	}

	private final void printNetworkState(Context context) {
		WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		if (wm != null) {
			LogUtil.d(TAG, "wifi state:" + wm.getWifiState() + ",is enable:" + wm.isWifiEnabled());
		} else {
			LogUtil.d(TAG, "WifiManager is null");
		}
		ConnectivityManager cManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mi = cManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (mi != null) {
			LogUtil.d(TAG, "mobile network state:" + mi.getState() + ",is available:" + mi.isAvailable());
		} else {
			LogUtil.d(TAG, "NetworkInfo is null");
		}
	}
	
	// 获得回应数据
	public synchronized byte[] getResponseBytes() {
		long curTime = System.currentTimeMillis();
		while (!this.finish) {
			try {
				wait();
			} catch (InterruptedException e) {
				return null;
			}
		}
		LogUtil.d(TAG, "app detail after getResponseBytes, wait spent:"
				+ (System.currentTimeMillis() - curTime));
		if (!this.cancel && this.success && responsebytes != null
				&& responsebytes.length > 0) {
			return responsebytes;
		} else {
			return null;
		}
	}

}
