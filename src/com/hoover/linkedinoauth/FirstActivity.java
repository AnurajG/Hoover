package com.hoover.linkedinoauth;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class FirstActivity extends Activity {
	Button sign_in;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_first);
		sign_in=(Button) findViewById(R.id.btn_sign);
		sign_in.setOnClickListener(new OnClickListener() {                       
           	@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
           		openBrowser1();
			}           
        });  
		
		
	}
	private void openBrowser1() 
    {       
       //this intent is used to open other activity wich contains another webView
        Intent intent = new Intent(FirstActivity.this,MainActivity.class);
        startActivity(intent); 
    }
}
