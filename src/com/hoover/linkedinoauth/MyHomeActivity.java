package com.hoover.linkedinoauth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyHomeActivity extends Activity {
	TextView myhoov;
	TextView mycomments;
	TextView mynotifications;
	ImageButton buttonUp;
	private GestureDetector gestureDetector;
	View.OnTouchListener gestureListener;
	RelativeLayout layout;
	float downYValue = 0;
	private final String TAG="HOME";
	private static final int MIN_DISTANCE_FOR_FLING = 100;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myhome);
		layout=(RelativeLayout)findViewById(R.id.home_layout);
		myhoov = (TextView) findViewById(R.id.myhoov);
		mycomments = (TextView) findViewById(R.id.mycomment);
		mynotifications = (TextView) findViewById(R.id.mynotification);
		buttonUp = (ImageButton) findViewById(R.id.buttonUp);

		layout.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motion) {

				Log.i(TAG, "In onTouch");
				// Get the action that was done on this touch event
				switch (motion.getAction())
				{

				case MotionEvent.ACTION_DOWN:
				{
					Log.i(TAG, "In onTouch1");
					// store the X value when the user's finger was pressed down
					downYValue = motion.getY();
					break;
				}

				case MotionEvent.ACTION_UP:
				{
					Log.i(TAG, "In onTouch2");
					// Get the X value when the user released his/her finger
					float currentY = motion.getY();            
					// going backwards: pushing stuff to the right
					if (downYValue < currentY){

					}

					// going forwards: pushing stuff to the left
					if (downYValue - currentY > MIN_DISTANCE_FOR_FLING){
						Intent intent = new Intent(MyHomeActivity.this,HomeActivityNew.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
						//intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
						startActivity(intent); 
						overridePendingTransition(0 , R.anim.slide_down);
					}
					break;
				}
				}
				// if you return false, these actions will not be recorded
				return true;
			}

		});

		myhoov.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MyHomeActivity.this,MyHoovActivity.class);
				//intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(intent); 
			}
		});

		buttonUp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MyHomeActivity.this,HomeActivityNew.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				//intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(intent); 
				overridePendingTransition(0 , R.anim.slide_down);
			}
		});



	}






}