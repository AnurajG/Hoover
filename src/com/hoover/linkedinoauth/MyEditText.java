package com.hoover.linkedinoauth;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class MyEditText extends EditText {
	/* Must use this constructor in order for the layout files to instantiate the class properly */
	public MyEditText(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onKeyPreIme (int keyCode, KeyEvent event)
	{
		super.onKeyPreIme(keyCode, event);
		Resources r = getResources();
		Float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 450, r.getDisplayMetrics());
		ViewParent layoutParent = this.getParent();
		final ViewGroup group=(ViewGroup)layoutParent;
		ViewGroup.LayoutParams layoutParams= group.getLayoutParams();
		layoutParams.height =px.intValue();
		group.setLayoutParams(layoutParams);
		for(int i=0;i<group.getChildCount();i++){
			View v =group.getChildAt(i);
			if(v instanceof RecyclerView)
				v.setVisibility(View.VISIBLE);
		}
		return false;
	}

}