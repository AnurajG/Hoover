package com.hoover.linkedinoauth;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Intent;

import com.goebl.david.WebbException;

public class SaveLikeDislikeService extends IntentService {
	public static final String userId="userid";
	public static final String hoovId="hoovid";
	public static final String insertUp="insertUp";
	public static final String insertDown="insertDown";
	public static final String deleteUp="deleteUp";
	public static final String deleteDown="deleteDown";
	
	
	public SaveLikeDislikeService() {
		super(SaveLikeDislikeService.class.getName());
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		
		JSONObject f = new JSONObject();
		JSONObject push=new JSONObject();
		try {
			if(intent.getBooleanExtra(insertUp,false))
				f.put("document.hoovUpIds",intent.getStringExtra(userId));
			else
				f.put("document.hoovDownIds",intent.getStringExtra(userId));
			push.put("$addToSet", f);
        } catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
		try{   
			URL url = new URL("https://api.mongolab.com/api/1/databases/hoover/collections/hoov/"+intent.getStringExtra(hoovId)+"?apiKey=zvbjTNUW6COSTIZxJcPIW7_tniVCnDKC");
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
		
		boolean doNothing=false;
		JSONObject f1 = new JSONObject();
		JSONObject pull=new JSONObject();
		try {
			if(intent.getBooleanExtra(deleteUp,false))
				f1.put("document.hoovUpIds",intent.getStringExtra(userId));
			else if(intent.getBooleanExtra(deleteDown,false))
				f1.put("document.hoovDownIds",intent.getStringExtra(userId));
			else
				doNothing=true;
			pull.put("$pull", f1);
        } catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(!doNothing){
			try{   
				URL url = new URL("https://api.mongolab.com/api/1/databases/hoover/collections/hoov/"+intent.getStringExtra(hoovId)+"?apiKey=zvbjTNUW6COSTIZxJcPIW7_tniVCnDKC");
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				Random random = new Random();
				conn.setRequestMethod("PUT");
				conn.setDoOutput(true);
				conn.setRequestProperty("Content-Type", "application/json");
				conn.setRequestProperty("Accept", "application/json");
				OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
				osw.write(String.format(pull.toString(), random.nextInt(30), random.nextInt(20)));
				osw.flush();
				osw.close();
				int s= conn.getResponseCode();
				System.out.println("oo "+s);
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
	}

}
