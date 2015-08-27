package com.hoover.linkedinoauth;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.goebl.david.Webb;
import com.goebl.david.WebbException;
import com.hoover.util.HoovChapter;
import com.hoover.util.HoovFetchParams;
import com.hoover.util.HoovQueryBuilder;

public class HomeTopFragment extends ListFragment implements OnRefreshListener {
	private Context context;
	HoovListAdapter hAdapter;
	private ProgressDialog pd;
	private String company;
	private SwipeRefreshLayout refreshLayout;
	private String city;
	public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";


	public static final HomeTopFragment newInstance(String message,Context context, String comp, String cit) {
		HomeTopFragment f = new HomeTopFragment(context,comp,cit);
		Bundle bdl = new Bundle(1);
		bdl.putString(EXTRA_MESSAGE, message);
		f.setArguments(bdl);
		return f;

	}

	public HomeTopFragment(Context context, String comp, String cit) {
		this.context = context;
		this.company = comp;
		this.city=cit;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view;
		super.onCreate(savedInstanceState);
		view = inflater.inflate(R.layout.activity_home, container, false);
		refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
		refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override

			public void onRefresh() {
				PopulateAdapterAsyncTask tsk = new PopulateAdapterAsyncTask();
				Void v = null;
				tsk.execute(v);
				if (refreshLayout.isRefreshing()) {
					refreshLayout.setRefreshing(false);
				}
			}
		});

		hAdapter=new HoovListAdapter(context);
		setListAdapter(hAdapter);

		PopulateAdapterAsyncTask tsk = new PopulateAdapterAsyncTask();
		Void v = null;
		tsk.execute(v);

		return view;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		HoovChapter selectedHoov = new HoovChapter();
		selectedHoov = hAdapter.hoovChapterList.get(position);
		Intent myIntent = new Intent(context, HoovDetailsActivity.class);
		myIntent.putExtra("mongodbHoovId",selectedHoov.mongoHoovId);
		myIntent.putExtra("text",selectedHoov.hoovText);
		myIntent.putExtra("date",selectedHoov.hoovDate);
		Toast.makeText(context, selectedHoov.hoovDate + " ID #", Toast.LENGTH_SHORT).show();

		startActivity(myIntent);

	}
	public class PopulateAdapterAsyncTask extends AsyncTask<Void, Void, Boolean>{
		@Override
		protected void onPreExecute(){
			pd = ProgressDialog.show(context, "", "Fetching Hoovs..",true);
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

	public List<HoovChapter> fetchAllHoovs(HoovFetchParams params) {

		List<HoovChapter> user=new ArrayList<HoovChapter>();
		try 
		{			
			HoovFetchParams u = params;

			HoovQueryBuilder qb = new HoovQueryBuilder();						
			JSONObject q = new JSONObject();
			q.put("document.company",u.comapny);
			q.put("document.city",u.city);

			JSONObject f = new JSONObject();
			f.put("document.hoov",1);

			JSONObject s = new JSONObject();
			s.put("_id", "-1");

			//s={"priority": 1


			Webb webb = Webb.create();
			try{
				JSONArray array=webb.get("http://nodejs-hooverest.rhcloud.com/hoovlist")
						.param("city", u.city)
						.param("company", u.comapny)
						.ensureSuccess().asJsonArray().getBody();
				for(int i=0;i<array.length();i++){
					HoovChapter hc=new HoovChapter();
					JSONObject obj = (JSONObject)array.get(i);
					//JSONObject doc = obj.getJSONObject("document");
					//JSONObject ids = obj.getJSONObject("_id");
					hc.hoovText=obj.getString("_hoov");
					hc.mongoHoovId=obj.getString("_id");
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
					user.add(hc);
				}
			}catch (WebbException we){
				we.printStackTrace();
			}


		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return user;	


	}
	public List<HoovChapter> getDataForListView()
	{
		List<HoovChapter> hoovChaptersList = new ArrayList<HoovChapter>();


		//GetHoovsAsyncTask gtask=new GetHoovsAsyncTask();
		HoovFetchParams params=new HoovFetchParams();
		params.city=city;
		params.comapny=company;
		hoovChaptersList=fetchAllHoovs(params);

		return hoovChaptersList;

	}


	public class HoovListAdapter extends BaseAdapter{

		String company;
		String city;
		private Context context;

		public HoovListAdapter(Context context) {
			this.context = context;
		}


		List<HoovChapter> hoovChapterList = new ArrayList<HoovChapter>();
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
				LayoutInflater inflater =  LayoutInflater.from(context);
				arg1 = inflater.inflate(R.layout.hoov_list, arg2,false);
			}
			TextView h_text = (TextView)arg1.findViewById(R.id.hoov_text);
			TextView h_date = (TextView)arg1.findViewById(R.id.hoov_date);

			HoovChapter chapter = hoovChapterList.get(arg0);

			h_text.setText(chapter.hoovText);
			h_date.setText(chapter.hoovDate);

			return arg1;
		}

	}

	@Override
	public void onRefresh() {
		PopulateAdapterAsyncTask tsk = new PopulateAdapterAsyncTask();
		Void v = null;
		tsk.execute(v);
		if (refreshLayout.isRefreshing()) {
			refreshLayout.setRefreshing(false);
		}

	}
}
