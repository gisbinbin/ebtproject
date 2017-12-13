package com.rm.until;

import com.rm.serverces.EBTServerces;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkChangedReceiver extends BroadcastReceiver {
	String TAG="NetworkChangedReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            @SuppressWarnings("deprecation")
			NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            if (info != null) {
                if (NetworkInfo.State.CONNECTED == info.getState() && info.isAvailable()) {
                    if (info.getType() == ConnectivityManager.TYPE_WIFI
                            || info.getType() == ConnectivityManager.TYPE_MOBILE) {
                    	if(!Common.isServiceWork(context,"com.rm.serverces.EBTServerces"))
                    	{
                    		Intent service = new Intent(context,EBTServerces.class);
                    		service.setAction("com.rm.serverces.ACTION_EBTSERVICE");
                    		context.startService(service);
                    	}
                    }
                } else {
                    Log.i(TAG, getConnectionType(info.getType()) + "断开");
                }
            }
        }
    }
    
	private String getConnectionType(int type) {
        String connType = "";
        if (type == ConnectivityManager.TYPE_MOBILE) {
            connType = "3G网络数据";
        } else if (type == ConnectivityManager.TYPE_WIFI) {
            connType = "WIFI网络";
        }
        return connType;
    }
}
