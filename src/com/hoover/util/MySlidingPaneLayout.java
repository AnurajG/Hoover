package com.hoover.util;

import android.content.Context;
import android.support.v4.widget.SlidingPaneLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MySlidingPaneLayout extends SlidingPaneLayout{

	public MySlidingPaneLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MySlidingPaneLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public MySlidingPaneLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		if(isOpen()) return false;
		return super.onInterceptTouchEvent(arg0);
	}  


}
