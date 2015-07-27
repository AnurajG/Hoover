package com.hoover.linkedinoauth;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;
//import org.json.simple.parser.JSONParser;

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

public class FirstActivity extends Activity {
	Button sign_in;
	private ProgressDialog pd;
	public void getUser(String id){
		try{
			UserQueryBuilder qb = new UserQueryBuilder();						

			//URL url = new URL(qb.buildContactsSaveURL()+"&q={\"document.deviceId\":\""+id+"\"}&f={\"document.id\":1,\"document.city\":1}");
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
		sign_in=(Button) findViewById(R.id.btn_sign);
		sign_in.setOnClickListener(new OnClickListener() {                       
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				TelephonyManager tManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
				String uuid = tManager.getDeviceId();

				GetUserAsyncTask tsk= new GetUserAsyncTask();
				tsk.execute(uuid);
			}           
		});  


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
					user.id=doc.getString("id");
					user.company=doc.getString("company");
					user.city=doc.getString("city");
					user.deviceId=doc.getString("deviceId");
				}
				conn.disconnect();
			} catch (Exception e) {
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
				//this intent is used to open other activity wich contains another webView
				Intent intent = new Intent(FirstActivity.this,MainActivity.class);
				startActivity(intent); 
			}else{
				SharedPreferences preferences = FirstActivity.this.getSharedPreferences("user_info", 0);
				SharedPreferences.Editor editor = preferences.edit();
				editor.putString("userId", data.id);
				editor.putString("userCompany", data.company);
				editor.putString("userCity", data.city);
				editor.putString("userDeviceId", data.deviceId);
				editor.commit();

				//this intent is used to open other activity wich contains another webView
				Intent intent = new Intent(FirstActivity.this,HomeActivity.class);
				startActivity(intent); 
			}


		}

	}
	//	private void openBrowser1() 
	//	{       
	//		//this intent is used to open other activity wich contains another webView
	//		Intent intent = new Intent(FirstActivity.this,MainActivity.class);
	//		startActivity(intent); 
	//	}
}
