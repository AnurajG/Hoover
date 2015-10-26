package com.hoover.linkedinoauth;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hoover.floating.FloatingActionButton;
import com.hoover.floating.ScrollDirectionListener;
import com.hoover.linkedinoauth.HomeFollowViewAdapter.MyClickListener;
import com.hoover.util.EmojiMapUtil;
import com.hoover.util.HoovActionOptions;
import com.hoover.util.HoovChapter;
import com.hoover.util.HoovFetchParams;
import com.hoover.util.HoovFetchParams.eOrder;
import com.hoover.util.HoovFetchParams.eType;
import com.yalantis.phoenix.PullToRefreshView;

public class HomeFollowFragment extends Fragment{

	private	PullToRefreshView  mPullToRefreshView;

	String city;
	String company;
	String userId;
	private Context context;
	boolean noMoreDataToLoad=false;

	ProgressBar mProgressBar;
	//HoovListAdapter adapter;

	private RecyclerView mRecyclerView;
	private HomeFollowViewAdapter mAdapter;

	private LinearLayoutManager mLayoutManager;
	ArrayList<HoovChapter> results = new ArrayList<HoovChapter>();

	private int limit = 8;
	public static final int TOAST_DELAY = 4000;
	private List<HoovChapter> HoovChapterlist_t = null;
	private ArrayList<HoovChapter> dreggn = new ArrayList<HoovChapter>();
	public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
	private eType type;
	private View toast_layout;

	public static final HomeFollowFragment newInstance(String message,Context context, String comp, String cit,String id, eType t) {
		HomeFollowFragment f = new HomeFollowFragment(context,comp,cit,id,t);
		Bundle bdl = new Bundle(1);
		bdl.putString(EXTRA_MESSAGE, message);
		f.setArguments(bdl);
		return f;

	}

	public void refresh(boolean isHot){
		//results.addAll(HoovChapterlist_t);
		mAdapter = new HomeFollowViewAdapter(dreggn,context,userId);
		mRecyclerView.setAdapter(mAdapter);
		if(isHot){
			final HoovFetchParams params=new HoovFetchParams();
			params.city=city;
			params.comapny=company;
			params.order=eOrder.TOP;
			params.type=type;
			new GetHoovsAsyncTask().execute(params);

		}else{
			final HoovFetchParams params=new HoovFetchParams();
			params.city=city;
			params.comapny=company;
			params.order=eOrder.NEW;
			params.type=type;
			new GetHoovsAsyncTask().execute(params);
		}
	}

	public HomeFollowFragment(Context context, String comp, String cit, String id, eType t) {
		this.context = context;
		this.company = comp;
		this.city=cit;
		this.userId=id;
		this.type=t;
	}
	public class HoovScrollDirectionListener implements ScrollDirectionListener{

		@Override
		public void onScrollDown() {
			ActionBar ac = ((AppCompatActivity)getActivity()).getSupportActionBar();
			ac.show();
		}

