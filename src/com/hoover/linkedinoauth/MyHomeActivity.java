package com.hoover.linkedinoauth;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.goebl.david.Webb;
import com.hoover.util.HoovChapter;
import com.hoover.util.HoovFetchParams;
import com.hoover.util.HoovQueryBuilder;
import com.hoover.util.MyHoovFetchParams;

public class MyHomeActivity extends ListActivity{

	HoovListAdapter hAdapter;
	private ProgressDialog pd;
	private SwipeRefreshLayout refreshLayout;
	String city;
	String company;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myhome);


		hAdapter=new HoovListAdapter();
		setListAdapter(hAdapter);
		refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
		refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override

			public void onRefresh() {
				hAdapter=new HoovListAdapter();
				setListAdapter(hAdapter);
				if (refreshLayout.isRefreshing()) {
					refreshLayout.setRefreshing(false);
				}
			}
		});
		PopulateAdapterAsyncTask tsk = new PopulateAdapterAsyncTask();
		Void v = null;
		tsk.execute(v);
	}
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		HoovChapter selectedHoov = new HoovChapter();
		selectedHoov = hAdapter.hoovChapterList.get(position);
		Intent myIntent = new Intent(MyHomeActivity.this, HoovDetailsActivity.class);
		myIntent.putExtra("mongodbHoovId",selectedHoov.mongoHoovId);
		myIntent.putExtra("text",selectedHoov.hoovText);
		myIntent.putExtra("date",selectedHoov.hoovDate);
		Toast.makeText(getBaseContext(), selectedHoov.hoovDate + " ID #", Toast.LENGTH_SHORT).show();

		startActivity(myIntent);

	}

	public class PopulateAdapterAsyncTask extends AsyncTask<Void, Void, Boolean>{
		@Override
		protected void onPreExecute(){
			pd = ProgressDialog.show(MyHomeActivity.this, "", "Fetching Profile..",true);
		}
		@Override
		protected Boolean doInBackground(Void... params) {
			hAdapter.hoovChapterList=getDataForListView();
			return true;
		}

		@Override
		protected void onPostExecute(Boolean b){
			if(pd!=null && pd.isShowing()){
				pd.dismiss();
			}
			hAdapter.notifyDataSetChanged();
			super.onPostExecute(b);
		}
	}

	public List<HoovChapter> getDataForListView()
	{
		List<HoovChapter> hoovChaptersList = new ArrayList<HoovChapter>();
		SharedPreferences preferences = this.getSharedPreferences("user_info", 0);
		String userId = preferences.getString("userId", null);

		//GetHoovsAsyncTask gtask=new GetHoovsAsyncTask();
		MyHoovFetchParams params=new MyHoovFetchParams();
		params.userId=userId;

		hoovChaptersList=fetchAllHoovs(params);

		SharedPreferences preferences2 = MyHomeActivity.this.getSharedPreferences("hoov_tmp", 0);
		String hoovArray = preferences2.getString("hoovArray", null);
		if(hoovArray!=null){
			try {
				JSONArray array=new JSONArray(hoovArray);
				for(int i = 0;i<array.length();i++){
					HoovChapter hc=new HoovChapter();
					hc.hoovText=array.getJSONObject(i).getString("hoovText");
					hc.hoovDate="";
					hc.posted=false;
					hc.mongoHoovId="-1";

					hoovChaptersList.add(hc);
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}

		}


		return hoovChaptersList;

	}
	public class HoovListAdapter extends BaseAdapter{

		String userId;
		//List<HoovChapter> hoovChapterList = getDataForListView();
		List<HoovChapter> hoovChapterList=new ArrayList<HoovChapter>();
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return hoovChapterList.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return hoovChapterList.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			if(arg1==null){
				LayoutInflater inflater = (LayoutInflater)MyHomeActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				arg1 = inflater.inflate(R.layout.my_hoov_list, arg2,false);
			}
			TextView h_text = (TextView)arg1.findViewById(R.id.my_hoov_text);
			TextView h_date = (TextView)arg1.findViewById(R.id.my_hoov_date);

			ImageView h_status = (ImageView)arg1.findViewById(R.id.my_hoov_status);

			HoovChapter chapter = hoovChapterList.get(arg0);

			if(chapter.posted)
				h_status.setImageResource(R.drawable.blue_tick);
			else
				h_status.setImageResource(R.drawable.loading);

			h_text.setText(chapter.hoovText);
			h_date.setText(chapter.hoovDate);

			return arg1;
		}

	}

	public List<HoovChapter> fetchAllHoovs(MyHoovFetchParams params){

		List<HoovChapter> user=new ArrayList<HoovChapter>();
		try 
		{			
			MyHoovFetchParams u = params;

			HoovQueryBuilder qb = new HoovQueryBuilder();						
			JSONObject q = new JSONObject();
			q.put("document.id",u.userId);

			JSONObject f = new JSONObject();
			f.put("document.hoov",1);



			Webb webb = Webb.create();
			JSONArray array=webb.get("https://api.mongolab.com/api/1/databases/hoover/collections/hoov").param("apiKey", "zvbjTNUW6COSTIZxJcPIW7_tniVCnDKC")
					.param("q", q.toString())
					.param("f", f.toString())
					.ensureSuccess().asJsonArray().getBody();
			for(int i=0;i<array.length();i++){
				HoovChapter hc=new HoovChapter();
				JSONObject obj = (JSONObject)array.get(i);
				JSONObject doc = obj.getJSONObject("document");
				JSONObject ids = obj.getJSONObject("_id");
				hc.hoovText=doc.getString("hoov");
				hc.mongoHoovId=ids.getString("$oid");
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
				hc.posted=true;
				user.add(hc);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return user;	

	}


}