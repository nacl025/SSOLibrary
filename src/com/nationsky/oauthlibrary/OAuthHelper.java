package com.nationsky.oauthlibrary;

import java.util.ArrayList;
import java.util.List;

import com.nationsky.oauthlibrary.net.HttpManager;
import com.nationsky.oauthlibrary.util.FileUtil;
import com.nationsky.oauthlibrary.util.LogUtil;
import com.nationsky.oauthlibrary.view.ILoginListener;
import com.nationsky.oauthlibrary.view.LoginDialog;
import com.nationsky.oauthlibrary.view.LoginLayout;
import com.nationsky.oauthlibrary.view.LoginWindow;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
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
	private final String TAG = this.getClass().getName();
	private final String UserIDCode_Key = "oauth_userIdCode";
	
	private Context mContext;
	
	private RemoteInfo mRemoteInfo;		
	private AlertDialog mLoginDialog;	
	private LoginWindow mLoginWindow;
	private HttpManager mHttpManager;
	private IOAuthListener mListener;
	private boolean mUserCancel;
	
	private volatile static OAuthHelper singleton;
	
	public static OAuthHelper getInstance(Context context) {

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
		mRemoteInfo = new RemoteInfo();
		mHttpManager = HttpManager.getInstance(context);
	}
		
	public void authorize(String address, IOAuthListener listener){
		mListener = listener;
		mUserCancel = false;
		boolean isExit = false;
		List<RemoteInfo> list = FileUtil.readConfig();
		if (list == null) {
			list = new ArrayList<RemoteInfo>();
		}
		for (RemoteInfo remoteInfo : list) {
			if (remoteInfo.address.equals(address)) {
				isExit = true;
				mRemoteInfo.key = remoteInfo.key;
				mRemoteInfo.address = remoteInfo.address;
			}
		}
		if(!isExit){			
			mRemoteInfo.key = "remote" + list.size();
			mRemoteInfo.address = address;
			list.add(mRemoteInfo);
			FileUtil.writeConfig(list);
		}
		
		if(FileUtil.exitTokenFile(mRemoteInfo.key)){
			String tokenValue = FileUtil.readTokenFile(mRemoteInfo.key);
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
					getUserCodeByPassword(parameters);
				}

				@Override
				public void Cancel() {
					if (mListener != null) {
						mListener.onCancel();
						mUserCancel = true;
						mLoginDialog.hide();
					}
				}
			});
			builder.setView(view);
			mLoginDialog = builder.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
		 

		/*try {
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
		}*/

	}
	
	private void getUserCodeByPassword(OAuthParameters oauthParameters){
		LogUtil.d(TAG, "begin getUserCodeByPassword ...");
		oauthParameters = mHttpManager.requestToken(oauthParameters, mRemoteInfo.address);
		LogUtil.d(TAG, "getUserCodeByPassword step1");
		if(oauthParameters.getError().getStatusCode() == 0){//用户名密码认证成功
			//存到本地文件
			FileUtil.writeTokenFile(mRemoteInfo.key, oauthParameters.getTokenId());			
			//获取UserIDcode
			oauthParameters = mHttpManager.requestUserIdCode(oauthParameters, mRemoteInfo.address);
			LogUtil.d(TAG, "getUserCodeByPassword step2");
			if(mListener != null && !mUserCancel){
				if(oauthParameters.getError().getStatusCode() == 0){//获取userIdCode成功
					Bundle bundle = new Bundle(); 
					bundle.putString(UserIDCode_Key, oauthParameters.getUserIdCode());
					mListener.onComplete(bundle);
				}else {//获取userIdCode失败
					mListener.onError(oauthParameters.getError());
				}
			}
		}else {//用户名密码认证失败
			if(mListener != null && !mUserCancel){
				mListener.onError(oauthParameters.getError());
			}
		}
		mLoginDialog.hide();
	}
	
	private void getUserCodeByToken(String tokenID) {
		LogUtil.d(TAG, "begin getUserCodeByToken ...");

		OAuthParameters oauthParameters = mHttpManager.requestTokenValid(
				tokenID, mRemoteInfo.address);
		LogUtil.d(TAG, "getUserCodeByToken step1");
		if(oauthParameters.getError().getStatusCode() == 0){//Token验证成功
			//获取UserIDcode
			oauthParameters = mHttpManager.requestUserIdCode(oauthParameters, mRemoteInfo.address);
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
		}else {//Token验证失败
			if(mListener != null){
				mListener.onError(oauthParameters.getError());
			}
		}
	}
	
	public void loginOut(String address){
		LogUtil.i(TAG, "loginout");
		List<RemoteInfo> list = FileUtil.readConfig();
		if (list == null) {
			return;
		}		
		for (RemoteInfo remoteInfo : list) {
			if (remoteInfo.address.equals(address)) {
				FileUtil.removeTokenFile(remoteInfo.key);
			}
		}
	}

}
