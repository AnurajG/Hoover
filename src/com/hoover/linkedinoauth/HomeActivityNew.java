package com.hoover.linkedinoauth;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class HomeActivityNew extends FragmentActivity{
	FragmentManager fm;
	String userComapny;
	String userCity;
	String userId;
	HomePageAdapter pageAdapter;
	Button hoov_in;
	SlidingTabLayout tabs;
	CharSequence Titles[]={"Newest","Hottest"};
	ViewPager pager;


	@SuppressWarnings("deprecation")
	protected void onCreate(Bundle savedInstanceState) {  
		super.onCreate(savedInstanceState);  
		//setContentView(R.layout.activity_home);

		/*fm = getFragmentManager();  

		SharedPreferences preferences = this.getSharedPreferences("user_info", 0);
		userComapny = preferences.getString("userCompany", null);
		userCity = preferences.getString("userCity", null);


		if (fm.findFragmentById(android.R.id.content) == null) {  
			HomeFragment list = new HomeFragment(this,userComapny,userCity);  
			fm.beginTransaction().add(android.R.id.content, list).commit();  
		} */
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		ColorDrawable colorDrawable = new ColorDrawable(Color.WHITE);
		actionBar.setStackedBackgroundDrawable(colorDrawable);
		
		
		SharedPreferences preferences = this.getSharedPreferences("user_info", 0);
		userComapny = preferences.getString("userCompany", null);
		userCity = preferences.getString("userCity", null);
		userId = preferences.getString("userId", null);

		setContentView(R.layout.activity_home_new);

		hoov_in=(Button) findViewById(R.id.hoov_in);
		hoov_in.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(HomeActivityNew.this,HoovActivity.class);
				startActivity(intent); 

			} 

		});

		List<Fragment> fragments = getFragments();
		pageAdapter = new HomePageAdapter(getSupportFragmentManager(), fragments);
		pager = (ViewPager)findViewById(R.id.pager);
		pager.setAdapter(pageAdapter);

		ActionBar.TabListener tabListener = new ActionBar.TabListener() {
			public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
				pager.setCurrentItem(tab.getPosition());
			}

			public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
				// hide the given tab
			}

			public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
				// probably ignore this event
			}
		};

		pager.setOnPageChangeListener(
				new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						// When swiping between pages, select the
						// corresponding tab.
						getActionBar().setSelectedNavigationItem(position);
					}
				});
		
		actionBar.addTab(actionBar.newTab().setText("Newest Hoovs").setTabListener(tabListener));
		actionBar.addTab(actionBar.newTab().setText("Hottest Hoovs").setTabListener(tabListener));
		
	}

	private List<Fragment> getFragments(){
		List<Fragment> fList = new ArrayList<Fragment>();
		fList.add(HomeFragment.newInstance("Fragment 1",this,userComapny,userCity,userId));
		fList.add(HomeTopFragment.newInstance("Fragment 2",this,userComapny,userCity,userId)); 
		return fList;

	}

}




