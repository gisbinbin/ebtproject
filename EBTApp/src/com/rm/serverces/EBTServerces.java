package com.rm.serverces;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.rm.config.SysConfig;
import com.rm.ebtapp.MainActivity;
import com.rm.ebtapp.R;
import com.rm.until.UploadUtil;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class EBTServerces extends Service {
	private String TAG="EBTServerces";
	SharedPreferences sp = null;
	@SuppressWarnings("rawtypes")
	List list=new ArrayList(); 
	@Override  
    public IBinder onBind(Intent intent) {  
        System.out.println("onBind-------->");  
        Toast.makeText(this, "OnBind(", Toast.LENGTH_SHORT).show();  
        return null;  
    }  
      
    /** 
     * 如果是第一次调用只执行一次 
     */  
    @Override  
    public void onCreate() {  
        super.onCreate();
        Log.i(TAG, "OnCreate"); 
    }
    
    @SuppressLint("NewApi")
	private void showNotification(String messagecontent)
    {
    	Notification.Builder mBuilder=new Notification.Builder(this)
    	.setSmallIcon(R.drawable.ic_launcher)
    	.setContentTitle("补拍信息")
    	.setContentText("您有"+messagecontent+"条记录需要补拍！");
    	Intent intent=new Intent(this,MainActivity.class);
    	TaskStackBuilder stackBuilder=TaskStackBuilder.create(this);
    	stackBuilder.addParentStack(MainActivity.class);
    	stackBuilder.addNextIntent(intent);
    	PendingIntent pendingIntent=stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    	mBuilder.setContentIntent(pendingIntent);
    	NotificationManager nm=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
    	Notification notification=mBuilder.build();
    	//notification.defaults = Notification.DEFAULT_ALL;
    	notification.flags=Notification.FLAG_AUTO_CANCEL;
    	nm.notify(0,notification);
    	startForeground(0, notification);
    }
    
    @Override  
	public int onStartCommand(Intent intent, int flags, int startId) {
//		AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
//		int anHour = 40 * 1000; // 这是一小时的毫秒数
//		long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
//		Intent i = new Intent(this, AlarmReceiver.class);
//		alarmCount=alarmCount+1;
//		PendingIntent pi = PendingIntent.getBroadcast(this, alarmCount, i, 0);
//		manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        flags = START_STICKY;
		return super.onStartCommand(intent, flags, startId);
	}
    
    @SuppressWarnings("deprecation")
	@Override  
    public void onStart(Intent intent, int startId) {  
        super.onStart(intent, startId);
        sp = this.getSharedPreferences("APPSETTING", Context.MODE_PRIVATE);
		new Thread(new Runnable() {
			@Override
			public void run() {
				JSONObject json = null;
				final Map<String, String> params = new HashMap<String, String>();
				String userid = sp.getString("userid", "");
				if(!userid.equals("")){
					params.put("PRINCIPAL_ID", userid);
					String result = "";
					try {
						result = UploadUtil.post(SysConfig.PROQUERTYDATAURL, params, null);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						if (!result.equals(""))
							json = new JSONObject(result);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (json != null) {
						try {
							String resu = json.getString("success");
							if (resu.equals("true")) {
								String data = json.getString("result");
								JSONArray prorecord = new JSONArray(data);
								if (prorecord.length() > 0)
									showNotification(String.valueOf(prorecord.length()));
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}).start();
    }
    
    @Override  
    public void onDestroy() {  
        super.onDestroy();
        Log.i(TAG, "onDestroy"); 
    }
}
