package com.hoover.linkedinoauth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hoover.util.NetworkUtil;

public class NetworkChangeReceiver extends BroadcastReceiver {
	 
    @Override
    public void onReceive(final Context context, final Intent intent) {
 
        Integer status = NetworkUtil.getConnectivityStatus(context);
        
        if(status==NetworkUtil.TYPE_CONNECTED){
        	Intent mServiceIntent = new Intent(context, SaveOfflineHoovService.class);
			context.startService(mServiceIntent);
        }
    }
}