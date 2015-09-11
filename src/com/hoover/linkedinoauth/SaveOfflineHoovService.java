package com.hoover.linkedinoauth;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;

import com.goebl.david.Webb;
import com.hoover.util.Hoov;
import com.hoover.util.HoovQueryBuilder;

public class SaveOfflineHoovService extends IntentService {
	private String userId;
	private String userCompany;
	private String userCity;

	private ArrayList<Integer> success=new ArrayList<Integer>();
	public SaveOfflineHoovService() {
		super(SaveOfflineHoovService.class.getName());
	}

	public class x {
		public Integer key;
		public String text;
	};

	@Override
	protected void onHandleIntent(Intent workIntent) {
		SharedPreferences preferences = this.getSharedPreferences("user_info", 0);
		userCompany = preferences.getString("userCompany", null);
		userCity = preferences.getString("userCity", null);
		userId = preferences.getString("userId", null);

		SharedPreferences preferences2 = SaveOfflineHoovService.this.getSharedPreferences("hoov_tmp", 0);
		String hoovArray = preferences2.getString("hoovArray", null);
		if(hoovArray!=null){

			try {
				JSONArray array=new JSONArray(hoovArray);
				for(int i=0;i<array.length();i++){
					JSONObject obj = (JSONObject)array.get(i);

					x en=new x();
					en.key=i;
					en.text=obj.getString("hoovText");
					Hoov h=new Hoov();
					h.id=userId;
					h.company=userCompany;
					h.city=userCity;
					h.hoov=en.text;

					String path=null;

					if(obj.has("parentId") && obj.getString("parentId")!=null){

						JSONObject f = new JSONObject();
						f.put("document.path",1);

						Webb webb = Webb.create();
						JSONObject parent=webb.get("https://api.mongolab.com/api/1/databases/hoover/collections/hoov/"+obj.getString("parentId")).param("apiKey", "zvbjTNUW6COSTIZxJcPIW7_tniVCnDKC")
								.ensureSuccess().asJsonObject().getBody();
						JSONObject d = parent.getJSONObject("document");
						String p = d.getString("path");


						if(p==null || p.compareTo("null")==0){
							path=","+obj.getString("parentId")+",";
						}else{
							path=p+obj.getString("parentId")+",";
						}

					}
					h.path=path;


					h.hoovUpIds =new ArrayList<String>();
					h.hoovDownIds=new ArrayList<String>();

					HoovQueryBuilder qb = new HoovQueryBuilder();						

					HttpClient httpClient = new DefaultHttpClient();
					HttpPost request = new HttpPost(qb.buildContactsSaveURL());

					StringEntity params;
					try {
						params = new StringEntity(qb.createContact(h));
						request.addHeader("content-type", "application/json");
						request.setEntity(params);
						HttpResponse response = httpClient.execute(request);
						if(response.getStatusLine().getStatusCode()<205){
							success.add(en.key);
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
				}
				JSONArray array2=new JSONArray();
				for(int i=0;i<array.length();i++){
					if(!success.contains(i)){
						array2.put(array.getJSONObject(i));
					}
				}
				SharedPreferences.Editor editor = preferences2.edit();
				editor.remove("hoovArray");
				editor.putString("hoovArray", array2.toString());
				editor.commit();

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	

		}

	}
	/*class AttemptHoovSubmit extends AsyncTask<x, String, Integer> {

		protected void onPreExecute() {
			super.onPreExecute();
		}
		//String hoov = hoovText.getText().toString();
		@Override
		protected Integer doInBackground(x... arg0) {



		}

		protected void onPostExecute(Integer key) {
			//this intent is used to open other activity wich contains another webView
			success.add(key);
		}

	}*/
}