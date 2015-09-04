package com.hoover.linkedinoauth;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

import com.goebl.david.WebbException;
import com.hoover.linkedinoauth.HomeFragment.GetHoovsAsyncTask;
import com.hoover.util.HoovFetchParams;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class HoovDetailsActivity extends Activity{
	private TextView hoovText;
	private TextView hoovDate;

	private String mongoHoovId;
	Button rehoov_in;
	Button deleteHoov;
	ProgressDialog mProgressDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hoov_detail);

		hoovText = (TextView) findViewById(R.id.hoovtextView);
		hoovDate= (TextView) findViewById(R.id.hoovdateView2);
		Intent intent = getIntent();

		hoovText.setText(intent.getStringExtra("text"));
		hoovDate.setText(intent.getStringExtra("date"));

		mongoHoovId=intent.getStringExtra("mongodbHoovId");

		rehoov_in=(Button) findViewById(R.id.rehoov);
		rehoov_in.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent myIntent = new Intent(HoovDetailsActivity.this,HoovActivity.class);
				myIntent.putExtra("mongodbParentId",mongoHoovId);
				startActivity(myIntent); 

			} 

		});
		deleteHoov=(Button) findViewById(R.id.delete);
		if(intent.getStringExtra("currentUserid").equals(intent.getStringExtra("selectedHoovUserId"))){
			deleteHoov.setVisibility(Button.VISIBLE);
		}
		deleteHoov.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				new DeleteHoovsAsyncTask().execute();
				

			} 

		});

	}
	
	public class DeleteHoovsAsyncTask extends AsyncTask<Void, Void,Void> {
		protected void onPreExecute(){
			super.onPreExecute();
			mProgressDialog = new ProgressDialog(HoovDetailsActivity.this);
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
			Intent myOrigIntent = new Intent(HoovDetailsActivity.this,HomeActivityNew.class);
			myOrigIntent.putExtra("hoovId",mongoHoovId);
			startActivity(myOrigIntent);
		}
		
	}
	
}
