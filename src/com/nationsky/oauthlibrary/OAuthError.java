package com.nationsky.oauthlibrary;

public class OAuthError {

	private int statusCode = -1;
	public static int success = 0;
	public static int erroConnectCode = 301;
	public static int failCheckUserCode = 100;
	public static int failCheckTokenCode = 101;
	public static int failGetUserCodeCode = 102;
	
	public OAuthError(int statusCode){
		this.statusCode = statusCode;
	}
	
	/**
	 * @return
	 * 0 -- �ɹ� 
	 * 
	 * 100--�û���������֤ʧ��
	 * 101--Token��֤ʧ��
	 * 102--��ȡuserIdCodeʧ��
	 * 
	 * 301--�����쳣
	 */
	public int getStatusCode() {
		return statusCode;
	}
	
}
