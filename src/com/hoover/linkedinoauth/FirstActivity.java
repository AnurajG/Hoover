package com.hoover.linkedinoauth;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.hoover.util.User;
import com.hoover.util.UserQueryBuilder;
import com.parse.ParseInstallation;
//import org.json.simple.parser.JSONParser;

public class FirstActivity extends Activity {
	private ProgressDialog pd;
	private String userCompany;
	private String userCity;
	private String userId;
	Button signIn;
	
	public void getUser(String id){
		try{
			UserQueryBuilder qb = new UserQueryBuilder();						

			URL url = new URL("https://api.mongolab.com/api/1/databases/hoover_user/collections/user?apiKey=zvbjTNUW6COSTIZxJcPIW7_tniVCnDKC&q={\"document.deviceId\":\"000000000000000\"}&f={\"document.id\":1,\"document.city\":1}");
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
			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				System.out.println(output);
			}

			conn.disconnect();
		} catch (Exception e) {
			String val = e.getMessage();
			//return null;
		}
		//return true;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_first);
		TelephonyManager tManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		String uuid = tManager.getDeviceId();

		SharedPreferences preferences = this.getSharedPreferences("user_info", 0);
		userCompany = preferences.getString("userCompany", null);
		userCity = preferences.getString("userCity", null);
		userId = preferences.getString("userId", null);
		signIn = (Button) findViewById(R.id.signin);
		signIn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(FirstActivity.this,MainActivity.class);
				startActivity(intent); 
			}
		});
		Bundle b=getIntent().getExtras();
		if(b!=null && b.containsKey("SignIn") && b.getBoolean("SignIn")){
			System.out.println("sign in enable");
			signIn.setVisibility(View.VISIBLE);
		}else if(userId==null || userCity == null || userCompany == null){
			GetUserAsyncTask tsk= new GetUserAsyncTask();
			tsk.execute(uuid);
		}else{
			Intent mServiceIntent = new Intent(FirstActivity.this, SaveOfflineHoovService.class);
			//mServiceIntent.putExtra("hoovText", hoovText.getText().toString());
			getApplicationContext().startService(mServiceIntent);

			Intent intent = new Intent(FirstActivity.this,HomeActivityNew.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(intent); 
		}




	}

	public class GetUserAsyncTask extends AsyncTask<String, Void, User> {
		@Override
		protected void onPreExecute(){
			pd = ProgressDialog.show(FirstActivity.this, "", "Logging in..",true);
		}


		@Override
		protected User doInBackground(String... params) {
			User user=new User();
			try 
			{			
				String u = params[0];

				UserQueryBuilder qb = new UserQueryBuilder();						

				URL url = new URL(qb.buildContactsSaveURL()+"&q={\"document.deviceId\":\""+u+"\"}");
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
				System.out.println("Output from Server .... \n");
				while ((output = br.readLine()) != null) {
					//String split=output.split(',');
					//JSONObject obj = new JSONObject(output);

					JSONArray array=new JSONArray(output);
					JSONObject obj = (JSONObject)array.get(0);
					JSONObject doc = obj.getJSONObject("document");
					JSONObject _id = obj.getJSONObject("_id");
					user.mongoId=_id.getString("$oid");
					user.id=doc.getString("id");
					user.company=doc.getString("company");
					user.city=doc.getString("city");
					user.deviceId=doc.getString("deviceId");
				}
				conn.disconnect();
			} catch (JSONException e) {
				return null;
			}catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			return user;	
		}

		@Override
		protected void onPostExecute(User data){
			if(pd!=null && pd.isShowing()){
				pd.dismiss();
			}
			if(data==null){
				signIn.setVisibility(View.VISIBLE);
				//this intent is used to open other activity wich contains another webView
				/*Intent intent = new Intent(FirstActivity.this,MainActivity.class);
				startActivity(intent); */
			}else{
				SharedPreferences preferences = FirstActivity.this.getSharedPreferences("user_info", 0);
				SharedPreferences.Editor editor = preferences.edit();
				editor.putString("userMongoId", data.mongoId);
				editor.putString("userId", data.id);
				editor.putString("userCompany", data.company);
				editor.putString("userCity", data.city);
				editor.putString("userDeviceId", data.deviceId);
				editor.commit();

				ParseInstallation installation = ParseInstallation.getCurrentInstallation();
				installation.put("userMongoId",data.mongoId);
				installation.put("userId",data.id);
				installation.put("userCompany",data.company);
				installation.put("userCity",data.city);
				installation.put("userDeviceId",data.deviceId);

				installation.saveInBackground();


				//this intent is used to open other activity wich contains another webView
				Intent intent = new Intent(FirstActivity.this,HomeActivityNew.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(intent); 
			}


		}

	}
	@Override
	public void onBackPressed() {
	    // Do Here what ever you want do on back press;
	}
	//	private void openBrowser1() 
	//	{       
	//		//this intent is used to open other activity wich contains another webView
	//		Intent intent = new Intent(FirstActivity.this,MainActivity.class);
	//		startActivity(intent); 
	//	}
}
