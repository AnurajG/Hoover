package com.hoover.linkedinoauth;

import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.hoover.linkedinoauth.Login.SaveUserAsyncTask;
import com.hoover.util.User;


import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
public class PrefsActivity extends PreferenceActivity{
	String newCompany=null,newCity=null;
	private static final String PROFILE_URL = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name,headline,location,positions)";
	private static final String OAUTH_ACCESS_TOKEN_PARAM ="oauth2_access_token";
	private static final String QUESTION_MARK = "?";
	private static final String EQUALS = "=";
	String userMongoId,userDeviceId,userId;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.activity_settings);
        final AlertDialog warningDailog = new AlertDialog.Builder(this).create();
        warningDailog.setTitle("Alert");
        warningDailog.setMessage("The company name doesn't match with the linkedin details.Please update your linkedin profile and try again");
        warningDailog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
        final Builder alertDialog=new AlertDialog.Builder(this)
        .setTitle("Switch Organisation")
        .setMessage("Are you sure you want to switch your organisation? All your existing hoovs will be deleted and you can't change the company again for 15 days")
        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) { 
            	SharedPreferences preferences = getSharedPreferences("user_info", 0);
            	SharedPreferences.Editor editor = preferences.edit();
				String accessToken = preferences.getString("accessToken", null);
        		userId = preferences.getString("userId", null);
        		userDeviceId=preferences.getString("userDeviceId", null);
        		userMongoId=preferences.getString("userMongoId", null);
            	String url=Login.getProfileUrl(accessToken);
            	String currLinkedinCompany=null;
            	GetProfileAsyncTask tsk= new GetProfileAsyncTask(url);
            	try{
            		JSONObject data=tsk.execute().get();
            		JSONObject obj1,obj2,obj3;
            		String str=null;
            		obj1=data.getJSONObject("positions");
            		newCity=data.getJSONObject("location").getString("name");
            		JSONArray arr=obj1.getJSONArray("values");
            		for(int i=0;i<arr.length();i++){
            			obj2=(JSONObject)arr.get(i);	
            			if(obj2.has("company")){
            				obj3=obj2.getJSONObject("company");
            				str=obj3.getString("name");
            			}
            		}

            		if(!str.equals(null)){
            			currLinkedinCompany=str;
            		}else{
            			currLinkedinCompany=data.getString("headline").split("at")[1].trim();
            		}
            	}catch(JSONException e) {
            		Log.e("Authorize","Error Parsing json "+e.getLocalizedMessage());	
            	} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	
            	if(newCompany.equals(currLinkedinCompany)){
        			//update company and delete existing hoov records
            		Intent myIntent = new Intent(PrefsActivity.this, DeleteProfileService.class);
	                myIntent.putExtra("userMongoId",userMongoId);
	                startService(myIntent);
	              	editor.putString("userCompany", newCompany);
    				editor.putString("userCity", newCity);
    				editor.commit();
    				User u=new User();
    				u.city=newCity;
    				u.company=newCompany;
    				u.id=userId;
    				u.deviceId=userDeviceId;
    				UpdateUserCompanyAsyncTask updateTask=new UpdateUserCompanyAsyncTask(PrefsActivity.this, u);
    				updateTask.execute();
    					
        		}else{
        			//give warning to users to update linkedin.
        			warningDailog.show();
        		}
        		
            }
         })
        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) { 
            	}
         })
        .setIcon(android.R.drawable.ic_dialog_alert);
        
       
         findPreference("pref_text").setOnPreferenceChangeListener(
                new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                	newCompany=(String) newValue;
                	alertDialog.show();
                	return true;
                }

            });
    }

}