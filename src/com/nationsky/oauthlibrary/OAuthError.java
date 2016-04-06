package com.nationsky.oauthlibrary;

public class OAuthError {

	private int statusCode = -1;

	public OAuthError(int statusCode){
		this.statusCode = statusCode;
	}
	
	/**
	 * @return
	 * 0 -- 成功 
	 * 100--用户名密码验证失败
	 * 101--Token验证失败
	 * 102--获取userIdCode失败
	 */
	public int getStatusCode() {
		return statusCode;
	}
	
}
