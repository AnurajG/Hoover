package com.hoover.linkedinoauth;

import android.os.Bundle;
import android.preference.PreferenceActivity;
public class PrefsActivity extends PreferenceActivity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.activity_settings);
    }   
}