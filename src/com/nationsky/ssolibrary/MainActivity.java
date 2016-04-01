package com.nationsky.ssolibrary;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.nationsky.ssolibrary.SSOHelper;
import com.nationsky.ssolibrary.net.HttpHelper;
import com.natiosky.ssolibrary.R;

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
	}
	
	@Override
	public void onClick(View v) {
		
		/*SSOHelper ssoHelper = SSOHelper.getInstance(getApplicationContext(), "192.168.1.1");
		ssoHelper.requestUserInfo();*/
		HttpHelper helper = HttpHelper.getInstance(getApplicationContext(), "211.90.37.33:8081");
		SsoParameters parameter = new SsoParameters();
		parameter.setUserName("demo1");
		parameter.setPassword("123456");
		try {
			helper.getToken(parameter);
			//helper.isTokenValid("AQIC5wM2LY4SfczwweU67BLFhTkqnpzP5c_gecOdTlBfikg.*AAJTSQACMDIAAlNLABM1ODgxMzU4MTk4NzAyMzgwODAwAAJTMQACMDE.*");
		} catch (Exception e) {
			e.printStackTrace();
		}
		//loginByGet();
		
		
	}
	   
    
}
