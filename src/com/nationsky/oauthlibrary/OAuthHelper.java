package com.nationsky.oauthlibrary;

import com.nationsky.oauthlibrary.net.HttpManager;
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
import android.content.Context;
import android.content.DialogInterface.OnKeyListener;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class OAuthHelper {	
	private final String TAG = "OAuthHelper";
	private final String UserIDCode_Key = "oauth_userIdCode";
	
	private Context mContext;
		
	private String mRemoteAddress;
	private AlertDialog mLoginDialog;	
	private LoginWindow mLoginWindow;
	private HttpManager mHttpManager;
	private IOAuthListener mListener;
	private boolean mUserCancel;
	
	private volatile static OAuthHelper singleton;
	
	public static OAuthHelper getInstance(Activity context) {

		if (singleton == null) {
			synchronized (OAuthHelper.class) {
				if (singleton == null) {
					singleton = new OAuthHelper(context);
				}
			}
		}
		return singleton;
	}

	private OAuthHelper(Context context) {
		mContext = context;
		mHttpManager = HttpManager.getInstance(context);
	}
		
	public void authorize(String remoteAddress, IOAuthListener listener){
		mListener = listener;
		mUserCancel = false;
		boolean isExit = false;
		
		mRemoteAddress = remoteAddress;
		String remoteFileName = DesCryptUtil.encryption(mRemoteAddress);
		if(FileUtil.exitTokenFile(remoteFileName)){
			String tokenValue = FileUtil.readTokenFile(remoteFileName);
			getUserCodeByToken(tokenValue);
		}else{
			showDialog();
		}
	}
	
	private void showDialog() {
		try {
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
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
			builder.setView(view);
			mLoginDialog = builder.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
		 
		

	}
	
	private void showWindow() {
		try {
			View view = new LoginLayout(mContext, new ILoginListener() {

				@Override
				public void OK(OAuthParameters parameters) {
					getUserCodeByPassword(parameters);
				}

				@Override
				public void Cancel() {
					if (mListener != null) {
						mListener.onCancel();
						mUserCancel = true;
					}
				}
			});
			mLoginWindow = new LoginWindow(mContext, view);
			mLoginWindow.show();
			LoginDialog dialog = new LoginDialog(mContext, view, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void getUserCodeByPassword(OAuthParameters oauthParameters){
		LogUtil.d(TAG, "begin getUserCodeByPassword ...");
		oauthParameters = mHttpManager.requestToken(oauthParameters, mRemoteAddress);
		LogUtil.d(TAG, "getUserCodeByPassword step1");
		if(oauthParameters.getError().getStatusCode() == 0){//用户名密码认证成功
			//存到本地文件
			String remoteFileName = DesCryptUtil.encryption(mRemoteAddress);
			FileUtil.writeTokenFile(remoteFileName, oauthParameters.getTokenId());			
			//获取UserIDcode
			oauthParameters = mHttpManager.requestUserIdCode(oauthParameters, mRemoteAddress);
			LogUtil.d(TAG, "getUserCodeByPassword step2");
		}else {//用户名密码认证失败
			if(mListener != null && !mUserCancel){
				mListener.onError(oauthParameters.getError());
			}
		}
		//mLoginDialog.hide();
	}
	
	private void getUserCodeByToken(String tokenID) {
		LogUtil.d(TAG, "begin getUserCodeByToken ...");

		OAuthParameters oauthParameters = mHttpManager.requestTokenValid(
				tokenID, mRemoteAddress);
		LogUtil.d(TAG, "getUserCodeByToken step1");
		if(oauthParameters.getError().getStatusCode() == 0){//Token验证成功
			//获取UserIDcode
			oauthParameters = mHttpManager.requestUserIdCode(oauthParameters, mRemoteAddress);
			LogUtil.d(TAG, "getUserCodeByToken step2");
			if(mListener != null){
				if(oauthParameters.getError().getStatusCode() == 0){//获取userIdCode成功
					Bundle bundle = new Bundle(); 
					bundle.putString(UserIDCode_Key, oauthParameters.getUserIdCode());
					mListener.onComplete(bundle);
				}else {//获取userIdCode失败
					mListener.onError(oauthParameters.getError());
				}
			}
		}else {//Token验证失败,需要重新输入用户密码验证
			LogUtil.d(TAG, "Token失效，重新获取");
			showDialog();
		}
	}
	
	public void loginOut(String remoteAddress){
		LogUtil.i(TAG, "loginout");
		String remoteFileName = DesCryptUtil.encryption(remoteAddress);
		FileUtil.removeTokenFile(remoteFileName);
	}

	private class getUserCodeByPasswordAsyncTask extends AsyncTask<OAuthParameters, Integer, OAuthParameters>{		
		@Override
		protected OAuthParameters doInBackground(OAuthParameters... params) {
			OAuthParameters oauthParameters = (OAuthParameters)params[0];
			
			LogUtil.d(TAG, "begin getUserCodeByPassword ...");
			oauthParameters = mHttpManager.requestToken(oauthParameters, mRemoteAddress);
			LogUtil.d(TAG, "getUserCodeByPassword step1");
			if(oauthParameters.getError().getStatusCode() == 0){//用户名密码认证成功
				//存到本地文件
				String remoteFileName = DesCryptUtil.encryption(mRemoteAddress);
				FileUtil.writeTokenFile(remoteFileName, oauthParameters.getTokenId());			
				//获取UserIDcode
				oauthParameters = mHttpManager.requestUserIdCode(oauthParameters, mRemoteAddress);
				LogUtil.d(TAG, "getUserCodeByPassword step2");
			}
			return oauthParameters;
		}
		
		@Override
		protected void onPostExecute(OAuthParameters oauthParameters) {
			if(mListener != null && !mUserCancel){
				if(oauthParameters.getError().getStatusCode() == 0){//获取userIdCode成功
					Bundle bundle = new Bundle(); 
					bundle.putString(UserIDCode_Key, oauthParameters.getUserIdCode());
					mListener.onComplete(bundle);
				}else {//获取userIdCode失败
					mListener.onError(oauthParameters.getError());
				}
			}
			mLoginDialog.dismiss();
		}		
	}
	
}
