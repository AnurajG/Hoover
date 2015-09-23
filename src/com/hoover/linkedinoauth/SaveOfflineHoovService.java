package com.hoover.linkedinoauth;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

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

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.gson.JsonObject;
import com.hoover.util.Hoov;
import com.hoover.util.HoovQueryBuilder;
import com.textrazor.AnalysisException;

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
					if(checkForModeration(obj.getString("hoovText")))
						h.status=0;
					else
						h.status=2;
					
					h.id=userId;
					h.company=userCompany;
					h.city=userCity;
					h.hoov=en.text;

					String path=null;

					h.commentHoovIds= new ArrayList<String>();
					h.hoovUpIds =new ArrayList<String>();
					h.hoovDownIds=new ArrayList<String>();
					h.parentId="null";

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
	public Boolean checkForModeration(String text){

		try {
			StringBuilder path= new StringBuilder();
			path.append("extractors=entities");
			path.append("&");
			path.append("text="+URLEncoder.encode(text, "UTF-8"));

			URL url = null;
			HttpURLConnection connection = null;
			url = new URL("https://api.textrazor.com/");
			connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("X-TextRazor-Key", "f9daeffbfb83eaeeeb5aacc1b6915f19cab0a300096e8ddd4f632158");
			connection.setRequestProperty("Content-Length", ""+ path.toString().length());
			OutputStream os = connection.getOutputStream();
			os.write( path.toString().getBytes() );
			connection.connect();
			InputStream resultingInputStream= connection.getInputStream();
			final Reader reader = new InputStreamReader(resultingInputStream);
			final char[] buf = new char[16384];
			int read;
			final StringBuffer sbuff = new StringBuffer();
			while((read = reader.read(buf)) > 0) {
				sbuff.append(buf, 0, read);
			}

			int status = connection.getResponseCode();
			if (status != 200) {
				System.out.println();
			}else{
				JSONObject res=new JSONObject(sbuff.toString());
				if(res.getJSONObject("response").has("entities")){
					JSONArray ent=res.getJSONObject("response").getJSONArray("entities");
					for(int i=0;i<ent.length();i++){
						JSONObject obj = (JSONObject)ent.get(i);
						if(obj.getString("type").contains("Person") || obj.getString("type").contains("Location"))
							return false;
					}
				}else
					return true;
			}

			connection.disconnect();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;

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