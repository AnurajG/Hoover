package com.hoover.linkedinoauth;


import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
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
import android.util.Log;
import android.widget.TextView;

import com.hoover.util.User;
import com.hoover.util.UserQueryBuilder;
public class Login extends Activity {
	//private static final String PROFILE_URL = "https://api.linkedin.com/v1/people/~";
	private static final String PROFILE_URL = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name,headline,location)";
	private static final String OAUTH_ACCESS_TOKEN_PARAM ="oauth2_access_token";
	private static final String QUESTION_MARK = "?";
	private static final String EQUALS = "=";

	private TextView welcomeText;
	private ProgressDialog pd;
	private String token;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		welcomeText = (TextView) findViewById(R.id.activity_profile_welcome_text);

		//Request basic profile of the user
		SharedPreferences preferences = this.getSharedPreferences("user_info", 0);
		String accessToken = preferences.getString("accessToken", null);
		token=accessToken;
		if(accessToken!=null){
			String profileUrl = getProfileUrl(accessToken);
			new GetProfileRequestAsyncTask().execute(profileUrl);
		}
	}

	private static final String getProfileUrl(String accessToken){
		return PROFILE_URL
				+QUESTION_MARK
				+OAUTH_ACCESS_TOKEN_PARAM+EQUALS+accessToken;
	}

	public class SaveUserAsyncTask extends AsyncTask<User, Void, String>{

		@Override
		protected String doInBackground(User... arg0) {
			try 
			{			
				User u = arg0[0];

				UserQueryBuilder qb = new UserQueryBuilder();						

				HttpClient httpClient = new DefaultHttpClient();
				HttpPost request = new HttpPost(qb.buildContactsSaveURL());

				StringEntity params =new StringEntity(qb.createContact(u));
				request.addHeader("content-type", "application/json");
				request.setEntity(params);
				HttpResponse response = httpClient.execute(request);

				if(response.getStatusLine().getStatusCode()<205){
					String jsonString = EntityUtils.toString(response.getEntity());
					return jsonString;
				}
				else{
					return null;
				}
			} catch (Exception e) {
				//e.getCause();
				String val = e.getMessage();
				return null;
			}		
		}
		@Override
		protected void onPostExecute(String output){
			if(output!=null){


				try {
					JSONObject obj = new JSONObject(output);
					JSONObject doc = obj.getJSONObject("document");
					JSONObject _id = obj.getJSONObject("_id");
					SharedPreferences preferences = Login.this.getSharedPreferences("user_info", 0);
					SharedPreferences.Editor editor = preferences.edit();
					editor.putString("userMongoId", _id.getString("$oid"));
					editor.putString("userId", doc.getString("id"));
					editor.putString("userCompany", doc.getString("company"));
					editor.putString("userCity", doc.getString("city"));
					editor.putString("userDeviceId", doc.getString("deviceId"));
					editor.commit();

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}



			}
		}
	}

	private class GetProfileRequestAsyncTask extends AsyncTask<String, Void, JSONObject>{

		@Override
		protected void onPreExecute(){
			pd = ProgressDialog.show(Login.this, "", "Loading..",true);
		}

		@Override
		protected JSONObject doInBackground(String... urls) {
			if(urls.length>0){
				String url = urls[0];
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
			}
			return null;
		}

		@Override
		protected void onPostExecute(JSONObject data){
			if(pd!=null && pd.isShowing()){
				pd.dismiss();
			}
			if(data!=null){
				try {
					User u=new User();
					u.id=data.getString("id");
					u.company=data.getString("headline").split("at")[1].trim();
					u.city=data.getJSONObject("location").getString("name");

					Intent i = new Intent(Login.this, HomeActivityNew.class);


					TelephonyManager tManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
					String uuid = tManager.getDeviceId();
					u.deviceId=uuid;

					SharedPreferences preferences = Login.this.getSharedPreferences("user_info", 0);
					SharedPreferences.Editor editor = preferences.edit();
					editor.putString("userId", u.id);
					editor.putString("userCompany", u.company);
					editor.putString("userCity", u.city);
					editor.putString("userDeviceId", u.deviceId);
					editor.commit();

					SaveUserAsyncTask tsk = new SaveUserAsyncTask();
					tsk.execute(u);
					startActivity(i);

					//h.hoov="xxx";

					/*SaveAsyncTask tsk = new SaveAsyncTask();
					tsk.execute(h);

					String welcomeTextString = String.format("Welcome %1$s %2$s, You are a %3$s.You live in  %4$s",data.getString("firstName"),data.getString("lastName"),data.getString("headline"),data.getJSONObject("location").getString("name"));
					welcomeText.setText(welcomeTextString);*/
				} catch (JSONException e) {
					Log.e("Authorize","Error Parsing json "+e.getLocalizedMessage());	
				}
			}
		}


	};
}