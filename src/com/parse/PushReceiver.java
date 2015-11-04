package com.parse;

import java.sql.Timestamp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.hoover.linkedinoauth.FirstActivity;
import com.hoover.linkedinoauth.HoovDetailsByIdActivity;

public class PushReceiver extends ParsePushBroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals("com.parse.push.intent.ORDERED_RECEIVE"))
			super.onPushReceive(context, intent);
		else if(intent.getAction().equals("com.parse.push.intent.OPEN"))
			onPushOpen(context, intent);
		else if(intent.getAction().equals("com.parse.push.intent.DELETE"))
			onPushDismiss(context, intent);
	}

	@Override
	public void onPushDismiss(Context context, Intent intent) {
		super.onPushDismiss(context, intent);
		SharedPreferences preferences = context.getSharedPreferences("hoov_tmp", 0);
		SharedPreferences.Editor editor = preferences.edit();
		String hoovArray = preferences.getString("notifyArray", null);
		//Intent i = new Intent(context, HoovDetailsByIdActivity.class);
		Bundle extras=intent.getExtras();
		java.util.Date date= new java.util.Date();
		if(hoovArray==null){
			JSONArray array=new JSONArray();
			JSONObject jo = new JSONObject();
			try {
				jo.put("date", new Timestamp(date.getTime()).toString());
				jo.put("notifyText",new JSONObject(extras.getString("com.parse.Data")));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			array.put(jo);
			editor.putString("notifyArray", array.toString());
			editor.commit();

		}
	}
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