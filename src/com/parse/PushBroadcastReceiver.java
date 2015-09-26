package com.parse;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class PushBroadcastReceiver extends ParsePushBroadcastReceiver {
	@Override
	protected void onPushReceive(Context context, Intent intent) {
		Intent i = new Intent("com.parse.push.intent.ORDERED_RECEIVE");
		//intent.getExtras();
		Bundle b=intent.getExtras();
		String jsonData = b.getString("com.parse.Data");
		JSONObject json;
		try {
			json = new JSONObject(jsonData);
			json.put("action", "com.parse.push.intent.ORDERED_RECEIVE");
			b.putString("com.parse.Data", json.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		i.putExtras(intent);
		context.sendOrderedBroadcast(i, null);
	}

	
}