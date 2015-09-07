package com.hoover.linkedinoauth;

mport java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.TextView;

import com.hoover.linkedinoauth.HomeFragment.HoovListAdapter;
import com.hoover.util.DataObject;
import com.hoover.util.EmojiMapUtil;
import com.hoover.util.HoovChapter;
import com.hoover.util.HoovFetchParams;

public class HoovDetailsActivity extends Activity{
	private TextView hoovText;
	private TextView hoovDate;

	private String mongoHoovId;
	Button rehoov_in;

	Button deleteHoov;
	ProgressDialog mProgressDialog;

	private RecyclerView mRecyclerView;
	private RecyclerView.Adapter mAdapter;
	private RecyclerView.LayoutManager mLayoutManager;
	ProgressDialog mProgressDialog;
	private List<HoovChapter> HoovChapterlist_t = null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hoov_detail);
		mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

		mRecyclerView.setHasFixedSize(true);
		mLayoutManager = new LinearLayoutManager(this);
		mRecyclerView.setLayoutManager(mLayoutManager);
		mAdapter = new MyRecyclerViewAdapter(getDataSet());
		mRecyclerView.setAdapter(mAdapter);

		hoovText = (TextView) findViewById(R.id.hoovtextView);
		hoovDate= (TextView) findViewById(R.id.hoovdateView2);
		Intent intent = getIntent();

		hoovText.setText(intent.getStringExtra("text"));
		hoovText.setTextSize(TypedValue.COMPLEX_UNIT_PX,
				getResources().getDimension(R.dimen.hoov_detail_text));
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
		deleHoov.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				new DeleteHoovsAsyncTask().execute();
				

			} 

		});
SharedPreferences preferences = this.getSharedPreferences("user_info", 0);
		String userComapny = preferences.getString("userCompany", null);
		String userCity = preferences.getString("userCity", null);
		
		GetHoovsAsyncTask tsk = new GetHoovsAsyncTask();
		HoovFetchParams p = new HoovFetchParams();
		p.city=userCity;
		p.comapny=userComapny;
		p.parentId=mongoHoovId;
		
		tsk.execute(p);

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

	 @Override
	    protected void onResume() {
	        super.onResume();
	        ((MyRecyclerViewAdapter) mAdapter).setOnItemClickListener(new MyRecyclerViewAdapter
	                .MyClickListener() {
	            @Override
	            public void onItemClick(int position, View v) {
	                Log.i("card view", " Clicked on Item " + position);
	            }
	        });
	    }
	 
	 
	 
	 private ArrayList<DataObject> getDataSet() {
	        ArrayList results = new ArrayList<DataObject>();
	        for (int index = 0; index < 20; index++) {
	            DataObject obj = new DataObject("Some Primary Text " + index,
	                    "Secondary " + index);
	            results.add(index, obj);
	        }
	        return results;
	    }
public class GetHoovsAsyncTask extends AsyncTask<HoovFetchParams, Void,Void> {
		@Override
		protected void onPreExecute(){
			super.onPreExecute();
			mProgressDialog = new ProgressDialog(HoovDetailsActivity.this);
			mProgressDialog.setTitle("Load Comments.....");
			mProgressDialog.setMessage("Loading...");
			mProgressDialog.setIndeterminate(false);
			mProgressDialog.show();
		}


		@Override
		protected Void doInBackground(HoovFetchParams... params) {
			HoovChapterlist_t=new ArrayList<HoovChapter>();
			try 
			{			
				HoovFetchParams u = params[0];
				JSONArray array;
				JSONObject p = new JSONObject();
				p.put("document.company",u.comapny);
				p.put("document.city",u.city);
				p.put("document.path", "/,"+u.parentId+",/");

				JSONObject q = new JSONObject();
				q.put("_id", -1);
				String url_str="http://nodejs-hooverest.rhcloud.com/commentlist?parent="+u.parentId;
				URL url = new URL(url_str);//(URLEncoder.encode("https://api.mongolab.com/api/1/databases/hoover/collections/hoov?q="+p.toString()+"&apiKey=zvbjTNUW6COSTIZxJcPIW7_tniVCnDKC","UTF-8"));
				URI uri=new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
				url = uri.toURL();
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				Random random = new Random();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Content-Type", "application/json");
				conn.setRequestProperty("Accept", "application/json");

				int s=conn.getResponseCode();

				BufferedReader streamReader = new BufferedReader(new InputStreamReader(conn.getInputStream())); 
				StringBuilder responseStrBuilder = new StringBuilder();

				String inputStr;
				while ((inputStr = streamReader.readLine()) != null)
					responseStrBuilder.append(inputStr);

				array = new JSONArray(responseStrBuilder.toString());
				for(int i=0;i<array.length();i++){
					HoovChapter hc=new HoovChapter();
					JSONObject obj = (JSONObject)array.get(i);
					JSONObject doc = obj.getJSONObject("document");
					//JSONObject ids = obj.getJSONObject("_id");
					JSONArray ups = doc.getJSONArray("hoovUpIds");
					JSONArray downs = doc.getJSONArray("hoovDownIds");
					hc.hoovText=EmojiMapUtil.replaceCheatSheetEmojis(doc.getString("hoov"));
					hc.mongoHoovId=obj.getString("_id");
					hc.hoov_up_ids=new ArrayList<String>();
					hc.hoov_down_ids =new ArrayList<String>();
					if (ups != null) { 
						int len = ups.length();
						for (int j=0;j<len;j++){ 
							hc.hoov_up_ids.add(ups.get(j).toString());
						} 
					} 
					if (downs != null) { 
						int len = downs.length();
						for (int j=0;j<len;j++){ 
							hc.hoov_down_ids.add(downs.get(j).toString());
						} 
					} 
					long tmp = new BigInteger(hc.mongoHoovId.substring(0, 8), 16).longValue();
					Long epoch=tmp;
					Long curr_epoch = System.currentTimeMillis()/1000;
					if(curr_epoch-epoch > 86400 )
						hc.hoovDate=""+(curr_epoch-epoch)/86400L+"d";
					else if(curr_epoch-epoch > 3600)
						hc.hoovDate=""+(curr_epoch-epoch)/3600+"h";
					else if(curr_epoch-epoch > 60)
						hc.hoovDate=""+(curr_epoch-epoch)/60+"m";
					else
						hc.hoovDate=""+(curr_epoch-epoch+60)+"s";	
					HoovChapterlist_t.add(hc);
				}

			} 
			catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Void dreggn = null;
			return dreggn;	
		}

		@Override
		protected void onPostExecute(final Void  data){
			super.onProgressUpdate(data);
			ArrayList results = new ArrayList<DataObject>();
			for(HoovChapter hc: HoovChapterlist_t){
				DataObject d=new DataObject(hc.hoovText, hc.hoovDate);
				results.add(d);
			}
			mAdapter = new MyRecyclerViewAdapter(results);
			mRecyclerView.setAdapter(mAdapter);
			mProgressDialog.dismiss();
			
		}


	}
	@Override
	protected void onResume() {
		super.onResume();
		((MyRecyclerViewAdapter) mAdapter).setOnItemClickListener(new MyRecyclerViewAdapter
				.MyClickListener() {
			@Override
			public void onItemClick(int position, View v) {
				Log.i("card view", " Clicked on Item " + position);
			}
		});
	}



	private ArrayList<DataObject> getDataSet() {
		ArrayList results = new ArrayList<DataObject>();
		return results;
	}
}
