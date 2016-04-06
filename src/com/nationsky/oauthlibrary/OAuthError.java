package com.nationsky.oauthlibrary;

public class OAuthError {

	private int statusCode = -1;

	public OAuthError(int statusCode){
		this.statusCode = statusCode;
	}
	
	/**
	 * @return
	 * 0 -- �ɹ� 
	 * 100--�û���������֤ʧ��
	 * 101--Token��֤ʧ��
	 * 102--��ȡuserIdCodeʧ��
	 */
	public int getStatusCode() {
		return statusCode;
	}
	
}
