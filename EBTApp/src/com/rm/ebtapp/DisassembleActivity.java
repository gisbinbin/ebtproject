package com.rm.ebtapp;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.rm.db.DBHelper;
import com.rm.model.ImgContent;
import com.rm.model.TableBox;
import com.rm.model.TableData;
import com.rm.until.AnalysisResult;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

public class DisassembleActivity extends Activity implements OnClickListener,
		OnLongClickListener {
	private final static int SAVE_DATA_CODE = 1000;
	private SharedPreferences mPreferences;
	private TableBox currentbox = null;
	private Context mcontext;
	private String CurrentImg = "";
	private String stable,dtable,ztable,ytable,wtable,acttable,bcttable,ccttable,hcttable,btable;
	TextView txt_pic1, txt_pic2, txt_pic3, txt_pic4, txt_ctpic1, txt_ctpic2,
			txt_ctpic3, txt_ctpic4, txt_boxpic,txt_boxpic2,txt_boxpic3;
	EditText txt_readdata, txt_wreaddata, boxid, txt_username, txt_userid,
			txt_assetsid, txt_useraddress, txt_equipmentclass,txt_inposition,txt_datatag, eb_cseal1,
			eb_cseal2, eb_cseal3,eb_cseal4,eb_cseal5, eb_cseal6;
	private Button savedata;
	ImageView camera1, camera2, camera3, camera4, ctcamera1, ctcamera2,
			ctcamera3, ctcamera4, to_installpage, scan_boxid, camerabox, camerabox2, camerabox3,
			scanseal1, scanseal2, scanseal3,scanseal4, scanseal5, scanseal6;
	private ImageView delcamera1, delcamera2, delcamera3, delcamera4, delctcamera1, delctcamera2,delctcamera3, delctcamera4;
	private String data = "";
	private int position=1;
	private Boolean first = true;
	private Boolean havesave=true;
	private String currentboxno;

	FrameLayout wgllayout,fztpic1, fztpic2;
	LinearLayout rel_zpic, ztpic1, ztpic2;
	ImageView delimg;
	
	//其他图片相关参数
	private TextView tbmoretbpics;
	private MyGridView tbgridView;
    private List<ImgContent> tbcontent=new ArrayList<ImgContent>();
    private ImgAdapter tbAdapter;
	private TextView boxmoretbpics;
	private MyGridView boxgridView;
    private List<ImgContent> boxcontent=new ArrayList<ImgContent>();
    private ImgAdapter boxAdapter;

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
    
    @SuppressLint("HandlerLeak")
	Handler handler = new Handler(){
    	@Override
		public void handleMessage(Message msg){
    		if(msg.what==SAVE_DATA_CODE)
    			saveData(false);
    	}
    };
    
	@SuppressLint("CommitPrefEdits")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_disassemble);
		mcontext = this;
		mPreferences = getSharedPreferences("APPSETTING", Context.MODE_PRIVATE);
		findViewById(R.id.img_user).setVisibility(View.GONE);
		findViewById(R.id.button_title).setVisibility(View.GONE);
		findViewById(R.id.textview_title).setVisibility(View.VISIBLE);
		((TextView) findViewById(R.id.textview_title)).setText("电表拆卸");
		Button mButtonBack = (Button) findViewById(R.id.button_back);
		mButtonBack.setVisibility(View.VISIBLE);
		mButtonBack.setOnClickListener(this);
		to_installpage = (ImageView) findViewById(R.id.img_toinstall);
		to_installpage.setVisibility(View.VISIBLE);
		to_installpage.setOnClickListener(this);
		Init();

		Intent intent = DisassembleActivity.this.getIntent();
		try {
			currentbox = (TableBox) intent.getSerializableExtra("boxdata");
			data = intent.getExtras().getString("data");
			Boolean isnull = (data != null && !data.equals(""));
			if (isnull)
				setView(data);
		} catch (Exception e) {
			// TODO: handle exception
		}
		try {
			position=intent.getExtras().getInt("loc");
			if(position>0)
			{
				txt_inposition.setText(String.valueOf(position));
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		first = true;
		setboxview();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (data != null && !data.equals("")) {
			Boolean mqwork = mPreferences.getBoolean("myqwork", false);
			if (mqwork) {
				if (txt_pic1.getContentDescription().toString().equals(stable) && first) {
					executeclick(R.id.camerabtn1);
					first = false;
				}
			}
		}
		final IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
		DisassembleActivity.this.registerReceiver(mHomeKeyEventReceiver, homeFilter);
	}
	
	@Override
    protected void onPause() {
		try {
			if (null != mHomeKeyEventReceiver) {
				DisassembleActivity.this.unregisterReceiver(mHomeKeyEventReceiver);
	        }
		} catch (Exception e) {
			// TODO: handle exception
		}
        super.onPause();
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
									saveData(true);
									DisassembleActivity.this.finish();
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
			DisassembleActivity.this.finish();
	}
	
	private void Init() {
		savedata = (Button) findViewById(R.id.savebtn);
		savedata.setOnClickListener(this);
		txt_username = (EditText) findViewById(R.id.value_username);
		txt_userid = (EditText) findViewById(R.id.value_userid);
		txt_userid.setInputType(EditorInfo.TYPE_CLASS_PHONE);
		txt_assetsid = (EditText) findViewById(R.id.value_assetsid);
		txt_assetsid.setInputType(EditorInfo.TYPE_CLASS_PHONE);
		txt_useraddress = (EditText) findViewById(R.id.value_useraddress);
		txt_equipmentclass = (EditText) findViewById(R.id.value_equipmentclass);
		txt_inposition=(EditText)findViewById(R.id.value_inposition);
		txt_datatag=(EditText)findViewById(R.id.value_datatag);
		txt_readdata = (EditText) findViewById(R.id.editdata);
		txt_readdata.setInputType(EditorInfo.TYPE_CLASS_PHONE);
		txt_wreaddata = (EditText) findViewById(R.id.owedditdata);
		txt_wreaddata.setInputType(EditorInfo.TYPE_CLASS_PHONE);
		boxid = (EditText) findViewById(R.id.ded_boxzb);
		boxid.setInputType(EditorInfo.TYPE_CLASS_PHONE);
		eb_cseal1 = (EditText) findViewById(R.id.value_cseal1);
		eb_cseal2 = (EditText) findViewById(R.id.value_cseal2);
		eb_cseal3 = (EditText) findViewById(R.id.value_cseal3);
		eb_cseal4 = (EditText) findViewById(R.id.value_cseal4);
		eb_cseal5 = (EditText) findViewById(R.id.value_cseal5);
		eb_cseal6 = (EditText) findViewById(R.id.value_cseal6);
		txt_pic1 = (TextView) findViewById(R.id.pic1name);
		txt_pic2 = (TextView) findViewById(R.id.pic2name);
		txt_pic3 = (TextView) findViewById(R.id.pic3name);
		txt_pic4 = (TextView) findViewById(R.id.pic4name);
		txt_ctpic1 = (TextView) findViewById(R.id.ctpic1name);
		txt_ctpic2 = (TextView) findViewById(R.id.ctpic2name);
		txt_ctpic3 = (TextView) findViewById(R.id.ctpic3name);
		txt_ctpic4 = (TextView) findViewById(R.id.ctpic4name);
		txt_boxpic = (TextView) findViewById(R.id.dboxpicname);
		txt_boxpic2 = (TextView) findViewById(R.id.dboxpicname2);
		txt_boxpic3 = (TextView) findViewById(R.id.dboxpicname3);

		camera1 = (ImageView) findViewById(R.id.camerabtn1);
		camera1.setOnClickListener(this);
		camera1.setOnLongClickListener(this);
		camera2 = (ImageView) findViewById(R.id.camerabtn2);
		camera2.setOnClickListener(this);
		camera2.setOnLongClickListener(this);
		camera3 = (ImageView) findViewById(R.id.camerabtn3);
		camera3.setOnClickListener(this);
		camera3.setOnLongClickListener(this);
		camera4 = (ImageView) findViewById(R.id.camerabtn4);
		camera4.setOnClickListener(this);
		camera4.setOnLongClickListener(this);
		ctcamera1 = (ImageView) findViewById(R.id.ctcamerabtn1);
		ctcamera1.setOnClickListener(this);
		ctcamera1.setOnLongClickListener(this);
		ctcamera2 = (ImageView) findViewById(R.id.ctcamerabtn2);
		ctcamera2.setOnClickListener(this);
		ctcamera2.setOnLongClickListener(this);
		ctcamera3 = (ImageView) findViewById(R.id.ctcamerabtn3);
		ctcamera3.setOnClickListener(this);
		ctcamera3.setOnLongClickListener(this);
		ctcamera4 = (ImageView) findViewById(R.id.ctcamerabtn4);
		ctcamera4.setOnClickListener(this);
		ctcamera4.setOnLongClickListener(this);
		camerabox = (ImageView) findViewById(R.id.dcamerabox);
		camerabox.setOnClickListener(this);
		camerabox.setOnLongClickListener(this);
		camerabox2 = (ImageView) findViewById(R.id.dcamerabox2);
		camerabox2.setOnClickListener(this);
		camerabox2.setOnLongClickListener(this);
		camerabox3 = (ImageView) findViewById(R.id.dcamerabox3);
		camerabox3.setOnClickListener(this);
		camerabox3.setOnLongClickListener(this);
		
		delcamera1 = (ImageView) findViewById(R.id.delcamerabtn1);
		delcamera1.setOnClickListener(this);
		delcamera2 = (ImageView) findViewById(R.id.delcamerabtn2);
		delcamera2.setOnClickListener(this);
		delcamera3 = (ImageView) findViewById(R.id.delcamerabtn3);
		delcamera3.setOnClickListener(this);
		delcamera4 = (ImageView) findViewById(R.id.delcamerabtn4);
		delcamera4.setOnClickListener(this);
		delctcamera1 = (ImageView) findViewById(R.id.delctcamerabtn1);
		delctcamera1.setOnClickListener(this);
		delctcamera2 = (ImageView) findViewById(R.id.delctcamerabtn2);
		delctcamera2.setOnClickListener(this);
		delctcamera3 = (ImageView) findViewById(R.id.delctcamerabtn3);
		delctcamera3.setOnClickListener(this);
		delctcamera4 = (ImageView) findViewById(R.id.delctcamerabtn4);
		delctcamera4.setOnClickListener(this);

		scanseal1 = (ImageView) findViewById(R.id.scan_cseal1);
		scanseal1.setOnClickListener(this);
		scanseal1.setOnLongClickListener(this);
		scanseal2 = (ImageView) findViewById(R.id.scan_cseal2);
		scanseal2.setOnClickListener(this);
		scanseal2.setOnLongClickListener(this);
		scanseal3 = (ImageView) findViewById(R.id.scan_cseal3);
		scanseal3.setOnClickListener(this);
		scanseal3.setOnLongClickListener(this);
		scanseal4 = (ImageView) findViewById(R.id.scan_cseal4);
		scanseal4.setOnClickListener(this);
		scanseal4.setOnLongClickListener(this);
		scanseal5 = (ImageView) findViewById(R.id.scan_cseal5);
		scanseal5.setOnClickListener(this);
		scanseal5.setOnLongClickListener(this);
		scanseal6 = (ImageView) findViewById(R.id.scan_cseal6);
		scanseal6.setOnClickListener(this);
		scanseal6.setOnLongClickListener(this);

		scan_boxid = (ImageView) findViewById(R.id.dscan_boxid);
		scan_boxid.setOnClickListener(this);
		
		stable=getResources().getString(R.string.stable);
		dtable=getResources().getString(R.string.dtable);
		ztable=getResources().getString(R.string.ztable);
		ytable=getResources().getString(R.string.ytable);
		wtable=getResources().getString(R.string.wtable);
		acttable=getResources().getString(R.string.acttable);
		bcttable=getResources().getString(R.string.bcttable);
		ccttable=getResources().getString(R.string.ccttable);
		hcttable=getResources().getString(R.string.hcttable);
		btable=getResources().getString(R.string.boxtable);

		tbmoretbpics=(TextView)findViewById(R.id.txt_cmorepics);
		tbmoretbpics.setOnClickListener(this);
		tbgridView = (MyGridView) findViewById(R.id.cgridtbView);
		tbAdapter=new ImgAdapter(DisassembleActivity.this,tbcontent,false);
		tbgridView.setAdapter(tbAdapter);
		settbGridViewOnclick();
		tbgridView.setOnItemLongClickListener(new OnItemLongClickListener() {// 设置事件监听(长按)
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				Boolean tbisShowDelete=tbAdapter.getShowDelete(position);
				if (tbisShowDelete) {
                    tbisShowDelete = false;
                    settbGridViewOnclick();
                } else {
                    tbisShowDelete = true;
                    tbgridView.setOnItemClickListener(null);
                }
                Log.d("TAG","onItemLongClicked");
                tbAdapter.setShowDelete(position,tbisShowDelete);
				return true;
			}
		});

		boxmoretbpics=(TextView)findViewById(R.id.txt_cmoreboxpic);
		boxmoretbpics.setOnClickListener(this);
		boxgridView = (MyGridView) findViewById(R.id.cgridboxView);
		boxAdapter=new ImgAdapter(DisassembleActivity.this,boxcontent,false);
		boxgridView.setAdapter(boxAdapter);
		setboxGridViewOnclick();
		boxgridView.setOnItemLongClickListener(new OnItemLongClickListener() {// 设置事件监听(长按)
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				Boolean boxisShowDelete=boxAdapter.getShowDelete(position);
				if (boxisShowDelete) {
                    boxisShowDelete = false;
                    setboxGridViewOnclick();
                } else {
                    boxisShowDelete = true;
                    boxgridView.setOnItemClickListener(null);
                }
                Log.d("TAG","onItemLongClicked");
                boxAdapter.setShowDelete(position,boxisShowDelete);
				return true;
			}
		});
	}

	private void settbGridViewOnclick() {
		tbgridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Boolean tbisShowDelete=tbAdapter.getShowDelete(position);
				if(!tbisShowDelete)
				{
					File file = new File(Environment.getExternalStorageDirectory()
							.getPath() + "/Photo/" + tbcontent.get(position).name);
					Intent intent = new Intent();
					intent.setAction(android.content.Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.fromFile(file), "image/*");
					startActivity(intent);
				}
			}
		});
	}
	private void setboxGridViewOnclick() {
		boxgridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Boolean boxisShowDelete=boxAdapter.getShowDelete(position);
				if(!boxisShowDelete)
				{
					File file = new File(Environment.getExternalStorageDirectory()
							.getPath() + "/Photo/" + boxcontent.get(position).name);
					Intent intent = new Intent();
					intent.setAction(android.content.Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.fromFile(file), "image/*");
					startActivity(intent);
				}
			}
		});
	}

	private void setView(String data) {
		try {
			AnalysisResult analysis = new AnalysisResult();
			TableData tabledata = analysis.getData(data);
			txt_username.setText(tabledata.getUserName());
			txt_userid.setText(tabledata.getUserId());
			txt_assetsid.setText(tabledata.getAssetsId());
			txt_useraddress.setText(tabledata.getUserAddress());
			txt_equipmentclass.setText(tabledata.getEquipmentClass());
			if(position>0)
				txt_inposition.setText(position);
			if (currentbox == null) {
				if (tabledata.getEquipmentClass().equals("0")) {
					txt_equipmentclass.setText("单相散表");
					hideCTPhoto();
				} else if (tabledata.getEquipmentClass().equals("1")) {
					txt_equipmentclass.setText("三相散表");
					resetview();
				} else {
				}
			} else {
				if (tabledata.getEquipmentClass().equals("1")) {
					txt_equipmentclass.setText("三相集中表");
					hideCTPhoto();
					resetview();
				} else {
					hideCTPhoto();
					txt_equipmentclass.setText("单相集中表");
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void hideCTPhoto() {
		findViewById(R.id.rel_ctpic).setVisibility(View.GONE);
		findViewById(R.id.drel_hctpic).setVisibility(View.GONE);
	}

	private void resetview() {
		wgllayout = (FrameLayout) findViewById(R.id.wgview);
		fztpic1 = (FrameLayout) findViewById(R.id.fztpic1);
		fztpic2 = (FrameLayout) findViewById(R.id.fztpic2);
		rel_zpic = (LinearLayout) findViewById(R.id.rel_zpic);
		ztpic1 = (LinearLayout) findViewById(R.id.ztpic1);
		ztpic2 = (LinearLayout) findViewById(R.id.ztpic2);
		delimg=(ImageView) findViewById(R.id.delcamerabtn3);
		fztpic1.setVisibility(View.GONE);
		wgllayout.setVisibility(View.VISIBLE);
		ztpic1.removeView(camera3);
		ztpic2.addView(camera3);
		ztpic1.removeView(txt_pic3);
		ztpic2.addView(txt_pic3);
		fztpic1.removeView(delimg);
		fztpic2.addView(delimg);
		rel_zpic.setVisibility(View.VISIBLE);
		txt_pic1.setText(ytable);
		txt_pic1.setContentDescription(ytable);
	}

	private void setboxview() {
		try {
			if (currentbox != null) {
				if (currentbox.getTABLE_CABINET_NO() != null
						&& !currentbox.getTABLE_CABINET_NO().equals("")) {
					boxid.setText(currentbox.getTABLE_CABINET_NO());
				}
				if (currentbox.getPICTURES() != null
						&& !currentbox.getPICTURES().equals("")) {
					txt_boxpic.setContentDescription(currentbox.getPICTURES());
					String boxpicpath = Environment
							.getExternalStorageDirectory().getPath()
							+ "/Photo/" + currentbox.getPICTURES();
					Bitmap bitmap3 = Common.getImageThumbnail(boxpicpath, 3);
					if(bitmap3!=null){
						camerabox.setImageBitmap(bitmap3);
						camerabox.setAdjustViewBounds(true);
					}
				}
				if (currentbox.getPICTURES2() != null
						&& !currentbox.getPICTURES2().equals("")) {
					txt_boxpic2.setContentDescription(currentbox.getPICTURES2());
					String boxpicpath = Environment
							.getExternalStorageDirectory().getPath()
							+ "/Photo/" + currentbox.getPICTURES2();
					Bitmap bitmap3 = Common.getImageThumbnail(boxpicpath, 3);
					camerabox2.setImageBitmap(bitmap3);
					camerabox2.setAdjustViewBounds(true);
				}
				if (currentbox.getPICTURES3() != null
						&& !currentbox.getPICTURES3().equals("")) {
					txt_boxpic3.setContentDescription(currentbox.getPICTURES3());
					String boxpicpath = Environment
							.getExternalStorageDirectory().getPath()
							+ "/Photo/" + currentbox.getPICTURES3();
					Bitmap bitmap3 = Common.getImageThumbnail(boxpicpath, 3);
					camerabox3.setImageBitmap(bitmap3);
					camerabox3.setAdjustViewBounds(true);
				}
				if(currentbox.getMORE_PICTURES()!=null&&!currentbox.getMORE_PICTURES().equals(""))
				{
					String[] arr=currentbox.getMORE_PICTURES().split(",");
					for(int i=0;i<arr.length;i++)
					{
						String path = Environment.getExternalStorageDirectory()
								.getPath() + "/Photo/" + arr[i];
						// ysImg(path);
						Bitmap addbmp = Common.getImageThumbnail(path, 3);
						if(addbmp!=null)
						{
							ImgContent con=new ImgContent();
							con.name= arr[i];
							con.icon=addbmp;
							boxcontent.add(con);
						}
					}
					boxAdapter=new ImgAdapter(DisassembleActivity.this,boxcontent,false);
			        boxgridView.setAdapter(boxAdapter);
					boxAdapter.notifyDataSetChanged();
				}
				if (currentbox.getSEAL_NUMBER() != null
						&& !currentbox.getSEAL_NUMBER().equals(""))
					eb_cseal1.setText(currentbox.getSEAL_NUMBER());
				if (currentbox.getSEAL_NUMBER2() != null
						&& !currentbox.getSEAL_NUMBER2().equals(""))
					eb_cseal2.setText(currentbox.getSEAL_NUMBER2());
				if (currentbox.getSEAL_NUMBER3() != null
						&& !currentbox.getSEAL_NUMBER3().equals(""))
					eb_cseal3.setText(currentbox.getSEAL_NUMBER3());

				if (currentbox.getSEAL_NUMBER4() != null
						&& !currentbox.getSEAL_NUMBER4().equals(""))
					eb_cseal4.setText(currentbox.getSEAL_NUMBER4());
				if (currentbox.getSEAL_NUMBER5() != null
						&& !currentbox.getSEAL_NUMBER5().equals(""))
					eb_cseal5.setText(currentbox.getSEAL_NUMBER5());
				if (currentbox.getSEAL_NUMBER6() != null
						&& !currentbox.getSEAL_NUMBER6().equals(""))
					eb_cseal6.setText(currentbox.getSEAL_NUMBER6());
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@SuppressLint({ "ShowToast", "SimpleDateFormat" })
	private void saveData(Boolean stopedit) {
		if (boxid.getText().toString() != null
				&& !boxid.getText().toString().equals("")) {
			currentboxno = boxid.getText().toString();
			currentbox = getBoxData(currentboxno);
		}
		String username = txt_username.getText().toString();
		if (username.equals("")) {
			Toast.makeText(this, "用户名为空", Toast.LENGTH_SHORT).show();
			return;
		}
		String userid = txt_userid.getText().toString();
		if (userid.equals("")) {
			Toast.makeText(this, "用户号为空", Toast.LENGTH_SHORT).show();
			return;
		}
		String assetsid = txt_assetsid.getText().toString();
		if (assetsid.equals("")) {
			Toast.makeText(this, "资产编号为空", Toast.LENGTH_SHORT).show();
			return;
		}
		String useraddress = txt_useraddress.getText().toString();
		if (useraddress.equals("")) {
			useraddress = "用户地址";
		}
		String equipmentclass = txt_equipmentclass.getText().toString();
		if (equipmentclass.equals("")) {
			equipmentclass = "设备类型";
		}
		String BIN_POSITION_NUMBER=txt_inposition.getText().toString();
		if(BIN_POSITION_NUMBER.equals(""))
			BIN_POSITION_NUMBER="1";
		String DATATag=txt_datatag.getText().toString();
		String readdata = txt_readdata.getText().toString();
		String wreaddata = txt_wreaddata.getText().toString();
		Boolean mdatacheck = mPreferences.getBoolean("mydatacheck", false);
		if (mdatacheck) {
			if (readdata.equals("")) {
				Toast.makeText(this, "电表读数为空", Toast.LENGTH_SHORT).show();
				return;
			}
			if (wreaddata.equals("")) {
				Toast.makeText(this, "无功读数为空", Toast.LENGTH_SHORT).show();
				return;
			}
		}
		String picname1 = txt_pic1.getContentDescription().toString();
		if (picname1.equals(stable) || picname1.equals(ytable))
			picname1 = "";
		String picname2 = txt_pic2.getContentDescription().toString();
		if (picname2.equals(dtable))
			picname2 = "";
		String picname3 = txt_pic3.getContentDescription().toString();
		if (picname3.equals(ztable))
			picname3 = "";
		String picname4 = txt_pic4.getContentDescription().toString();
		if (picname4.equals(wtable))
			picname4 = "";
		int count = 0;
		Boolean hasd = (picname1.equals("") || picname1.equals(stable) || picname1
				.equals(ytable));
		Boolean hass = (picname2.equals("") || picname2.equals(dtable));
		Boolean hasq = (picname3.equals("") || picname3.equals(ztable));
		Boolean hasw = (picname4.equals("") || picname4.equals(wtable));
		if (!hasd)
			count = count + 1;
		if (!hass)
			count = count + 1;
		if (!hasq)
			count = count + 1;
		if (!hasw)
			count = count + 1;
		if (count < 2) {
			Toast.makeText(this, "拍摄少于两张照片", Toast.LENGTH_SHORT).show();
		}
		ContentValues cv = new ContentValues();
		cv.put("username", username);
		cv.put("usernumber", userid);
		cv.put("assetsnumber", assetsid);
		cv.put("useraddress", useraddress);
		cv.put("equipmentclass", equipmentclass);
		cv.put("BIN_POSITION_NUMBER", BIN_POSITION_NUMBER);
		cv.put("TAG", DATATag);
		cv.put("readdata", readdata);
		cv.put("wreaddata", wreaddata);
		cv.put("ameterspic", picname1);
		cv.put("tmeterspic", picname2);
		cv.put("allpic", picname3);
		cv.put("OLD_REACTIVE_PICTURES", picname4);
		if(tbcontent.size()>0)
		{
			String otherpics="";
			for(int i=0;i<tbcontent.size();i++)
			{
				otherpics=otherpics+tbcontent.get(i).name;
				if(i<tbcontent.size()-1)
					otherpics=otherpics+",";
			}
			cv.put("OLD_MORE_PICTURES", otherpics);
		}
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateNowStr = sdf.format(d);
		cv.put("dtime", dateNowStr);
		cv.put("dstatic", 1);
		cv.put("istatic", 0);
		cv.put("static", 0);
		String tableno = boxid.getText().toString();
		if (tableno != null && !tableno.equals(""))
			cv.put("TABLE_CABINET_NO", tableno);

		String OCT_SINGLE_PICTURES = txt_ctpic1.getContentDescription().toString()
				.replace(" ", "");
		if (OCT_SINGLE_PICTURES != null && !OCT_SINGLE_PICTURES.equals(acttable.replace(" ", "")))
			cv.put("OCT_SINGLE_PICTURES", OCT_SINGLE_PICTURES);
		String OCT_DOUBLE_PICTURES = txt_ctpic2.getContentDescription().toString()
				.replace(" ", "");
		if (OCT_DOUBLE_PICTURES != null && !OCT_DOUBLE_PICTURES.equals(bcttable.replace(" ", "")))
			cv.put("OCT_DOUBLE_PICTURES", OCT_DOUBLE_PICTURES);
		String OCT_WHOLE_PICTURES = txt_ctpic3.getContentDescription().toString()
				.replace(" ", "");
		if (OCT_WHOLE_PICTURES != null && !OCT_WHOLE_PICTURES.equals(ccttable.replace(" ", "")))
			cv.put("OCT_WHOLE_PICTURES", OCT_WHOLE_PICTURES);
		String ROUND_CT = txt_ctpic4.getContentDescription().toString().replace(" ", "");
		if (ROUND_CT != null && !ROUND_CT.equals(hcttable.replace(" ", "")))
			cv.put("ROUND_CT", ROUND_CT);

		DBHelper helper = new DBHelper(this);

		if (currentbox != null) {
			ContentValues upd = new ContentValues();
			String pic = txt_boxpic.getContentDescription().toString();
			if (pic != null && !pic.equals("")&&!pic.equals(btable))
				upd.put("PICTURES", txt_boxpic.getContentDescription().toString());
			String txtboxpic2 = txt_boxpic2.getContentDescription().toString();
			if (txtboxpic2 != null && !txtboxpic2.equals("")&&!txtboxpic2.equals(btable)) {
				upd.put("PICTURES2", txtboxpic2);
			}
			String txtboxpic3 = txt_boxpic3.getContentDescription().toString();
			if (txtboxpic3 != null && !txtboxpic3.equals("")&&!txtboxpic3.equals(btable)) {
				upd.put("PICTURES3", txtboxpic3);
			}
			if(boxcontent.size()>0)
			{
				String otherpics="";
				for(int i=0;i<boxcontent.size();i++)
				{
					otherpics=otherpics+boxcontent.get(i).name;
					if(i<boxcontent.size()-1)
						otherpics=otherpics+",";
				}
				upd.put("MORE_PICTURES", otherpics);
			}
			String seal1 = eb_cseal1.getText().toString();
			if (seal1 != null && !seal1.equals(""))
				upd.put("SEAL_NUMBER", eb_cseal1.getText().toString());
			String seal2 = eb_cseal2.getText().toString();
			if (seal2 != null && !seal2.equals(""))
				upd.put("SEAL_NUMBER2", eb_cseal2.getText().toString());
			String seal3 = eb_cseal3.getText().toString();
			if (seal3 != null && !seal3.equals(""))
				upd.put("SEAL_NUMBER3", eb_cseal3.getText().toString());
			String seal4 = eb_cseal4.getText().toString();
			if (seal4 != null && !seal4.equals(""))
				upd.put("SEAL_NUMBER4", eb_cseal4.getText().toString());
			String seal5 = eb_cseal5.getText().toString();
			if (seal5 != null && !seal5.equals(""))
				upd.put("SEAL_NUMBER5", eb_cseal5.getText().toString());
			String seal6 = eb_cseal6.getText().toString();
			if (seal6 != null && !seal6.equals(""))
				upd.put("SEAL_NUMBER6", eb_cseal6.getText().toString());
			helper.update("TabBoxData", upd, "TABLE_CABINET_NO = ?",
					new String[] { currentbox.getTABLE_CABINET_NO() });
		} else {
			String edboxid = boxid.getText().toString();
			String txtboxpic = txt_boxpic.getContentDescription().toString();
			if (edboxid != null && txtboxpic != null && !edboxid.equals("")
					&& !txtboxpic.equals("")&&!txtboxpic.equals(btable)) {
				ContentValues upd = new ContentValues();
				upd.put("TABLE_CABINET_NO", edboxid);
				upd.put("PICTURES", txtboxpic);
				String txtboxpic2 = txt_boxpic2.getContentDescription().toString();
				if (txtboxpic2 != null && !txtboxpic2.equals("")&&!txtboxpic2.equals(btable)) {
					upd.put("PICTURES2", txtboxpic2);
				}
				String txtboxpic3 = txt_boxpic3.getContentDescription().toString();
				if (txtboxpic3 != null && !txtboxpic3.equals("")&&!txtboxpic3.equals(btable)) {
					upd.put("PICTURES3", txtboxpic3);
				}
				if(boxcontent.size()>0)
				{
					String otherpics="";
					for(int i=0;i<boxcontent.size();i++)
					{
						otherpics=otherpics+boxcontent.get(i).name;
						if(i<boxcontent.size()-1)
							otherpics=otherpics+",";
					}
					upd.put("MORE_PICTURES", otherpics);
				}
				Date dt = new Date();
				SimpleDateFormat sdft = new SimpleDateFormat("yyyy-MM-dd");
				String dateNowStrt = sdft.format(dt);
				upd.put("HLY_CREATE_DATE", dateNowStrt);
				upd.put("TSTATIC", 0);
				upd.put("STATIC", 0);
				String seal1 = eb_cseal1.getText().toString();
				if (seal1 != null && !seal1.equals(""))
					upd.put("SEAL_NUMBER", eb_cseal1.getText().toString());
				String seal2 = eb_cseal2.getText().toString();
				if (seal2 != null && !seal2.equals(""))
					upd.put("SEAL_NUMBER2", eb_cseal2.getText().toString());
				String seal3 = eb_cseal3.getText().toString();
				if (seal3 != null && !seal3.equals(""))
					upd.put("SEAL_NUMBER3", eb_cseal3.getText().toString());
				String seal4 = eb_cseal4.getText().toString();
				if (seal4 != null && !seal4.equals(""))
					upd.put("SEAL_NUMBER4", eb_cseal4.getText().toString());
				String seal5 = eb_cseal5.getText().toString();
				if (seal5 != null && !seal5.equals(""))
					upd.put("SEAL_NUMBER5", eb_cseal5.getText().toString());
				String seal6 = eb_cseal6.getText().toString();
				if (seal6 != null && !seal6.equals(""))
					upd.put("SEAL_NUMBER6", eb_cseal6.getText().toString());
				helper.insert(upd, "TabBoxData");
			}
		}

		try {
			Cursor cursor = helper.query("RecordData",
					new String[] { "_id,username,usernumber,assetsnumber" },
					"username = ? and usernumber = ? and assetsnumber=?",
					new String[] { username, userid, assetsid }, null, null,
					null);
			if (cursor != null) {
				if (cursor.moveToFirst() == false) {
					cv.put("istatic", 0);
					helper.insert(cv, "RecordData");
					if (currentbox != null) {
						if (currentbox.getTABLE_CABINET_NO() != null
								&& !currentbox.getTABLE_CABINET_NO().equals("")) {
							if (currentbox.getDATACOUNT() == null
									|| currentbox.getDATACOUNT().equals("")
									|| currentbox.getDATACOUNT().equals("0")) {
								ContentValues upd = new ContentValues();
								upd.put("DATACOUNT", "0" + "/" + "1");
								helper.update("TabBoxData", upd,
										"TABLE_CABINET_NO = ?",
										new String[] { currentbox
												.getTABLE_CABINET_NO() });
								currentbox.setDATACOUNT("0" + "/" + "1");
							} else {
								if (currentbox.getDATACOUNT().contains("/")) {
									String[] arr = currentbox.getDATACOUNT()
											.split("/");
									try {
										int first = Integer.parseInt(arr[0]);
										int sce = Integer.parseInt(arr[1]) + 1;
										ContentValues upd = new ContentValues();
										upd.put("DATACOUNT", first + "/" + sce);
										helper.update(
												"TabBoxData", upd, "TABLE_CABINET_NO = ?",
												new String[] { currentbox.getTABLE_CABINET_NO() });
										currentbox.setDATACOUNT(first + "/"
												+ sce);
									} catch (Exception e) {
										// TODO: handle exception
									}
								} else {
									ContentValues upd = new ContentValues();
									upd.put("DATACOUNT", "0" + "/" + "1");
									helper.update("TabBoxData", upd,
											"TABLE_CABINET_NO = ?",
											new String[] { currentbox
													.getTABLE_CABINET_NO() });
									currentbox.setDATACOUNT("0" + "/" + "1");
								}
							}
						}

					} else {
						String edboxid = boxid.getText().toString();
						String txtboxpic = txt_boxpic.getContentDescription().toString();
						if (edboxid != null && txtboxpic != null
								&& !edboxid.equals("") && !txtboxpic.equals("")&& !txtboxpic.equals(btable)) {
							ContentValues upd = new ContentValues();
							upd.put("TABLE_CABINET_NO", edboxid);
							upd.put("DATACOUNT", "0" + "/" + "1");
							helper.update("TabBoxData", upd,
									"TABLE_CABINET_NO = ?",
									new String[] { edboxid });
						}
					}
					Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
				} else {
					helper.update(
							"RecordData",
							cv,
							"username = ? and usernumber = ? and assetsnumber=?",
							new String[] { username, userid, assetsid });
					Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
				}
				cursor.close();
				if(stopedit){
					Intent mIntent = new Intent();
					this.setResult(RESULT_OK, mIntent);
					DisassembleActivity.this.finish();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private TableBox getBoxData(String NO) {
		TableBox resultdata = null;
		DBHelper helper = new DBHelper(this);
		try {
			Cursor cursor = helper
					.query("TabBoxData",
							new String[] { "_id,TABLE_CABINET_NO,HLY_CREATE_DATE,PICTURES,PICTURES2,PICTURES3,MORE_PICTURES,DATACOUNT" },
							"TABLE_CABINET_NO = ?",
							new String[] { currentboxno }, null, null, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					resultdata = new TableBox();
					resultdata
							.setID(cursor.getInt(cursor.getColumnIndex("_id")));
					resultdata.setTABLE_CABINET_NO(cursor.getString(cursor
							.getColumnIndex("TABLE_CABINET_NO")));
					resultdata.setHLY_CREATE_DATE(cursor.getString(cursor
							.getColumnIndex("HLY_CREATE_DATE")));
					resultdata.setPICTURES(cursor.getString(cursor
							.getColumnIndex("PICTURES")));
					resultdata.setPICTURES2(cursor.getString(cursor
							.getColumnIndex("PICTURES2")));
					resultdata.setPICTURES3(cursor.getString(cursor
							.getColumnIndex("PICTURES3")));
					resultdata.setMORE_PICTURES(cursor.getString(cursor
							.getColumnIndex("MORE_PICTURES")));
					resultdata.setDATACOUNT(cursor.getString(cursor
							.getColumnIndex("DATACOUNT")));
				}
				cursor.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultdata;
	}

	@Override
	public boolean onLongClick(View arg0) {
		executeclick(arg0.getId());
		return false;
	}

	@Override
	public void onClick(View arg0) {
		int viewid = arg0.getId();
		switch (viewid) {
		case R.id.savebtn:
			saveData(true);
			break;
		case R.id.button_back:
			onBackPressed();
			break;
		case R.id.img_toinstall:
			Intent Install_intent = new Intent(DisassembleActivity.this,
					InstallActivity.class);
			Install_intent.putExtra("boxdata", currentbox);
			Install_intent.putExtra("action", "C");
			Install_intent.putExtra("data", data);
			startActivity(Install_intent);
			DisassembleActivity.this.finish();
			break;
		case R.id.camerabtn1:
			String strtmp = txt_pic1.getContentDescription().toString();
			if (!strtmp.equals("") &&!strtmp.equals(stable) && !strtmp.equals(ytable)) {
				showImg(strtmp);
			} else
				executeclick(arg0.getId());
			break;
		case R.id.camerabtn4:
			String wstrtmp = txt_pic4.getContentDescription().toString();
			if (!wstrtmp.equals("")&&!wstrtmp.equals(wtable)) {
				showImg(wstrtmp);
			} else
				executeclick(arg0.getId());
			break;
		case R.id.camerabtn2:
			String strtmp2 = txt_pic2.getContentDescription().toString();
			if (!strtmp2.equals("")&&!strtmp2.equals(dtable)) {
				showImg(strtmp2);
			} else
				executeclick(arg0.getId());
			break;
		case R.id.camerabtn3:
			String strtmp3 = txt_pic3.getContentDescription().toString();
			if (!strtmp3.equals("")&&!strtmp3.equals(ztable)) {
				showImg(strtmp3);
			} else
				executeclick(arg0.getId());
			break;
		case R.id.dcamerabox:
			String strtmp4 = txt_boxpic.getContentDescription().toString();
			if (!strtmp4.equals("")&&!strtmp4.equals(btable)) {
				showImg(strtmp4);
			} else
				executeclick(arg0.getId());
			break;
		case R.id.dcamerabox2:
			String strtmp42 = txt_boxpic2.getContentDescription().toString();
			if (!strtmp42.equals("")&&!strtmp42.equals(btable)) {
				showImg(strtmp42);
			} else
				executeclick(arg0.getId());
			break;
		case R.id.dcamerabox3:
			String strtmp43 = txt_boxpic3.getContentDescription().toString();
			if (!strtmp43.equals("")&&!strtmp43.equals(btable)) {
				showImg(strtmp43);
			} else
				executeclick(arg0.getId());
			break;
		case R.id.ctcamerabtn1:
			String strtmp5 = txt_ctpic1.getContentDescription().toString().replace(" ", "");
			if (!strtmp5.equals("")&&!strtmp5.equals("CTA相")) {
				showImg(strtmp5);
			} else
				executeclick(arg0.getId());
			break;
		case R.id.ctcamerabtn2:
			String strtmp6 = txt_ctpic2.getContentDescription().toString().replace(" ", "");
			if (!strtmp6.equals("")&&!strtmp6.equals("CTB相")) {
				showImg(strtmp6);
			} else
				executeclick(arg0.getId());
			break;
		case R.id.ctcamerabtn3:
			String strtmp7 = txt_ctpic3.getContentDescription().toString().replace(" ", "");
			if (!strtmp7.equals("")&&!strtmp7.equals("CTC相")) {
				showImg(strtmp7);
			} else
				executeclick(arg0.getId());
			break;
		case R.id.ctcamerabtn4:
			String strtmp8 = txt_ctpic4.getContentDescription().toString().replace(" ", "");
			if (!strtmp8.equals("")&&!strtmp8.equals("CT合相")) {
				showImg(strtmp8);
			} else
				executeclick(arg0.getId());
			break;
		case R.id.dscan_boxid:
			startScan(R.id.dscan_boxid,0);
			break;
		case R.id.scan_cseal1:
			startScan(R.id.scan_cseal1,-1);
			break;
		case R.id.scan_cseal2:
			startScan(R.id.scan_cseal2,-1);
			break;
		case R.id.scan_cseal3:
			startScan(R.id.scan_cseal3,-1);
			break;
		case R.id.scan_cseal4:
			startScan(R.id.scan_cseal4,-1);
			break;
		case R.id.scan_cseal5:
			startScan(R.id.scan_cseal5,-1);
			break;
		case R.id.scan_cseal6:
			startScan(R.id.scan_cseal6,-1);
			break;
		default:
			executeclick(arg0.getId());
			break;
		}
	}
	
	private void startScan(int code,int zoomTo)
	{
		Intent intentScan = new Intent();
		intentScan.setClass(DisassembleActivity.this,
				QRScanActivity.class);
		intentScan.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intentScan.putExtra("zoomto", zoomTo);
		startActivityForResult(intentScan, code);
	}

	private void showImg(String filename) {
		File file = new File(Environment.getExternalStorageDirectory()
				.getPath() + "/Photo/" + filename);
		Intent intent = new Intent();
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), "image/*");
		startActivity(intent);
	}

	private void executeclick(int viewid) {
		switch (viewid) {
		case R.id.camerabtn1:
			startcamera(R.id.camerabtn1);
			break;
		case R.id.camerabtn2:
			startcamera(R.id.camerabtn2);
			break;
		case R.id.camerabtn3:
			startcamera(R.id.camerabtn3);
			break;
		case R.id.camerabtn4:
			startcamera(R.id.camerabtn4);
			break;
		case R.id.ctcamerabtn1:
			startcamera(R.id.ctcamerabtn1);
			break;
		case R.id.ctcamerabtn2:
			startcamera(R.id.ctcamerabtn2);
			break;
		case R.id.ctcamerabtn3:
			startcamera(R.id.ctcamerabtn3);
			break;
		case R.id.ctcamerabtn4:
			startcamera(R.id.ctcamerabtn4);
			break;
		case R.id.txt_cmorepics:
			startcamera(R.id.txt_cmorepics);
			break;
		case R.id.dcamerabox:
			startcamera(R.id.dcamerabox);
			break;
		case R.id.dcamerabox2:
			startcamera(R.id.dcamerabox2);
			break;
		case R.id.dcamerabox3:
			startcamera(R.id.dcamerabox3);
			break;
		case R.id.txt_cmoreboxpic:
			startcamera(R.id.txt_cmoreboxpic);
			break;
		case R.id.delcamerabtn1:
			delImg(camera1,delcamera1,txt_pic1);
			break;
		case R.id.delcamerabtn2:
			delImg(camera2,delcamera2,txt_pic3);
			break;
		case R.id.delcamerabtn3:
			delImg(camera3,delcamera3,txt_pic3);
			break;
		case R.id.delcamerabtn4:
			delImg(camera4,delcamera4,txt_pic4);
			break;
		case R.id.delctcamerabtn1:
			delImg(ctcamera1,delctcamera1,txt_ctpic1);
			break;
		case R.id.delctcamerabtn2:
			delImg(ctcamera2,delctcamera2,txt_ctpic2);
			break;
		case R.id.delctcamerabtn3:
			delImg(ctcamera3,delctcamera3,txt_ctpic3);
			break;
		case R.id.delctcamerabtn4:
			delImg(ctcamera4,delctcamera4,txt_ctpic4);
			break;
		}
	}

	private void delImg(ImageView delcamera,ImageView delcameraoff, TextView deltxt_pic) {
		// TODO Auto-generated method stub
		deltxt_pic.setContentDescription("");
		delcamera.setImageResource(R.drawable.btn_camear3x);
		delcameraoff.setVisibility(View.GONE);
	}

	private void startcamera(int CODE) {
		int random = (int) (Math.random() * (999 - 100)) + 100;
		String mFilePath = Environment.getExternalStorageDirectory().getPath()
				+ "/Photo";
		File file = new File(mFilePath);
		if (!file.exists() && !file.isDirectory()) {
			file.mkdir();
		}
		String filename = String.valueOf(System.currentTimeMillis()) + random
				+ ".jpg";
		mFilePath = mFilePath + "/" + filename;
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File Photofile = new File(mFilePath);
		Uri imageUri = Uri.fromFile(Photofile);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		startActivityForResult(intent, CODE);
		CurrentImg = filename;
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
		Boolean qphoto = mPreferences.getBoolean("myqphoto", false);
		switch (requestCode) {
		case R.id.camerabtn1:
			if (resultCode == RESULT_OK) {
				receive(txt_pic1,camera1,delcamera1);
				int temp = ((FrameLayout) findViewById(R.id.wgview)).getVisibility();
				if (temp == View.VISIBLE) {
					String strtmp4 = txt_pic4.getContentDescription().toString();
					if ((strtmp4.equals(wtable)||strtmp4.equals(""))&&qphoto)
						executeclick(R.id.camerabtn4);
				} else {
					String strtmp2 = txt_pic2.getContentDescription().toString();
					if ((strtmp2.equals(dtable)||strtmp2.equals(""))&&qphoto)
						executeclick(R.id.camerabtn2);
				}
			}
			break;
		case R.id.camerabtn4:
			if (resultCode == RESULT_OK) {
				receive(txt_pic4,camera4,delcamera4);
				String strtmp2 = txt_pic2.getContentDescription().toString();
				if ((strtmp2.equals(dtable)||strtmp2.equals(""))&&qphoto)
					executeclick(R.id.camerabtn2);
			}
			break;
		case R.id.camerabtn2:
			if (resultCode == RESULT_OK) 
				receive(txt_pic2,camera2,delcamera2);
			break;
		case R.id.camerabtn3:
			if (resultCode == RESULT_OK) {
				receive(txt_pic3,camera3,delcamera3);
				Boolean mimgcheck = mPreferences.getBoolean("myimgcheck", false);
				if (mimgcheck) {
					showImg(txt_pic3.getContentDescription().toString());
				}
			}
			break;
		case R.id.ctcamerabtn1:
			if (resultCode == RESULT_OK) {
				receive(txt_ctpic1,ctcamera1,delctcamera1);
				String strtmp = txt_ctpic2.getContentDescription().toString()
						.replace(" ", "");
				if ((strtmp.equals("CTB相")||strtmp.equals(""))&&qphoto)
					executeclick(R.id.ctcamerabtn2);
			}
			break;
		case R.id.ctcamerabtn2:
			if (resultCode == RESULT_OK) {
				receive(txt_ctpic2,ctcamera2,delctcamera2);
				String strtmp = txt_ctpic3.getContentDescription().toString()
						.replace(" ", "");
				if ((strtmp.equals("CTC相")||strtmp.equals(""))&&qphoto)
					executeclick(R.id.ctcamerabtn3);
			}
			break;
		case R.id.ctcamerabtn3:
			if (resultCode == RESULT_OK) {
				receive(txt_ctpic3,ctcamera3,delctcamera3);
				String strtmp = txt_ctpic4.getContentDescription().toString()
						.replace(" ", "");
				if ((strtmp.equals("CT合相")||strtmp.equals(""))&&qphoto)
					executeclick(R.id.ctcamerabtn4);
			}
			break;
		case R.id.ctcamerabtn4:
			if (resultCode == RESULT_OK)
				receive(txt_ctpic4,ctcamera4,delctcamera4);
			break;
		case R.id.dscan_boxid:
			if (resultCode == RESULT_OK) {
				Bundle bundle = data.getExtras();
				if (bundle.getString("name").equals("QR_CODE")) {
					Toast.makeText(DisassembleActivity.this, "请对准表箱条形码进行扫描",Toast.LENGTH_SHORT).show();
					return;
				}
				boxid.setText(bundle.getString("result"));
			}
			break;
		case R.id.scan_cseal1:
			if (resultCode == RESULT_OK)
				eb_cseal1.setText(data.getExtras().getString("result"));
			break;
		case R.id.scan_cseal2:
			if (resultCode == RESULT_OK)
				eb_cseal2.setText(data.getExtras().getString("result"));
			break;
		case R.id.scan_cseal3:
			if (resultCode == RESULT_OK) 
				eb_cseal3.setText(data.getExtras().getString("result"));
			break;
		case R.id.scan_cseal4:
			if (resultCode == RESULT_OK)
				eb_cseal4.setText(data.getExtras().getString("result"));
			break;
		case R.id.scan_cseal5:
			if (resultCode == RESULT_OK)
				eb_cseal5.setText(data.getExtras().getString("result"));
			break;
		case R.id.scan_cseal6:
			if (resultCode == RESULT_OK)
				eb_cseal6.setText(data.getExtras().getString("result"));
			break;
		case R.id.dcamerabox:
			txt_boxpic.setContentDescription(CurrentImg);
			String path = Environment.getExternalStorageDirectory().getPath()
					+ "/Photo/" + txt_boxpic.getContentDescription().toString();
			//ysImg(path);
			Bitmap bitmap = Common.getImageThumbnail(path, 3);// BitmapFactory.decodeFile(path);
			camerabox.setImageBitmap(bitmap);
			camerabox.setAdjustViewBounds(true);
			break;
		case R.id.dcamerabox2:
			txt_boxpic2.setContentDescription(CurrentImg);
			String path2 = Environment.getExternalStorageDirectory().getPath()
					+ "/Photo/" + txt_boxpic2.getContentDescription().toString();
			//ysImg(path2);
			Bitmap bitmap2 = Common.getImageThumbnail(path2, 3);
			camerabox2.setImageBitmap(bitmap2);
			camerabox2.setAdjustViewBounds(true);
			break;
		case R.id.dcamerabox3:
			txt_boxpic3.setContentDescription(CurrentImg);
			String path3 = Environment.getExternalStorageDirectory().getPath()
					+ "/Photo/" + txt_boxpic3.getContentDescription().toString();
			//ysImg(path3);
			Bitmap bitmap3 = Common.getImageThumbnail(path3, 3);
			camerabox3.setImageBitmap(bitmap3);
			camerabox3.setAdjustViewBounds(true);
			break;
		case R.id.txt_cmorepics:
			if (resultCode == RESULT_OK) {
				String morepath = Environment.getExternalStorageDirectory()
						.getPath() + "/Photo/" + CurrentImg;
				// ysImg(path);
				Bitmap addbmp = Common.getImageThumbnail(morepath, 3);
				ImgContent con=new ImgContent();
				con.name=CurrentImg;
				con.icon=addbmp;
				tbcontent.add(con);
				tbAdapter=new ImgAdapter(DisassembleActivity.this,tbcontent,false);
		        tbgridView.setAdapter(tbAdapter);
				tbAdapter.notifyDataSetChanged();
			}
			break;
		case R.id.txt_cmoreboxpic:
			if (resultCode == RESULT_OK) {
				String morepath = Environment.getExternalStorageDirectory()
						.getPath() + "/Photo/" + CurrentImg;
				// ysImg(path);
				Bitmap addbmp = Common.getImageThumbnail(morepath, 3);
				ImgContent con=new ImgContent();
				con.name=CurrentImg;
				con.icon=addbmp;
				boxcontent.add(con);
				boxAdapter=new ImgAdapter(DisassembleActivity.this,boxcontent,false);
		        boxgridView.setAdapter(boxAdapter);
				boxAdapter.notifyDataSetChanged();
			}
			break;
		}
	}
}
