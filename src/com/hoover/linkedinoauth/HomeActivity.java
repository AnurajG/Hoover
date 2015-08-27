package com.hoover.linkedinoauth;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.goebl.david.Webb;
import com.goebl.david.WebbException;
import com.hoover.util.HoovChapter;
import com.hoover.util.HoovFetchParams;
import com.hoover.util.HoovQueryBuilder;
import com.hoover.util.NetworkUtil;

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
		/*AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

		Intent alarmIntent = new Intent(this, SaveHoovService.class);
		PendingIntent pending = PendingIntent.getService(this, 0, alarmIntent, 0);
		final AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

		alarm.cancel(pending);
		long interval = 30000;//milliseconds
		alarm.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(),interval, pending);*/

		Integer status = NetworkUtil.getConnectivityStatus(this);

		if(status==NetworkUtil.TYPE_NOT_CONNECTED){
			Intent noNetIntent = new Intent(this, NoNetConnectivity.class);
			this.startService(noNetIntent);
		}else{
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
					PopulateAdapterAsyncTask tsk = new PopulateAdapterAsyncTask();
					Void v = null;
					tsk.execute(v);
					if (refreshLayout.isRefreshing()) {
						refreshLayout.setRefreshing(false);
					}
				}
			});
		}
		PopulateAdapterAsyncTask tsk = new PopulateAdapterAsyncTask();
		Void v = null;
		tsk.execute(v);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.profile_view_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_myprofile:
			Intent intent = new Intent(HomeActivity.this,MyHomeActivity.class);
			startActivity(intent); 
			return true;
		case R.id.action_settings:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
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
	public class PopulateAdapterAsyncTask extends AsyncTask<Void, Void, Boolean>{
		@Override
		protected void onPreExecute(){
			pd = ProgressDialog.show(HomeActivity.this, "", "Fetching Hoovs..",true);
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
		String userComapny = preferences.getString("userCompany", null);
		String userCity = preferences.getString("userCity", null);

		//GetHoovsAsyncTask gtask=new GetHoovsAsyncTask();
		HoovFetchParams params=new HoovFetchParams();
		params.city=userCity;
		params.comapny=userComapny;
		hoovChaptersList=fetchAllHoovs(params);

		return hoovChaptersList;

	}
	public class HoovListAdapter extends BaseAdapter{

		String company;
		String city;
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
				JSONArray array=webb.get("https://api.mongolab.com/api/1/databases/hoover/collections/hoov").param("apiKey", "zvbjTNUW6COSTIZxJcPIW7_tniVCnDKC")
						.param("q", q.toString())
						.param("f", f.toString())
						.param("s", f.toString())
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




}