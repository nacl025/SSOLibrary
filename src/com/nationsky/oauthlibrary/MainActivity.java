package com.nationsky.oauthlibrary;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.nationsky.oauthlibrary.OAuthHelper;
import com.nationsky.oauthlibrary.net.HttpManager;
import com.nationsky.oauthlibrary.util.FileUtil;
import com.nationsky.oauthlibrary.view.LoginDialog;
import com.nationsky.oauthlibrary.view.LoginLayout;
import com.natiosky.oauthlibrary.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initView();
		
	}
	
	private void initView(){
		
		Button button1  = (Button)findViewById(R.id.button1);
		button1.setOnClickListener(this);
		
		findViewById(R.id.button2).setOnClickListener(this);
		findViewById(R.id.button3).setOnClickListener(this);
		findViewById(R.id.button4).setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		OAuthHelper oAuthHelper  = OAuthHelper.getInstance(this);
		switch (v.getId()) {
		case R.id.button1:
			
			oAuthHelper.authorize("211.90.37.33:8081", new IOAuthListener() {
				
				@Override
				public void onError(OAuthError error) {
					Toast.makeText(getApplicationContext(),
							"Error:" + error.getStatusCode(), Toast.LENGTH_LONG).show();
					
				}
				
				@Override
				public void onComplete(Bundle values) {
					String userCode = values.getString("oauth_userIdCode");
					Toast.makeText(getApplicationContext(),
							"UserCode:" + userCode, Toast.LENGTH_LONG).show();
					
				}
				
				@Override
				public void onCancel() {
					Toast.makeText(getApplicationContext(),
							"UserCancle", Toast.LENGTH_LONG).show();
					
					
				}
			});
			break;
		case R.id.button2:
			FileUtil.writeTokenFile("remote0", "123456");
			break;
		case R.id.button3:
			String vvString = FileUtil.readTokenFile("remote0");
			break;
		case R.id.button4:
			oAuthHelper.loginOut("211.90.37.33:8081");
			break;
		default:
			break;
		}
	}
	   
    
}
