package com.hoover.linkedinoauth;


import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.hoover.linkedinoauth.FirstActivity.GetUserAsyncTask;
import com.hoover.util.HoovChapter;
import com.hoover.util.User;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class HoovActivity extends Activity implements OnClickListener{
	private EditText hoovText;
	private Button hoov;
	private String userId;
	private String userCompany;
	private String userCity;
	private ProgressDialog pDialog;

	protected void onCreate(Bundle savedInstanceState) {       
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hoov); 

		SharedPreferences preferences = this.getSharedPreferences("user_info", 0);
		userCompany = preferences.getString("userCompany", null);
		userCity = preferences.getString("userCity", null);
		userId = preferences.getString("userId", null);

		hoovText = (EditText)findViewById(R.id.editText1);
		hoov = (Button)findViewById(R.id.button1);
		hoov.setOnClickListener(this);
	}

	public class SubmitHoovAsyncTask extends AsyncTask<String, Void, Boolean>{
		@Override
		protected void onPreExecute(){
			pDialog = ProgressDialog.show(HoovActivity.this, "", "Hooving..",true);
		}
		@Override
		protected Boolean doInBackground(String... params) {
			SharedPreferences preferences = HoovActivity.this.getSharedPreferences("hoov_tmp", 0);
			String hoovArray = preferences.getString("hoovArray", null);

			if(hoovArray==null){
				JSONArray array=new JSONArray();
				JSONObject jo = new JSONObject();
				try {
					jo.put("hoovText",params[0]);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				array.put(jo);

				SharedPreferences.Editor editor = preferences.edit();
				editor.putString("hoovArray", array.toString());
				editor.commit();

			}else{
				try {
					JSONArray array=new JSONArray(hoovArray);
					JSONObject jo = new JSONObject();
					jo.put("hoovText",params[0]);
					array.put(jo);

					SharedPreferences.Editor editor = preferences.edit();
					editor.putString("hoovArray", array.toString());
					editor.commit();
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
			Intent mServiceIntent = new Intent(HoovActivity.this, SaveOfflineHoovService.class);
			//mServiceIntent.putExtra("hoovText", hoovText.getText().toString());
			getApplicationContext().startService(mServiceIntent);

			Intent intent = new Intent(HoovActivity.this,HomeActivity.class);
			startActivity(intent); 
			return null;
		}
		@Override
		protected void onPostExecute(Boolean b){
			if(pDialog!=null && pDialog.isShowing()){
				pDialog.dismiss();
			}
			super.onPostExecute(b);
		}

	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button1:
			SubmitHoovAsyncTask tsk= new SubmitHoovAsyncTask();
			tsk.execute(hoovText.getText().toString());
		}

	}

}
