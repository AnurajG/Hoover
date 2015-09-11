package com.hoover.linkedinoauth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.goebl.david.WebbException;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class DeleteProfileService extends Service{
	private String userId;
	Thread deleteThread;
	public void onCreate(){
        super.onCreate();
        
	}
	public int onStartCommand(Intent intent, int flags, int startId){
		userId=intent.getStringExtra("userId");

		final JSONObject q = new JSONObject();
		try {
			q.put("document.id", userId);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Runnable r=new Runnable() {
	        public void run() {
	        	try{   
	        		//https://api.mongolab.com/api/1/databases/hoover_user/collections/user/?q={%22document.id%22:%22m4csWWsb1i%22}&apiKey=zvbjTNUW6COSTIZxJcPIW7_tniVCnDKC
	    			URL url = new URL("https://api.mongolab.com/api/1/databases/hoover_user/collections/user/?q="+q+"&apiKey=zvbjTNUW6COSTIZxJcPIW7_tniVCnDKC");
	    			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	    			Random random = new Random();
	    			conn.setRequestMethod("GET");
	    			conn.setDoOutput(false);
	    			conn.setRequestProperty("Content-Type", "application/json");
	    			conn.setRequestProperty("Accept", "application/json");
	    			int s=conn.getResponseCode();
	    			BufferedReader streamReader = new BufferedReader(new InputStreamReader(conn.getInputStream())); 
					StringBuilder responseStrBuilder = new StringBuilder();

					String inputStr;
					while ((inputStr = streamReader.readLine()) != null)
						responseStrBuilder.append(inputStr);

					JSONArray array = new JSONArray(responseStrBuilder.toString());
					JSONObject obj = (JSONObject)array.get(0);
					JSONObject oid = obj.getJSONObject("_id");
					String mongoUserId=oid.getString("$oid");
	    			System.out.println("id "+mongoUserId);
	    			URL url2 = new URL("https://api.mongolab.com/api/1/databases/hoover_user/collections/user/"+mongoUserId+"/?apiKey=zvbjTNUW6COSTIZxJcPIW7_tniVCnDKC");
	    			HttpURLConnection conn1 = (HttpURLConnection) url2.openConnection();
	    			conn1.setRequestMethod("DELETE");
	    			conn1.setDoOutput(false);
	    			conn1.setRequestProperty("Content-Type", "application/json");
	    			conn1.setRequestProperty("Accept", "application/json");
	    			s=conn1.getResponseCode();
	    			//System.out.println("id "+id);
	    			
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
	              
	        }

	     };
	     deleteThread= new Thread(r);
	     deleteThread.start();
		
		return START_NOT_STICKY;
		
	}
	public void onDestroy(){
		super.onDestroy();
	}
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
