package com.nationsky.ssolibrary.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


public class LoginLayout extends LinearLayout {

	private Context mContext;
	
	IButtonCallBack mButtonCallBack;
	
	Handler mHandler;
	
	public LoginLayout(Context context) {
		super(context);
		mContext = context;
		// 根
		LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		this.setLayoutParams(params1);
		this.setBackgroundColor(Color.parseColor("#eeeeee"));
		this.setOrientation(LinearLayout.VERTICAL);

		// 头标
		TextView textView_title = new TextView(mContext);
		LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		int topMargin = dip2px(mContext, 20);
		params2.topMargin = topMargin;
		params2.gravity = Gravity.CENTER_HORIZONTAL;
		textView_title.setLayoutParams(params2);
		textView_title.setTextSize(18);
		textView_title.setText("身份认证");
		this.addView(textView_title);

		// 用户名
		EditText editText_name = new EditText(mContext);
		LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		int topMargin1 = dip2px(mContext, 5);
		params3.topMargin = topMargin1;
		editText_name.setLayoutParams(params3);
		editText_name.setSingleLine(true);
		editText_name.setTextColor(Color.parseColor("#333333"));
		editText_name.setHint("请输入用户名");
		this.addView(editText_name);

		// 密码
		EditText editText_pass = new EditText(mContext);
		LinearLayout.LayoutParams paramsaPass = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		int topMarginPass = dip2px(mContext, 10);
		paramsaPass.topMargin = topMarginPass;
		editText_pass.setLayoutParams(paramsaPass);
		editText_pass.setHint("请输入密码");
		int type = (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		editText_pass.setInputType(type);
		// editText_pass.setSingleLine(true);千万不能放开，放开的话setInputType就没作用了。
		editText_pass.setTextColor(Color.parseColor("#333333"));
		this.addView(editText_pass);

		// 按钮布局
		LinearLayout layout_login = new LinearLayout(mContext);
		LinearLayout.LayoutParams params5 = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		int topMargin3 = dip2px(mContext, 20);
		params5.topMargin = topMargin3;
		layout_login.setLayoutParams(params5);

		// 确认按钮
		Button button_ok = new Button(mContext);
		LinearLayout.LayoutParams paramsOK = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		paramsOK.weight = 1;
		button_ok.setLayoutParams(paramsOK);
		button_ok.setText("确定");
		button_ok.setOnClickListener(listener_OK);
		layout_login.addView(button_ok);

		// 取消按钮
		Button button_cancel = new Button(mContext);
		LinearLayout.LayoutParams paramsCancel = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		paramsCancel.weight = 1;
		button_cancel.setLayoutParams(paramsCancel);
		button_cancel.setText("取消");
		button_cancel.setOnClickListener(listener_Cancel);
		layout_login.addView(button_cancel);
		this.addView(layout_login);

		// 进度条
		ProgressBar bar_login = new ProgressBar(mContext);
		LinearLayout.LayoutParams paramsBar = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		int topMarginBar = dip2px(mContext, 20);
		paramsBar.topMargin = topMarginBar;
		paramsBar.gravity = Gravity.CENTER_HORIZONTAL;
		bar_login.setLayoutParams(paramsBar);
		bar_login.setVisibility(View.GONE);
		// BeanUtils.setFieldValue(bar_login, "mOnlyIndeterminate", new
		// Boolean(false));
		this.addView(bar_login);

	}
	
	public LoginLayout(Context context, Handler handler){
		this(context);
		mHandler = handler;		
	}
	
	public LoginLayout(Context context, IButtonCallBack callBack) {
		this(context);
        mButtonCallBack = callBack;
		
	}
		
	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	private int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	private int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	private OnClickListener listener_OK = new OnClickListener() {
		
		@Override
		public void onClick(View v) {			
			if (mHandler != null) {
				Message message = mHandler.obtainMessage(0);
				mHandler.sendMessage(message);
			} else if (mButtonCallBack != null) {
				mButtonCallBack.OK();
			}
		}
	};
	
    private OnClickListener listener_Cancel = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (mHandler != null) {
				Message message = mHandler.obtainMessage(1);
				mHandler.sendMessage(message);
			} else if (mButtonCallBack != null) {
				mButtonCallBack.Cancel();
			}
		}
	};
	
	public interface IButtonCallBack {  
	    void OK(); 
	    void Cancel();
	}
}
