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

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hoover.util.EmojiMapUtil;
import com.hoover.util.HoovActionOptions;
import com.hoover.util.HoovChapter;
import com.hoover.util.HoovFetchParams;
import com.hoover.util.HoovFetchParams.eOrder;
import com.hoover.util.HoovFetchParams.eType;

public class HomeFragment extends ListFragment implements OnRefreshListener{

	private	PullToRefreshLayout mPullToRefreshLayout;

	String city;
	String company;
	String userId;
	private Context context;
	boolean noMoreDataToLoad=false;
	
	ProgressBar mProgressBar;
	HoovListAdapter adapter;
	private int limit = 8;
	private List<HoovChapter> HoovChapterlist_t = null;
	private List<HoovChapter> dreggn = new ArrayList<HoovChapter>();
	public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
	private eType type;
	boolean likeButtonPressed=false;
	boolean dislikeButtonPressed=false;
	public static final HomeFragment newInstance(String message,Context context, String comp, String cit,String id, eType t) {
		HomeFragment f = new HomeFragment(context,comp,cit,id,t);
		Bundle bdl = new Bundle(1);
		bdl.putString(EXTRA_MESSAGE, message);
		f.setArguments(bdl);
		return f;

	}

	public void refresh(boolean isHot){
		adapter = new HoovListAdapter(context,dreggn);
		setListAdapter(adapter);
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

	public HomeFragment(Context context, String comp, String cit, String id, eType t) {
		this.context = context;
		this.company = comp;
		this.city=cit;
		this.userId=id;
		this.type=t;
	}


	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view;
		super.onCreate(savedInstanceState);
		view = inflater.inflate(R.layout.activity_home, container, false);
		final HoovFetchParams params=new HoovFetchParams();
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
	@Override
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
		.theseChildrenArePullable(android.R.id.list, android.R.id.empty)
		.listener(this)
		.setup(mPullToRefreshLayout);

	}


	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		HoovChapter selectedHoov = new HoovChapter();
		selectedHoov = adapter.hoovChapterList.get(position);
		Intent myIntent = new Intent(context, HoovDetailsActivity.class);
		myIntent.putExtra("chapter", selectedHoov);
		startActivity(myIntent);

	}

	public void myUPClickHandler(View v) {
		RelativeLayout rl=(RelativeLayout)v.getParent();
		final int position = getListView().getPositionForView(rl);
		HoovChapter selectedHoov = new HoovChapter();
		selectedHoov = adapter.hoovChapterList.get(position);
		String hoovId=selectedHoov.mongoHoovId;
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
	}
	public void myDOWNClickHandler(View v) {
		RelativeLayout rl=(RelativeLayout)v.getParent();
		final int position = getListView().getPositionForView(rl);
		HoovChapter selectedHoov = new HoovChapter();
		selectedHoov = adapter.hoovChapterList.get(position);
		String hoovId=selectedHoov.mongoHoovId;
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
			TextView h_comment_count=(TextView)arg1.findViewById(R.id.hoov_comment_count);
			Button h_up_button=(Button)arg1.findViewById(R.id.hoov_up_button);
			Button h_down_button=(Button)arg1.findViewById(R.id.hoov_down_button);
			Button h_comment_button=(Button)arg1.findViewById(R.id.hoov_comment_button);


			View.OnClickListener upHandler = new View.OnClickListener() {
				public void onClick(View v) {
					myUPClickHandler(v);
				}
			};

			View.OnClickListener downHandler = new View.OnClickListener() {
				public void onClick(View v) {
					myDOWNClickHandler(v);
				}
			};
			View.OnClickListener commentHandler = new View.OnClickListener() {
				public void onClick(View v) {
					HoovChapter selectedHoov = new HoovChapter();
					RelativeLayout rl=(RelativeLayout)v.getParent();
					final int position = getListView().getPositionForView(rl);
					selectedHoov = adapter.hoovChapterList.get(position);
					Intent myIntent = new Intent(context, HoovDetailsActivity.class);
					myIntent.putExtra("chapter", selectedHoov);
					startActivity(myIntent);
				}
			};
			h_comment_button.setOnClickListener(commentHandler);
			h_up_button.setOnClickListener(upHandler);
			h_down_button.setOnClickListener(downHandler);
			h_up_button.setTag(0);
			h_down_button.setTag(0);
			HoovChapter chapter = hoovChapterList.get(arg0);

			if(chapter.hoov_up_ids.contains(userID)){
				h_up_button.setBackground(getResources().getDrawable(R.drawable.greenup));
				h_up_button.setTag(1);
				//h_up_button.setEnabled(false);
			}else if(chapter.hoov_down_ids.contains(userID)){
				h_down_button.setBackground(getResources().getDrawable(R.drawable.reddown));
				h_down_button.setTag(1);
				//h_down_button.setEnabled(false);
			}
			h_text.setText(chapter.hoovText);
			h_date.setText(chapter.hoovDate);
			h_up_count.setText(String.valueOf(chapter.hoov_up_ids.size()));
			h_down_count.setText(String.valueOf(chapter.hoov_down_ids.size()));
			h_comment_count.setText(String.valueOf(chapter.commentHoovIds.size()));
			return arg1;
		}
		public int updateUpCountView(int arg0, View arg1) {
			View p=(View) arg1.getParent();
			TextView h_up_count = (TextView)p.findViewById(R.id.hoov_up_count);
			TextView h_down_count = (TextView)p.findViewById(R.id.hoov_down_count);

			Button h_up_button = (Button)p.findViewById(R.id.hoov_up_button);
			Button h_down_button = (Button)p.findViewById(R.id.hoov_down_button);

			String curr_up_value=(String)h_up_count.getText();
			int final_up_value=Integer.parseInt(curr_up_value);
			
			String curr_down_value=(String)h_down_count.getText();
			int final_down_value=Integer.parseInt(curr_down_value);
			if(h_up_button.getTag().equals(1)){
				//If like button is green
				final_up_value=Integer.parseInt(curr_up_value) - 1;
				h_up_button.setBackground(getResources().getDrawable(R.drawable.up));
				h_up_button.setTag(0);
			}else{
				//If like button is not green 
				final_up_value=Integer.parseInt(curr_up_value) + 1;
				h_up_button.setTag(1);
				h_up_button.setBackground(getResources().getDrawable(R.drawable.greenup));
				if(h_down_button.getTag().equals(1)){
					//if dislike button is red
					final_down_value= final_down_value- 1;
					h_down_count.setText(String.valueOf(final_down_value));
					h_down_button.setBackground(getResources().getDrawable(R.drawable.down));
					h_down_button.setTag(0);;
				}
			}
			h_up_count.setText(String.valueOf(final_up_value));
			return final_down_value;
		}
		public int updateDownCountView(int arg0, View arg1) {
			View p=(View) arg1.getParent();
			TextView h_down_count = (TextView)p.findViewById(R.id.hoov_down_count);
			TextView h_up_count = (TextView)p.findViewById(R.id.hoov_up_count);

			Button h_up_button = (Button)p.findViewById(R.id.hoov_up_button);
			Button h_down_button = (Button)p.findViewById(R.id.hoov_down_button);

			String curr_down_value=(String)h_down_count.getText();
			int final_down_value=Integer.parseInt(curr_down_value);
			
			String curr_up_value=(String)h_up_count.getText();
			int final_up_value=Integer.parseInt(curr_up_value);
			
			
			if(h_down_button.getTag().equals(1)){
				//If dislike button is red
				final_down_value=Integer.parseInt(curr_down_value) - 1;
				h_down_button.setBackground(getResources().getDrawable(R.drawable.down));
				h_down_button.setTag(0);
			}else{
				final_down_value=Integer.parseInt(curr_down_value) + 1;
				h_down_button.setBackground(getResources().getDrawable(R.drawable.reddown));
				h_down_button.setTag(1);
				//If like button is green
				if(h_up_button.getTag().equals(1)){
					final_up_value=final_up_value - 1;
					h_up_count.setText(String.valueOf(final_up_value));
					h_up_button.setBackground(getResources().getDrawable(R.drawable.up));
					h_up_button.setTag(0);
				}	
			}
			
			h_down_count.setText(String.valueOf(final_down_value));
			//h_down_button.setEnabled(false);
			return final_up_value;
		}

	}
	public void onRefreshStarted(View view) {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				HoovChapterlist_t=new ArrayList<HoovChapter>();
				final HoovFetchParams para=new HoovFetchParams();
				para.city=city;
				para.comapny=company;
				para.order=eOrder.NEW;
				para.type=type;
				getHoovs(para,false);
				return null;	

			}

			@Override
			protected void onPostExecute(Void result) {
				// super.onPostExecute(result);

				adapter = new HoovListAdapter(context,HoovChapterlist_t);
				setListAdapter(adapter);

				// Notify PullToRefreshLayout that the refresh has finished
				mPullToRefreshLayout.setRefreshComplete();
			}
		}.execute();


	}
	public void getHoovs(HoovFetchParams hp,boolean increaseLimit){
		try{
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
		catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	public class GetHoovsAsyncTask extends AsyncTask<HoovFetchParams, Integer,HoovFetchParams> {
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
			getHoovs(params[0],false);
			return params[0];	
		}

		@Override
		protected void onPostExecute(final HoovFetchParams  data){
			//listview = (ListView) findViewById(android.R.id.list);
			adapter = new HoovListAdapter(context,HoovChapterlist_t);
			setListAdapter(adapter);
			registerForContextMenu(getListView());

			mProgressBar.setVisibility(View.GONE);
			getListView().setOnScrollListener(new OnScrollListener() {

				@Override
				public void onScrollStateChanged(AbsListView view,
						int scrollState) { // TODO Auto-generated method stub
					int threshold = 1;
					int count = getListView().getCount();

					if (scrollState == SCROLL_STATE_IDLE) {
						if (getListView().getLastVisiblePosition() >= count
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
			}

			@Override
			protected Void doInBackground(HoovFetchParams... params) {
				// Create the array
				HoovChapterlist_t=new ArrayList<HoovChapter>();
				getHoovs(params[0],true);
				return null;	
			}

			@Override
			protected void onPostExecute(Void result) {
				int position = getListView().getLastVisiblePosition();
				adapter = new HoovListAdapter(context,HoovChapterlist_t);
				getListView().setAdapter(adapter);
				registerForContextMenu(getListView());
				getListView().setSelectionFromTop(position, 0);
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
			HoovChapter selectedHoov = adapter.hoovChapterList.get(pos);
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
		HoovChapter selectedHoov = adapter.hoovChapterList.get(pos);
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