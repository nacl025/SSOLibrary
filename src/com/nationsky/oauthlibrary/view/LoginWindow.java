package com.nationsky.oauthlibrary.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;

public class LoginWindow {
	protected WindowManager mWindowManager;
	protected WindowManager.LayoutParams mLayoutParams;
	protected Context mContext;
	protected View mView;
	protected boolean mShow;

	public LoginWindow(Context context, View view) {
		mShow = false;
		mContext = context;
		mWindowManager = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);
		mView = view;
		mLayoutParams = new WindowManager.LayoutParams();
		mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
		mLayoutParams.width = LayoutParams.WRAP_CONTENT;
		mLayoutParams.height = LayoutParams.WRAP_CONTENT;
		mLayoutParams.gravity = Gravity.CENTER;
		mLayoutParams.format = PixelFormat.RGBA_8888;
		mLayoutParams.width = getDialogWidth(mWindowManager);
	}

	public boolean isShow() {
		return mShow;
	}

	public void hide() {
		if (mShow) {
			if (mView != null && mWindowManager != null) {
				try {
					mWindowManager.removeView(mView);
					mShow = false;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void show() {
		if (!mShow) {
			if (mView != null && mWindowManager != null
					&& mLayoutParams != null) {
				try {
					mWindowManager.addView(mView, mLayoutParams);
					mShow = true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	protected int getDialogWidth(WindowManager wm) {
		DisplayMetrics metric = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(metric);
		int width = metric.widthPixels;
		int sw = (int) (width / metric.density);
		int sideboard = (sw >= 720) ? 120 : 30;
		int rs = width - (int) (sideboard * metric.density);
		return rs;
	}
}
