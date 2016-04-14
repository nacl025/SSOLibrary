package com.nationsky.oauthlibrary;

import com.nationsky.oauthlibrary.net.HttpManager;
import com.nationsky.oauthlibrary.net.HttpURLConnectionHelper;
import com.nationsky.oauthlibrary.util.DesCryptUtil;
import com.nationsky.oauthlibrary.util.FileUtil;
import com.nationsky.oauthlibrary.util.LogUtil;
import com.nationsky.oauthlibrary.view.ILoginListener;
import com.nationsky.oauthlibrary.view.LoginDialog;
import com.nationsky.oauthlibrary.view.LoginLayout;
import com.nationsky.oauthlibrary.view.LoginWindow;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OAuthHelper {	
	private final static String TAG = "OAuthHelper";
	private final static String UserIDCode_Key = "oauth_userIdCode";
	private static Context mContext;
		
	private static String mRemoteAddress;
	private static Dialog mLoginDialog;	
	private static IOAuthListener mListener;
	private static boolean mUserCancel;
			
	public static void authorize(Activity context, String remoteAddress, IOAuthListener listener){
		mContext = context;
		mListener = listener;
		mRemoteAddress = remoteAddress;
		mUserCancel = false;
		boolean isExit = false;
			
		String remoteFileName = DesCryptUtil.encryption(mRemoteAddress);
		if(FileUtil.exitTokenFile(remoteFileName)){
			String tokenValue = FileUtil.readTokenFile(remoteFileName);
			//getUserCodeByToken(tokenValue);
			new getUserCodeByTokenAsyncTask().execute(tokenValue);
		}else{
			showLoginDialog();
		}
	}
		
	private static void showLoginDialog() {
		try {		
			View view = new LoginLayout(mContext, new ILoginListener() {
				@Override
				public void OK(OAuthParameters parameters) {
					new getUserCodeByPasswordAsyncTask().execute(parameters);
				}
				
				@Override
				public void Cancel() {
					if (mListener != null) {
						mListener.onCancel();
						mUserCancel = true;						
					}
					mLoginDialog.dismiss();
					
				}
			});
			
			if(mLoginDialog != null){
				mLoginDialog = null;			
			}
			mLoginDialog = new LoginDialog(mContext,view);
			mLoginDialog.show();			
		} catch (Exception e) {
			e.printStackTrace();
			LogUtil.e(TAG, e);
			mLoginDialog.dismiss();
		}
		 
	}
		
	private static void getUserCodeByToken(String tokenID) {
		LogUtil.d(TAG, "begin getUserCodeByToken ...");

		OAuthParameters oauthParameters = HttpManager.requestTokenValid(
				tokenID, mRemoteAddress);
		LogUtil.d(TAG, "getUserCodeByToken step1");
		if(oauthParameters.getError().getStatusCode() == 0){//Token��֤�ɹ�
			//��ȡUserIDcode
			oauthParameters = HttpManager.requestUserIdCode(oauthParameters, mRemoteAddress);
			LogUtil.d(TAG, "getUserCodeByToken step2");
			if(mListener != null && !mUserCancel){
				if(oauthParameters.getError().getStatusCode() == 0){//��ȡuserIdCode�ɹ�
					Bundle bundle = new Bundle(); 
					bundle.putString(UserIDCode_Key, oauthParameters.getUserIdCode());
					mListener.onComplete(bundle);
				}else {//��ȡuserIdCodeʧ��
					mListener.onError(oauthParameters.getError());
				}
			}
		}else {//Token��֤ʧ��,��Ҫ���������û�������֤		
			if (oauthParameters.getError().getStatusCode() == OAuthError.erroConnectCode) {
				if (mListener != null)
					mListener.onError(oauthParameters.getError());
			} else {
				LogUtil.d(TAG, "TokenʧЧ�����»�ȡ");
				showLoginDialog();
			}		
		}
	}
	
	public static void loginOut(String remoteAddress){
		LogUtil.i(TAG, "loginout");
		String remoteFileName = DesCryptUtil.encryption(remoteAddress);
		FileUtil.removeTokenFile(remoteFileName);
	}

	private static class getUserCodeByPasswordAsyncTask extends AsyncTask<OAuthParameters, Integer, OAuthParameters>{		
		@Override
		protected OAuthParameters doInBackground(OAuthParameters... params) {
			OAuthParameters oauthParameters = (OAuthParameters)params[0];
			
			LogUtil.d(TAG, "begin getUserCodeByPassword ...");
			oauthParameters = HttpManager.requestToken(oauthParameters, mRemoteAddress);
			LogUtil.d(TAG, "getUserCodeByPassword step1");
			if(oauthParameters.getError().getStatusCode() == 0 && !mUserCancel){//�û���������֤�ɹ�
				//�浽�����ļ�
				String remoteFileName = DesCryptUtil.encryption(mRemoteAddress);
				FileUtil.writeTokenFile(remoteFileName, oauthParameters.getTokenId());			
				//��ȡUserIDcode
				oauthParameters = HttpManager.requestUserIdCode(oauthParameters, mRemoteAddress);
				LogUtil.d(TAG, "getUserCodeByPassword step2");
			}
			return oauthParameters;
		}
		
		@Override
		protected void onPostExecute(OAuthParameters oauthParameters) {
			if(mListener != null && !mUserCancel){
				if(oauthParameters.getError().getStatusCode() == 0){//��ȡuserIdCode�ɹ�
					Bundle bundle = new Bundle(); 
					bundle.putString(UserIDCode_Key, oauthParameters.getUserIdCode());
					mListener.onComplete(bundle);
				}else {//��ȡuserIdCodeʧ��
					mListener.onError(oauthParameters.getError());
				}
			}
			mLoginDialog.dismiss();
		}		
	}

	private static class getUserCodeByTokenAsyncTask extends AsyncTask<String, Integer, OAuthParameters>{
		@Override
		protected OAuthParameters doInBackground(String... params) {
			String tokenID = (String)params[0];
			
			OAuthParameters oauthParameters = HttpManager.requestTokenValid(
					tokenID, mRemoteAddress);
			LogUtil.d(TAG, "getUserCodeByToken step1");
			if(oauthParameters.getError().getStatusCode() == 0 && !mUserCancel){//Token��֤�ɹ�
				oauthParameters = HttpManager.requestUserIdCode(oauthParameters, mRemoteAddress);
				LogUtil.d(TAG, "getUserCodeByToken step2");
			}						
			return oauthParameters;
		}
		
		@Override
		protected void onPostExecute(OAuthParameters result) {
			if(mListener != null && !mUserCancel){
				if(result.getError().getStatusCode() == 0){//��ȡuserIdCode�ɹ�
					Bundle bundle = new Bundle(); 
					bundle.putString(UserIDCode_Key, result.getUserIdCode());
					mListener.onComplete(bundle);
				}else if(result.getError().getStatusCode() == OAuthError.failCheckTokenCode ){			
					LogUtil.d(TAG, "TokenʧЧ�����»�ȡ");
					showLoginDialog();
				} else {
					LogUtil.d(TAG, "��ȡUserCodeʧ��");
					mListener.onError(result.getError());
				}
			}
			
		}
	}
}
