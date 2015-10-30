package com.hoover.linkedinoauth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.Animator.AnimatorListener;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.goebl.david.WebbException;
import com.hoover.util.EmojiMapUtil;
import com.hoover.util.Hoov;
import com.hoover.util.HoovChapter;
import com.hoover.util.HoovFetchParams;
import com.hoover.util.HoovInsertParams;
import com.hoover.util.HoovQueryBuilder;
import com.hoover.util.SimpleGestureFilter;
import com.hoover.util.SimpleGestureFilter.SimpleGestureListener;

public class HoovDetailsActivity extends Activity implements SimpleGestureListener{
	private TextView hoovText;
	private TextView hoovDate;

	private String mongoHoovId;
	Button rehoov_in;
	EditText myEditText;

	ImageButton deleteHoov;

	private RecyclerView mRecyclerView;
	private RecyclerView.Adapter mAdapter;
	private RecyclerView.LayoutManager mLayoutManager;
	ProgressBar mProgressBar;
	private List<HoovChapter> HoovChapterlist_t = null;
	ArrayList<Integer> randArray;
	private HashMap<String, String> imageMap = null;
	private Integer imageCounter;
	ArrayList<HoovChapter> results = new ArrayList<HoovChapter>();
	String userComapny;
	String userCity;
	String userId;
	String path;
	//String currentUserId;
	Context context;
	RelativeLayout layout;
	HoovChapter hc;

