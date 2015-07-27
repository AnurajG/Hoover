package com.hoover.linkedinoauth;


import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hoover.customviews.SaveHoovAsyncTask;
import com.hoover.util.Hoov;
import com.hoover.util.HoovQueryBuilder;

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
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button1:
			AttemptHoovSubmit tsk=new AttemptHoovSubmit();
			tsk.execute(hoovText.getText().toString());
		}

	}
	class AttemptHoovSubmit extends AsyncTask<String, String, Boolean> {

		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(HoovActivity.this);
			pDialog.setMessage("Attempting for Hoov...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}
		//String hoov = hoovText.getText().toString();
		@Override
		protected Boolean doInBackground(String... arg0) {
			Hoov h=new Hoov();
			h.id=userId;
			h.company=userCompany;
			h.city=userCity;
			h.hoov=arg0[0];

			HoovQueryBuilder qb = new HoovQueryBuilder();						

			HttpClient httpClient = new DefaultHttpClient();
			HttpPost request = new HttpPost(qb.buildContactsSaveURL());

			StringEntity params;
			try {
				params = new StringEntity(qb.createContact(h));
				request.addHeader("content-type", "application/json");
				request.setEntity(params);
				HttpResponse response = httpClient.execute(request);
				if(response.getStatusLine().getStatusCode()<205){
					return true;
				}
				else{
					return false;
				}

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;

		}

		protected void onPostExecute(Boolean message) {
			if(pDialog!=null && pDialog.isShowing()){
				pDialog.dismiss();
			}

			//this intent is used to open other activity wich contains another webView
			Intent intent = new Intent(HoovActivity.this,HomeActivity.class);
			startActivity(intent);
		}

	}
}
