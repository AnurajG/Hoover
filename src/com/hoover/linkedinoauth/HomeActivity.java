package com.hoover.linkedinoauth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import android.view.ViewParent;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.goebl.david.Response;
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
					hAdapter=new HoovListAdapter();
					setListAdapter(hAdapter);
					if (refreshLayout.isRefreshing()) {
						refreshLayout.setRefreshing(false);
					}
				}
			});
		}
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

	public void myUPClickHandler(View v) {
		RelativeLayout rl=(RelativeLayout)v.getParent();
		final int position = getListView().getPositionForView(rl);
		HoovChapter selectedHoov = new HoovChapter();
		selectedHoov = hAdapter.hoovChapterList.get(position);
		String hoovId=selectedHoov.mongoHoovId;
		SharedPreferences preferences = this.getSharedPreferences("user_info", 0);
		String userId = preferences.getString("userId", null);
		TextView h_down_count = (TextView)rl.findViewById(R.id.hoov_down_count);
		int currDownCount=Integer.parseInt((String)h_down_count.getText());
		
		int finalDownCount=hAdapter.updateUpCountView(position, v);
		Intent intent = new Intent(this, SaveLikeDislikeService.class);
		intent.putExtra(SaveLikeDislikeService.userId,userId);
	    intent.putExtra(SaveLikeDislikeService.hoovId,hoovId);
	    intent.putExtra(SaveLikeDislikeService.insertUp,true);
	    if(finalDownCount<currDownCount)
	    	intent.putExtra(SaveLikeDislikeService.deleteDown,true);
	    startService(intent);
	    System.out.println("yeah");
	}
	public void myDOWNClickHandler(View v) {
		RelativeLayout rl=(RelativeLayout)v.getParent();
		final int position = getListView().getPositionForView(rl);
		HoovChapter selectedHoov = new HoovChapter();
		selectedHoov = hAdapter.hoovChapterList.get(position);
		String hoovId=selectedHoov.mongoHoovId;
		SharedPreferences preferences = this.getSharedPreferences("user_info", 0);
		String userId = preferences.getString("userId", null);
		TextView h_up_count = (TextView)rl.findViewById(R.id.hoov_up_count);
		int currUpCount=Integer.parseInt((String)h_up_count.getText());
		
		int finalUpCount=hAdapter.updateDownCountView(position, v);
		Intent intent = new Intent(this, SaveLikeDislikeService.class);
	    intent.putExtra(SaveLikeDislikeService.userId,userId);
	    intent.putExtra(SaveLikeDislikeService.hoovId,hoovId);
	    intent.putExtra(SaveLikeDislikeService.insertDown,true);
	    if(finalUpCount<currUpCount)
	    	intent.putExtra(SaveLikeDislikeService.deleteUp,true);
	    startService(intent);	
	    System.out.println("yeah");
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
	public String getUserId()
	{
		SharedPreferences preferences = this.getSharedPreferences("user_info", 0);
		String userId = preferences.getString("userId", null);
		
		return userId;

	}
	public class HoovListAdapter extends BaseAdapter{

		String company;
		String city;
		List<HoovChapter> hoovChapterList = getDataForListView();
		String userID=getUserId();
		
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
			TextView h_up_count = (TextView)arg1.findViewById(R.id.hoov_up_count);
			TextView h_down_count = (TextView)arg1.findViewById(R.id.hoov_down_count);
			Button h_up_button=(Button)arg1.findViewById(R.id.hoov_up_button);
			Button h_down_button=(Button)arg1.findViewById(R.id.hoov_down_button);
			HoovChapter chapter = hoovChapterList.get(arg0);
			
			if(chapter.hoov_up_ids.contains(userID)){
				h_up_button.setBackground(getResources().getDrawable(R.drawable.greenup));
				h_up_button.setEnabled(false);
			}else if(chapter.hoov_down_ids.contains(userID)){
				h_down_button.setBackground(getResources().getDrawable(R.drawable.reddown));
				h_down_button.setEnabled(false);
			}
				
			h_text.setText(chapter.hoovText);
			h_date.setText(chapter.hoovDate);
			h_up_count.setText(String.valueOf(chapter.hoov_up_ids.size()));
			h_down_count.setText(String.valueOf(chapter.hoov_down_ids.size()));
			return arg1;
		}
		public int updateUpCountView(int arg0, View arg1) {
			View p=(View) arg1.getParent();
			TextView h_up_count = (TextView)p.findViewById(R.id.hoov_up_count);
			TextView h_down_count = (TextView)p.findViewById(R.id.hoov_down_count);
			
			Button h_up_button = (Button)p.findViewById(R.id.hoov_up_button);
			Button h_down_button = (Button)p.findViewById(R.id.hoov_down_button);
			
			String curr_up_value=(String)h_up_count.getText();
			int final_up_value=Integer.parseInt(curr_up_value) + 1;
			h_up_count.setText(String.valueOf(final_up_value));
			
			String curr_down_value=(String)h_down_count.getText();
			int final_down_value=Integer.parseInt(curr_down_value);
			if(!h_down_button.isEnabled()){
				final_down_value= final_down_value- 1;
				h_down_count.setText(String.valueOf(final_down_value));
				h_down_button.setEnabled(true);
				h_down_button.setBackground(getResources().getDrawable(R.drawable.down));
			}
			h_up_button.setBackground(getResources().getDrawable(R.drawable.greenup));
			h_up_button.setEnabled(false);
			
			return final_down_value;
		}
		public int updateDownCountView(int arg0, View arg1) {
			View p=(View) arg1.getParent();
			TextView h_down_count = (TextView)p.findViewById(R.id.hoov_down_count);
			TextView h_up_count = (TextView)p.findViewById(R.id.hoov_up_count);
			
			Button h_up_button = (Button)p.findViewById(R.id.hoov_up_button);
			Button h_down_button = (Button)p.findViewById(R.id.hoov_down_button);
			
			String curr_down_value=(String)h_down_count.getText();
			int final_down_value=Integer.parseInt(curr_down_value) + 1;
			h_down_count.setText(String.valueOf(final_down_value));
			String curr_up_value=(String)h_up_count.getText();
			int final_up_value=Integer.parseInt(curr_up_value);
			if(!h_up_button.isEnabled()){
				final_up_value=final_up_value - 1;
				h_up_count.setText(String.valueOf(final_up_value));
				h_up_button.setEnabled(true);
				h_up_button.setBackground(getResources().getDrawable(R.drawable.up));
			}
			h_down_button.setBackground(getResources().getDrawable(R.drawable.reddown));
			h_down_button.setEnabled(false);
			return final_up_value;
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

				/*JSONObject f = new JSONObject();
				f.put("document.hoov",1);
				
				JSONObject g = new JSONObject();
				g.put("document.id",1);*/
				

				JSONObject s = new JSONObject();
				s.put("_id", "-1");
				
				//s={"priority": 1


				Webb webb = Webb.create();
				JSONArray array=webb.get("https://api.mongolab.com/api/1/databases/hoover/collections/hoov").param("apiKey", "zvbjTNUW6COSTIZxJcPIW7_tniVCnDKC")
						.param("q", q.toString()).ensureSuccess().asJsonArray().getBody();
						/*.param("f", f.toString())
						.param("g", g.toString())*/
						

				for(int i=0;i<array.length();i++){
					HoovChapter hc=new HoovChapter();
					JSONObject obj = (JSONObject)array.get(i);
					JSONObject doc = obj.getJSONObject("document");
					JSONObject ids = obj.getJSONObject("_id");
					JSONArray ups = doc.getJSONArray("hoovUpIds");
					JSONArray downs = doc.getJSONArray("hoovDownIds");
					hc.hoovText=doc.getString("hoov");
					hc.mongoHoovId=ids.getString("$oid");
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