package com.nationsky.oauthlibrary;

public class OAuthError {

	private int statusCode = -1;
	public static int erroConnectCode = 301;

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
