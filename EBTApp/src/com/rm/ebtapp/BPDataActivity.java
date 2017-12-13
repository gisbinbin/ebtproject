package com.rm.ebtapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.rm.config.SysConfig;
import com.rm.db.BLL;
import com.rm.db.DBHelper;
import com.rm.model.MistakeRecord;
import com.rm.until.MistakeViewAdapter;
import com.rm.until.UploadUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class BPDataActivity extends Activity implements OnClickListener {
	private final static int REFRESH_DATA_CODE = 1000;
	private String TAG = "BPDataActivity";
	private SQLiteDatabase db;
	private final DBHelper helper = new DBHelper(this);
	private SharedPreferences sp = null;
	
	private LinearLayout reltitle;
	private Button left_but, right_but;
	private TextView bpcount;
	private ImageView imgsyncdata;
	private ListView bptableListView;
	private List<MistakeRecord> mlistInfo = new ArrayList<MistakeRecord>();
	private MistakeViewAdapter adapter;
	private int type=0;
	
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler(){
    	@Override
		public void handleMessage(Message msg){
    		if(msg.what==REFRESH_DATA_CODE)
    			loadData(type);
    	}
    };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_bpdata);

		db = helper.getWritableDatabase();
        sp = this.getSharedPreferences("APPSETTING", Context.MODE_PRIVATE);

		ImageView mButton = (ImageView) findViewById(R.id.bpbutton);
		mButton.setOnClickListener(this);
		findViewById(R.id.textview_title).setVisibility(View.GONE);
		reltitle = (LinearLayout) findViewById(R.id.button_title);
		reltitle.setVisibility(View.VISIBLE);
		left_but = (Button) findViewById(R.id.left_btn);
		left_but.setOnClickListener(this);
		right_but = (Button) findViewById(R.id.right_btn);
		right_but.setOnClickListener(this);
		bpcount = (TextView) findViewById(R.id.bpcount);

		findViewById(R.id.img_user).setVisibility(View.GONE);
		Button backbutton=(Button)findViewById(R.id.button_back);
		backbutton.setVisibility(View.VISIBLE);
		backbutton.setOnClickListener(this);
		
		imgsyncdata=(ImageView)findViewById(R.id.img_refresh);
		imgsyncdata.setVisibility(View.VISIBLE);
		imgsyncdata.setOnClickListener(this);
		
		init();
		loadData(type);
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		loadData(type);
	}
	
	private void loadData(int type) {
		mlistInfo=BLL.getMistakeRecord(BPDataActivity.this,type);
		adapter = new MistakeViewAdapter(BPDataActivity.this, mlistInfo);
		bptableListView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
	}

	private void init() {
		bptableListView=(ListView)findViewById(R.id.bpdataview);
		bptableListView.setItemsCanFocus(false);
		bptableListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				MistakeRecord temp=mlistInfo.get(position);
				Intent Install_intent = new Intent(BPDataActivity.this,	BPPicActivity.class);
				Install_intent.putExtra("data", temp);
				startActivity(Install_intent);
			}
		});
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.button_back:
			this.finish();
			break;
		case R.id.bpbutton:
			Intent intent = new Intent();
			intent.setClass(BPDataActivity.this, QRScanActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra("zoomto", 0);
			startActivityForResult(intent, R.id.bpbutton);
			break;
		case R.id.img_refresh:
			new AlertDialog.Builder(this)
			.setTitle("请先确认已补拍的数据已提交？")
			.setIcon(R.drawable.warning)
			.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							SyncData();
						}
					})
			.setNegativeButton("返回",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int which) {
						}
					}).show();
			break;
		case R.id.left_btn:
			left_but.setBackgroundResource(R.drawable.switch_button_left_checked);
			left_but.setTextColor(getResources().getColorStateList(R.color.backcolor));
			right_but.setBackgroundResource(R.drawable.switch_button_right);
			right_but.setTextColor(getResources().getColorStateList(R.color.white));
			type=0;
			loadData(type);
			break;
		case R.id.right_btn:
			left_but.setBackgroundResource(R.drawable.switch_button_left);
			left_but.setTextColor(getResources().getColorStateList(	R.color.white));
			right_but.setBackgroundResource(R.drawable.switch_button_right_checked);
			right_but.setTextColor(getResources().getColorStateList(R.color.backcolor));
			type=1;
			loadData(type);
			break;
		default:
			break;
		}
	}
		
	private void SyncData() {
		final ProgressDialog dialog = new ProgressDialog(this);  
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("正在加载数据……");  
        dialog.show();  
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				JSONObject json = null;
				final Map<String, String> params = new HashMap<String, String>();
				String userid = sp.getString("userid", "");
				if(!userid.equals(""))
				{
					params.put("PRINCIPAL_ID", userid);//"27342");//
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
								helper.delall("BPIMG");
								String data = json.getString("result");
								if(data!=null&&!data.equals("[]"))
								{
									JSONArray prorecords = new JSONArray(data);
									List<MistakeRecord> list=BLL.JSONArrayToMistakeRecord(prorecords);
									for(int i=0;i<list.size();i++)
									{
										BLL.insertMistake(BPDataActivity.this,list.get(i));
									}
								}
								 Message message = new Message();
								 message.what = REFRESH_DATA_CODE;
								 handler.sendMessage(message);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}
				dialog.cancel();
			}
		}).start();
	}
}
