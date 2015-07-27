package com.hoover.linkedinoauth;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.goebl.david.Webb;
import com.hoover.util.HoovChapter;
import com.hoover.util.HoovFetchParams;
import com.hoover.util.HoovQueryBuilder;

public class HomeActivity extends ListActivity{

	HoovListAdapter hAdapter;
	private ProgressDialog pd;
	private SwipeRefreshLayout refreshLayout;
	String city;
	String company;
	
	Button hoov_in;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		
		hAdapter=new HoovListAdapter();
		setListAdapter(hAdapter);
		refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
		//refreshLayout.setOnRefreshListener(this);
		hoov_in=(Button) findViewById(R.id.hoov_in);
		hoov_in.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(HomeActivity.this,HoovActivity.class);
				startActivity(intent); 

			} 

		});

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
	}
	@Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        HoovChapter selectedHoov = new HoovChapter();
        selectedHoov = hAdapter.hoovChapterList.get(position);
        Intent myIntent = new Intent(HomeActivity.this, HoovDetailsActivity.class);
        myIntent.putExtra("mongodbHoovId",selectedHoov.mongoHoovId);
        myIntent.putExtra("text",selectedHoov.hoovText);
        myIntent.putExtra("date",selectedHoov.hoovDate);
        Toast.makeText(getBaseContext(), selectedHoov.hoovDate + " ID #", Toast.LENGTH_SHORT).show();
        
        startActivity(myIntent);

    }
	
	public List<HoovChapter> getDataForListView()
	{
		List<HoovChapter> hoovChaptersList = new ArrayList<HoovChapter>();
		SharedPreferences preferences = this.getSharedPreferences("user_info", 0);
		String userComapny = preferences.getString("userCompany", null);
		String userCity = preferences.getString("userCity", null);

		GetHoovsAsyncTask gtask=new GetHoovsAsyncTask();
		HoovFetchParams params=new HoovFetchParams();
		params.city=userCity;
		params.comapny=userComapny;
		try {
			hoovChaptersList=gtask.execute(params).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		return hoovChaptersList;

	}
	public class HoovListAdapter extends BaseAdapter{

		String company;
		String city;
		List<HoovChapter> hoovChapterList = getDataForListView();
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
				LayoutInflater inflater = (LayoutInflater)HomeActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

	public class GetHoovsAsyncTask extends AsyncTask<HoovFetchParams, Void, List<HoovChapter>> {
		@Override
		protected void onPreExecute(){
			pd = ProgressDialog.show(HomeActivity.this, "", "Fetching hoovs..",true);
		}


		@Override
		protected List<HoovChapter> doInBackground(HoovFetchParams... params) {
			List<HoovChapter> user=new ArrayList<HoovChapter>();
			try 
			{			
				HoovFetchParams u = params[0];

				HoovQueryBuilder qb = new HoovQueryBuilder();						
				JSONObject q = new JSONObject();
				q.put("document.company",u.comapny);
				q.put("document.city",u.city);

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
						hc.hoovDate=""+(curr_epoch-epoch)+"s";	
					user.add(hc);
				}

			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			return user;	
		}

		@Override
		protected void onPostExecute(List<HoovChapter>  data){
			if(pd!=null && pd.isShowing()){
				pd.dismiss();
			}
			super.onPostExecute(data);
		}

	}



}