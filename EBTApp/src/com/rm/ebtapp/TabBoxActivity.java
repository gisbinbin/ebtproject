package com.rm.ebtapp;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.rm.db.DBHelper;
import com.rm.model.ImgContent;
import com.rm.until.Common;
import com.rm.until.ImgAdapter;
import com.rm.until.MyGridView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class TabBoxActivity extends Activity implements OnClickListener,
		OnLongClickListener {
	private final static int SAVE_DATA_CODE = 1000;
	private SharedPreferences mPreferences;
	private EditText readData, eboxseal1, eboxseal2, eboxseal3, eboxseal4,
			eboxseal5, eboxseal6;
	private ImageView camera, camera2, camera3, delcamera, delcamera2,
			delcamera3, boxseal1, boxseal2, boxseal3, boxseal4, boxseal5,
			boxseal6;
	private TextView picname, picname2, picname3;
	private Button savebtn;
	private Boolean first = true;
	private Boolean havesave =true;

	private String CurrentImg;
	private Intent intent = null;
	private String strbox1, strbox2, strbox3;

	private TextView moretxv;
	private MyGridView gridView;
	
    private List<ImgContent> names=new ArrayList<ImgContent>();
    private ImgAdapter mainAdapter;
    
    @SuppressLint("HandlerLeak")
	Handler handler = new Handler(){
    	@Override
		public void handleMessage(Message msg){
    		if(msg.what==SAVE_DATA_CODE)
    			savedata();
    	}
    };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_tabbox);
		mPreferences = getSharedPreferences("APPSETTING", Context.MODE_PRIVATE);
		findViewById(R.id.img_user).setVisibility(View.GONE);
		findViewById(R.id.button_title).setVisibility(View.GONE);
		findViewById(R.id.textview_title).setVisibility(View.VISIBLE);
		((TextView) findViewById(R.id.textview_title)).setText("电箱信息");
		Button mButtonBack = (Button) findViewById(R.id.button_back);
		mButtonBack.setVisibility(View.VISIBLE);
		mButtonBack.setOnClickListener(this);
		Init();
		intent = TabBoxActivity.this.getIntent();
	}

	@Override
	public void onResume() {
		super.onResume();
		Boolean mqwork = mPreferences.getBoolean("myqwork", false);
		if (mqwork) {
			String action = intent.getStringExtra("action");
			if (action.equals("scan"))
				if (picname.getContentDescription().toString().equals(strbox1)
						&& first) {
					doclick(R.id.camerabox);
					first = false;
				}
		}
		
		registerHomeKeyReceiver(this);
	}
	@Override
    protected void onPause() {
        unregisterHomeKeyReceiver(this);
        super.onPause();
    }

	private void unregisterHomeKeyReceiver(TabBoxActivity tabBoxActivity) {
		try {
			if (null != mHomeKeyEventReceiver) {
				TabBoxActivity.this.unregisterReceiver(mHomeKeyEventReceiver);
	        }
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void registerHomeKeyReceiver(TabBoxActivity tabBoxActivity) {
		final IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
		TabBoxActivity.this.registerReceiver(mHomeKeyEventReceiver, homeFilter);
	}

	public void onBackPressed() {
		if (!havesave) {
			new AlertDialog.Builder(this)
					.setTitle("正在采集数据，请先确认保存？")
					.setIcon(R.drawable.warning)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									savedata();
									TabBoxActivity.this.finish();
								}
							})
					.setNegativeButton("返回",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
								}
							}).show();
		} else
			TabBoxActivity.this.finish();
	}
	
	private void Init() {
		savebtn = (Button) findViewById(R.id.saveboxbtn);
		savebtn.setOnClickListener(this);
		camera = (ImageView) findViewById(R.id.camerabox);
		camera.setOnClickListener(this);
		camera.setOnLongClickListener(this);
		camera2 = (ImageView) findViewById(R.id.camerabox2);
		camera2.setOnClickListener(this);
		camera2.setOnLongClickListener(this);
		camera3 = (ImageView) findViewById(R.id.camerabox3);
		camera3.setOnClickListener(this);
		camera3.setOnLongClickListener(this);
		delcamera = (ImageView) findViewById(R.id.delcamerabox);
		delcamera.setOnClickListener(this);
		delcamera2 = (ImageView) findViewById(R.id.delcamerabox2);
		delcamera2.setOnClickListener(this);
		delcamera3 = (ImageView) findViewById(R.id.delcamerabox3);
		delcamera3.setOnClickListener(this);
		boxseal1 = (ImageView) findViewById(R.id.scan_boxseal1);
		boxseal1.setOnClickListener(this);
		boxseal2 = (ImageView) findViewById(R.id.scan_boxseal2);
		boxseal2.setOnClickListener(this);
		boxseal3 = (ImageView) findViewById(R.id.scan_boxseal3);
		boxseal3.setOnClickListener(this);
		boxseal4 = (ImageView) findViewById(R.id.scan_boxseal4);
		boxseal4.setOnClickListener(this);
		boxseal5 = (ImageView) findViewById(R.id.scan_boxseal5);
		boxseal5.setOnClickListener(this);
		boxseal6 = (ImageView) findViewById(R.id.scan_boxseal6);
		boxseal6.setOnClickListener(this);
		picname = (TextView) findViewById(R.id.boxpicname);
		picname2 = (TextView) findViewById(R.id.boxpicname2);
		picname3 = (TextView) findViewById(R.id.boxpicname3);
		readData = (EditText) findViewById(R.id.ed_boxzb);
		readData.setInputType(EditorInfo.TYPE_CLASS_PHONE);
		eboxseal1 = (EditText) findViewById(R.id.value_boxseal1);
		eboxseal2 = (EditText) findViewById(R.id.value_boxseal2);
		eboxseal3 = (EditText) findViewById(R.id.value_boxseal3);
		eboxseal4 = (EditText) findViewById(R.id.value_boxseal4);
		eboxseal5 = (EditText) findViewById(R.id.value_boxseal5);
		eboxseal6 = (EditText) findViewById(R.id.value_boxseal6);
		Intent intent = TabBoxActivity.this.getIntent();
		String zbstr = "";
		try {
			zbstr = intent.getExtras().getString("data");
		} catch (Exception e) {
			// TODO: handle exception
		}
		readData.setText(zbstr);

		strbox1 = getResources().getString(R.string.boxtable1);
		strbox2 = getResources().getString(R.string.boxtable2);
		strbox3 = getResources().getString(R.string.boxtable3);
		picname.setContentDescription(strbox1);
		picname2.setContentDescription(strbox2);
		picname3.setContentDescription(strbox3);

		moretxv = (TextView) findViewById(R.id.txt_moreboxpic);
		moretxv.setOnClickListener(this);
		gridView = (MyGridView) findViewById(R.id.gridView1);
		
		mainAdapter=new ImgAdapter(TabBoxActivity.this,names,false);
        gridView.setAdapter(mainAdapter);
        setGridViewOnclick();
		gridView.setOnItemLongClickListener(new OnItemLongClickListener() {// 设置事件监听(长按)
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				Boolean isShowDelete=mainAdapter.getShowDelete(position);
				if (isShowDelete) {
                    isShowDelete = false;
                    //setGridViewOnclick();
                } else {
                    isShowDelete = true;
                }
                Log.d("TAG","onItemLongClicked");
                mainAdapter.setShowDelete(position,isShowDelete);
                //mainAdapter.setClickItemIndex(position);
				return true;
			}
		});
	}
	
	private void setGridViewOnclick()
	{
		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Boolean isShowDelete=mainAdapter.getShowDelete(position);
				if(!isShowDelete)
				{
					readPic(names.get(position).name);
				}
			}
		});
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.camerabox:
			String ostrtmp1 = picname.getContentDescription().toString();
			if (!ostrtmp1.equals(strbox1)) {
				readPic(ostrtmp1);
			} else {
				doclick(arg0.getId());
			}
			break;
		case R.id.delcamerabox:
			picname.setContentDescription(strbox1);
			camera.setImageResource(R.drawable.btn_camear3x);
			delcamera.setVisibility(View.GONE);
			break;
		case R.id.camerabox2:
			String ostrtmp2 = picname2.getContentDescription().toString();
			if (!ostrtmp2.equals(strbox2)) {
				readPic(ostrtmp2);
			} else {
				doclick(arg0.getId());
			}
			break;
		case R.id.delcamerabox2:
			picname2.setContentDescription(strbox2);
			camera2.setImageResource(R.drawable.btn_camear3x);
			delcamera2.setVisibility(View.GONE);
			break;
		case R.id.camerabox3:
			String ostrtmp3 = picname3.getContentDescription().toString();
			if (!ostrtmp3.equals(strbox3)) {
				readPic(ostrtmp3);
			} else {
				doclick(arg0.getId());
			}
			break;
		case R.id.delcamerabox3:
			picname3.setContentDescription(strbox3);
			camera3.setImageResource(R.drawable.btn_camear3x);
			delcamera3.setVisibility(View.GONE);
			break;
		case R.id.txt_moreboxpic:
			startCamera(R.id.txt_moreboxpic);
			break;
		default:
			doclick(arg0.getId());
			break;
		}
	}

	private void doclick(int viewid) {
		switch (viewid) {
		case R.id.button_back:
			onBackPressed();
			//TabBoxActivity.this.finish();
			break;
		case R.id.saveboxbtn:
			savedata();
			break;
		case R.id.camerabox:
			startCamera(R.id.camerabox);
			break;
		case R.id.camerabox2:
			startCamera(R.id.camerabox2);
			break;
		case R.id.camerabox3:
			startCamera(R.id.camerabox3);
			break;
		case R.id.scan_boxseal1:
			startScan(R.id.scan_boxseal1, -1);
			break;
		case R.id.scan_boxseal2:
			startScan(R.id.scan_boxseal2, -1);
			break;
		case R.id.scan_boxseal3:
			startScan(R.id.scan_boxseal3, -1);
			break;
		case R.id.scan_boxseal4:
			startScan(R.id.scan_boxseal4, -1);
			break;
		case R.id.scan_boxseal5:
			startScan(R.id.scan_boxseal5, -1);
			break;
		case R.id.scan_boxseal6:
			startScan(R.id.scan_boxseal6, -1);
			break;
		default:
			break;
		}
	}

	public void startCamera(int code) {
		String mFilePath = Environment.getExternalStorageDirectory().getPath()
				+ "/Photo";
		File file = new File(mFilePath);
		if (!file.exists() && !file.isDirectory()) {
			file.mkdir();
		}
		int random = (int) (Math.random() * (999 - 100)) + 100;
		String filename = String.valueOf(System.currentTimeMillis()) + random
				+ ".jpg";
		mFilePath = mFilePath + "/" + filename;
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File Photofile = new File(mFilePath);
		Uri imageUri = Uri.fromFile(Photofile);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		startActivityForResult(intent, code);
		CurrentImg = filename;
	}

	private void startScan(int code, int zoomTo) {
		Intent intentboxseal = new Intent();
		intentboxseal.setClass(TabBoxActivity.this, QRScanActivity.class);
		intentboxseal.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intentboxseal.putExtra("zoomto", zoomTo);
		startActivityForResult(intentboxseal, code);
	}

	@SuppressLint("SimpleDateFormat")
	private void savedata() {
		String zcbh = readData.getText().toString();
		if (zcbh.equals("")) {
			Toast.makeText(this, "表箱资产编号为空", Toast.LENGTH_SHORT).show();
			return;
		}
		String txtpicname1 = picname.getContentDescription().toString();
		if (txtpicname1.equals(strbox1)) {
			txtpicname1 = "";
			Toast.makeText(this, "请拍摄照片", Toast.LENGTH_SHORT).show();
		}
		String txtpicname2 = picname2.getContentDescription().toString();
		if (txtpicname2.equals(strbox2)) {
			txtpicname2 = "";
		}
		String txtpicname3 = picname3.getContentDescription().toString();
		if (txtpicname3.equals(strbox3)) {
			txtpicname3 = "";
		}
		String SEAL_NUMBER = eboxseal1.getText().toString();
		if (SEAL_NUMBER == null || SEAL_NUMBER.equals("")) {
			SEAL_NUMBER = "";
			Toast.makeText(this, "封印号为空", Toast.LENGTH_SHORT).show();
		}
		String SEAL_NUMBER2 = eboxseal2.getText().toString();
		if (SEAL_NUMBER2 == null || SEAL_NUMBER2.equals("")) {
			SEAL_NUMBER2 = "";
		}
		String SEAL_NUMBER3 = eboxseal3.getText().toString();
		if (SEAL_NUMBER3 == null || SEAL_NUMBER3.equals("")) {
			SEAL_NUMBER3 = "";
		}
		String SEAL_NUMBER4 = eboxseal4.getText().toString();
		if (SEAL_NUMBER4 == null || SEAL_NUMBER4.equals("")) {
			SEAL_NUMBER4 = "";
		}
		String SEAL_NUMBER5 = eboxseal5.getText().toString();
		if (SEAL_NUMBER5 == null || SEAL_NUMBER5.equals("")) {
			SEAL_NUMBER5 = "";
		}
		String SEAL_NUMBER6 = eboxseal6.getText().toString();
		if (SEAL_NUMBER6 == null || SEAL_NUMBER6.equals("")) {
			SEAL_NUMBER6 = "";
		}
		ContentValues cv = new ContentValues();
		cv.put("TABLE_CABINET_NO", zcbh);
		cv.put("PICTURES", txtpicname1);
		cv.put("PICTURES2", txtpicname2);
		cv.put("PICTURES3", txtpicname3);
		if(names.size()>0)
		{
			String otherpics="";
			for(int i=0;i<names.size();i++)
			{
				otherpics=otherpics+names.get(i).name;
				if(i<names.size()-1)
					otherpics=otherpics+",";
			}
			cv.put("MORE_PICTURES", otherpics);
		}
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateNowStr = sdf.format(d);
		cv.put("HLY_CREATE_DATE", dateNowStr);
		cv.put("TSTATIC", 0);
		cv.put("STATIC", 0);
		cv.put("DATACOUNT", 0);
		cv.put("SEAL_NUMBER", SEAL_NUMBER);
		cv.put("SEAL_NUMBER2", SEAL_NUMBER2);
		cv.put("SEAL_NUMBER3", SEAL_NUMBER3);
		cv.put("SEAL_NUMBER4", SEAL_NUMBER4);
		cv.put("SEAL_NUMBER5", SEAL_NUMBER5);
		cv.put("SEAL_NUMBER6", SEAL_NUMBER6);
		DBHelper helper = new DBHelper(this);
		try {
			Cursor cursor = helper.query("TabBoxData",
					new String[] { "_id,TABLE_CABINET_NO" },
					"TABLE_CABINET_NO = ?", new String[] { zcbh }, null, null,
					null);
			if (cursor != null) {
				if (cursor.moveToFirst() == false) {
					helper.insert(cv, "TabBoxData");
					Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
				} else {
					helper.update("TabBoxData", cv, "TABLE_CABINET_NO = ?",
							new String[] { zcbh });
					Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
				}
				cursor.close();
				Intent mIntent = new Intent();
				mIntent.putExtra("result", zcbh);
				this.setResult(RESULT_OK, mIntent);
				TabBoxActivity.this.finish();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	private void readPic(String picname)
	{
		File file = new File(Environment.getExternalStorageDirectory()
				.getPath() + "/Photo/" + picname);
		Intent intent = new Intent();
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), "image/*");
		startActivity(intent);
	}
	
	private void receive(TextView txt_pic,ImageView imgViewocamera,ImageView delimgview)
	{
		txt_pic.setContentDescription(CurrentImg);
		String path = Environment.getExternalStorageDirectory().getPath() + "/Photo/"
				+ txt_pic.getContentDescription().toString();
		Bitmap bitmap = Common.getImageThumbnail(path, 3);
		imgViewocamera.setImageBitmap(bitmap);
		imgViewocamera.setAdjustViewBounds(true);
		if(delimgview!=null)
			delimgview.setVisibility(View.VISIBLE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		havesave=false;
		try {
			switch (requestCode) {
			case R.id.camerabox:
				if (resultCode == RESULT_OK)
					receive(picname,camera,delcamera);
				break;
			case R.id.camerabox2:
				if (resultCode == RESULT_OK)
					receive(picname2,camera2,delcamera2);
				break;
			case R.id.camerabox3:
				if (resultCode == RESULT_OK) {
					receive(picname3,camera3,delcamera3);
					Boolean mqwork = mPreferences.getBoolean("myqwork", false);
					if (mqwork) {
						savedata();
					}
				}
				break;
			case R.id.scan_boxseal1:
				if (resultCode == RESULT_OK)
					eboxseal1.setText(data.getExtras().getString("result"));
				break;
			case R.id.scan_boxseal2:
				if (resultCode == RESULT_OK)
					eboxseal2.setText(data.getExtras().getString("result"));
				break;
			case R.id.scan_boxseal3:
				if (resultCode == RESULT_OK)
					eboxseal3.setText(data.getExtras().getString("result"));
				break;
			case R.id.scan_boxseal4:
				if (resultCode == RESULT_OK) 
					eboxseal4.setText(data.getExtras().getString("result"));
				break;
			case R.id.scan_boxseal5:
				if (resultCode == RESULT_OK) 
					eboxseal5.setText(data.getExtras().getString("result"));
				break;
			case R.id.scan_boxseal6:
				if (resultCode == RESULT_OK) 
					eboxseal6.setText(data.getExtras().getString("result"));
				break;
			case R.id.txt_moreboxpic:
				if (resultCode == RESULT_OK) {
					String path = Environment.getExternalStorageDirectory().getPath() + "/Photo/" + CurrentImg;
					Bitmap addbmp = Common.getImageThumbnail(path, 3);
					ImgContent content=new ImgContent();
					content.name=CurrentImg;
					content.icon=addbmp;
					names.add(content);
					mainAdapter=new ImgAdapter(TabBoxActivity.this,names,false);
			        gridView.setAdapter(mainAdapter);
					mainAdapter.notifyDataSetChanged();
				}
				break;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	@Override
	public boolean onLongClick(View arg0) {
		doclick(arg0.getId());
		return false;
	}
	
	private BroadcastReceiver mHomeKeyEventReceiver = new BroadcastReceiver() {  
        String SYSTEM_REASON = "reason";  
        String SYSTEM_HOME_KEY = "homekey";  
        //String SYSTEM_HOME_KEY_LONG = "recentapps";
        
        @Override  
        public void onReceive(Context context, Intent intent) {  
            String action = intent.getAction();  
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {  
                String reason = intent.getStringExtra(SYSTEM_REASON);  
                if (reason.equals(SYSTEM_HOME_KEY)) {
                    Toast.makeText(getApplicationContext(), "Key home", Toast.LENGTH_LONG).show();
                    Message message = new Message();
        			message.what = SAVE_DATA_CODE;
        			handler.sendMessage(message);
                } 
            }   
        }  
    }; 
}
