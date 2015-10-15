package com.hoover.linkedinoauth;

import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class HomePageAdapter extends FragmentPagerAdapter {

	private List<Fragment> fragments;
	private Context mcontext;

	public HomePageAdapter(FragmentManager fm, List<Fragment> fragments,Context c) {
		super(fm);
		this.mcontext=c;
		this.fragments=fragments;
	}

	@Override
	public android.support.v4.app.Fragment getItem(int position) {
		return this.fragments.get(position);
	}

	@Override
	public int getCount() {
		return this.fragments.size();

	}
	@Override
    public CharSequence getPageTitle (int position) {
		Locale l = Locale.getDefault();
		switch (position) {
		case 0:
			return mcontext.getString(R.string.tab1_title).toUpperCase(l);
		case 1:
			return mcontext.getString(R.string.tab2_title).toUpperCase(l);
		case 2:
			return mcontext.getString(R.string.tab3_title).toUpperCase(l);
	
		}
		return null;
    }

}	
