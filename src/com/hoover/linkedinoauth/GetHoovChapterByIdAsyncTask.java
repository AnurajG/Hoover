package com.hoover.linkedinoauth;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.json.JSONObject;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hoover.util.HoovChapter;
import com.hoover.util.UserQueryBuilder;

public class GetHoovChapterByIdAsyncTask extends AsyncTask<String, Void, HoovChapter>{

	@Override
	protected HoovChapter doInBackground(String... hoovid) {
		HoovChapter hc = new HoovChapter();
		try{
			String id = hoovid[0];

			UserQueryBuilder qb = new UserQueryBuilder();						

			URL url = new URL("https://api.mongolab.com/api/1/databases/hoover/collections/hoov/"+id+"?apiKey=zvbjTNUW6COSTIZxJcPIW7_tniVCnDKC&q");
			//URL url = new URL("https://api.mongolab.com/api/1/databases/hoover_user/collections/user?apiKey=zvbjTNUW6COSTIZxJcPIW7_tniVCnDKC&q={\"document.deviceId\":\"000000000000000\"}&f={\"document.id\":1,\"document.city\":1}");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

			String output;
			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {

			}

			JSONObject r= new JSONObject(output);
			hc.hoovText=r.getJSONObject("documnet").getString("hoov");
			hc.hoovUserId=r.getJSONObject("documnet").getString("id");
			hc.mongoHoovId=id;
			hc.hoov_up_ids=new Gson().fromJson(r.getJSONObject("documnet").getJSONArray("hoovUpIds").toString(), new TypeToken<List<String>>(){}.getType());
			hc.hoov_down_ids = new Gson().fromJson(r.getJSONObject("documnet").getJSONArray("hoovDownIds").toString(), new TypeToken<List<String>>(){}.getType());
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
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return hc;
	}

}
