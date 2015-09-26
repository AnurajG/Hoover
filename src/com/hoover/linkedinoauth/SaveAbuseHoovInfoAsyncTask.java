package com.hoover.linkedinoauth;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import com.goebl.david.WebbException;

import android.os.AsyncTask;

public class SaveAbuseHoovInfoAsyncTask extends AsyncTask<Void, Void,Void>  {
	private final String mongoHoovId;
	private final String userId;
	public SaveAbuseHoovInfoAsyncTask(String mongoHoovId,String userId) {
		super();
		this.mongoHoovId = mongoHoovId;
		this.userId=userId;
	}
	@Override
	protected Void doInBackground(Void... arg0) {
		// TODO Auto-generated method stub
		JSONObject f = new JSONObject();
		JSONObject push=new JSONObject();
		try {
			f.put("document.abuserUserIds",userId);
			push.put("$addToSet", f);
        } catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
		try{   
			URL url = new URL("https://api.mongolab.com/api/1/databases/hoover/collections/hoov/"+mongoHoovId+"?apiKey=zvbjTNUW6COSTIZxJcPIW7_tniVCnDKC");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			Random random = new Random();
			conn.setRequestMethod("PUT");
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Accept", "application/json");
			OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
			osw.write(String.format(push.toString(), random.nextInt(30), random.nextInt(20)));
			osw.flush();
			osw.close();
			int s=conn.getResponseCode();
			System.out.println("");
		}catch(WebbException we){
			we.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	protected void onPostExecute(Void data){
		
	}

}
