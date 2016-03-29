package com.nationsky.ssolibrary.util;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class DesCryptUtil {
	
	// ���룬����Ҫ��8�ı���
	private static final String password = "edmundzy";

	/**
	 * 
	 * <���ַ�������Des���ܣ����ַ���ת��Ϊ�ֽ��������>
	 */
	public static byte[] desCrypt(byte[] datasource) {
		try {
			SecureRandom random = new SecureRandom();

			DESKeySpec desKey = new DESKeySpec(password.getBytes());

			// ����һ���ܳ׹�����Ȼ��������DESKeySpecת����

			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");

			SecretKey securekey = keyFactory.generateSecret(desKey);

			// Cipher����ʵ����ɼ��ܲ���

			Cipher cipher = Cipher.getInstance("DES");

			// ���ܳ׳�ʼ��Cipher����

			cipher.init(Cipher.ENCRYPT_MODE, securekey, random);

			// ���ڣ���ȡ���ݲ�����

			// ��ʽִ�м��ܲ���

			return cipher.doFinal(datasource);

		}

		catch (Throwable e) {
			e.printStackTrace();
		}
		return null;

	}
	
	/**
	 * <�����ܵ������ֽ�����ת��Ϊ�����ֽ�����>
	 *  
	 */
	public static byte[] decrypt(byte[] src) throws Exception{

		// DES�㷨Ҫ����һ�������ε������Դ

		SecureRandom random = new SecureRandom();

		// ����һ��DESKeySpec����

		DESKeySpec desKey = new DESKeySpec(password.getBytes());

		// ����һ���ܳ׹���

		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");

		// ��DESKeySpec����ת����SecretKey����

		SecretKey securekey = keyFactory.generateSecret(desKey);

		// Cipher����ʵ����ɽ��ܲ���

		Cipher cipher = Cipher.getInstance("DES");

		// ���ܳ׳�ʼ��Cipher����

		cipher.init(Cipher.DECRYPT_MODE, securekey, random);

		// ������ʼ���ܲ���

		return cipher.doFinal(src);

	}

}
