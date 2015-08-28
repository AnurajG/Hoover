package com.hoover.linkedinoauth;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.hoover.util.HoovInsertParams;

public class HoovActivity extends Activity implements OnClickListener{
	private EditText hoovText;
	private Button hoov;
	private String userId;
	private String userCompany;
	private String userCity;
	private ProgressDialog pDialog;
	private String parentId;

	protected void onCreate(Bundle savedInstanceState) {       
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hoov); 

		SharedPreferences preferences = this.getSharedPreferences("user_info", 0);
		Intent intent = getIntent();
		parentId=intent.getStringExtra("mongodbParentId");
		userCompany = preferences.getString("userCompany", null);
		userCity = preferences.getString("userCity", null);
		userId = preferences.getString("userId", null);

		hoovText = (EditText)findViewById(R.id.editText1);
		hoov = (Button)findViewById(R.id.button1);
		hoov.setOnClickListener(this);
	}

	public class SubmitHoovAsyncTask extends AsyncTask<HoovInsertParams, Void, Boolean>{
		@Override
		protected void onPreExecute(){
			pDialog = ProgressDialog.show(HoovActivity.this, "", "Hooving..",true);
		}
		@Override
		protected Boolean doInBackground(HoovInsertParams... params) {
			SharedPreferences preferences = HoovActivity.this.getSharedPreferences("hoov_tmp", 0);
			String hoovArray = preferences.getString("hoovArray", null);

			if(hoovArray==null){
				JSONArray array=new JSONArray();
				JSONObject jo = new JSONObject();
				try {
					jo.put("parentId", params[0].parentId);
					jo.put("hoovText",params[0].text);
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
					jo.put("parentId", params[0].parentId);
					jo.put("hoovText",params[0].text);
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

			Intent intent = new Intent(HoovActivity.this,HomeFragment.class);
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
		InputMethodManager inputMethodManager = (InputMethodManager)  this.getSystemService(Activity.INPUT_METHOD_SERVICE);
	    inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
		switch (v.getId()) {
		case R.id.button1:
			SubmitHoovAsyncTask tsk= new SubmitHoovAsyncTask();
			HoovInsertParams p = new HoovInsertParams();
			p.text=hoovText.getText().toString();
			p.parentId=parentId;
			tsk.execute(p);
		}

	}

}
