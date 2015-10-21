package com.hoover.linkedinoauth;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ActionBar.LayoutParams;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hoover.SwipeContainer.CardLayoutManager;
import com.hoover.SwipeContainer.SwipeFlingAdapterView;
import com.hoover.util.EmojiMapUtil;
import com.hoover.util.HoovChapter;
import com.hoover.util.HoovFetchParams;
import com.hoover.util.HoovFetchParams.eOrder;
import com.hoover.util.HoovFetchParams.eType;

public class MyRecyclerViewFragment extends Fragment {

	String city;
	String company;
	String userId;
	private Context context;
	private List<HoovChapter> HoovChapterlist_t = null;
	private HomeViewAdapter mAdapter;
	private HashSet<String> oldHoovsId;
	private TransparentProgressDialog pd;
	//private RecyclerView mRecyclerView;
	ArrayList<HoovChapter> results=new ArrayList<HoovChapter>();
	ImageView emptyMsg;
	private int limit = 5;
	SwipeFlingAdapterView flingContainer;
	public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
	boolean filled=false;
	private int i;
	public static final MyRecyclerViewFragment newInstance(String message,Context context, String comp, String cit,String id) {
		MyRecyclerViewFragment f = new MyRecyclerViewFragment(context,comp,cit,id);
		Bundle bdl = new Bundle(1);
		bdl.putString(EXTRA_MESSAGE, message);
		f.setArguments(bdl);
		return f;

	}
	public MyRecyclerViewFragment(Context context, String comp, String cit, String uid) {
		this.context = context;
		this.company = comp;
		this.city=cit;
		this.userId=uid;

	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View view;
		super.onCreate(savedInstanceState);
		view = inflater.inflate(R.layout.acitvity_recyclerview, container, false);

		mAdapter = new HomeViewAdapter(getDataSet(),context,userId);
		flingContainer=(SwipeFlingAdapterView)view.findViewById(R.id.frame);
		emptyMsg =(ImageView)view.findViewById(R.id.emptytxt);
		final HoovFetchParams params=new HoovFetchParams();
		params.city=city;
		params.comapny=company;
		params.order=eOrder.NEW;
		params.type=eType.HOME;

		flingContainer.setAdapter(mAdapter);
		CardLayoutManager llm = new CardLayoutManager();
		//LinearLayoutManager llm = new LinearLayoutManager(context);
		//llm.setOrientation(CardLayoutManager.VERTICAL);
		flingContainer.setLayoutManager(llm);
		oldHoovsId = new HashSet<String>();
		flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
			@Override
			public void removeFirstObjectInAdapter() {
				// this is the simplest way to delete an object from the Adapter (/AdapterView)
				oldHoovsId.add(results.get(0).mongoHoovId);
				results.remove(0);
				mAdapter.notifyDataSetChanged();
			}

			@Override
			public void onLeftCardExit(Object dataObject) {
				//Consider browsing later
				makeToast(context, "Browse Later!");
				System.out.println("-1-----"+results.size());
				System.out.println("-2-----"+mAdapter.getItemCount());
				System.out.println("-3-----"+mAdapter.mDataset.size());
			}

			@Override
			public void onRightCardExit(Object dataObject) {
				//follow the hoovs
				HoovChapter hc=(HoovChapter) dataObject;
				SaveFollowHoovInfoAsyncTask tsk= new SaveFollowHoovInfoAsyncTask(hc.mongoHoovId,userId);
				tsk.execute();
				makeToast(context, "Followed!");
			}

			@Override
			public void onAdapterAboutToEmpty(int itemsInAdapter) {
				// Ask for more data here
				System.out.println("results"+results);

				if(filled==true){
					LoadMoreDataTask tsk = new LoadMoreDataTask();
					tsk.execute(params);
				}
				//
				mAdapter.notifyDataSetChanged();

			}

			@Override
			public void onScroll(float scrollProgressPercent) {
				View view = flingContainer.getSelectedView();
				//view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
				//view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
			}
		});
		flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
			@Override
			public void onItemClicked(int itemPosition, Object dataObject) {
			}
		});



		new GetHoovsAsyncTask().execute(params);
		return view;


	}

	/*@Override
	protected void onResume() {
	    super.onResume();
	    mAdapter.notifyDataSetChanged();
	}*/
	static void makeToast(Context ctx, String s){
		Toast.makeText(ctx, s, Toast.LENGTH_SHORT).show();
	}


	public void right() {
		/**
		 * Trigger the right event manually.
		 */
		flingContainer.getTopCardListener().selectRight();

	}

	public void left() {
		flingContainer.getTopCardListener().selectLeft();
	}

	private ArrayList<HoovChapter> getDataSet() {
		ArrayList<HoovChapter> results = new ArrayList<HoovChapter>();
		
		return results;
	}
	public class GetHoovsAsyncTask extends AsyncTask<HoovFetchParams, Integer,HoovFetchParams> {
		private Exception e=null;
		@Override
		protected void onPreExecute(){
			super.onPreExecute();
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
				mAdapter = new HomeViewAdapter(results,context,userId);
				filled=true;
				flingContainer.setAdapter(mAdapter);
				//flingContainer.refreshDrawableState();

				/*	mRecyclerView.addOnScrollListener(new OnScrollListener() {

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
				});*/
			}else{
				/*TextView text = (TextView) toast_layout.findViewById(R.id.text);
				text.setText("No internet. Check network connection.");

				Toast toast = new Toast(context);
				toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
				toast.setDuration(TOAST_DELAY);
				toast.setView(toast_layout);
				toast.show();*/
			}

		}
	

	}
	
	
	private class LoadMoreDataTask extends AsyncTask<HoovFetchParams, Void, Void> {
		private Exception e=null;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = new TransparentProgressDialog(context, R.drawable.progress);
			pd.show();
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
			pd.dismiss();
			if(e==null){
				for(int i=0;i<HoovChapterlist_t.size();i++){
					if(!oldHoovsId.contains(HoovChapterlist_t.get(i).mongoHoovId)){
						results.add(HoovChapterlist_t.get(i));
					}
						
				}
				mAdapter.notifyDataSetChanged();
				if(results.size()==0){
					emptyMsg.setVisibility(View.VISIBLE);
				
					flingContainer.setVisibility(View.GONE);
				}
			}
		}
	}
	public void getHoovs(HoovFetchParams hp,boolean increaseLimit) throws Exception{
		HoovFetchParams u = hp;
		JSONArray array;
		URL url = null;
		if(increaseLimit)
			limit=limit+5;
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
	private class TransparentProgressDialog extends Dialog {
		
		private ImageView iv;
			
		public TransparentProgressDialog(Context context, int resourceIdOfImage) {
			super(context, R.style.TransparentProgressDialog);
	        	WindowManager.LayoutParams wlmp = getWindow().getAttributes();
	        	wlmp.gravity = Gravity.CENTER_HORIZONTAL;
	        	getWindow().setAttributes(wlmp);
			setTitle(null);
			setCancelable(false);
			setOnCancelListener(null);
			LinearLayout layout = new LinearLayout(context);
			layout.setOrientation(LinearLayout.VERTICAL);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			iv = new ImageView(context);
			iv.setImageResource(resourceIdOfImage);
			layout.addView(iv, params);
			addContentView(layout, params);
		}
			
		@Override
		public void show() {
			super.show();
			RotateAnimation anim = new RotateAnimation(0.0f, 360.0f , Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f);
			anim.setInterpolator(new LinearInterpolator());
			anim.setRepeatCount(Animation.INFINITE);
			anim.setDuration(3000);
			iv.setAnimation(anim);
			iv.startAnimation(anim);
		}
	}

}
