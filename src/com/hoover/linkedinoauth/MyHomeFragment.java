package com.hoover.linkedinoauth;

import org.json.JSONArray;
import org.json.JSONException;

import com.hoover.util.SlideClickListener;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

public class MyHomeFragment extends Fragment {
	TextView myhoov;
	TextView mycomments;
	TextView mynotifications;
	TextView notifications;
	ImageButton buttonUp;
	Context context;
	//NonSwipeableViewPager vp;

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
		//vp=(NonSwipeableViewPager) getActivity().findViewById(R.id.profile_pager);
		view = inflater.inflate(R.layout.activity_myhome, container, false);

		myhoov = (TextView) view.findViewById(R.id.myhoov);
		mycomments = (TextView) view.findViewById(R.id.mycomment);
		mynotifications = (TextView) view.findViewById(R.id.mynotification);
		notifications = (TextView) view.findViewById(R.id.notify_number);
		buttonUp = (ImageButton) view.findViewById(R.id.buttonUp);
		notifications.setText("1");

		SharedPreferences preferences2 = context.getSharedPreferences("hoov_tmp", 0);
		String notifyArray = preferences2.getString("notifyArray", null);
		//vp.setPagingEnabled(false);

		/*vp.setOnTouchListener(new View.OnTouchListener() {

		    @Override
		     public boolean onTouch(View v, MotionEvent event)
		     { 
		        return true; }
		     });*/

		if(notifyArray!=null){
			try {
				JSONArray array=new JSONArray(notifyArray);
				if(array.length()>0){
					notifications.setText(""+array.length());
				}else{
					notifications.setVisibility(View.GONE);
				}
			}catch(JSONException je){
			}
		}else{
			notifications.setVisibility(View.GONE);
		}

		myhoov.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				( (SlideClickListener) getActivity()).onChangeBookmark(1);
			}
		});

		mynotifications.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				( (SlideClickListener) getActivity()).onChangeBookmark(3);
				System.out.println("");
			}
		});


		buttonUp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context,HomeActivityNew.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				//intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(intent); 
				//context.overridePendingTransition(0 , R.anim.slide_down);
			}
		});


		return view;


	}






}