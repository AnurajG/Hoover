package com.hoover.util;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.widget.Toast;

public class SwipeGestureDetector extends SimpleOnGestureListener {
	private static final int SWIPE_MIN_DISTANCE = 50;
	private static final int SWIPE_MAX_OFF_PATH = 200;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	
	private Context context;

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		try {
			Toast t = Toast.makeText(context, "Gesture detected", Toast.LENGTH_SHORT);
			t.show();
			float diffAbs = Math.abs(e1.getY() - e2.getY());
			float diff = e1.getX() - e2.getX();

			if (diffAbs > SWIPE_MAX_OFF_PATH)
				return false;

			// Left swipe
			if (diff > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				
			} 
			// Right swipe
			else if (-diff > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				
			}
		} catch (Exception e) {
			Log.e("Home", "Error on gestures");
		}
		return false;
	}

}