		@Override
		public void onScrollUp() {
			ActionBar ac = ((AppCompatActivity)getActivity()).getSupportActionBar();
			ac.hide();
		}
		
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view;
		super.onCreate(savedInstanceState);
		view = inflater.inflate(R.layout.activity_home, container, false);
		mRecyclerView = (RecyclerView)view.findViewById(R.id.hoov_recycler_view);
		FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
		fab.attachToRecyclerView(mRecyclerView,new HoovScrollDirectionListener());
		fab.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(),HoovActivity.class);
				startActivity(intent); 

			} 

		});


		mPullToRefreshView = (PullToRefreshView) view.findViewById(R.id.swipe_container);
		mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
			@Override
			public void onRefresh() {
				onRefreshStarted(getView());
			}
		});

		toast_layout = inflater.inflate(R.layout.hoov_toast,
				(ViewGroup) view.findViewById(R.id.toast_layout_root));

		final HoovFetchParams params=new HoovFetchParams();
		mRecyclerView.setHasFixedSize(true);
		mLayoutManager = new LinearLayoutManager(context);
		mRecyclerView.setLayoutManager(mLayoutManager);
		mAdapter = new HomeFollowViewAdapter(getDataSet(),context,userId);
		mRecyclerView.setAdapter(mAdapter);
		mAdapter.setOnItemClickListener(new HoovListClickListener());

		params.city=city;
		params.comapny=company;
		params.order=eOrder.NEW;
		params.type=type;

		mProgressBar=(ProgressBar)view.findViewById(R.id.a_progressbar);
		mProgressBar.setBackgroundResource(R.drawable.anim_progress);
		final AnimationDrawable mailAnimation = (AnimationDrawable) mProgressBar.getBackground();
		mProgressBar.post(new Runnable() {
			public void run() {
				if ( mailAnimation != null ) mailAnimation.start();
			}
		});


		new GetHoovsAsyncTask().execute(params);
		return view;
	}

	private ArrayList<HoovChapter> getDataSet() {
		ArrayList<HoovChapter> results = new ArrayList<HoovChapter>();
		return results;
	}


	/*@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		ViewGroup viewGroup = (ViewGroup) view;

		// As we're using a ListFragment we create a PullToRefreshLayout manually
		mPullToRefreshLayout = new PullToRefreshLayout(viewGroup.getContext());

		// We can now setup the PullToRefreshLayout
		ActionBarPullToRefresh.from(getActivity())
		// We need to insert the PullToRefreshLayout into the Fragment's ViewGroup
		.insertLayoutInto(viewGroup)
		// Here we mark just the ListView and it's Empty View as pullable
		.allChildrenArePullable()
		.listener(this)
		.setup(mPullToRefreshLayout);

	}*/


	public String getUserId(){
		return userId;
	}

	public void onRefreshStarted(View view) {
		new AsyncTask<Void, Void, Void>() {
			private Exception e=null;

			@Override
			protected Void doInBackground(Void... params) {
				HoovChapterlist_t=new ArrayList<HoovChapter>();
				final HoovFetchParams para=new HoovFetchParams();
				para.city=city;
				para.comapny=company;
				para.order=eOrder.NEW;
				para.type=type;
				try {
					getHoovs(para,false);
				} catch (Exception e) {
					this.e=e;
					e.printStackTrace();
				}
				return null;	

			}

			@Override
			protected void onPostExecute(Void result) {
				if(e==null){
					super.onPostExecute(result);
					results.clear();
					results.addAll(HoovChapterlist_t);
					mAdapter = new HomeFollowViewAdapter(results,context,userId);
					mRecyclerView.setAdapter(mAdapter);

					// Notify PullToRefreshLayout that the refresh has finished
					mPullToRefreshView.setRefreshing(false);
				}else{
					mPullToRefreshView.setRefreshing(false);
					TextView text = (TextView) toast_layout.findViewById(R.id.text);
					text.setText("No internet. Check network connection.");

					Toast toast = new Toast(context);
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
					toast.setDuration(TOAST_DELAY);
					toast.setView(toast_layout);
					toast.show();
				}
			}
		}.execute();


	}
	public class HoovListClickListener implements MyClickListener{

		@Override
		public void onItemClick(int position, View v) {
			HoovChapter selectedHoov = new HoovChapter();
			selectedHoov = mAdapter.mDataset.get(position);
			Intent myIntent = new Intent(context, HoovDetailsActivity.class);
			myIntent.putExtra("chapter", selectedHoov);
			startActivity(myIntent);

		}

	}
	public void getHoovs(HoovFetchParams hp,boolean increaseLimit) throws Exception{
		HoovFetchParams u = hp;
		JSONArray array;
		URL url = null;
		if(increaseLimit)
			limit=limit+8;
		switch(u.type){
		case HOME:
			switch(u.order){
			case NEW:
				JSONObject p = new JSONObject();
				p.put("document.company",u.comapny);
				p.put("document.city",u.city);
				p.put("document.parentId","null");
				p.put("document.status",0);
				//publishProgress(0);
				JSONObject q = new JSONObject();
				q.put("_id", -1);
				String url_str="https://api.mongolab.com/api/1/databases/hoover/collections/hoov?q="+p.toString()+"&l="+limit+"&s="+q+"&apiKey=zvbjTNUW6COSTIZxJcPIW7_tniVCnDKC";
				url = new URL(url_str);//(URLEncoder.encode("https://api.mongolab.com/api/1/databases/hoover/collections/hoov?q="+p.toString()+"&apiKey=zvbjTNUW6COSTIZxJcPIW7_tniVCnDKC","UTF-8"));
				URI uri=new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
				url = uri.toURL();
				break;
			case TOP:
				JSONObject p1 = new JSONObject();
				p1.put("document.company",u.comapny);
				p1.put("document.city",u.city);
				JSONObject q1 = new JSONObject();
				q1.put("_id", -1);
				String url_str1="http://nodejs-hooverest.rhcloud.com/hoovlist?city="+city+"&company="+company;
				url = new URL(url_str1);//(URLEncoder.encode("https://api.mongolab.com/api/1/databases/hoover/collections/hoov?q="+p.toString()+"&apiKey=zvbjTNUW6COSTIZxJcPIW7_tniVCnDKC","UTF-8"));
				URI uri1=new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
				url = uri1.toURL();
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				Random random = new Random();
				conn.setRequestMethod("GET");
				break;
			}
			break;
		case FOLLOW:
			JSONObject p = new JSONObject();
			p.put("document.company",u.comapny);
			p.put("document.city",u.city);
			JSONObject q = new JSONObject();
			q.put("_id", -1);
			String url_str="http://nodejs-hooverest.rhcloud.com/followhoovlist?userId="+userId;
			url = new URL(url_str);//(URLEncoder.encode("https://api.mongolab.com/api/1/databases/hoover/collections/hoov?q="+p.toString()+"&apiKey=zvbjTNUW6COSTIZxJcPIW7_tniVCnDKC","UTF-8"));
			URI uri=new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
			url = uri.toURL();
		}

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
		//publishProgress(90);
		array = new JSONArray(responseStrBuilder.toString());
		for(int i=0;i<array.length();i++){
			HoovChapter hc=new HoovChapter();
			JSONObject obj = (JSONObject)array.get(i);
			JSONObject doc = obj.getJSONObject("document");
			String hid = "";
			switch(u.type){
			case HOME:
				switch(u.order){
				case NEW:
					JSONObject ids = obj.getJSONObject("_id");
					hid=ids.getString("$oid");
					break;
				case TOP:
					hid=obj.getString("_id");
					break;
				}
				break;
			case FOLLOW:
				hid=obj.getString("_id");
				break;
			}

			JSONArray ups = doc.getJSONArray("hoovUpIds");
			JSONArray downs = doc.getJSONArray("hoovDownIds");
			JSONArray followers= doc.getJSONArray("followerUserIds");
			JSONArray abuseds= doc.getJSONArray("abuserUserIds");
			hc.hoovText=EmojiMapUtil.replaceCheatSheetEmojis(doc.getString("hoov"));
			hc.mongoHoovId=hid;
			hc.hoov_up_ids=new Gson().fromJson(ups.toString(), new TypeToken<List<String>>(){}.getType());
			hc.hoov_down_ids =new Gson().fromJson(downs.toString(), new TypeToken<List<String>>(){}.getType());
			hc.hoovUserId =doc.getString("id");
			hc.commentHoovIds = new Gson().fromJson(doc.getJSONArray("commentHoovIds").toString(), new TypeToken<List<String>>(){}.getType());
			ArrayList<String> followArr=new Gson().fromJson(followers.toString(), new TypeToken<List<String>>(){}.getType());
			ArrayList<String> abuseArr=new Gson().fromJson(abuseds.toString(), new TypeToken<List<String>>(){}.getType());

			hc.abused=false;
			hc.followed=false;
			if (followers != null && followArr.contains(userId)) { 
				hc.followed=true;
			} 
			if (abuseds != null && abuseArr.contains(userId)) { 
				hc.abused=true;
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
	public class GetHoovsAsyncTask extends AsyncTask<HoovFetchParams, Integer,HoovFetchParams> {
		private Exception e=null;
		@Override
		protected void onPreExecute(){
			super.onPreExecute();
			mProgressBar.setVisibility(View.VISIBLE);
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			mProgressBar.setProgress(values[0]);
		}
		@Override
		protected HoovFetchParams doInBackground(HoovFetchParams... params) {
			HoovChapterlist_t=new ArrayList<HoovChapter>();
			try {
				getHoovs(params[0],false);
			} catch (Exception e) {
				this.e=e;
				e.printStackTrace();
			}
			return params[0];	
		}

		@Override
		protected void onPostExecute(final HoovFetchParams  data){
			if(e==null){
				//listview = (ListView) findViewById(android.R.id.list);
				results.clear();
				results.addAll(HoovChapterlist_t);
				mAdapter = new HomeFollowViewAdapter(results,context,userId);
				mRecyclerView.setAdapter(mAdapter);

				mProgressBar.setVisibility(View.GONE);
				mRecyclerView.addOnScrollListener(new OnScrollListener() {

					@Override
					public void onScrollStateChanged(RecyclerView view,
							int scrollState) { // TODO Auto-generated method stub
						int threshold = 1;
						int count = mRecyclerView.getAdapter().getItemCount();

						if (scrollState == RecyclerView.SCROLL_STATE_IDLE) {
							if (mRecyclerView.getChildCount() >= count
									- threshold) {
								if(noMoreDataToLoad){
									AlertDialog alertDialog = new AlertDialog.Builder(context).create();
									alertDialog.setTitle("Alert");
									alertDialog.setMessage("No more hoovs to show");
									alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
											new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int which) {
											dialog.dismiss();
										}
									});
									alertDialog.show();

								}else{
									// Execute LoadMoreDataTask AsyncTask
									new LoadMoreDataTask().execute(data);
								}
							}
						}
					}
				});
			}else{
				mProgressBar.setVisibility(View.GONE);
				TextView text = (TextView) toast_layout.findViewById(R.id.text);
				text.setText("No internet. Check network connection.");

				Toast toast = new Toast(context);
				toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
				toast.setDuration(TOAST_DELAY);
				toast.setView(toast_layout);
				toast.show();
			}

		}
		private class LoadMoreDataTask extends AsyncTask<HoovFetchParams, Void, Void> {
			private Exception e=null;
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}

			@Override
			protected Void doInBackground(HoovFetchParams... params) {
				// Create the array
				HoovChapterlist_t=new ArrayList<HoovChapter>();
				try {
					getHoovs(params[0],true);
				} catch (Exception e) {
					this.e=e;
					e.printStackTrace();
				}
				return null;	
			}

			@Override
			protected void onPostExecute(Void result) {
				if(e==null){
					int position = mRecyclerView.getChildCount();
					results.addAll(HoovChapterlist_t);
					mAdapter = new HomeFollowViewAdapter(results,context,userId);
					mRecyclerView.setAdapter(mAdapter);
					registerForContextMenu(mRecyclerView);
					mLayoutManager.scrollToPositionWithOffset(position, 0);
				}else{
					TextView text = (TextView) toast_layout.findViewById(R.id.text);
					text.setText("No internet. Check network connection.");

					Toast toast = new Toast(context);
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
					toast.setDuration(TOAST_DELAY);
					toast.setView(toast_layout);
					toast.show();
				}
			}
		}

	}
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (v.getId()==android.R.id.list) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
			//menu.setHeaderTitle("Options");
			int pos=info.position;
			HoovChapter selectedHoov = mAdapter.mDataset.get(pos);
			String selectedHoovUserId=selectedHoov.hoovUserId;
			List<String> menuItems=new ArrayList<String>();
			if(!selectedHoovUserId.equals(userId)){//selected hoov is not current user's hoov
				if(!selectedHoov.followed)
					menuItems.add(HoovActionOptions.FOLLOW.getoptionString());
				else
					menuItems.add(HoovActionOptions.UNFOLLOW.getoptionString());
				menuItems.add(HoovActionOptions.MARK_ABUSE.getoptionString());
			}else{//selected hoov is current user's hoov
				menuItems.add(HoovActionOptions.DELETE.getoptionString());

			}
			for (int i = 0; i<menuItems.size(); i++) {
				menu.add(Menu.NONE, i, i, menuItems.get(i));
			}
		}
	}
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		//int menuItemIndex = item.getItemId();
		String menuItemName=item.getTitle().toString();
		int pos=info.position;
		HoovChapter selectedHoov = mAdapter.mDataset.get(pos);
		String selectedHoovUserId=selectedHoov.hoovUserId;
		final String selectedmongoHoovId=selectedHoov.mongoHoovId;
		AlertDialog alertDialog;
		HoovActionOptions[] al=HoovActionOptions.values();
		String actionName=null;
		for(int i=0;i<HoovActionOptions.values().length;i++){
			if(al[i].optionString.equals(menuItemName)){
				actionName=al[i].name();
				break;
			}
		}
		if(actionName!= null){
			switch(HoovActionOptions.valueOf(actionName)){
			case FOLLOW:
				SaveFollowHoovInfoAsyncTask tsk= new SaveFollowHoovInfoAsyncTask(selectedmongoHoovId,userId);
				tsk.execute();
				alertDialog = new AlertDialog.Builder(context).create();
				alertDialog.setTitle("Alert");
				alertDialog.setMessage("You are now following this hoov");
				alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
						new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				alertDialog.show();
				selectedHoov.followed=true;
				break;
			case UNFOLLOW:
				DeleteFollowHoovInfoAsyncTask tsk2= new DeleteFollowHoovInfoAsyncTask(selectedmongoHoovId,userId);
				tsk2.execute();
				alertDialog = new AlertDialog.Builder(context).create();
				alertDialog.setTitle("Alert");
				alertDialog.setMessage("You have stopped following this hoov");
				alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
						new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				alertDialog.show();
				selectedHoov.followed=false;	
				break;
			case MARK_ABUSE:
				if(selectedHoov.abused){
					alertDialog = new AlertDialog.Builder(context).create();
					alertDialog.setTitle("Alert");
					alertDialog.setMessage("You have already abused this hoov");
					alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
					alertDialog.show();

				}else{
					SaveAbuseHoovInfoAsyncTask tsk1= new SaveAbuseHoovInfoAsyncTask(selectedmongoHoovId,userId);
					tsk1.execute();
					alertDialog = new AlertDialog.Builder(context).create();
					alertDialog.setTitle("Alert");
					alertDialog.setMessage("You have successfully abused this hoov");
					alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
					alertDialog.show();
					selectedHoov.abused=true;
				}
				break;
			case DELETE:
				new AlertDialog.Builder(this.getActivity())
				.setTitle("Delete Hoov")
				.setMessage("Are you sure you want to delete this hoov?")
				.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) { 
						DeleteHoovsAsyncTask tsk= new DeleteHoovsAsyncTask(context,selectedmongoHoovId);
						tsk.execute();
					}
				})
				.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) { 

					}
				})
				.setIcon(android.R.drawable.ic_dialog_alert)
				.show();
				break;
			default:
				break;

			}
		}
		return true;
	}


}