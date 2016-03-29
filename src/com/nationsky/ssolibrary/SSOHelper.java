package com.nationsky.ssolibrary;

import java.util.List;

import android.content.Context;

public class SSOHelper {	
	private Context mContext;
	
	private RemoteInfo mRemoteInfo;
		
	private FileHelper mFileHelper;
	
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
		
		List<RemoteInfo> list = mFileHelper.readConfig();
		boolean isExit = false;
		for (RemoteInfo remoteInfo : list) {
			if(remoteInfo.address.equals(am_Address)){
				isExit = true;
				mRemoteInfo.key =  remoteInfo.key;
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
		
	public UserInfo getUserInfo(){		
		if(mFileHelper.exitTokenFile(mRemoteInfo.key)){
			//TODO 如果存在，验证TOKEN是否过期，不过期，获取UserIDcode
		}else{
			//如果不存在，表示第一次使用，需要使用用户名验证
			//首先弹出用户名输入框
			
		}
		
		
		
		
		return new UserInfo("", "");
	}	
}
