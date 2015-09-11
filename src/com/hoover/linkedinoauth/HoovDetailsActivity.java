package com.hoover.linkedinoauth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.goebl.david.WebbException;
import com.hoover.util.EmojiMapUtil;
import com.hoover.util.Hoov;
import com.hoover.util.HoovChapter;
import com.hoover.util.HoovFetchParams;
import com.hoover.util.HoovInsertParams;
import com.hoover.util.HoovQueryBuilder;

public class HoovDetailsActivity extends Activity{
	private TextView hoovText;
	private TextView hoovDate;

	private String mongoHoovId;
	Button rehoov_in;
	EditText myEditText;

	ImageButton deleteHoov;

	private RecyclerView mRecyclerView;
	private RecyclerView.Adapter mAdapter;
	private RecyclerView.LayoutManager mLayoutManager;
	ProgressDialog mProgressDialog;
	private List<HoovChapter> HoovChapterlist_t = null;
	ArrayList<HoovChapter> results = new ArrayList<HoovChapter>();
	String userComapny;
	String userCity;
	String userId;
	String path;


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
		path=intent.getStringExtra("path");
		hoovText.setTextSize(TypedValue.COMPLEX_UNIT_PX,
				getResources().getDimension(R.dimen.hoov_detail_text));
		hoovDate.setText(intent.getStringExtra("date"));

		mongoHoovId=intent.getStringExtra("mongodbHoovId");

		myEditText = (EditText) findViewById(R.id.commenttext);

		// Check if no view has focus:
		View view = this.getCurrentFocus();
		if (view != null) {  
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
		}

		rehoov_in=(Button) findViewById(R.id.submit);
		rehoov_in.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
				inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
				String toPost=myEditText.getText().toString();
				myEditText.getText().clear();

				SubmitHoovAsyncTask s_tsk= new SubmitHoovAsyncTask();
				HoovInsertParams p = new HoovInsertParams();
				p.text=EmojiMapUtil.replaceUnicodeEmojis(toPost);
				p.parentId=mongoHoovId;
				s_tsk.execute(p);

			} 

		});

		deleteHoov=(ImageButton) findViewById(R.id.delete);
		deleteHoov.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				DeleteHoovsAsyncTask tsk= new DeleteHoovsAsyncTask();
				tsk.execute();
			} 

		});



		if(intent.getStringExtra("currentUserid").equals(intent.getStringExtra("selectedHoovUserId"))){
			deleteHoov.setVisibility(Button.VISIBLE);
		}

		rehoov_in=(Button) findViewById(R.id.submit);
		if(myEditText.length() == 0) rehoov_in.setEnabled(false);
		myEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable arg0) {
				Integer l= 320 - myEditText.length();
				if(l<0 || l==320) 
					rehoov_in.setEnabled(false);
				else
					rehoov_in.setEnabled(true);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
		});


		SharedPreferences preferences = this.getSharedPreferences("user_info", 0);
		userComapny = preferences.getString("userCompany", null);
		userCity = preferences.getString("userCity", null);
		userId = preferences.getString("userId", null);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
				);
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
					hc.hoovUserId=userId;
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

			results.addAll(HoovChapterlist_t);
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



	private ArrayList<HoovChapter> getDataSet() {
		ArrayList<HoovChapter> results = new ArrayList<HoovChapter>();
		return results;
	}

	public class SubmitHoovAsyncTask extends AsyncTask<HoovInsertParams, Void, String>{

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if(result!=null){
				HoovChapter hc=new HoovChapter();
				hc.hoovText=result;
				hc.hoovUserId=userId;
				results.add(hc);
				mAdapter = new MyRecyclerViewAdapter(results);
				mRecyclerView.setAdapter(mAdapter);
				mProgressDialog.dismiss();
			}
		}

		@Override
		protected String doInBackground(HoovInsertParams... params) {


			Hoov h=new Hoov();
			h.id=userId;
			h.company=userComapny;
			h.city=userCity;
			h.hoov=params[0].text;




			h.path=path+","+mongoHoovId;


			h.hoovUpIds =new ArrayList<String>();
			h.hoovDownIds=new ArrayList<String>();

			HoovQueryBuilder qb = new HoovQueryBuilder();						

			HttpClient httpClient = new DefaultHttpClient();
			HttpPost request = new HttpPost(qb.buildContactsSaveURL());

			StringEntity param;
			try {
				param = new StringEntity(qb.createContact(h));
				request.addHeader("content-type", "application/json");
				request.setEntity(param);
				HttpResponse response = httpClient.execute(request);
				if(response.getStatusLine().getStatusCode()<205){
					return h.hoov;
				}
				else{
					System.out.println("");
				}

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}



	}
}
