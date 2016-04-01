package com.nationsky.ssolibrary;

import java.util.ArrayList;
import java.util.List;
import com.nationsky.ssolibrary.view.LoginLayout;
import com.nationsky.ssolibrary.view.LoginLayout.User;
import com.nationsky.ssolibrary.view.LoginWindow;
import com.natiosky.ssolibrary.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
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

public class SSOHelper {	
	private Context mContext;
	
	private RemoteInfo mRemoteInfo;
		
	private FileHelper mFileHelper;
	
	private AlertDialog mLoginDialog;
	
	private LoginWindow mLoginWindow;
	
	private volatile static SSOHelper singleton;
	
	public static SSOHelper getInstance(Context context, String am_Address) {

		if (singleton == null) {
			synchronized (SSOHelper.class) {
				if (singleton == null) {
					singleton = new SSOHelper(context, am_Address);
				}
			}
		}
		return singleton;
	}

	private SSOHelper(Context context, String am_Address) {
		mContext = context;
		mRemoteInfo = new RemoteInfo();
		mFileHelper = FileHelper.getInstance();
		
		boolean isExit = false;
		List<RemoteInfo> list = mFileHelper.readConfig();
		if (list == null) {
			list = new ArrayList<RemoteInfo>();
		}
		for (RemoteInfo remoteInfo : list) {
			if (remoteInfo.address.equals(am_Address)) {
				isExit = true;
				mRemoteInfo.key = remoteInfo.key;
				mRemoteInfo.address = remoteInfo.address;
			}
		}
		if(!isExit){			
			mRemoteInfo.key = "remote" + list.size();
			mRemoteInfo.address = am_Address;
			list.add(mRemoteInfo);

			mFileHelper.createRemoteDir(mRemoteInfo.key);
			mFileHelper.writeConfig(list);
		}
	}
		
	public void requestUserInfo(){
		if(mFileHelper.exitTokenFile(mRemoteInfo.key)){
			//TODO 如果存在，验证TOKEN是否过期，不过期，获取UserIDcode
		}else{
			//如果不存在，表示第一次使用，需要使用用户名验证
			//首先弹出用户名输入框
			Login();
		}
	}
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message message) {
			if (message == null && mLoginDialog != null)
				return;
			User user = (User) message.obj;
			if (message.what == 0) {// 确认按钮

			} else {

			}
			mLoginWindow.hide();
		}
	};
	
	
	private void Login() {

		/*
		 * try { AlertDialog.Builder builder = new
		 * AlertDialog.Builder(mContext); View view = new LoginLayout(mContext,
		 * mHandler); builder.setView(view); mLoginDialog = builder.show(); }
		 * catch (Exception e) { e.printStackTrace(); }
		 */

		try {
			View view = new LoginLayout(mContext, mHandler);
			mLoginWindow = new LoginWindow(mContext, view);
			mLoginWindow.show();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
