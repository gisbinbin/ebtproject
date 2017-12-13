package com.rm.until;

import com.rm.serverces.EBTServerces;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent arg1) {
		Intent i = new Intent(context, EBTServerces.class);
		i.setAction("com.rm.serverces.ACTION_EBTSERVICE");
		context.startService(i);
	}
}
