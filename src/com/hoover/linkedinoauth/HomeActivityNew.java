package com.hoover.linkedinoauth;




import java.util.ArrayList;
import java.util.List;

import com.hoover.util.NavigationDrawerItem;
import com.hoover.util.NavigationDrawerListAdapter;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

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
	Button hoov_in;
	SlidingTabLayout tabs;
	CharSequence Titles[]={"Newest","Hottest"};
	ViewPager pager;
	
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
		// Find People
		navDrawerItems.add(new NavigationDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
		// Photos
		navDrawerItems.add(new NavigationDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
		// Communities, Will add a counter here
		navDrawerItems.add(new NavigationDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1), true, "22"));
		// Pages
		navDrawerItems.add(new NavigationDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
		// What's hot, We  will add a counter here
		navDrawerItems.add(new NavigationDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1), true, "50+"));
		

		// Recycle the typed array
		navMenuIcons.recycle();

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
				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
			}

			
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			// on first time display view for first nav item
			displayView(-1);
		}

		
		
	}
	/**
	 * Diplaying fragment view for selected nav drawer list item
	 * */
	private void displayView(int position) {
		// update the main content by replacing fragments
		Fragment fragment = null;
		switch (position) {
		case 0:
			Toast.makeText(getApplicationContext(), "0...", Toast.LENGTH_LONG).show();
			break;
		case 1:
			Toast.makeText(getApplicationContext(), "1...", Toast.LENGTH_LONG).show();
			break;
		case 2:
			Toast.makeText(getApplicationContext(), "2...", Toast.LENGTH_LONG).show();
			break;
		case 3:
			Toast.makeText(getApplicationContext(), "3...", Toast.LENGTH_LONG).show();
			break;
		case 4:
			Toast.makeText(getApplicationContext(), "4...", Toast.LENGTH_LONG).show();
			break;
		case 5:
			Toast.makeText(getApplicationContext(), "5....", Toast.LENGTH_LONG).show();
			break;

		default:
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
			
			getActionBar().addTab(getActionBar().newTab().setText("Newest Hoovs").setTabListener(tabListener));
			getActionBar().addTab(getActionBar().newTab().setText("Hottest Hoovs").setTabListener(tabListener));
			break;
		}

		/*if (fragment != null) {
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.frame_container, fragment).commit();
// update selected item and title, then close the drawer
			mDrawerList.setItemChecked(position, true);
			mDrawerList.setSelection(position);
			setTitle(navMenuTitles[position]);
			mDrawerLayout.closeDrawer(mDrawerList);
		} else {
			// error in creating fragment
			Log.e("MainActivity", "Error in creating fragment");
		}*/
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
			//displayView(position);
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




