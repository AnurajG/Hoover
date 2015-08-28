package com.hoover.linkedinoauth;

import java.io.BufferedReader;
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
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hoover.util.HoovChapter;
import com.hoover.util.HoovFetchParams;

public class HomeTopFragment extends ListFragment implements OnRefreshListener{

	private SwipeRefreshLayout refreshLayout;
	String city;
	String company;
	String userId;
	private Context context;
	Button hoov_in;
	//ListView listview;
	ProgressDialog mProgressDialog;
	HoovListAdapter adapter;
	private int limit = 8;
	private List<HoovChapter> HoovChapterlist_t = null;
	public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
	
	public static final HomeTopFragment newInstance(String message,Context context, String comp, String cit,String id) {
		HomeTopFragment f = new HomeTopFragment(context,comp,cit,id);
		Bundle bdl = new Bundle(1);
		bdl.putString(EXTRA_MESSAGE, message);
		f.setArguments(bdl);
		return f;

	}

	public HomeTopFragment(Context context, String comp, String cit, String id) {
		this.context = context;
		this.company = comp;
		this.city=cit;
		this.userId=id;
	}
	
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view;
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_home);
		//============================?
		/*Integer status = NetworkUtil.getConnectivityStatus(this);

		if(status==NetworkUtil.TYPE_NOT_CONNECTED){
			Intent noNetIntent = new Intent(this, NoNetConnectivity.class);
			this.startService(noNetIntent);
		}else*/
		view = inflater.inflate(R.layout.activity_home, container, false);
		//SharedPreferences preferences = this.getSharedPreferences("user_info", 0);
		//String userComapny = preferences.getString("userCompany", null);
		//String userCity = preferences.getString("userCity", null);

		final HoovFetchParams params=new HoovFetchParams();
		params.city=city;
		params.comapny=company;
		new GetHoovsAsyncTask().execute(params);
		refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
		/*hoov_in=(Button) findViewById(R.id.hoov_in);
		hoov_in.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(HomeActivity.this,HoovActivity.class);
				startActivity(intent); 
			} 
		});*/
		refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				new GetHoovsAsyncTask().doInBackground(params);
				if (refreshLayout.isRefreshing()) {
					refreshLayout.setRefreshing(false);
				}
			}
		});
		return view;
	}
	
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		HoovChapter selectedHoov = new HoovChapter();
		selectedHoov = adapter.hoovChapterList.get(position);
		Intent myIntent = new Intent(context, HoovDetailsActivity.class);
		myIntent.putExtra("mongodbHoovId",selectedHoov.mongoHoovId);
		myIntent.putExtra("text",selectedHoov.hoovText);
		myIntent.putExtra("date",selectedHoov.hoovDate);
		Toast.makeText(context, selectedHoov.hoovDate + " ID #", Toast.LENGTH_SHORT).show();

		startActivity(myIntent);

	}

	public void myUPClickHandler(View v) {
		RelativeLayout rl=(RelativeLayout)v.getParent();
		final int position = getListView().getPositionForView(rl);
		HoovChapter selectedHoov = new HoovChapter();
		selectedHoov = adapter.hoovChapterList.get(position);
		String hoovId=selectedHoov.mongoHoovId;
		//SharedPreferences preferences = this.getSharedPreferences("user_info", 0);
		//String userId = preferences.getString("userId", null);
		TextView h_down_count = (TextView)rl.findViewById(R.id.hoov_down_count);
		int currDownCount=Integer.parseInt((String)h_down_count.getText());
		
		int finalDownCount=adapter.updateUpCountView(position, v);
		Intent intent = new Intent(getActivity(), SaveLikeDislikeService.class);
		intent.putExtra(SaveLikeDislikeService.userId,userId);
	    intent.putExtra(SaveLikeDislikeService.hoovId,hoovId);
	    intent.putExtra(SaveLikeDislikeService.insertUp,true);
	    if(finalDownCount<currDownCount)
	    	intent.putExtra(SaveLikeDislikeService.deleteDown,true);
	    getActivity().startService(intent);
	    System.out.println("yeah");
	}
	public void myDOWNClickHandler(View v) {
		RelativeLayout rl=(RelativeLayout)v.getParent();
		final int position = getListView().getPositionForView(rl);
		HoovChapter selectedHoov = new HoovChapter();
		selectedHoov = adapter.hoovChapterList.get(position);
		String hoovId=selectedHoov.mongoHoovId;
		//SharedPreferences preferences = this.getSharedPreferences("user_info", 0);
		//String userId = preferences.getString("userId", null);
		TextView h_up_count = (TextView)rl.findViewById(R.id.hoov_up_count);
		int currUpCount=Integer.parseInt((String)h_up_count.getText());
		
		int finalUpCount=adapter.updateDownCountView(position, v);
		Intent intent = new Intent(getActivity(), SaveLikeDislikeService.class);
	    intent.putExtra(SaveLikeDislikeService.userId,userId);
	    intent.putExtra(SaveLikeDislikeService.hoovId,hoovId);
	    intent.putExtra(SaveLikeDislikeService.insertDown,true);
	    if(finalUpCount<currUpCount)
	    	intent.putExtra(SaveLikeDislikeService.deleteUp,true);
	    getActivity().startService(intent);	
	}
	public String getUserId()
	{
		//SharedPreferences preferences = this.getSharedPreferences("user_info", 0);
		//String userId = preferences.getString("userId", null);
		
		return userId;

	}
	public class HoovListAdapter extends BaseAdapter{

		String company;
		String city;
		List<HoovChapter> hoovChapterList =null;
		String userID=getUserId();
		Context mcontext;
		LayoutInflater inflater;
		protected int count;
		
		public HoovListAdapter(Context con,List<HoovChapter> hoovChapterList) {
			mcontext=con;
			inflater = LayoutInflater.from(mcontext);
			this.hoovChapterList = hoovChapterList;
		}
		
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
				arg1 = inflater.inflate(R.layout.hoov_list,null);
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
	public class GetHoovsAsyncTask extends AsyncTask<HoovFetchParams, Void,HoovFetchParams> {
		@Override
		protected void onPreExecute(){
			super.onPreExecute();
			mProgressDialog = new ProgressDialog(getActivity());
			mProgressDialog.setTitle("Load Hoovs.....");
			mProgressDialog.setMessage("Loading...");
			mProgressDialog.setIndeterminate(false);
			mProgressDialog.show();
		}


		@Override
		protected HoovFetchParams doInBackground(HoovFetchParams... params) {
			HoovChapterlist_t=new ArrayList<HoovChapter>();
			try 
			{			
				HoovFetchParams u = params[0];
				JSONArray array;
				JSONObject p = new JSONObject();
				p.put("document.company",u.comapny);
				p.put("document.city",u.city);
				JSONObject q = new JSONObject();
				q.put("_id", -1);
				String url_str="http://nodejs-hooverest.rhcloud.com/hoovlist?city="+city+"&company="+company;
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
					//JSONObject doc = obj.getJSONObject("document");
					//JSONObject ids = obj.getJSONObject("_id");
					JSONArray ups = obj.getJSONArray("_hoovUpIds");
					JSONArray downs = obj.getJSONArray("_hoovDownIds");
					hc.hoovText=obj.getString("_hoov");
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
			return params[0];	
		}

		@Override
		protected void onPostExecute(final HoovFetchParams  data){
			//listview = (ListView) findViewById(android.R.id.list);
			adapter = new HoovListAdapter(context,HoovChapterlist_t);
			setListAdapter(adapter);
			mProgressDialog.dismiss();
			getListView().setOnScrollListener(new OnScrollListener() {
 
				@Override
				public void onScrollStateChanged(AbsListView view,
						int scrollState) { // TODO Auto-generated method stub
					int threshold = 1;
					int count = getListView().getCount();
 
					if (scrollState == SCROLL_STATE_IDLE) {
						if (getListView().getLastVisiblePosition() >= count
								- threshold) {
							// Execute LoadMoreDataTask AsyncTask
							new LoadMoreDataTask().execute(data);
						}
					}
				}
 
				@Override
				public void onScroll(AbsListView view, int firstVisibleItem,
						int visibleItemCount, int totalItemCount) {
					// TODO Auto-generated method stub
 
				}
 
			});
		
		}
		private class LoadMoreDataTask extends AsyncTask<HoovFetchParams, Void, Void> {
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				mProgressDialog.setTitle("Load More Hoovs.....");
				mProgressDialog.setMessage("Loading more...");
				mProgressDialog.setIndeterminate(false);
				mProgressDialog.show();
			}
 
			@Override
			protected Void doInBackground(HoovFetchParams... params) {
				// Create the array

				HoovChapterlist_t=new ArrayList<HoovChapter>();
				try 
				{			
					HoovFetchParams u = params[0];
					JSONArray array;
					JSONObject p = new JSONObject();
					p.put("document.company",u.comapny);
					p.put("document.city",u.city);
					JSONObject q = new JSONObject();
					q.put("_id", -1);
					String url_str="http://nodejs-hooverest.rhcloud.com/hoovlist?city="+city+"&company="+company;
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
						//JSONObject doc = obj.getJSONObject("document");
						//JSONObject ids = obj.getJSONObject("_id");
						JSONArray ups = obj.getJSONArray("_hoovUpIds");
						JSONArray downs = obj.getJSONArray("_hoovDownIds");
						hc.hoovText=obj.getString("_hoov");
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
					return null;
				}
				return null;	
			
			}
 
			@Override
			protected void onPostExecute(Void result) {
				int position = getListView().getLastVisiblePosition();
				adapter = new HoovListAdapter(context,HoovChapterlist_t);
				getListView().setAdapter(adapter);
				getListView().setSelectionFromTop(position, 0);
				mProgressDialog.dismiss();
			}
		}

	}
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		
	}
}