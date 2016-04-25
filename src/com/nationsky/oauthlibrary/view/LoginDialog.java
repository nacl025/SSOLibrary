package com.nationsky.oauthlibrary.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;


public class LoginDialog extends Dialog{

	private static int theme = android.R.style.Theme_Translucent_NoTitleBar;
	protected View mView;
	private Context mContext;
	
	public LoginDialog(Context context, View view) {
		super(context, theme);
		mContext = context;
        mView = view;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setCancelable(false);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		//====ע�⣺�������������onStart�ڣ��������onCreate�����Unable to add window�쳣====
		WindowManager windowManager = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);
		
		WindowManager.LayoutParams lp = this.getWindow().getAttributes();
		lp.gravity = Gravity.CENTER;
		lp.width = getDialogWidth(windowManager); // ���  ���趨��ߵĻ����޷����ô�ֱ����
		lp.height = dip2px(mContext,300); // �߶�
		this.getWindow().setAttributes(lp);
			
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);	
		addContentView(mView, params);
	}
	
	
	
	
	/**
	 * �����ֻ��ķֱ��ʴ� dp �ĵ�λ ת��Ϊ px(����)
	 */
	private int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
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
