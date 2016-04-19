package com.nationsky.oauthlibrary.net;

import android.text.TextUtils;

import com.nationsky.oauthlibrary.OAuthError;
import com.nationsky.oauthlibrary.OAuthParameters;
import com.nationsky.oauthlibrary.util.LogUtil;

public class HttpManager {
	private static String TAG = "HttpManager";
	private static String mAddress = "";
	private final static String mCategories = "/am/identity/";

	public static OAuthParameters requestToken(OAuthParameters oauthParameters, String... address){
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
					OAuthError error = new OAuthError(OAuthError.failCheckUserCode);
					oauthParameters.setError(error);
				}		
			} else {
				if (response.responseCode == HttpURLConnectionHelper.connectExceptionFlag) {
					OAuthError error = new OAuthError(OAuthError.erroConnectCode);
					oauthParameters.setError(error);
				} else {
					OAuthError error = new OAuthError(OAuthError.failCheckUserCode);
					oauthParameters.setError(error);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogUtil.e(TAG, e);
			OAuthError error = new OAuthError(OAuthError.failCheckUserCode);
			oauthParameters.setError(error);
		}	
		return oauthParameters;
	}
	
	public static OAuthParameters requestTokenValid(String tokenId, String... address){
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
					OAuthError error = new OAuthError(OAuthError.failCheckTokenCode);
					oauthParameters.setError(error);
				}		
			} else {
				if (response.responseCode == HttpURLConnectionHelper.connectExceptionFlag) {
					OAuthError error = new OAuthError(OAuthError.erroConnectCode);
					oauthParameters.setError(error);
				} else {
					OAuthError error = new OAuthError(OAuthError.failCheckTokenCode);
					oauthParameters.setError(error);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogUtil.e(TAG, e);
			OAuthError error = new OAuthError(OAuthError.failCheckTokenCode);
			oauthParameters.setError(error);
		}	
		return oauthParameters;
	}
	
	public static OAuthParameters requestUserIdCode(OAuthParameters oauthParameters,
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
					OAuthError error = new OAuthError(OAuthError.failGetUserCodeCode);
					oauthParameters.setError(error);
				}
			} else {
				if (response.responseCode == HttpURLConnectionHelper.connectExceptionFlag) {
					OAuthError error = new OAuthError(OAuthError.erroConnectCode);
					oauthParameters.setError(error);
				} else {
					OAuthError error = new OAuthError(OAuthError.failGetUserCodeCode);
					oauthParameters.setError(error);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogUtil.e(TAG, e);
			OAuthError error = new OAuthError(OAuthError.failGetUserCodeCode);
			oauthParameters.setError(error);
		}
		return oauthParameters;
	}

	private static ResponseInfo sendRequest(String url){
		HttpURLConnectionHelper helper = new HttpURLConnectionHelper();
		helper.setUrl(url);
		helper.reset();		
		HttpRequestThread thread = new HttpRequestThread(helper);
		new Thread(thread).start();
		
		ResponseInfo responseInfo = new ResponseInfo();		
		responseInfo.responseCode = helper.getResponseCode();
		byte[] responseByte = helper.getResponseBytes();
		if (responseByte != null)
			responseInfo.responseString = new String(responseByte);
		return responseInfo;
	}

	private static class ResponseInfo{
		public int responseCode;
		public String responseString;
	}
	
	private static class HttpRequestThread implements Runnable{
		private HttpURLConnectionHelper mHelper;
		public HttpRequestThread(HttpURLConnectionHelper helper) {
			this.mHelper = helper;
		}
		public void run(){
			try {
				mHelper.sendRequest();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
}

