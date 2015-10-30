package com.hoover.linkedinoauth;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class MyHomeFragment extends Fragment {
	TextView myhoov;
	TextView mycomments;
	TextView mynotifications;
	Context context;
	public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";

	public static final MyHomeFragment newInstance(String message,Context context, String comp, String cit,String id) {
		MyHomeFragment f = new MyHomeFragment(context);
		Bundle bdl = new Bundle(1);
		bdl.putString(EXTRA_MESSAGE, message);
		f.setArguments(bdl);
		return f;

	}
	public MyHomeFragment(Context context) {
		this.context = context;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
		View view;
		super.onCreate(savedInstanceState);
		view = inflater.inflate(R.layout.activity_myhome, container, false);

		myhoov = (TextView) view.findViewById(R.id.myhoov);
		mycomments = (TextView) view.findViewById(R.id.mycomment);
		mynotifications = (TextView) view.findViewById(R.id.mynotification);
		myhoov.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ViewPager vp=(ViewPager) getActivity().findViewById(R.id.profile_pager);
				vp.setCurrentItem(1,true);
			}
		});
		return view;


	}






}