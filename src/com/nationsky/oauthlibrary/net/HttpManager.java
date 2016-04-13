package com.nationsky.oauthlibrary.net;

import android.content.Context;
import android.text.TextUtils;

import com.nationsky.oauthlibrary.OAuthError;
import com.nationsky.oauthlibrary.OAuthParameters;
import com.nationsky.oauthlibrary.util.LogUtil;

public class HttpManager {
	private String TAG = "HttpManager";
	private String mAddress = "211.90.37.6:8083";
	private final String mCategories = "/am/identity/";
	
	private Context mContext;
	private HttpURLConnectionHelper mHttp;
	
	private volatile static HttpManager singleton;

	public static HttpManager getInstance(Context context) {
		if (singleton == null) {
			synchronized (HttpManager.class) {
				if (singleton == null) {
					singleton = new HttpManager(context);
				}
			}
		}
		return singleton;
	}

	private HttpManager(Context context) {
		mContext = context;
		mHttp = new HttpURLConnectionHelper();
	}

	public OAuthParameters requestToken(OAuthParameters oauthParameters, String... address){
		try {
			StringBuilder sb = null;
			sb = new StringBuilder("http://");
			if(!TextUtils.isEmpty(address[0])){
				mAddress = address[0];
			}
			sb.append(mAddress);
			sb.append(mCategories);
			sb.append("authenticate?username=");
			sb.append(oauthParameters.getUserName());
			sb.append("&password=");
			sb.append(oauthParameters.getPassword());
			sb.append("&uri=service=initService");
			LogUtil.i(TAG, "send data to oauth server(URL),this url is getToken: "
					+ sb.toString());
			ResponseInfo response = sendRequest(sb.toString());
			if (response.responseCode == 200) {
				LogUtil.i(TAG, "GetToken Respones:" + response);
				String[] tokenId = response.responseString.split("=");
				if (tokenId.length > 1 && !tokenId[1].equals("")) {
					oauthParameters.setTokenId(tokenId[1]);
				}else {
					OAuthError error = new OAuthError(100);
					oauthParameters.setError(error);
				}		
			} else {
				if (response.responseCode == HttpURLConnectionHelper.connectExceptionFlag) {
					OAuthError error = new OAuthError(301);
					oauthParameters.setError(error);
				} else {
					OAuthError error = new OAuthError(100);
					oauthParameters.setError(error);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogUtil.e(TAG, e);
			OAuthError error = new OAuthError(100);
			oauthParameters.setError(error);
		}	
		return oauthParameters;
	}
	
	public OAuthParameters requestTokenValid(String tokenId, String... address){
		OAuthParameters oauthParameters = new OAuthParameters();
		try {
			StringBuilder sb = null;
			sb = new StringBuilder("http://");
			if(!TextUtils.isEmpty(address[0])){
				mAddress = address[0];
			}
			sb.append(mAddress);
			sb.append(mCategories);
			sb.append("isTokenValid?tokenid=");
			sb.append(tokenId);
			LogUtil.i(
					TAG,
					"send data to oauth server(URL),this url is isTokenValid: "
							+ sb.toString());
			
			ResponseInfo response = sendRequest(sb.toString());
			if (response.responseCode == 200) {
				LogUtil.i(TAG, "isTokenValid Respones:" + response);

				String[] result = response.responseString.replaceAll("\r|\n", "").split("=");
				if (result.length > 1 && result[1].equals("true")) {
                   oauthParameters.setTokenId(tokenId);
				} else {
					OAuthError error = new OAuthError(101);
					oauthParameters.setError(error);
				}		
			} else {
				if (response.responseCode == HttpURLConnectionHelper.connectExceptionFlag) {
					OAuthError error = new OAuthError(301);
					oauthParameters.setError(error);
				} else {
					OAuthError error = new OAuthError(101);
					oauthParameters.setError(error);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogUtil.e(TAG, e);
			OAuthError error = new OAuthError(101);
			oauthParameters.setError(error);
		}	
		return oauthParameters;
	}
	
	public OAuthParameters requestUserIdCode(OAuthParameters oauthParameters,
			String... address) {
		try {
			StringBuilder sb = null;
			String tokenId = oauthParameters.getTokenId().replaceAll("\r|\n",
					"");
			sb = new StringBuilder("http://");
			if (!TextUtils.isEmpty(address[0])) {
				mAddress = address[0];
			}
			sb.append(mAddress);
			sb.append(mCategories);
			sb.append("attributes?subjectid=");
			sb.append(tokenId);
			sb.append("&attributenames=useridcode");
			LogUtil.i(TAG, "send data to oauth server(URL): " + sb.toString());
			ResponseInfo response = sendRequest(sb.toString());
			if (response.responseCode == 200) {
				LogUtil.i(TAG, "getUserIdCode Respones：" + response);
				String[] userIdCode = response.responseString.split("=");
				if (userIdCode.length > 1 && !userIdCode[3].equals("")) {
					oauthParameters.setUserIdCode(userIdCode[3]);
				} else {
					OAuthError error = new OAuthError(102);
					oauthParameters.setError(error);
				}
			} else {
				if (response.responseCode == HttpURLConnectionHelper.connectExceptionFlag) {
					OAuthError error = new OAuthError(301);
					oauthParameters.setError(error);
				} else {
					OAuthError error = new OAuthError(102);
					oauthParameters.setError(error);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogUtil.e(TAG, e);
			OAuthError error = new OAuthError(102);
			oauthParameters.setError(error);
		}
		return oauthParameters;
	}

	private ResponseInfo sendRequest(String url){
		
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
		ResponseInfo responseInfo = new ResponseInfo();		
		responseInfo.responseCode = mHttp.getResponseCode();
		byte[] responseByte = mHttp.getResponseBytes();
		if (responseByte != null)
			responseInfo.responseString = new String(responseByte);
		return responseInfo;
	}

	private class ResponseInfo{
		public int responseCode;
		public String responseString;
	}
}

