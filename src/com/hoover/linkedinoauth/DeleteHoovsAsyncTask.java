package com.hoover.linkedinoauth;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.goebl.david.WebbException;

public class DeleteHoovsAsyncTask extends AsyncTask<Void, Void,Void>{
	private final Context mContext;
	private final String mongoHoovId;
	public DeleteHoovsAsyncTask(Context mContext, String mongoHoovId) {
		super();
		this.mContext = mContext;
		this.mongoHoovId = mongoHoovId;
	}
	ProgressDialog mProgressDialog;
	
	protected void onPreExecute(){
		super.onPreExecute();
		mProgressDialog = new ProgressDialog(mContext);
		mProgressDialog.setTitle("Delete Hoov.....");
		mProgressDialog.setMessage("Deleting...");
		mProgressDialog.setIndeterminate(false);
		mProgressDialog.show();
	}
	@Override
	protected Void doInBackground(Void... arg0) {
		// TODO Auto-generated method stub
		try{   
			URL url = new URL("https://api.mongolab.com/api/1/databases/hoover/collections/hoov/"+mongoHoovId+"?apiKey=zvbjTNUW6COSTIZxJcPIW7_tniVCnDKC");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			Random random = new Random();
			conn.setRequestMethod("DELETE");
			conn.setDoOutput(false);
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Accept", "application/json");
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
		mProgressDialog.dismiss();
		Intent myOrigIntent = new Intent(mContext,HomeActivityNew.class);
		myOrigIntent.putExtra("hoovId",mongoHoovId);
		mContext.startActivity(myOrigIntent);
	}



}
