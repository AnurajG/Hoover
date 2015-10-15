package com.hoover.linkedinoauth;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.IntentService;
import android.content.Intent;

public class SendParsePushService extends IntentService {
	public static final String parseURL="https://api.parse.com/1/push";
	public static final String appId="5QnLXvhEOlJovLNbSZrOfm67mtMbzeXOm6OwGp73";
	public static final String restKey="IuQ8e20rnrtEzSOqMFmzjdJbxF8PjZEvjJUfkzrQ";



	public SendParsePushService() {
		super(SendParsePushService.class.getName());
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub

		String data=intent.getStringExtra("data");
		try{   
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost request = new HttpPost(parseURL);
			StringEntity params;
			params = new StringEntity(data);
			request.addHeader("X-Parse-Application-Id", appId);
			request.addHeader("X-Parse-REST-API-Key", restKey);
			request.addHeader("content-type", "application/json");
			request.setEntity(params);
			HttpResponse response = httpClient.execute(request);
			if(response.getStatusLine().getStatusCode()<205){
				System.out.println("success");
			}
			else{
				System.out.println("failed");
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
}

