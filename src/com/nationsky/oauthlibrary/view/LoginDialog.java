package com.nationsky.oauthlibrary.view;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;

public class LoginDialog extends Dialog{

	private static int theme = android.R.style.Theme_Translucent_NoTitleBar;

	private ProgressDialog mSpinner;
	protected WindowManager.LayoutParams mLayoutParams;
	protected View mView;
	
	public LoginDialog(Context context, View view, ILoginListener listener) {
		super(context, theme);
        mView = view;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSpinner = new ProgressDialog(getContext());
		mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mSpinner.setMessage("Loading...");
		mSpinner.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				onBackPressed();
				return false;
			}

		});
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFeatureDrawableAlpha(Window.FEATURE_OPTIONS_PANEL, 0);  

		addContentView(mView, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
	}
	
	@Override
	public void onBackPressed() {
		try {
			mSpinner.dismiss();
/*			if (null != mView) {
				mWebView.stopLoading();
				mView.destroy();
			}*/
		} catch (Exception e) {
		}
		dismiss();
	}
}
