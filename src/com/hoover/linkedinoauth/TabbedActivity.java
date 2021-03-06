package com.hoover.linkedinoauth;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

import com.hoover.util.HoovFetchParams.eType;
import com.hoover.util.VerticalViewPager;

public class TabbedActivity extends Fragment {
	public static final String TAG = TabbedActivity.class.getSimpleName();
	ViewPager mViewPager;
	HomePageAdapter pageAdapter;
	String userComapny;
	String userCity;
	String userId;
	//Button hoov_in;
	//Switch toggle;
	private MyRecyclerViewFragment homeF;
	private HomeFollowFragment followF;
	private MyHomeFragment myhomeF;

	public static TabbedActivity newInstance(String userComapny,String userCity,String userId) {
		TabbedActivity tabact=new TabbedActivity();
		tabact.userCity=userCity;
		tabact.userComapny=userComapny;
		tabact.userId=userId;
		return tabact;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}
	private List<Fragment> getFragments(){
		List<Fragment> fList = new ArrayList<Fragment>();
		homeF=MyRecyclerViewFragment.newInstance("Fragment 1",this.getActivity(),userComapny,userCity,userId);
		//myhomeF=MyHomeFragment.newInstance("Fragment 1",this.getActivity(),userComapny,userCity,userId);
		followF=HomeFollowFragment.newInstance("Fragment 2",this.getActivity(),userComapny,userCity,userId,eType.FOLLOW);

		//fList.add(myhomeF);
		fList.add(homeF);
		fList.add(followF);


		return fList;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.activity_tabbed_home, container, false);
		//hoov_in=(Button)v.findViewById(R.id.hoov_in);
		//toggle=(Switch)v.findViewById(R.id.hoov_toggle);
		/*hoov_in.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(),HoovActivity.class);
				startActivity(intent); 

			} 

		});*/
		/*toggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				if(isChecked){
					//homeF.refresh(true);
					toggle.setText("Hottest");
				}else{
					//homeF.refresh(false);
					toggle.setText("Newest");
				}

			}
		});

		if(toggle.isChecked()){
			toggle.setText("Hottest");
		}else{
			toggle.setText("Newest");
		}*/
		List<Fragment> fragments = getFragments();
		pageAdapter = new HomePageAdapter(getChildFragmentManager(), fragments,this.getActivity());
		mViewPager = (ViewPager) v.findViewById(R.id.pager);
		mViewPager.setAdapter(pageAdapter);
		mViewPager.setCurrentItem(0);
		return v;
	}
}
