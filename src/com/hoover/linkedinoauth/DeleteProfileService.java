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
	private String userMongoId;
	Thread deleteThread;
	public void onCreate(){
        super.onCreate();
        
	}
	public int onStartCommand(Intent intent, int flags, int startId){
		userMongoId=intent.getStringExtra("userMongoId");

		
		Runnable r=new Runnable() {
	        public void run() {
	        	try{   
	        		URL url2 = new URL("https://api.mongolab.com/api/1/databases/hoover_user/collections/user/"+userMongoId+"/?apiKey=zvbjTNUW6COSTIZxJcPIW7_tniVCnDKC");
	    			HttpURLConnection conn1 = (HttpURLConnection) url2.openConnection();
	    			conn1.setRequestMethod("DELETE");
	    			conn1.setDoOutput(false);
	    			conn1.setRequestProperty("Content-Type", "application/json");
	    			conn1.setRequestProperty("Accept", "application/json");
	    			int s=conn1.getResponseCode();
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
