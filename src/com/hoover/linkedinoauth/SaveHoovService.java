package com.hoover.linkedinoauth;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;

import com.hoover.util.Hoov;
import com.hoover.util.HoovQueryBuilder;

public class SaveHoovService extends IntentService {
	private String userId;
	private String userCompany;
	private String userCity;

	private ArrayList<Integer> success;
	public SaveHoovService() {
		super(SaveHoovService.class.getName());
	}

	@Override
	protected void onHandleIntent(Intent workIntent) {
		String text=workIntent.getStringExtra("hoovText");
		SharedPreferences preferences = this.getSharedPreferences("user_info", 0);
		userCompany = preferences.getString("userCompany", null);
		userCity = preferences.getString("userCity", null);
		userId = preferences.getString("userId", null);
		
		
		Hoov h=new Hoov();
		h.id=userId;
		h.company=userCompany;
		h.city=userCity;
		h.hoov=text;

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
				System.out.println("done");
			}
			else{
				System.out.println("not done");
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}


	/*class AttemptHoovSubmit extends AsyncTask<String, String, Boolean> {

		protected void onPreExecute() {
			super.onPreExecute();
		}
		//String hoov = hoovText.getText().toString();
		@Override
		protected Boolean doInBackground(String... arg0) {

			

		}

		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
		}

	}*/
}
