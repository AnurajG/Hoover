package com.hoover.linkedinoauth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class HoovDetailsActivity extends Activity{
	private TextView hoovText;
	private TextView hoovDate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hoov_detail);

		hoovText = (TextView) findViewById(R.id.hoovtextView);
		hoovDate= (TextView) findViewById(R.id.hoovdateView2);
		Intent intent = getIntent();
		
		hoovText.setText(intent.getStringExtra("text"));
		hoovDate.setText(intent.getStringExtra("date"));
	}
}
