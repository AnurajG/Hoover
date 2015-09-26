package com.hoover.linkedinoauth;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hoover.util.HoovChapter;
import com.hoover.util.UserQueryBuilder;

public class HoovDetailsByIdActivity extends Activity{

	private String mongoHoovId;
	ProgressBar mProgressBar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_busy);
		mProgressBar=(ProgressBar)findViewById(R.id.b_progressbar);
		mProgressBar.setBackgroundResource(R.drawable.anim_progress);
		final AnimationDrawable mailAnimation = (AnimationDrawable) mProgressBar.getBackground();
		mProgressBar.post(new Runnable() {
			public void run() {
				if ( mailAnimation != null ) mailAnimation.start();
			}
		});
		Intent intent = getIntent();
		mongoHoovId=intent.getStringExtra("hoovId");
		GetHoovChapterByIdAsyncTask tsk = new GetHoovChapterByIdAsyncTask();
		tsk.execute(mongoHoovId);
	}

	public class GetHoovChapterByIdAsyncTask extends AsyncTask<String, Integer, HoovChapter>{
		@Override
		protected void onPreExecute(){
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			mProgressBar.setProgress(values[0]);
		}

		@Override
		protected HoovChapter doInBackground(String... hoovid) {
			HoovChapter hc = new HoovChapter();
			try{
				String id = hoovid[0];

				URL url = new URL("https://api.mongolab.com/api/1/databases/hoover/collections/hoov/"+id+"?apiKey=zvbjTNUW6COSTIZxJcPIW7_tniVCnDKC&q");
				//URL url = new URL("https://api.mongolab.com/api/1/databases/hoover_user/collections/user?apiKey=zvbjTNUW6COSTIZxJcPIW7_tniVCnDKC&q={\"document.deviceId\":\"000000000000000\"}&f={\"document.id\":1,\"document.city\":1}");
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Accept", "application/json");

				if (conn.getResponseCode() != 200) {
					throw new RuntimeException("Failed : HTTP error code : "
							+ conn.getResponseCode());
				}

				BufferedReader br = new BufferedReader(new InputStreamReader(
						(conn.getInputStream())));

				String output;
				publishProgress(90);
				while ((output = br.readLine()) != null) {
					JSONObject r= new JSONObject(output);
					hc.hoovText=r.getJSONObject("document").getString("hoov");
					hc.hoovUserId=r.getJSONObject("document").getString("id");
					hc.mongoHoovId=id;
					hc.hoov_up_ids=new Gson().fromJson(r.getJSONObject("document").getJSONArray("hoovUpIds").toString(), new TypeToken<List<String>>(){}.getType());
					hc.hoov_down_ids = new Gson().fromJson(r.getJSONObject("document").getJSONArray("hoovDownIds").toString(), new TypeToken<List<String>>(){}.getType());
					hc.commentHoovIds = new Gson().fromJson(r.getJSONObject("document").getJSONArray("commentHoovIds").toString(), new TypeToken<List<String>>(){}.getType());
					long tmp = new BigInteger(hc.mongoHoovId.substring(0, 8), 16).longValue();
					Long epoch=tmp;
					Long curr_epoch = System.currentTimeMillis()/1000;
					if(curr_epoch-epoch > 86400 )
						hc.hoovDate=""+(curr_epoch-epoch)/86400L+"d";
					else if(curr_epoch-epoch > 3600)
						hc.hoovDate=""+(curr_epoch-epoch)/3600+"h";
					else if(curr_epoch-epoch > 60)
						hc.hoovDate=""+(curr_epoch-epoch)/60+"m";
					else
						hc.hoovDate=""+(curr_epoch-epoch+60)+"s";
				}

				publishProgress(100);
				conn.disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			return hc;
		}
		@Override
		protected void onPostExecute(HoovChapter data){
			mProgressBar.setVisibility(View.GONE);
			Intent intent = new Intent(HoovDetailsByIdActivity.this,HoovDetailsActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			intent.putExtra("chapter", data);
			startActivity(intent); 
		}
	}
}
