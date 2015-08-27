package com.hoover.linkedinoauth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class HoovDetailsActivity extends Activity{
	private TextView hoovText;
	private TextView hoovDate;

	private String mongoHoovId;
	Button rehoov_in;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hoov_detail);

		hoovText = (TextView) findViewById(R.id.hoovtextView);
		hoovDate= (TextView) findViewById(R.id.hoovdateView2);
		Intent intent = getIntent();

		hoovText.setText(intent.getStringExtra("text"));
		hoovDate.setText(intent.getStringExtra("date"));

		mongoHoovId=intent.getStringExtra("mongodbHoovId");

		rehoov_in=(Button) findViewById(R.id.rehoov);
		rehoov_in.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent myIntent = new Intent(HoovDetailsActivity.this,HoovActivity.class);
				myIntent.putExtra("mongodbParentId",mongoHoovId);
				startActivity(myIntent); 

			} 

		});

	}
}
