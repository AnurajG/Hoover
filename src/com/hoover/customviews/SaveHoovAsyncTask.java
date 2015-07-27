package com.hoover.customviews;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import android.os.AsyncTask;

import com.hoover.util.Hoov;
import com.hoover.util.HoovQueryBuilder;

public class SaveHoovAsyncTask extends AsyncTask<Hoov, Void, Boolean> {

	@Override
	protected Boolean doInBackground(Hoov... arg0) {
		try 
		{			
			Hoov hoov = arg0[0];
			
			HoovQueryBuilder qb = new HoovQueryBuilder();						
			
			HttpClient httpClient = new DefaultHttpClient();
	        HttpPost request = new HttpPost(qb.buildContactsSaveURL());

	        StringEntity params =new StringEntity(qb.createContact(hoov));
	        request.addHeader("content-type", "application/json");
	        request.setEntity(params);
	        HttpResponse response = httpClient.execute(request);
	        
	        if(response.getStatusLine().getStatusCode()<205)
	        {
	        	return true;
	        }
	        else
	        {
	        	return false;
	        }
		} catch (Exception e) {
			//e.getCause();
			String val = e.getMessage();
			return false;
		}		
	}

}