package com.hoover.linkedinoauth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class MyHomeActivity extends Activity {
	TextView myhoov;
	TextView mycomments;
	TextView mynotifications;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myhome);
		myhoov = (TextView) findViewById(R.id.myhoov);
		mycomments = (TextView) findViewById(R.id.mycomment);
		mynotifications = (TextView) findViewById(R.id.mynotification);
		myhoov.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MyHomeActivity.this,MyHoovActivity.class);
				//intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(intent); 
			}
		});


	}






}