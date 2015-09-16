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
import com.hoover.util.HoovChapter;

public class DeleteHoovsAsyncTask extends AsyncTask<Void, Void,Void>{
	private final Context mContext;
	private final String mongoHoovId;
	private boolean comment=false;
	private HoovChapter hChapter;
	private String id;
	public DeleteHoovsAsyncTask(Context mContext, String mongoHoovId) {
		super();
		this.mContext = mContext;
		this.mongoHoovId = mongoHoovId;
	}

	public DeleteHoovsAsyncTask(Context mContext, String mongoHoovId, Boolean isComment,HoovChapter hc, String currentUserId) {
		super();
		this.mContext = mContext;
		this.mongoHoovId = mongoHoovId;
		this.comment=isComment;
		this.hChapter=hc;
		this.id=currentUserId;
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
		String urlPrefix;
		if(comment)
			urlPrefix="http://nodejs-hooverest.rhcloud.com/deletecomment"+"?comment=";
		else
			urlPrefix="http://nodejs-hooverest.rhcloud.com/deletehoov"+"?parent=";
		try{   
			URL url = new URL(urlPrefix+mongoHoovId);
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
		Intent myOrigIntent = null;
		if(comment){
			myOrigIntent = new Intent(mContext,HoovDetailsActivity.class);
			myOrigIntent.putExtra("currentUserid", id);
			myOrigIntent.putExtra("chapter", hChapter);
		}else{
			myOrigIntent = new Intent(mContext,HomeActivityNew.class);
			myOrigIntent.putExtra("hoovId",mongoHoovId);
		}
		mProgressDialog.dismiss();
		mContext.startActivity(myOrigIntent);
	}



}
