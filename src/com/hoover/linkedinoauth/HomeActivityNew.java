package com.hoover.linkedinoauth;





import java.util.ArrayList;
import java.util.List;

import com.hoover.util.HoovFetchParams;
import com.hoover.util.NavigationDrawerItem;
import com.hoover.util.NavigationDrawerListAdapter;
import com.tjeannin.apprate.AppRate;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class HomeActivityNew extends FragmentActivity{
	FragmentManager fm;
	String userComapny;
	String userCity;
	String userId;
	HomePageAdapter pageAdapter;
	SlidingTabLayout tabs;
	CharSequence Titles[]={"Newest","Hottest"};
	ViewPager pager;
	ProgressDialog mProgressDialog;
	AppRate appRate;
	
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	@SuppressWarnings("deprecation")
	private ActionBarDrawerToggle mDrawerToggle;

	// nav drawer title
	private CharSequence mDrawerTitle;

	// used to store app title
	private CharSequence mTitle;

	// slide menu items
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;

	private ArrayList<NavigationDrawerItem> navDrawerItems;
	private NavigationDrawerListAdapter adapter;
	@SuppressWarnings("deprecation")
	protected void onCreate(Bundle savedInstanceState) {  
		super.onCreate(savedInstanceState);  
		final ActionBar actionBar = getActionBar();
		ColorDrawable colorDrawable = new ColorDrawable(Color.WHITE);
		actionBar.setStackedBackgroundDrawable(colorDrawable);
		
		
		SharedPreferences preferences = this.getSharedPreferences("user_info", 0);
		userComapny = preferences.getString("userCompany", null);
		userCity = preferences.getString("userCity", null);
		userId = preferences.getString("userId", null);

		setContentView(R.layout.activity_home_new);

		
		mTitle = mDrawerTitle = getTitle();

		// load slide menu items
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

		// nav drawer icons from resources
		navMenuIcons = getResources()
				.obtainTypedArray(R.array.nav_drawer_icons);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

		navDrawerItems = new ArrayList<NavigationDrawerItem>();

		// adding nav drawer items to array
		// Home
		navDrawerItems.add(new NavigationDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
		// Settings 
		navDrawerItems.add(new NavigationDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
		// Delete Profile
		navDrawerItems.add(new NavigationDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
		/*// Communities, Will add a counter here
		navDrawerItems.add(new NavigationDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1), true, "22"));*/
		// Rate Us
		navDrawerItems.add(new NavigationDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));
		// Invite
		navDrawerItems.add(new NavigationDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
			

		// Recycle the typed array
		navMenuIcons.recycle();
		// set a custom shadow that overlays the main content when the drawer oepns
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,  GravityCompat.START);
		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

		// setting the nav drawer list adapter
		adapter = new NavigationDrawerListAdapter(getApplicationContext(),
				navDrawerItems);
		mDrawerList.setAdapter(adapter);
		//enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, //nav menu toggle icon
				R.string.app_name, // nav drawer open - description for accessibility
				R.string.app_name // nav drawer close - description for accessibility
		) {
			
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				mDrawerList.bringToFront();
				mDrawerLayout.requestLayout();
				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
			}

			
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		if (savedInstanceState == null) {
			// on first time display view for first nav item
			displayView(0);
		}

		
		
	}
	/**
	 * Diplaying fragment view for selected nav drawer list item
	 * */
	private void displayView(int position) {
		// update the main content by replacing fragments
		Fragment fragment 	 = null;
		ListFragment listfragment = null;
		switch (position) {
		case 0:
			//HOME
			getSupportFragmentManager()
			.beginTransaction()
			.replace(R.id.frame_container, TabbedActivity.newInstance(userComapny,userCity,userId), TabbedActivity.TAG).commit();
			mDrawerList.setItemChecked(position, true);
			mDrawerList.setSelection(position);
			setTitle(navMenuTitles[position]);
			mDrawerLayout.closeDrawer(mDrawerList);
			break;
		case 1:
			//SETTINGS
		case 2:
			//DELETE PROFILE
			final String uid=this.userId;
			new AlertDialog.Builder(this)
	        .setTitle("Delete entry")
	        .setMessage("Are you sure you want to delete this entry?")
	        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) { 
	            	getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.frame_container, DeleteProfileFragment.newInstance(uid), DeleteProfileFragment.TAG).commit();
	            	finish();
	            }
	         })
	        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) { 
	            	System.out.println("NO");
	            	mDrawerList.setItemChecked(0, true);
	    			mDrawerList.setSelection(0);
	    			mDrawerLayout.closeDrawer(mDrawerList);
	            }
	         })
	        .setIcon(android.R.drawable.ic_dialog_alert)
	         .show();
			
           
			break;
		case 3:
			//RATE US
			mDrawerLayout.closeDrawer(mDrawerList);
			mDrawerList.setItemChecked(0, true);
			mDrawerList.setSelection(0);
			
			if(appRate==null){
				appRate = new AppRate(this);
				appRate.init();
			}else{
				AppRate.reset(this);
				appRate.init();
			}
			break;
		case 4:
			//Invite Freinds
			mDrawerLayout.closeDrawer(mDrawerList);
			Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
			sharingIntent.setType("text/plain");
			String shareBody = "Ye mera app hai...hohohohohhahahah";
			sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
			sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
			
			startActivity(Intent.createChooser(sharingIntent, "Share via"));
			mDrawerList.setItemChecked(0, true);
			mDrawerList.setSelection(0);
			break;
		default:
			
			break;
		}

	}
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action bar actions click
		switch (item.getItemId()) {
		case R.id.action_settings:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	

	/**
	 * Slide menu item click listener
	 * */
	private class SlideMenuClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// display view for selected nav drawer item
			displayView(position);
		}
	}
	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}
	private List<Fragment> getFragments(){
		List<Fragment> fList = new ArrayList<Fragment>();
		fList.add(HomeFragment.newInstance("Fragment 1",this,userComapny,userCity,userId));
		fList.add(HomeTopFragment.newInstance("Fragment 2",this,userComapny,userCity,userId)); 
		return fList;

	}
	
	

}




