package com.hoover.linkedinoauth;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class GetProfileAsyncTask extends AsyncTask<Void, Void, JSONObject> {
    String url;
    public GetProfileAsyncTask(String url) {
		super();
		this.url = url;
	}
	@Override
	protected JSONObject doInBackground(Void... params) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url);
		httpget.setHeader("x-li-format", "json");
		try{
			HttpResponse response = httpClient.execute(httpget);
				if(response!=null){
					//If status is OK 200
					if(response.getStatusLine().getStatusCode()==200){
						String result = EntityUtils.toString(response.getEntity());
						//Convert the string result to a JSON Object
						JSONObject basicdata;
						basicdata= new JSONObject(result);
						return basicdata;
					}
				}
			}catch(IOException e){
				Log.e("Authorize","Error Http response "+e.getLocalizedMessage());	
			} catch (JSONException e) {
				Log.e("Authorize","Error Http response "+e.getLocalizedMessage());	
			}
		
		return null;
	}

}
