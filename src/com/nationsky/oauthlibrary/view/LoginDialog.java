package com.nationsky.oauthlibrary.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
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
		
		WindowManager.LayoutParams lp = this.getWindow().getAttributes();
		lp.gravity = Gravity.CENTER;
		lp.width = dip2px(mContext,400); // ���  ���趨��ߵĻ����޷����ô�ֱ����
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
	
	@Override
	public void onBackPressed() {
		return;
	}
}
