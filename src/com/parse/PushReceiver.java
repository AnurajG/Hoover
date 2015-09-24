package com.parse;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.hoover.linkedinoauth.FirstActivity;
import com.hoover.linkedinoauth.HomeActivityNew;
import com.hoover.linkedinoauth.HoovDetailsByIdActivity;
import com.parse.ParseAnalytics;
import com.parse.ParsePushBroadcastReceiver;

public class PushReceiver extends ParsePushBroadcastReceiver {

	@Override
	public void onPushOpen(Context context, Intent intent) {

		//To track "App Opens"
		ParseAnalytics.trackAppOpenedInBackground(intent);
		//Here is data you sent
		Intent i = new Intent(context, HoovDetailsByIdActivity.class);
		Bundle extras=intent.getExtras();
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		if (extras != null) {
			String jsonData = extras.getString("com.parse.Data");
			try {
				JSONObject json;
				json = new JSONObject(jsonData);
				String id;
				if(!json.has("hoovId")){
					Intent def = new Intent(context, FirstActivity.class);
					def.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(def);
					return;
				}
				id = json.getString("hoovId");
				i.putExtra("hoovId", id);
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}else{
			Intent def = new Intent(context, FirstActivity.class);
			def.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(def);

		}
		context.startActivity(i);
	}
}