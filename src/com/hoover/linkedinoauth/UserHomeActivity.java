package com.hoover.linkedinoauth;


import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.hoover.customviews.SaveHoovAsyncTask;
import com.hoover.util.Hoov;
public class UserHomeActivity extends Activity {
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

	private class GetProfileRequestAsyncTask extends AsyncTask<String, Void, JSONObject>{

		@Override
		protected void onPreExecute(){
			pd = ProgressDialog.show(UserHomeActivity.this, "", "Loading..",true);
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
					Hoov h=new Hoov();
					h.id=data.getString("id");
					h.company=data.getString("headline");
					h.city=data.getJSONObject("location").getString("name");
					h.hoov="xxx";

					SaveHoovAsyncTask tsk = new SaveHoovAsyncTask();
					tsk.execute(h);

					String welcomeTextString = String.format("Welcome %1$s %2$s, You are a %3$s.You live in  %4$s",data.getString("firstName"),data.getString("lastName"),data.getString("headline"),data.getJSONObject("location").getString("name"));
					welcomeText.setText(welcomeTextString);
				} catch (JSONException e) {
					Log.e("Authorize","Error Parsing json "+e.getLocalizedMessage());	
				}
			}
		}


	};
}