	private SimpleGestureFilter detector;
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hoov_detail);
		context=this;
		layout=(RelativeLayout)findViewById(R.id.card_layout);
		layout.setGravity(Gravity.CENTER_HORIZONTAL);
		/*ObjectAnimator animation = ObjectAnimator.ofFloat(layout, "ScaleY", 1.0f,1.5f);
		animation.setDuration(2000);
		animation.setInterpolator(new AccelerateDecelerateInterpolator());
		animation.start();*/

		com.hoover.linkedinoauth.Application appState = ((com.hoover.linkedinoauth.Application)getApplicationContext());
		randArray=appState.getRandArray();
		Collections.shuffle(randArray);
		imageCounter=0;

		imageMap=new HashMap<String, String>();


		mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
		Intent intent = getIntent();
		hc=(HoovChapter) intent.getSerializableExtra("chapter");

		//currentUserId=intent.getStringExtra("currentUserid");

		mRecyclerView.setHasFixedSize(true);
		mLayoutManager = new LinearLayoutManager(this);
		mRecyclerView.setLayoutManager(mLayoutManager);
		mAdapter = new HoovDetailsViewAdapter(getDataSet(),getImageMap(),this,userId);
		mRecyclerView.setAdapter(mAdapter);

		hoovText = (TextView) findViewById(R.id.hoovtextView);
		hoovDate= (TextView) findViewById(R.id.hoovdateView2);
		mProgressBar=(ProgressBar)findViewById(R.id.a_progressbar);



		hoovText.setText(hc.hoovText);
		path=intent.getStringExtra("path");
		hoovText.setTextSize(TypedValue.COMPLEX_UNIT_PX,
				getResources().getDimension(R.dimen.hoov_detail_text));
		hoovDate.setText(hc.hoovDate);

		mongoHoovId=hc.mongoHoovId;

		myEditText = (EditText) findViewById(R.id.commenttext);

		// Check if no view has focus:
		View view = this.getCurrentFocus();
		if (view != null) {  
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
		}
		// Detect touched area 
        detector = new SimpleGestureFilter(this,this);
		/*gestureDetector = new GestureDetector(new SwipeGestureDetector());
	    gestureListener = new View.OnTouchListener() {
	        @Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return gestureDetector.onTouchEvent(event);
			}
	    };
	    hoovText.setOnTouchListener(gestureListener);*/
		rehoov_in=(Button) findViewById(R.id.submit);
		rehoov_in.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
				inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
				String toPost=myEditText.getText().toString();
				myEditText.getText().clear();

				SubmitHoovAsyncTask s_tsk= new SubmitHoovAsyncTask();
				HoovInsertParams p = new HoovInsertParams();
				p.text=EmojiMapUtil.replaceUnicodeEmojis(toPost);
				p.parentId=mongoHoovId;
				s_tsk.execute(p);

			} 

		});

		/*deleteHoov=(ImageButton) findViewById(R.id.delete);
		deleteHoov.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				DeleteHoovsAsyncTask tsk= new DeleteHoovsAsyncTask(context,mongoHoovId);
				tsk.execute();
			} 

		});



		if(currentUserId.equals(hc.hoovUserId)){
			deleteHoov.setVisibility(Button.VISIBLE);
		}*/

		rehoov_in=(Button) findViewById(R.id.submit);
		if(myEditText.length() == 0) rehoov_in.setEnabled(false);
		myEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable arg0) {
				Integer l= 320 - myEditText.length();
				if(l<0 || l==320) 
					rehoov_in.setEnabled(false);
				else
					rehoov_in.setEnabled(true);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
		});


		/*mRecyclerView.setOnScrollListener(new HidingScrollListener(){
			@Override
			public void onHide() {
				RelativeLayout.LayoutParams lp=(RelativeLayout.LayoutParams) myEditText.getLayoutParams();
				myEditText.animate().translationY(myEditText.getHeight()+lp.bottomMargin).setInterpolator(new AccelerateInterpolator(2)).start();
			}
			@Override
			public void onShow() {
				myEditText.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
			}
		});*/
		SharedPreferences preferences = this.getSharedPreferences("user_info", 0);
		userComapny = preferences.getString("userCompany", null);
		userCity = preferences.getString("userCity", null);
		userId = preferences.getString("userId", null);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
				);
		GetHoovsAsyncTask tsk = new GetHoovsAsyncTask();
		HoovFetchParams p = new HoovFetchParams();
		p.city=userCity;
		p.comapny=userComapny;
		p.parentId=mongoHoovId;

		tsk.execute(p);

	}
	 @Override
	    public boolean dispatchTouchEvent(MotionEvent me){
	        // Call onTouchEvent of SimpleGestureFilter class
	         this.detector.onTouchEvent(me);
	       return super.dispatchTouchEvent(me);
	    }
	    @Override
	     public void onSwipe(int direction) {
	      String str = "";
	      
	      switch (direction) {
	      
	      case SimpleGestureFilter.SWIPE_LEFTRIGHT : str = "Swipeeddddddd";

			ObjectAnimator animation = ObjectAnimator.ofFloat(layout, "rotationY", 0.0f, 150.0f);
			animation.addListener(new AnimatorListener() {
				
				@Override
				public void onAnimationStart(Animator animation) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onAnimationRepeat(Animator animation) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onAnimationEnd(Animator animation) {
					finish();
				}
				
				@Override
				public void onAnimationCancel(Animator animation) {
					// TODO Auto-generated method stub
					
				}
			});
			animation.setDuration(250);
			//animation.setRepeatCount(ObjectAnimator.INFINITE);
			animation.setInterpolator(new AccelerateInterpolator());
			animation.start();
			//finish();
	                                               break;
	     
	      case SimpleGestureFilter.SWIPE_DOWN :  str = "Swipe Down";
	                                                     break;
	      case SimpleGestureFilter.SWIPE_UP :    str = "Swipe Up";
	                                                     break;
	      
	      }
	       Toast.makeText(this, str, Toast.LENGTH_LONG).show();
	     }
	      
	     @Override
	     public void onDoubleTap() {
	        Toast.makeText(this, "Double Tap", Toast.LENGTH_LONG).show();
	     }
	

	//Must unregister onPause()
	@Override
	protected void onPause() {
		super.onPause();
		overridePendingTransition(0, 0);
		context.unregisterReceiver(mMessageReceiver);
	}


	//This is the handler that will manager to process the broadcast intent
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle extras=intent.getExtras();

			if (extras != null) {
				String jsonData = extras.getString("com.parse.Data");
				try {
					JSONObject json;
					json = new JSONObject(jsonData);

					if(json.has("hoovId")){
						String id;
						id = json.getString("hoovId");
						if(id.equals(mongoHoovId)){
							abortBroadcast();
							GetHoovsAsyncTask tsk = new GetHoovsAsyncTask();
							HoovFetchParams p = new HoovFetchParams();
							results.clear();
							p.city=userCity;
							p.comapny=userComapny;
							p.parentId=mongoHoovId;
							tsk.execute(p);
						}
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
				String message = intent.getStringExtra("message");
				System.out.println(message);
			}
		}
	};

	public class GetHoovsAsyncTask extends AsyncTask<HoovFetchParams, Integer,Void> {
		@Override
		protected void onPreExecute(){
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			mProgressBar.setProgress(values[0]);
		}

		@Override
		protected Void doInBackground(HoovFetchParams... params) {
			HoovChapterlist_t=new ArrayList<HoovChapter>();
			try 
			{			
				HoovFetchParams u = params[0];
				JSONArray array;
				JSONObject p = new JSONObject();
				p.put("document.company",u.comapny);
				p.put("document.city",u.city);
				p.put("document.path", "/,"+u.parentId+",/");

				JSONObject q = new JSONObject();
				q.put("_id", -1);
				String url_str="http://nodejs-hooverest.rhcloud.com/commentlist?parent="+u.parentId;
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
					JSONObject doc = obj.getJSONObject("document");
					//JSONObject ids = obj.getJSONObject("_id");
					JSONArray ups = doc.getJSONArray("hoovUpIds");
					JSONArray downs = doc.getJSONArray("hoovDownIds");
					hc.hoovText=EmojiMapUtil.replaceCheatSheetEmojis(doc.getString("hoov"));
					hc.mongoHoovId=obj.getString("_id");
					hc.hoov_up_ids=new ArrayList<String>();
					hc.hoov_down_ids =new ArrayList<String>();
					hc.hoovUserId=doc.getString("id");
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
					imageMap.put(hc.hoovUserId, "");
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
			Void dreggn = null;
			return dreggn;	
		}

		@Override
		protected void onPostExecute(final Void  data){
			super.onPostExecute(data);
			Resources r = getResources();
			Float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 450, r.getDisplayMetrics());
			
			ValueAnimator anim = ValueAnimator.ofInt(layout.getMeasuredHeight(), px.intValue());
			anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
				@Override
				public void onAnimationUpdate(ValueAnimator valueAnimator) {
					int val = (Integer) valueAnimator.getAnimatedValue();
					ViewGroup.LayoutParams layoutParams = layout.getLayoutParams();
					layoutParams.height = val;
					layout.setLayoutParams(layoutParams);
				}
			});
			anim.setDuration(2000);
			anim.setInterpolator(new AccelerateDecelerateInterpolator());
			anim.start(); 

			results.addAll(HoovChapterlist_t);
			for(String key:imageMap.keySet()){
				imageMap.put(key, "av_"+randArray.get(imageCounter++));
			}

			mAdapter = new HoovDetailsViewAdapter(results,imageMap,getApplicationContext(),userId);
			mRecyclerView.setAdapter(mAdapter);
			mProgressBar.setVisibility(View.GONE);

		}


	}
	@Override
	protected void onResume() {
		super.onResume();
		context .registerReceiver(mMessageReceiver, new IntentFilter("com.parse.push.intent.ORDERED_RECEIVE"));
		((HoovDetailsViewAdapter) mAdapter).setOnItemClickListener(new HoovDetailsViewAdapter
				.MyClickListener() {
			@Override
			public void onItemClick(int position, View v) {
				Log.i("card view", " Clicked on Item " + position);
			}
		});
	}



	private ArrayList<HoovChapter> getDataSet() {
		ArrayList<HoovChapter> results = new ArrayList<HoovChapter>();
		return results;
	}

	private HashMap<String,String>  getImageMap() {
		HashMap<String,String> results = new HashMap<String, String>();
		return results;
	}

	public void deleteComment(Integer position){
		DeleteCommentAsyncTask tsk=new DeleteCommentAsyncTask();
		tsk.execute(position);
	}

	public class DeleteCommentAsyncTask extends AsyncTask<Integer, Void,Integer>{

		ProgressDialog mProgressDialog;

		protected void onPreExecute(){
			super.onPreExecute();
			mProgressDialog = new ProgressDialog(context);
			mProgressDialog.setTitle("Delete Hoov.....");
			mProgressDialog.setMessage("Deleting...");
			mProgressDialog.setIndeterminate(false);
			mProgressDialog.show();
		}
		@Override
		protected Integer doInBackground(Integer... position) {
			String urlPrefix;
			urlPrefix="http://nodejs-hooverest.rhcloud.com/deletecomment"+"?comment=";
			try{   
				URL url = new URL(urlPrefix+results.get(position[0]).mongoHoovId);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				Random random = new Random();
				conn.setRequestMethod("DELETE");
				conn.setDoOutput(false);
				conn.setRequestProperty("Content-Type", "application/json");
				conn.setRequestProperty("Accept", "application/json");
				int s=conn.getResponseCode();
				System.out.println("");
			}catch(WebbException we){
				we.printStackTrace();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return position[0];
		}
		protected void onPostExecute(Integer data){
			results.remove(data.intValue());

			mAdapter = new HoovDetailsViewAdapter(results,imageMap,getApplicationContext(),userId);
			mRecyclerView.setAdapter(mAdapter);
			mRecyclerView.refreshDrawableState();
			//mRecyclerView.ref
			mProgressDialog.dismiss();

		}



	}

	public class SubmitHoovAsyncTask extends AsyncTask<HoovInsertParams, Void, String>{

		String hoovId;
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if(result!=null){
				if(!imageMap.keySet().contains(userId))
					imageMap.put(userId, "av_"+randArray.get(imageCounter++));
				HoovChapter _hc=new HoovChapter();
				_hc.hoovText=result;
				_hc.hoovUserId=userId;
				_hc.hoov_down_ids=new ArrayList<String>();
				_hc.hoov_up_ids=new ArrayList<String>();
				_hc.mongoHoovId=hoovId;
				results.add(_hc);
				mAdapter = new HoovDetailsViewAdapter(results,imageMap,getApplicationContext(),userId);
				mRecyclerView.setAdapter(mAdapter);

				HashSet<String> commentUserIds= new HashSet<String>();
				commentUserIds.add(hc.hoovUserId);
				for(HoovChapter h:results){
					commentUserIds.add(h.hoovUserId);
				}
				commentUserIds.remove(userId);
				JSONArray comments=new JSONArray(commentUserIds);




				/*ParseQuery<ParseInstallation> pushQuery = ParseInstallation.getQuery();
				pushQuery.whereContainedIn("userId", commentUserIds);

				// Send push notification to query
				ParsePush push = new ParsePush();
				push.setQuery(pushQuery); // Set our Installation query
				push.setMessage("YaY! Someone commented on the hoov you posted!");
				push.sendInBackground();*/

				JSONObject where= new JSONObject();
				JSONObject where2= new JSONObject();
				JSONObject user= new JSONObject();
				JSONObject inq= new JSONObject();
				JSONObject alert=new JSONObject();
				JSONObject data=new JSONObject();
				JSONObject in=new JSONObject();


				try {
					in.put("$in", comments);
					where.put("userId",in);
					inq.put("where", where);
					user.put("$inQuery", inq);
					where2.put("Installation", user);
					data.put("where", where);


					alert.put("alert", "YaY! Someone commented on the hoov you posted!");
					alert.put("title", "Hoover Comment Received");
					alert.put("hoovId", mongoHoovId);

					data.put("data", alert);

				} catch (JSONException e) {
					e.printStackTrace();
				}


				Intent mServiceIntent = new Intent(HoovDetailsActivity.this, SendParsePushService.class);
				mServiceIntent.putExtra("data",data.toString());
				getApplicationContext().startService(mServiceIntent);

			}
		}

		@Override
		protected String doInBackground(HoovInsertParams... params) {


			Hoov h=new Hoov();
			h.id=userId;
			h.company=userComapny;
			h.city=userCity;
			h.hoov=params[0].text;






			h.commentHoovIds=new ArrayList<String>();
			h.abuserUserIds=new ArrayList<String>();
			h.followerUserIds=new ArrayList<String>();
			h.hoovUpIds =new ArrayList<String>();
			h.hoovDownIds=new ArrayList<String>();
			h.parentId=mongoHoovId;

			HoovQueryBuilder qb = new HoovQueryBuilder();						

			HttpClient httpClient = new DefaultHttpClient();
			HttpPost request = new HttpPost(qb.buildContactsSaveURL());

			StringEntity param;
			try {
				param = new StringEntity(qb.createContact(h));
				request.addHeader("content-type", "application/json");
				request.setEntity(param);
				HttpResponse response = httpClient.execute(request);
				if(response.getStatusLine().getStatusCode()<205){
					String jsonString = EntityUtils.toString(response.getEntity());
					try {
						JSONObject obj=new JSONObject(jsonString);
						hoovId=obj.getJSONObject("_id").getString("$oid");

						JSONObject f1 = new JSONObject();
						JSONObject addToSet=new JSONObject();

						f1.put("document.commentHoovIds",hoovId);
						addToSet.put("$addToSet", f1);

						URL url = new URL("https://api.mongolab.com/api/1/databases/hoover/collections/hoov/"+mongoHoovId+"?apiKey=zvbjTNUW6COSTIZxJcPIW7_tniVCnDKC");
						HttpURLConnection conn = (HttpURLConnection) url.openConnection();
						Random random = new Random();
						conn.setRequestMethod("PUT");
						conn.setDoOutput(true);
						conn.setRequestProperty("Content-Type", "application/json");
						conn.setRequestProperty("Accept", "application/json");
						OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
						osw.write(String.format(addToSet.toString(), random.nextInt(30), random.nextInt(20)));
						osw.flush();
						osw.close();
						int s= conn.getResponseCode();
						System.out.println("oo "+s);
					} catch (JSONException e) {
						e.printStackTrace();
					}

					System.out.println(jsonString);
					return h.hoov;
				}
				else{
					System.out.println("");
				}

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}



	}

}
