package com.hoover.linkedinoauth;


import java.util.List;
import java.util.Vector;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

public class ProfileHomeActivity extends FragmentActivity {
	public static final String TAG = TabbedActivity.class.getSimpleName();
	ViewPager mViewPager;
	FragmentManager fm;
	private String userComapny;
	private String userCity;
	private String userId;
	private Context context;

	private static final int NUM_PAGES = 1;
	private PagerAdapter mPagerAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile_home);

		// Instantiate a ViewPager and a PagerAdapter.
		//mViewPager = (ViewPager) findViewById(R.id.profile_pager);
		//mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
		//mViewPager.setAdapter(mPagerAdapter);



		SharedPreferences preferences = this.getSharedPreferences("user_info", 0);
		userComapny = preferences.getString("userCompany", null);
		userCity = preferences.getString("userCity", null);
		userId = preferences.getString("userId", null);

		context=this;
		initialisePaging();


	}

	private void initialisePaging() {

		List<Fragment> fragments = new Vector<Fragment>();
		fragments.add(MyHomeFragment.newInstance(TAG+"1",context,userComapny,userCity,userId ));
		fragments.add(MyHoovFragment.newInstance(TAG+"1",context,userComapny,userCity,userId ));
		this.mPagerAdapter  = new ScreenSlidePagerAdapter(super.getSupportFragmentManager(), fragments);
		//
		ViewPager pager = (ViewPager)super.findViewById(R.id.profile_pager);
		pager.setAdapter(this.mPagerAdapter);
	}



	private class ScreenSlidePagerAdapter extends FragmentPagerAdapter { 
		private List<Fragment> fragments;
		/**
		 * @param fm
		 * @param fragments
		 */
		public ScreenSlidePagerAdapter(FragmentManager fm, List<Fragment> fragments) {
			super(fm);
			this.fragments = fragments;
		}
		/* (non-Javadoc)
		 * @see android.support.v4.app.FragmentPagerAdapter#getItem(int)
		 */
		@Override
		public Fragment getItem(int position) {
			return this.fragments.get(position);
		}

		/* (non-Javadoc)
		 * @see android.support.v4.view.PagerAdapter#getCount()
		 */
		@Override
		public int getCount() {
			return this.fragments.size();
		}
	}



}