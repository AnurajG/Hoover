package com.hoover.linkedinoauth;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
				Intent intent = new Intent(context,MyHoovActivity.class);
				startActivity(intent); 
			}
		});
		return view;


	}






}