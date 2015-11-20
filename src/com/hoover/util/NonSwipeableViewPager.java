package com.hoover.util;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class NonSwipeableViewPager extends ViewPager {
	private boolean pagingenabled;

	public NonSwipeableViewPager(Context context) {
		super(context);
	}

	public NonSwipeableViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		if (pagingenabled) {
			return super.onInterceptTouchEvent(event);
		}
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (pagingenabled) {
			return super.onTouchEvent(event);
		}
		return false;
	}
	public void setPagingEnabled(boolean enabled) {
	    this.pagingenabled = enabled;
	}
}