package com.hoover.linkedinoauth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.goebl.david.WebbException;
import com.hoover.util.HoovChapter;
import com.hoover.util.User;
import com.hoover.util.UserQueryBuilder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;

public class UpdateUserCompanyAsyncTask extends AsyncTask<Void, Void,Void>{
	private final Context mContext;
	private final User u;
	public UpdateUserCompanyAsyncTask(Context mContext, User u) {
		super();
		this.mContext = mContext;
		this.u=u;
	}

	@Override
	protected Void doInBackground(Void... arg0) {

		try 
		{			
			UserQueryBuilder qb = new UserQueryBuilder();						

			HttpClient httpClient = new DefaultHttpClient();
			HttpPost request = new HttpPost(qb.buildContactsSaveURL());

			StringEntity params =new StringEntity(qb.createContact(u));
			request.addHeader("content-type", "application/json");
			request.setEntity(params);
			HttpResponse response = httpClient.execute(request);

			
		} catch (Exception e) {
			//e.getCause();
			String val = e.getMessage();
			return null;
		}		
	
		return null;
		
	}
	
	@Override
	protected void onPostExecute(Void data){
		AlertDialog infoDailog = new AlertDialog.Builder(mContext).create();
		infoDailog.setTitle("Info");
		infoDailog.setMessage("You have successfully switched");
		infoDailog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Intent startProfileActivity = new Intent(mContext, HomeActivityNew.class);
				mContext.startActivity(startProfileActivity);
			}
		});
		infoDailog.show();
		
		
	}

}
