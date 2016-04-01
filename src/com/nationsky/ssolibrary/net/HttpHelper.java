package com.nationsky.ssolibrary.net;

import java.net.URLEncoder;

import android.content.Context;
import android.text.TextUtils;

import com.nationsky.ssolibrary.SsoParameters;
import com.nationsky.ssolibrary.util.LogUtil;

public class HttpHelper {
	private String TAG = this.getClass().getName();
	private String mserver_address = "211.90.37.6:8083";
	private final String msub_categories = "/am/identity/";
	
	//http://211.90.37.33:8081/am/identity/authenticate?username=demo123&password=123456&uri=service=initService
	
	private Context mContext;
    private HttpURLConnectionHelper mHttp;
	
	private volatile static HttpHelper singleton;

	public static HttpHelper getInstance(Context context, String address) {
		if (singleton == null) {
			synchronized (HttpHelper.class) {
				if (singleton == null) {
					singleton = new HttpHelper(context, address);
				}
			}
		}
		return singleton;
	}

	private HttpHelper(Context context, String address) {
		mserver_address = address;
		mContext = context;
		mHttp = new HttpURLConnectionHelper();
	}

	public SsoParameters getToken(SsoParameters ssoParameters) throws Exception {
		StringBuilder sb = null;
		sb = new StringBuilder("http://");
		sb.append(mserver_address);
		sb.append(msub_categories);
		sb.append("authenticate?username=");
		sb.append(ssoParameters.getUserName());
		sb.append("&password=");
		sb.append(ssoParameters.getPassword());
		sb.append("&uri=service=initService");
		LogUtil.d(TAG, "send data to sso server(URL),this url is getToken: "
				+ sb.toString());
		String response = sendRequest(sb.toString());
		if(TextUtils.isEmpty(response)){
			LogUtil.d(TAG, "GetToken Respones is Null");
		}else {
			LogUtil.d(TAG, "GetToken Respones" + response);

			String[] tokenId = response.split("=");
			if (tokenId.length > 1 && !response.contains("验证失败")
					&& !response.contains("密码")) {
				ssoParameters.setTokenId(tokenId[1]);
			}
			if (response.contains("503")) {
				ssoParameters.setErrorCode("503");
			}
		}

		return ssoParameters;
	}
	
	public boolean isTokenValid(String tokenId) throws Exception {
		StringBuilder sb = null;
		sb = new StringBuilder("http://");
		sb.append(mserver_address);
		sb.append(msub_categories);
		sb.append("isTokenValid?tokenid=");
		sb.append(tokenId);
		LogUtil.d(
				TAG,
				"send data to sso server(URL),this url is isTokenValid: "
						+ sb.toString());
		String response = sendRequest(sb.toString());
		if (TextUtils.isEmpty(response)) {
			LogUtil.d(TAG, "isTokenValid Respones is Null");
		} else {
			LogUtil.d(TAG, "isTokenValid Respones" + response);

			if (response.contains("=")) {
				String result = response.split("=")[1];
				if (result.equals("true")) {
					return true;
				}
			}
		}
		return false;
	}
	
	public String getUserIdCode(String tokenId) throws Exception {
		StringBuilder sb = null;
		tokenId = tokenId.replaceAll("\r|\n", "");
		sb = new StringBuilder("http://");
		sb.append(mserver_address);
		sb.append(msub_categories);
		sb.append("attributes?subjectid=");
		sb.append(tokenId);
		sb.append("&attributenames=useridcode");
		LogUtil.d(TAG, "send data to sso server(URL): " + sb.toString());
		String response = sendRequest(sb.toString());
		if (TextUtils.isEmpty(response)) {
			LogUtil.d(TAG, "isTokenValid Respones is Null");
		} else {
			LogUtil.d(TAG, "getUserIdCode Respones" + response);
			String[] userIdCode = response.split("=");
			if (userIdCode.length > 1 && !userIdCode[3].equals("")) {
				return userIdCode[3];
			}
		}
		return null;
	}

	private String sendRequest(String url){
		mHttp.setUrl(url);
		mHttp.reset();
		new Thread() {
			@Override
			public void run() {
				try {
					mHttp.sendRequest(mContext);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		}.start();
		
		byte[] responseByte = mHttp.getResponseBytes();
		if(responseByte == null){
			return null;
		}
		String responseString = new String(responseByte);
		return responseString;
	}
}
