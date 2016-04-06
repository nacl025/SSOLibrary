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
	}
	
	@Override
	public void onClick(View v) {

		/*HttpManager helper = HttpManager.getInstance(getApplicationContext());
		OAuthParameters parameter = new OAuthParameters();
		parameter.setUserName("demo1");
		parameter.setPassword("123456");
		try {
			helper.requestToken(parameter, "211.90.37.33:8081");
			//helper.isTokenValid("AQIC5wM2LY4SfczwweU67BLFhTkqnpzP5c_gecOdTlBfikg.*AAJTSQACMDIAAlNLABM1ODgxMzU4MTk4NzAyMzgwODAwAAJTMQACMDE.*");
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		//loginByGet();
		
		/*OAuthHelper oAuthHelper  = OAuthHelper.getInstance(this);
		oAuthHelper.authorize("211.90.37.33:8081", new IOAuthListener() {
			
			@Override
			public void onError(OAuthError error) {
				 String vvString = "";
				 String vvString1 = "";
				
			}
			
			@Override
			public void onComplete(Bundle values) {
				String vvString = "";
				 String vvString1 = "";
				
			}
			
			@Override
			public void onCancel() {
				String vvString = "";
				 String vvString1 = "";
				
				
			}
		});*/
		
		/*try {
			View view = new LoginLayout(this);
			mLoginWindow = new LoginWindow(mContext, view);
			mLoginWindow.show();
			LoginDialog dialog = new LoginDialog(this, view, null);
			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		
		
		
		switch (v.getId()) {
		case R.id.button1:
			OAuthHelper oAuthHelper  = OAuthHelper.getInstance(this);
			oAuthHelper.authorize("211.90.37.33:8081", new IOAuthListener() {
				
				@Override
				public void onError(OAuthError error) {
					 String vvString = "";
					 String vvString1 = "";
					
				}
				
				@Override
				public void onComplete(Bundle values) {
					String vvString = "";
					 String vvString1 = "";
					
				}
				
				@Override
				public void onCancel() {
					String vvString = "";
					 String vvString1 = "";
					
					
				}
			});
			break;
		case R.id.button2:
			FileUtil.writeTokenFile("remote0", "123456");
			break;
		case R.id.button3:
			String vvString = FileUtil.readTokenFile("remote0");
			break;

		default:
			break;
		}
	}
	   
    
}
