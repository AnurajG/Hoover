package com.hoover.linkedinoauth;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SlidingPaneLayout;

import com.hoover.util.SlideClickListener;

public class SlidingHomeActivity extends FragmentActivity implements SlideClickListener{
	SlidingPaneLayout pane; 
	private String userComapny;
	private String userCity;
	private String userId;

	@Override protected void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.activity_sliding_panel); 
		SharedPreferences preferences = this.getSharedPreferences("user_info", 0);
		userComapny = preferences.getString("userCompany", null);
		userCity = preferences.getString("userCity", null);
		userId = preferences.getString("userId", null);
		// details omitted

		pane = (SlidingPaneLayout) findViewById(R.id.sp);
		pane.setShadowDrawableLeft(getResources().getDrawable(R.drawable.shade));
		pane.setShadowDrawableRight(getResources().getDrawable(R.drawable.shade));
		pane.setSliderFadeColor(Color.WHITE);

		pane.openPane();
		FragmentManager fm=getSupportFragmentManager();

		fm.beginTransaction().add(R.id.pane1, MyHomeFragment.newInstance("1",this,userComapny,userCity,userId ), "pane1").commit();
		//fm.beginTransaction().add(R.id.pane2, MyHoovFragment.newInstance("1",this,userComapny,userCity,userId ), "pane2").commit();
	}

	@Override
	public void onChangeBookmark(Integer id) {
		//System.out.println("Bookmark ["+bookmark+"]");
		FragmentManager fm=getSupportFragmentManager();
		if(id==1)
			fm.beginTransaction().replace(R.id.pane2, MyHoovFragment.newInstance("1",this,userComapny,userCity,userId ), "pane2").commit();
		if(id==3)
			fm.beginTransaction().replace(R.id.pane2, NotifyListFragment.newInstance("1",this,userComapny,userCity,userId ), "pane2").commit();
		pane.closePane();
	} 
}