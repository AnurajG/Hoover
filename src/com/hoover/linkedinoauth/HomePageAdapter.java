package com.hoover.linkedinoauth;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class HomePageAdapter extends FragmentPagerAdapter {

	private List<Fragment> fragments;

	public HomePageAdapter(FragmentManager fm, List<Fragment> fragments) {
		super(fm);
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

}	
