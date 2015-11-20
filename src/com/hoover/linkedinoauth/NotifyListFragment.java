package com.hoover.linkedinoauth;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;

import com.hoover.floating.ScrollDirectionListener;
import com.hoover.linkedinoauth.HomeFollowViewAdapter.MyClickListener;
import com.hoover.util.HoovActionOptions;
import com.hoover.util.HoovChapter;
import com.hoover.util.NonSwipeableViewPager;
import com.yalantis.phoenix.PullToRefreshView;

public class NotifyListFragment extends Fragment{

	private	PullToRefreshView  mPullToRefreshView;

	String city;
	String company;
	String userId;
	private Context context;
	boolean noMoreDataToLoad=false;

	ProgressBar mProgressBar;
	//HoovListAdapter adapter;

	private RecyclerView mRecyclerView;
	private NotifyListViewAdapter mAdapter;

	private LinearLayoutManager mLayoutManager;
	ArrayList<HoovChapter> results = new ArrayList<HoovChapter>();

	private int limit = 8;
	public static final int TOAST_DELAY = 4000;
	private List<HoovChapter> HoovChapterlist_t = null;
	private ArrayList<HoovChapter> dreggn = new ArrayList<HoovChapter>();
	public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";


	public static final NotifyListFragment newInstance(String message,Context context, String comp, String cit,String id) {
		NotifyListFragment f = new NotifyListFragment(context,comp,cit,id);
		Bundle bdl = new Bundle(1);
		bdl.putString(EXTRA_MESSAGE, message);
		f.setArguments(bdl);
		return f;

	}

	public NotifyListFragment(Context context, String comp, String cit, String id) {
		this.context = context;
		this.company = comp;
		this.city=cit;
		this.userId=id;
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
		view = inflater.inflate(R.layout.activity_notify_list, container, false);
		//NonSwipeableViewPager vp=(NonSwipeableViewPager) getActivity().findViewById(R.id.profile_pager);
		//vp.setPagingEnabled(true);
		mRecyclerView = (RecyclerView)view.findViewById(R.id.notify_recycler_view);
		HoovChapterlist_t=new ArrayList<HoovChapter>();



		mPullToRefreshView = (PullToRefreshView) view.findViewById(R.id.swipe_container);
		mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
			@Override
			public void onRefresh() {
				onRefreshStarted(getView());
			}
		});

		mRecyclerView.setHasFixedSize(true);
		mLayoutManager = new LinearLayoutManager(context);
		mRecyclerView.setLayoutManager(mLayoutManager);
		mAdapter = new NotifyListViewAdapter(getDataSet(),userId);
		mRecyclerView.setAdapter(mAdapter);
		//mAdapter.setOnItemClickListener(new HoovListClickListener());

		mProgressBar=(ProgressBar)view.findViewById(R.id.a_progressbar);
		mProgressBar.setBackgroundResource(R.drawable.anim_progress);
		final AnimationDrawable mailAnimation = (AnimationDrawable) mProgressBar.getBackground();
		mProgressBar.post(new Runnable() {
			public void run() {
				if ( mailAnimation != null ) mailAnimation.start();
			}
		});

		Void params = null;
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
				try {
					getHoovs(false);
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
					mAdapter = new NotifyListViewAdapter(results,userId);
					mRecyclerView.setAdapter(mAdapter);

					// Notify PullToRefreshLayout that the refresh has finished
					mPullToRefreshView.setRefreshing(false);
				}else{
					mPullToRefreshView.setRefreshing(false);
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
	public void getHoovs(boolean increaseLimit) throws Exception{
		SharedPreferences preferences2 = context.getSharedPreferences("hoov_tmp", 0);
		String notifyArray = preferences2.getString("notifyArray", null);

		if(notifyArray!=null){

			try {
				JSONArray array=new JSONArray(notifyArray);
				for(int i=0;i<notifyArray.length();i++){
					JSONObject obj=array.getJSONObject(i);
					HoovChapter hc = new HoovChapter();
					hc.hoovText=obj.getJSONObject("notifyText").toString();
					hc.hoovDate=obj.getString("date");
					HoovChapterlist_t.add(hc);
				}
			}catch(JSONException e){

			}
		}


	}
	public class GetHoovsAsyncTask extends AsyncTask<Void, Integer,Void> {
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
		protected Void doInBackground(Void... params) {
			HoovChapterlist_t=new ArrayList<HoovChapter>();
			try {
				getHoovs(false);
			} catch (Exception e) {
				this.e=e;
				e.printStackTrace();
			}
			return params[0];	
		}

		@Override
		protected void onPostExecute(final Void  data){
			if(e==null){
				//listview = (ListView) findViewById(android.R.id.list);
				results.clear();
				results.addAll(HoovChapterlist_t);
				mAdapter = new NotifyListViewAdapter(results,userId);
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
			}

		}
		private class LoadMoreDataTask extends AsyncTask<Void, Void, Void> {
			private Exception e=null;
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}

			@Override
			protected Void doInBackground(Void... params) {
				// Create the array
				HoovChapterlist_t=new ArrayList<HoovChapter>();
				try {
					getHoovs(true);
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
					mAdapter = new NotifyListViewAdapter(results,userId);
					mRecyclerView.setAdapter(mAdapter);
					registerForContextMenu(mRecyclerView);
					mLayoutManager.scrollToPositionWithOffset(position, 0);
				}else{

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