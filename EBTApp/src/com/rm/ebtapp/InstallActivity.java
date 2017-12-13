package com.rm.ebtapp;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.rm.db.DBHelper;
import com.rm.model.ImgContent;
import com.rm.model.PHOMessageData;
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

public class InstallActivity extends Activity implements OnClickListener,
		OnLongClickListener {
	private final static int PHO_DATA_CODE = 1000;
	private final static int LIST_DATA_CODE = 1001;
	private final static int BOX_DATA_CODE = 1002;
	private final static int TABLE_DATA_CODE = 1003;
	private final static int SAVE_DATA_CODE = 1004;

	private SharedPreferences mPreferences;
	private Context mcontext;
	private Boolean first = true, mainstart = true;
	private Boolean havesave =true;
	private TableBox currentbox = null;
	private String currentboxno = "";
	private String CurrentImg = "";
	private TableData record;
	private String Action = "";
	private String data = "";
	private TextView txt_npic1, txt_npic2, txt_npic3, txt_npic4, txt_opic1, txt_opic2,	txt_opic3, txt_opic4;
	private TextView txt_ctnpic1, txt_ctnpic2, txt_ctnpic3,	txt_ctnpic4, txt_ctopic1, txt_ctopic2, txt_ctopic3, txt_ctopic4;
	private TextView txt_boxpic,txt_boxpic2,txt_boxpic3;
	private TextView txt_username, txt_userid, txt_assetsid, txt_useraddress, txt_equipmentclass,txt_inposition,txt_datatag;
	private EditText txt_nassetsid, txt_readdata, txt_dwreaddata, txt_nreaddata,txt_wreaddata,iboxzb;
	private EditText txt_seal1, txt_seal2, txt_seal3,  txt_seal4, txt_seal5, txt_seal6,txt_seal7, txt_seal8, txt_seal9;
	private EditText eb_cseal1,	eb_cseal2, eb_cseal3,eb_cseal4,	eb_cseal5, eb_cseal6;
	private Button savedatabt;
	private ImageView ocamera1, ocamera2, ocamera3, ocamera4, camera1, camera2,	camera3, camera4;
	private ImageView ctocamera1, ctocamera2, ctocamera3, ctocamera4, ctcamera1, ctcamera2, ctcamera3, ctcamera4;
	private ImageView boxcamera,boxcamera2,boxcamera3,	scan_oldtable,scan_nassetsid,scan_box;
	private ImageView scan_seal1, scan_seal2, scan_seal3,scan_seal4, scan_seal5, scan_seal6,scan_seal7, scan_seal8, scan_seal9; 
	private ImageView scan_boxseal1, scan_boxseal2, scan_boxseal3,scan_boxseal4, scan_boxseal5, scan_boxseal6;
	private ImageView delocamera1, delocamera2, delocamera3, delocamera4, delcamera1, delcamera2, delcamera3, delcamera4;
	private ImageView delctocamera1, delctocamera2,	delctocamera3, delctocamera4, delctcamera1, delctcamera2,delctcamera3, delctcamera4;

	private LinearLayout r_seal4, r_seal5, r_seal6;

	private FrameLayout nwgview, owgview, nfztpic1, nfztpic2, ofztpic1,
			ofztpic2;
	private LinearLayout rel_nzpic, rel_ozpic, nztpic1, nztpic2, oztpic1,
			oztpic2;

	// 其他图片相关参数
	private TextView ntbmoretbpics;
	private MyGridView ntbgridView;
	private List<ImgContent> ntbcontent = new ArrayList<ImgContent>();
	private ImgAdapter ntbAdapter;
	private TextView otbmoretbpics;
	private MyGridView otbgridView;
	private List<ImgContent> otbcontent = new ArrayList<ImgContent>();
	private ImgAdapter otbAdapter;
	private TextView boxmoretbpics;
	private MyGridView boxgridView;
	private List<ImgContent> boxcontent = new ArrayList<ImgContent>();
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
                    Message message = new Message();
        			message.what = SAVE_DATA_CODE;
        			handler.sendMessage(message);
                } 
            }   
        }  
    };

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				TableData temptb = (TableData) msg.obj;
				txt_username.setText(temptb.getUserName());
				txt_userid.setText(temptb.getUserId());
				txt_assetsid.setText(temptb.getAssetsId());
				txt_useraddress.setText(temptb.getUserAddress());
				resetView(temptb.getEquipmentClass());
				break;
			case TABLE_DATA_CODE:
				TableData temptb1 = (TableData) msg.obj;
				txt_username.setText(temptb1.getUserName());
				txt_userid.setText(temptb1.getUserId());
				txt_assetsid.setText(temptb1.getAssetsId());
				txt_useraddress.setText(temptb1.getUserAddress());
				// txt_equipmentclass.setText(temptb1.getEquipmentClass());
				resetView(temptb1.getEquipmentClass());
				txt_inposition.setText(temptb1.getBIN_POSITION_NUMBER());
				txt_datatag.setText(temptb1.getDATATAG());
				txt_readdata.setText(temptb1.getReaddata());
				txt_dwreaddata.setText(temptb1.getMdReaddata());
				txt_nassetsid.setText(temptb1.getN_assetsnumber());
				txt_nreaddata.setText(temptb1.getN_readdata());
				txt_wreaddata.setText(temptb1.getN_wreaddata());
				txt_seal1.setText(temptb1.getSealId1());
				txt_seal2.setText(temptb1.getSealId2());
				txt_seal3.setText(temptb1.getSealId3());
				txt_seal4.setText(temptb1.getSealId4());
				txt_seal5.setText(temptb1.getSealId5());
				txt_seal6.setText(temptb1.getSealId6());
				txt_seal7.setText(temptb1.getSealId7());
				txt_seal8.setText(temptb1.getSealId8());
				txt_seal9.setText(temptb1.getSealId9());
				Boolean mqwork = mPreferences.getBoolean("myqwork", false);
				String nb = txt_nassetsid.getText().toString();
				if (mqwork && mainstart && nb.equals("")) {
					doClick(R.id.scan_nassetsid);
					mainstart = false;
				}
				break;
			case PHO_DATA_CODE:
				try {
					PHOMessageData phodata = (PHOMessageData) msg.obj;
					phodata.imgview.setImageBitmap(phodata.bmp);
					phodata.imgview.setAdjustViewBounds(true);
					phodata.txtview.setContentDescription(phodata.bmpname);
				} catch (Exception e) {
					// TODO: handle exception
				}
				break;
			case LIST_DATA_CODE:
				try {
					int listid = (int) msg.obj;
					if(listid==1)
					{
						ntbAdapter=new ImgAdapter(InstallActivity.this,ntbcontent,false);
				        ntbgridView.setAdapter(ntbAdapter);
						ntbAdapter.notifyDataSetChanged();
					}
					else if(listid==2)
					{
						otbAdapter=new ImgAdapter(InstallActivity.this,otbcontent,false);
				        otbgridView.setAdapter(otbAdapter);
						otbAdapter.notifyDataSetChanged();
					}
					else
					{
						boxAdapter=new ImgAdapter(InstallActivity.this,boxcontent,false);
						boxgridView.setAdapter(boxAdapter);
						boxAdapter.notifyDataSetChanged();
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
				break;
			case BOX_DATA_CODE:
				TableBox tbox = (TableBox) msg.obj;
				iboxzb.setText(tbox.getTABLE_CABINET_NO());
				if(tbox.getSEAL_NUMBER()!=null&&!tbox.getSEAL_NUMBER().equals(""))
					eb_cseal1.setText(tbox.getSEAL_NUMBER());
				if(tbox.getSEAL_NUMBER2()!=null&&!tbox.getSEAL_NUMBER2().equals(""))
					eb_cseal2.setText(tbox.getSEAL_NUMBER2());
				if(tbox.getSEAL_NUMBER3()!=null&&!tbox.getSEAL_NUMBER3().equals(""))
					eb_cseal3.setText(tbox.getSEAL_NUMBER3());
				if(tbox.getSEAL_NUMBER4()!=null&&!tbox.getSEAL_NUMBER4().equals(""))
					eb_cseal4.setText(tbox.getSEAL_NUMBER4());
				if(tbox.getSEAL_NUMBER5()!=null&&!tbox.getSEAL_NUMBER5().equals(""))
					eb_cseal5.setText(tbox.getSEAL_NUMBER5());
				if(tbox.getSEAL_NUMBER6()!=null&&!tbox.getSEAL_NUMBER6().equals(""))
					eb_cseal6.setText(tbox.getSEAL_NUMBER6());
				break;
			case SAVE_DATA_CODE:
				savedata(false);
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_install);
		mPreferences = getSharedPreferences("APPSETTING", Context.MODE_PRIVATE);
		mcontext = this;
		findViewById(R.id.img_user).setVisibility(View.GONE);
		findViewById(R.id.button_title).setVisibility(View.GONE);
		findViewById(R.id.textview_title).setVisibility(View.VISIBLE);
		((TextView) findViewById(R.id.textview_title)).setText("电表安装");
		Button mButtonBack = (Button) findViewById(R.id.button_back);
		mButtonBack.setVisibility(View.VISIBLE);
		mButtonBack.setOnClickListener(this);
		Init();
		Intent intent = InstallActivity.this.getIntent();
		Action = intent.getExtras().getString("action");
		data = intent.getExtras().getString("data");
		currentbox = (TableBox) intent.getSerializableExtra("boxdata");
		first = true;
		mainstart = true;
		setView(Action, data);
	}

	protected void resetView(String equipmentClass) {
		if (currentbox == null) {
			if (equipmentClass.equals("0") || equipmentClass.equals("单相散表")) {
				txt_equipmentclass.setText("单相散表");
				hideCTPhoto();
			} else if (equipmentClass.equals("1")
					|| equipmentClass.equals("三相散表")) {
				txt_equipmentclass.setText("三相散表");
				movecontrol();
			} else {
			}
		} else {
			if (equipmentClass.equals("1") || equipmentClass.equals("三相集中表")) {
				txt_equipmentclass.setText("三相集中表");
				hideCTPhoto();
				movecontrol();
			} else if (equipmentClass.equals("0")
					|| equipmentClass.equals("单相集中表")) {
				txt_equipmentclass.setText("单相集中表");
				hideCTPhoto();
			} else if (equipmentClass.equals("三相散表")) {
				txt_equipmentclass.setText("三相散表");
				movecontrol();
			} else if (equipmentClass.equals("单相散表")) {
				txt_equipmentclass.setText("单相散表");
				hideCTPhoto();
			} else {
			}
		}
	}

	private void movecontrol() {
		// nwgview,owgview,rel_nzpic,rel_ozpic,nztpic1,nztpic2,oztpic1,onztpic2;
		nwgview = (FrameLayout) findViewById(R.id.nwgview);
		owgview = (FrameLayout) findViewById(R.id.owgview);
		rel_nzpic = (LinearLayout) findViewById(R.id.rel_nzpic);
		rel_ozpic = (LinearLayout) findViewById(R.id.rel_ozpic);
		nztpic1 = (LinearLayout) findViewById(R.id.nztpic1);
		nztpic2 = (LinearLayout) findViewById(R.id.nztpic2);
		oztpic1 = (LinearLayout) findViewById(R.id.oztpic1);
		oztpic2 = (LinearLayout) findViewById(R.id.oztpic2);
		nfztpic1 = (FrameLayout) findViewById(R.id.nfztpic1);
		nfztpic2 = (FrameLayout) findViewById(R.id.nfztpic2);
		ofztpic1 = (FrameLayout) findViewById(R.id.ofztpic1);
		ofztpic2 = (FrameLayout) findViewById(R.id.ofztpic2);

		nztpic1.setVisibility(View.GONE);
		oztpic1.setVisibility(View.GONE);
		nfztpic1.setVisibility(View.GONE);
		ofztpic1.setVisibility(View.GONE);
		nwgview.setVisibility(View.VISIBLE);
		owgview.setVisibility(View.VISIBLE);
		nztpic1.removeView(camera3);
		nztpic2.addView(camera3);
		oztpic1.removeView(ocamera3);
		oztpic2.addView(ocamera3);
		nztpic1.removeView(txt_npic3);
		nztpic2.addView(txt_npic3);
		oztpic1.removeView(txt_opic3);
		oztpic2.addView(txt_opic3);
		nfztpic1.removeView(delcamera3);
		ofztpic1.removeView(delocamera3);
		nfztpic2.addView(delcamera3);
		ofztpic2.addView(delocamera3);
		rel_nzpic.setVisibility(View.VISIBLE);
		txt_npic1.setContentDescription("有功");
		txt_npic1.setText("有功");
		rel_ozpic.setVisibility(View.VISIBLE);
		txt_opic1.setContentDescription("有功");
		txt_opic1.setText("有功");
		
		r_seal4=(LinearLayout)findViewById(R.id.rel_seal4);
		r_seal4.setVisibility(View.VISIBLE);
		r_seal5=(LinearLayout)findViewById(R.id.rel_seal5);
		r_seal5.setVisibility(View.VISIBLE);
		r_seal6=(LinearLayout)findViewById(R.id.rel_seal6);
		r_seal6.setVisibility(View.VISIBLE);
	}

	private void hideCTPhoto() {
		findViewById(R.id.rel_nctpic).setVisibility(View.GONE);
		findViewById(R.id.rel_octpic).setVisibility(View.GONE);
		findViewById(R.id.drel_nhctpic).setVisibility(View.GONE);
		findViewById(R.id.drel_ohctpic).setVisibility(View.GONE);
	}

	@Override
	public void onResume() {
		super.onResume();

		final IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
		InstallActivity.this.registerReceiver(mHomeKeyEventReceiver, homeFilter);
	}
	
	@Override
    protected void onPause() {
		try {
			if (null != mHomeKeyEventReceiver) {
				InstallActivity.this.unregisterReceiver(mHomeKeyEventReceiver);
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
									savedata(true);
									InstallActivity.this.finish();
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
			InstallActivity.this.finish();
	}
	
	@Override
	public boolean onLongClick(View arg0) {
		doClick(arg0.getId());
		return false;
	}

	@Override
	public void onClick(View arg0) {
		int viewid = arg0.getId();
		switch (viewid) {
		case R.id.ncamera1:
			String nstrtmp1 = txt_npic1.getContentDescription().toString();
			if (!nstrtmp1.equals("单表") && !nstrtmp1.equals("有功")&& !nstrtmp1.equals("")) {
				showImage(nstrtmp1);
			} else
				doClick(viewid);
			break;
		case R.id.ncamera4:
			String nwstrtmp1 = txt_npic4.getContentDescription().toString();
			if (!nwstrtmp1.equals("无功")&&!nwstrtmp1.equals("")) {
				showImage(nwstrtmp1);
			} else
				doClick(viewid);
			break;
		case R.id.ncamera2:
			String nstrtmp2 = txt_npic2.getContentDescription().toString();
			if (!nstrtmp2.equals("双表")&&!nstrtmp2.equals("")) {
				showImage(nstrtmp2);
			} else
				doClick(viewid);
			break;
		case R.id.ncamera3:
			String nstrtmp3 = txt_npic3.getContentDescription().toString();
			if (!nstrtmp3.equals("整体")&&!nstrtmp3.equals("")) {
				showImage(nstrtmp3);
			} else
				doClick(viewid);
			break;
		case R.id.nctcamerabtn1:
			String nstrtmp30 = txt_ctnpic1.getContentDescription().toString()
					.replace(" ", "");
			if (!nstrtmp30.equals("CTA相")&&!nstrtmp30.equals("")) {
				showImage(nstrtmp30);
			} else
				doClick(viewid);
			break;
		case R.id.nctcamerabtn2:
			String nstrtmp31 = txt_ctnpic2.getContentDescription().toString()
					.replace(" ", "");
			if (!nstrtmp31.equals("CTB相")&&!nstrtmp31.equals("")) {
				showImage(nstrtmp31);
			} else
				doClick(viewid);
			break;
		case R.id.nctcamerabtn3:
			String nstrtmp32 = txt_ctnpic3.getContentDescription().toString()
					.replace(" ", "");
			if (!nstrtmp32.equals("CTC相")&&!nstrtmp32.equals("")) {
				showImage(nstrtmp32);
			} else
				doClick(viewid);
			break;
		case R.id.nctcamerabtn4:
			String nstrtmph = txt_ctnpic4.getContentDescription().toString()
					.replace(" ", "");
			if (!nstrtmph.equals("CT合相")&&!nstrtmph.equals("")) {
				showImage(nstrtmph);
			} else
				doClick(viewid);
			break;
		case R.id.ocamerabtn1:
			String ostrtmp1 = txt_opic1.getContentDescription().toString();
			if (!ostrtmp1.equals("单表") && !ostrtmp1.equals("有功") && !ostrtmp1.equals("")) {
				showImage(ostrtmp1);
			} else
				doClick(viewid);
			break;
		case R.id.ocamerabtn4:
			String owstrtmp = txt_opic4.getContentDescription().toString();
			if (!owstrtmp.equals("无功")&&!owstrtmp.equals("")) {
				showImage(owstrtmp);
			} else
				doClick(viewid);
			break;
		case R.id.ocamerabtn2:
			String ostrtmp2 = txt_opic2.getContentDescription().toString();
			if (!ostrtmp2.equals("双表")&&!ostrtmp2.equals("")) {
				showImage(ostrtmp2);
			} else
				doClick(viewid);
			break;
		case R.id.ocamerabtn3:
			String ostrtmp3 = txt_opic3.getContentDescription().toString();
			if (!ostrtmp3.equals("整体")&&!ostrtmp3.equals("")) {
				showImage(ostrtmp3);
			} else
				doClick(viewid);
			break;
		case R.id.octcamerabtn1:
			String ostrtmp30 = txt_ctopic1.getContentDescription().toString()
					.replace(" ", "");
			if (!ostrtmp30.equals("CTA相")&&!ostrtmp30.equals("")) {
				showImage(ostrtmp30);
			} else
				doClick(viewid);
			break;
		case R.id.octcamerabtn2:
			String ostrtmp31 = txt_ctopic2.getContentDescription().toString()
					.replace(" ", "");
			if (!ostrtmp31.equals("CTB相")&&!ostrtmp31.equals("")) {
				showImage(ostrtmp31);
			} else
				doClick(viewid);
			break;
		case R.id.octcamerabtn3:
			String ostrtmp32 = txt_ctopic3.getContentDescription().toString()
					.replace(" ", "");
			if (!ostrtmp32.equals("CTC相")&&!ostrtmp32.equals("")) {
				showImage(ostrtmp32);
			} else
				doClick(viewid);
			break;
		case R.id.octcamerabtn4:
			String ostrtmph = txt_ctopic4.getContentDescription().toString()
					.replace(" ", "");
			if (!ostrtmph.equals("CT合相")&&!ostrtmph.equals("")) {
				showImage(ostrtmph);
			} else
				doClick(viewid);
			break;
		case R.id.icamerabox:
			String ostrtmp4 = txt_boxpic.getContentDescription().toString();
			if (!ostrtmp4.equals("表箱")&&!ostrtmp4.equals("")) {
				showImage(ostrtmp4);
			} else
				doClick(viewid);
			break;
		case R.id.icamerabox2:
			String ostrtmp42 = txt_boxpic2.getContentDescription().toString();
			if (!ostrtmp42.equals("表箱")&&!ostrtmp42.equals("")) {
				showImage(ostrtmp42);
			} else
				doClick(viewid);
			break;
		case R.id.icamerabox3:
			String ostrtmp43 = txt_boxpic3.getContentDescription().toString();
			if (!ostrtmp43.equals("表箱")&&!ostrtmp43.equals("")) {
				showImage(ostrtmp43);
			} else
				doClick(viewid);
			break;
		default:
			doClick(viewid);
			break;
		}
	}

	private void showImage(String filename) {
		File file = new File(Environment.getExternalStorageDirectory()
				.getPath() + "/Photo/" + filename);
		Intent intent = new Intent();
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), "image/*");
		startActivity(intent);
	}

	private void doClick(int viewid) {
		switch (viewid) {
		case R.id.isavebtn:
			savedata(true);
			break;
		case R.id.button_back:
			onBackPressed();
			break;
		case R.id.scan_nassetsid:
			startScan(R.id.scan_nassetsid,0);
			break;
		case R.id.scan_oldtable:
			startScan(R.id.scan_oldtable,0);
			break;
		case R.id.iscan_boxid:
			startScan(R.id.iscan_boxid,0);
			break;
		case R.id.scan_seal1:
			startScan(R.id.scan_seal1,-1);
			break;
		case R.id.scan_seal2:
			startScan(R.id.scan_seal2,-1);
			break;
		case R.id.scan_seal3:
			startScan(R.id.scan_seal3,-1);
			break;
		case R.id.scan_seal4:
			startScan(R.id.scan_seal4,-1);
			break;
		case R.id.scan_seal5:
			startScan(R.id.scan_seal5,-1);
			break;
		case R.id.scan_seal6:
			startScan(R.id.scan_seal6,-1);
			break;
		case R.id.scan_seal7:
			startScan(R.id.scan_seal7,-1);
			break;
		case R.id.scan_seal8:
			startScan(R.id.scan_seal8,-1);
			break;
		case R.id.scan_seal9:
			startScan(R.id.scan_seal9,-1);
			break;
		case R.id.ncamera1:
			startCamera(R.id.ncamera1);
			break;
		case R.id.ncamera2:
			startCamera(R.id.ncamera2);
			break;
		case R.id.ncamera3:
			startCamera(R.id.ncamera3);
			break;
		case R.id.ncamera4:
			startCamera(R.id.ncamera4);
			break;
		case R.id.nctcamerabtn1:
			startCamera(R.id.nctcamerabtn1);
			break;
		case R.id.nctcamerabtn2:
			startCamera(R.id.nctcamerabtn2);
			break;
		case R.id.nctcamerabtn3:
			startCamera(R.id.nctcamerabtn3);
			break;
		case R.id.nctcamerabtn4:
			startCamera(R.id.nctcamerabtn4);
			break;
		case R.id.txt_ntbmorepics:
			startCamera(R.id.txt_ntbmorepics);
			break;
		case R.id.ocamerabtn1:
			startCamera(R.id.ocamerabtn1);
			break;
		case R.id.ocamerabtn2:
			startCamera(R.id.ocamerabtn2);
			break;
		case R.id.ocamerabtn3:
			startCamera(R.id.ocamerabtn3);
			break;
		case R.id.ocamerabtn4:
			startCamera(R.id.ocamerabtn4);
			break;
		case R.id.octcamerabtn1:
			startCamera(R.id.octcamerabtn1);
			break;
		case R.id.octcamerabtn2:
			startCamera(R.id.octcamerabtn2);
			break;
		case R.id.octcamerabtn3:
			startCamera(R.id.octcamerabtn3);
			break;
		case R.id.octcamerabtn4:
			startCamera(R.id.octcamerabtn4);
			break;
		case R.id.txt_otbmorepics:
			startCamera(R.id.txt_otbmorepics);
			break;
		case R.id.icamerabox:
			startCamera(R.id.icamerabox);
			break;
		case R.id.icamerabox2:
			startCamera(R.id.icamerabox2);
			break;
		case R.id.icamerabox3:
			startCamera(R.id.icamerabox3);
			break;
		case R.id.txt_amoreboxpic:
			startCamera(R.id.txt_amoreboxpic);
			break;
		case R.id.scan_aseal1:
			startScan(R.id.scan_aseal1,-1);
			break;
		case R.id.scan_aseal2:
			startScan(R.id.scan_aseal2,-1);
			break;
		case R.id.scan_aseal3:
			startScan(R.id.scan_aseal3,-1);
			break;
		case R.id.scan_aseal4:
			startScan(R.id.scan_aseal4,-1);
			break;
		case R.id.scan_aseal5:
			startScan(R.id.scan_aseal5,-1);
			break;
		case R.id.scan_aseal6:
			startScan(R.id.scan_aseal6,-1);
			break;
		case R.id.delocamerabtn1:
			delImg(ocamera1,delocamera1,txt_opic1);
			break;
		case R.id.delocamerabtn2:
			delImg(ocamera2,delocamera2,txt_opic2);
			break;
		case R.id.delocamerabtn3:
			delImg(ocamera3,delocamera3,txt_opic3);
			break;
		case R.id.delocamerabtn4:
			delImg(ocamera4,delocamera4,txt_opic4);
			break;
		case R.id.delncamera1:
			delImg(camera1,delcamera1,txt_npic1);
			break;
		case R.id.delncamera2:
			delImg(camera2,delcamera2,txt_npic2);
			break;
		case R.id.delncamera3:
			delImg(camera3,delcamera3,txt_npic3);
			break;
		case R.id.delncamera4:
			delImg(camera4,delcamera4,txt_npic4);
			break;
		case R.id.deloctcamerabtn1:
			delImg(ctocamera1,delctocamera1,txt_ctopic1);
			break;
		case R.id.deloctcamerabtn2:
			delImg(ctocamera2,delctocamera2,txt_ctopic2);
			break;
		case R.id.deloctcamerabtn3:
			delImg(ctocamera3,delctocamera3,txt_ctopic3);
			break;
		case R.id.deloctcamerabtn4:
			delImg(ctocamera4,delctocamera4,txt_ctopic4);
			break;
		case R.id.delnctcamerabtn1:
			delImg(ctcamera1,delctcamera1,txt_ctnpic1);
			break;
		case R.id.delnctcamerabtn2:
			delImg(ctcamera2,delctcamera2,txt_ctnpic2);
			break;
		case R.id.delnctcamerabtn3:
			delImg(ctcamera3,delctcamera3,txt_ctnpic3);
			break;
		case R.id.delnctcamerabtn4:
			delImg(ctcamera4,delctcamera4,txt_ctnpic4);
			break;
		default:
			break;
		}
	}

	private void delImg(ImageView delcamera,ImageView delcameraoff, TextView deltxt_pic) {
		deltxt_pic.setContentDescription("");
		delcamera.setImageResource(R.drawable.btn_camear3x);
		delcameraoff.setVisibility(View.GONE);
	}

	private void startScan(int CODE,int zoomTo) {
		Intent intent = new Intent();
		intent.setClass(InstallActivity.this, QRScanActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("zoomto", zoomTo);
		startActivityForResult(intent, CODE);
	}

	private void startCamera(int CODE) {
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
	

	private TableBox getBoxData(String NO) {
		TableBox resultdata = null;
		DBHelper helper = new DBHelper(this);
		try {
			Cursor cursor = helper
					.query("TabBoxData",
							new String[] { "_id,TABLE_CABINET_NO,HLY_CREATE_DATE,PICTURES,PICTURES2,PICTURES3,MORE_PICTURES,DATACOUNT,SEAL_NUMBER,SEAL_NUMBER2,SEAL_NUMBER3,SEAL_NUMBER4,SEAL_NUMBER5,SEAL_NUMBER6" },
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
					resultdata.setSEAL_NUMBER(cursor.getString(cursor
							.getColumnIndex("SEAL_NUMBER")));
					resultdata.setSEAL_NUMBER2(cursor.getString(cursor
							.getColumnIndex("SEAL_NUMBER2")));
					resultdata.setSEAL_NUMBER3(cursor.getString(cursor
							.getColumnIndex("SEAL_NUMBER3")));
					resultdata.setSEAL_NUMBER4(cursor.getString(cursor
							.getColumnIndex("SEAL_NUMBER4")));
					resultdata.setSEAL_NUMBER5(cursor.getString(cursor
							.getColumnIndex("SEAL_NUMBER5")));
					resultdata.setSEAL_NUMBER6(cursor.getString(cursor
							.getColumnIndex("SEAL_NUMBER6")));
				}
				cursor.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultdata;
	}

	@SuppressLint("SimpleDateFormat")
	private void savedata(Boolean stopedit) {
		Boolean finish=true;
		if (iboxzb.getText().toString() != null
				&& !iboxzb.getText().toString().equals("")) {
			currentboxno = iboxzb.getText().toString();
			currentbox = getBoxData(currentboxno);
		}
		String nassetsid = txt_nassetsid.getText().toString();
		if (nassetsid.equals("")) {
			finish=false;
			Toast.makeText(this, "新资产编号为空", Toast.LENGTH_SHORT).show();
		}
		String npicname1 = txt_npic1.getContentDescription().toString();
		String npicname2 = txt_npic2.getContentDescription().toString();
		String npicname3 = txt_npic3.getContentDescription().toString();
		String npicname4 = txt_npic4.getContentDescription().toString();
		int count = 0;
		Boolean hasnd = (npicname1.equals("") || npicname1.equals("单表") || npicname1.equals("有功"));
		if (npicname1.equals("单表") || npicname1.equals("有功"))
			npicname1 = "";
		Boolean hasns = (npicname2.equals("") || npicname2.equals("双表"));
		if (npicname2.equals("双表"))
			npicname2 = "";
		Boolean hasnq = (npicname3.equals("") || npicname3.equals("整体"));
		if (npicname3.equals("整体"))
			npicname3 = "";
		Boolean hasnw = (npicname4.equals("") || npicname4.equals("无功"));
		if (npicname4.equals("无功"))
			npicname4 = "";
		if (!hasnd)
			count = count + 1;
		if (!hasns)
			count = count + 1;
		if (!hasnq)
			count = count + 1;
		if (!hasnw)
			count = count + 1;
		if (count < 2) {
			Toast.makeText(this, "请至少拍摄两张新表照片", Toast.LENGTH_SHORT).show();
		}

		Boolean mdatacheck = mPreferences.getBoolean("mydatacheck", false);
		String nreaddata = txt_nreaddata.getText().toString();
		if (nreaddata.equals(""))
			nreaddata = "0";
		String wreaddata = txt_wreaddata.getText().toString();
		if (wreaddata.equals(""))
			wreaddata = "0";
		String sealnumber1 = txt_seal1.getText().toString();
		String sealnumber2 = txt_seal2.getText().toString();
		String sealnumber3 = txt_seal3.getText().toString();
		String sealnumber4 = txt_seal4.getText().toString();
		String sealnumber5 = txt_seal5.getText().toString();
		String sealnumber6 = txt_seal6.getText().toString();
		String sealnumber7 = txt_seal7.getText().toString();
		String sealnumber8 = txt_seal8.getText().toString();
		String sealnumber9 = txt_seal9.getText().toString();
		String readdata = txt_readdata.getText().toString();
		String dwreaddata = txt_dwreaddata.getText().toString();
		if (mdatacheck) {
			if (nreaddata.equals("0")) {
				Toast.makeText(this, "新表有功读数为空", Toast.LENGTH_SHORT).show();
			}
			if (wreaddata.equals("0")) {
				Toast.makeText(this, "新表无功读数为空", Toast.LENGTH_SHORT).show();
			}
			/*if (sealnumber1.equals("")) {
				Toast.makeText(this, "封印号1为空", Toast.LENGTH_SHORT).show();
			}
			if (sealnumber2.equals("")) {
				Toast.makeText(this, "封印号2为空", Toast.LENGTH_SHORT).show();
			}
			if (sealnumber3.equals("")) {
				Toast.makeText(this, "封印号3为空", Toast.LENGTH_SHORT).show();
			}
			if (sealnumber4.equals("")) {
				Toast.makeText(this, "封印号4为空", Toast.LENGTH_SHORT).show();
			}
			if (sealnumber5.equals("")) {
				Toast.makeText(this, "封印号5为空", Toast.LENGTH_SHORT).show();
			}
			if (sealnumber6.equals("")) {
				Toast.makeText(this, "封印号6为空", Toast.LENGTH_SHORT).show();
			}
			if (sealnumber7.equals("")) {
				Toast.makeText(this, "封印号7为空", Toast.LENGTH_SHORT).show();
			}
			if (sealnumber8.equals("")) {
				Toast.makeText(this, "封印号8为空", Toast.LENGTH_SHORT).show();
			}
			if (sealnumber9.equals("")) {
				Toast.makeText(this, "封印号9为空", Toast.LENGTH_SHORT).show();
			}*/
			readdata = txt_readdata.getText().toString();
			if (readdata.equals("")) {
				Toast.makeText(this, "旧表读数为空", Toast.LENGTH_SHORT).show();
			}
			dwreaddata = txt_dwreaddata.getText().toString();
			if (dwreaddata.equals("")) {
				Toast.makeText(this, "无功读数为空", Toast.LENGTH_SHORT).show();
			}
		}

		String username = txt_username.getText().toString();
		if (username.equals("")) {
			Toast.makeText(this, "旧表用户名为空", Toast.LENGTH_SHORT).show();
		}
		String userid = txt_userid.getText().toString();
		if (userid.equals("")) {
			Toast.makeText(this, "旧表用户号为空", Toast.LENGTH_SHORT).show();
		}
		String assetsid = txt_assetsid.getText().toString();
		if (assetsid.equals("")) {
			Toast.makeText(this, "旧表资产编号为空", Toast.LENGTH_SHORT).show();
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

		String picname1 = txt_opic1.getContentDescription().toString();
		if (picname1.equals("单表") || picname1.equals("有功")) {
			picname1 = "";
		}
		String picname2 = txt_opic2.getContentDescription().toString();
		if (picname2.equals("双表")) {
			picname2 = "";
		}
		String picname3 = txt_opic3.getContentDescription().toString();
		if (picname3.equals("整体")) {
			picname3 = "";
		}
		String picname4 = txt_opic4.getContentDescription().toString();
		if (picname4.equals("无功")) {
			picname4 = "";
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
		cv.put("wreaddata", dwreaddata);
		cv.put("ameterspic", picname1);
		cv.put("tmeterspic", picname2);
		cv.put("allpic", picname3);
		cv.put("OLD_REACTIVE_PICTURES", picname4);
		if(otbcontent.size()>0)
		{
			String otherpics="";
			for(int i=0;i<otbcontent.size();i++)
			{
				otherpics=otherpics+otbcontent.get(i).name;
				if(i<otbcontent.size()-1)
					otherpics=otherpics+",";
			}
			cv.put("OLD_MORE_PICTURES", otherpics);
		}

		cv.put("n_assetsnumber", nassetsid);
		cv.put("n_ameterspic", npicname1);
		cv.put("n_tmeterspic", npicname2);
		cv.put("n_allpic", npicname3);
		cv.put("NEW_REACTIVE_PICTURES", npicname4);
		if(ntbcontent.size()>0)
		{
			String otherpics="";
			for(int i=0;i<ntbcontent.size();i++)
			{
				otherpics=otherpics+ntbcontent.get(i).name;
				if(i<ntbcontent.size()-1)
					otherpics=otherpics+",";
			}
			cv.put("NEW_MORE_PICTURES", otherpics);
		}
		cv.put("n_readdata", nreaddata);
		cv.put("n_wreaddata", wreaddata);
		cv.put("seal1", sealnumber1);
		cv.put("seal2", sealnumber2);
		cv.put("seal3", sealnumber3);
		cv.put("seal4", sealnumber4);
		cv.put("seal5", sealnumber5);
		cv.put("seal6", sealnumber6);
		cv.put("seal7", sealnumber7);
		cv.put("seal8", sealnumber8);
		cv.put("seal9", sealnumber9);
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateNowStr = sdf.format(d);
		cv.put("itime", dateNowStr);

		if (picname1.equals(""))
			cv.put("dstatic", -1);
		else
			cv.put("dstatic", 1);
		if(finish)
			cv.put("istatic", 1);
		cv.put("static", 0);

		String OCT_SINGLE_PICTURES = txt_ctopic1.getContentDescription().toString().replace(" ", "");
		if (OCT_SINGLE_PICTURES == null || OCT_SINGLE_PICTURES.equals("CTA相"))
			OCT_SINGLE_PICTURES="";
		String OCT_DOUBLE_PICTURES = txt_ctopic2.getContentDescription().toString().replace(" ", "");
		if (OCT_DOUBLE_PICTURES == null || OCT_DOUBLE_PICTURES.equals("CTB相"))
			OCT_DOUBLE_PICTURES="";
		String OCT_WHOLE_PICTURES = txt_ctopic3.getContentDescription().toString().replace(" ", "");
		if (OCT_WHOLE_PICTURES == null || OCT_WHOLE_PICTURES.equals("CTC相"))
			OCT_WHOLE_PICTURES="";
		String ROUND_CT = txt_ctopic4.getContentDescription().toString().replace(" ", "");
		if (ROUND_CT == null || ROUND_CT.equals("CT合相"))
			ROUND_CT="";

		String NCT_SINGLE_PICTURES = txt_ctnpic1.getContentDescription().toString().replace(" ", "");
		if (NCT_SINGLE_PICTURES == null || NCT_SINGLE_PICTURES.equals("CTA相"))
			NCT_SINGLE_PICTURES="";
		String NCT_DOUBLE_PICTURES = txt_ctnpic2.getContentDescription().toString().replace(" ", "");
		if (NCT_DOUBLE_PICTURES == null || NCT_DOUBLE_PICTURES.equals("CTB相"))
			NCT_DOUBLE_PICTURES="";
		String NCT_WHOLE_PICTURES = txt_ctnpic3.getContentDescription().toString().replace(" ", "");
		if (NCT_WHOLE_PICTURES == null || NCT_WHOLE_PICTURES.equals("CTC相"))
			NCT_WHOLE_PICTURES="";
		String NEW_ROUND_CT = txt_ctnpic4.getContentDescription().toString().replace(" ", "");
		if (NEW_ROUND_CT == null || NEW_ROUND_CT.equals("CT合相"))
			NEW_ROUND_CT="";

		String tableno = iboxzb.getText().toString();
		if (tableno != null && !tableno.equals(""))
			cv.put("TABLE_CABINET_NO", tableno);
		
		cv.put("OCT_SINGLE_PICTURES", OCT_SINGLE_PICTURES);
		cv.put("OCT_DOUBLE_PICTURES", OCT_DOUBLE_PICTURES);
		cv.put("OCT_WHOLE_PICTURES", OCT_WHOLE_PICTURES);
		cv.put("ROUND_CT", ROUND_CT);
		cv.put("NCT_SINGLE_PICTURES", NCT_SINGLE_PICTURES);
		cv.put("NCT_DOUBLE_PICTURES", NCT_DOUBLE_PICTURES);
		cv.put("NCT_WHOLE_PICTURES", NCT_WHOLE_PICTURES);
		cv.put("NEW_ROUND_CT", NEW_ROUND_CT);
		
		DBHelper helper = new DBHelper(this);

		if (currentbox != null) {
			ContentValues upd = new ContentValues();
			String pic = txt_boxpic.getContentDescription().toString();
			if (pic != null && !pic.equals("") && !pic.equals("表箱"))
				upd.put("PICTURES", txt_boxpic.getContentDescription().toString());
			String pic2 = txt_boxpic2.getContentDescription().toString();
			if (pic2 != null && !pic2.equals("") && !pic2.equals("表箱"))
				upd.put("PICTURES2", txt_boxpic2.getContentDescription().toString());
			String pic3 = txt_boxpic3.getContentDescription().toString();
			if (pic3 != null && !pic3.equals("") && !pic3.equals("表箱"))
				upd.put("PICTURES3", txt_boxpic3.getContentDescription().toString());
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
			String edboxid = iboxzb.getText().toString();
			if (edboxid != null && !edboxid.equals("")) {
				ContentValues upd = new ContentValues();
				upd.put("TABLE_CABINET_NO", edboxid);
				String pic = txt_boxpic.getContentDescription().toString();
				if (pic != null && !pic.equals("") && !pic.equals("表箱"))
					upd.put("PICTURES", txt_boxpic.getContentDescription().toString());
				String pic2 = txt_boxpic2.getContentDescription().toString();
				if (pic2 != null && !pic2.equals("") && !pic2.equals("表箱"))
					upd.put("PICTURES2", txt_boxpic2.getContentDescription().toString());
				String pic3 = txt_boxpic3.getContentDescription().toString();
				if (pic3 != null && !pic3.equals("") && !pic3.equals("表箱"))
					upd.put("PICTURES3", txt_boxpic3.getContentDescription().toString());
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
				if(finish)
					upd.put("DATACOUNT", "1" + "/" + "1");
				else
					upd.put("DATACOUNT", "0" + "/" + "1");
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
			Cursor cursor = helper
					.query("RecordData",
							new String[] { "_id,username,usernumber,assetsnumber,istatic" },
							"username = ? and usernumber = ? and assetsnumber=?",
							new String[] { username, userid, assetsid }, null,
							null, null);
			if (cursor != null) {
				if (cursor.moveToFirst() == false) {
					helper.insert(cv, "RecordData");
					if (currentbox != null) {
						if (currentbox.getTABLE_CABINET_NO() != null
								&& !currentbox.getTABLE_CABINET_NO().equals("")) {
							if (currentbox.getDATACOUNT() == null
									&& currentbox.getDATACOUNT().equals("")
									|| currentbox.getDATACOUNT().equals("0")) {
								ContentValues upd = new ContentValues();
								if(finish)
									upd.put("DATACOUNT", "1" + "/" + "1");
								else
									upd.put("DATACOUNT", "0" + "/" + "1");
								//upd.put("DATACOUNT", "1" + "/" + "1");
								helper.update("TabBoxData", upd,"TABLE_CABINET_NO = ?",	new String[] { currentbox.getTABLE_CABINET_NO() });
							} else {
								if (currentbox.getDATACOUNT().contains("/")) {
									String[] arr = currentbox.getDATACOUNT().split("/");
									try {
										int first = Integer.parseInt(arr[0]) + 1;
										if (picname1.equals("")) 
											first = first - 1;
										int sce = Integer.parseInt(arr[1]) + 1;
										ContentValues upd = new ContentValues();
										if(finish)
											upd.put("DATACOUNT", first + "/" + sce);
										else
											upd.put("DATACOUNT", (first-1) + "/" + sce);
										helper.update("TabBoxData",	upd,"TABLE_CABINET_NO = ?",	new String[] { currentbox.getTABLE_CABINET_NO() });
									} catch (Exception e) {
										// TODO: handle exception
									}
								}
							}

						}
					} else {
						String edboxid = iboxzb.getText().toString();
						if (edboxid != null && !edboxid.equals("")) {
							ContentValues upd = new ContentValues();
							upd.put("TABLE_CABINET_NO", edboxid);
							if(finish)
								upd.put("DATACOUNT", "1" + "/" + "1");
							else
								upd.put("DATACOUNT", "0" + "/" + "1");
							//upd.put("DATACOUNT", "1" + "/" + "1");
							helper.update("TabBoxData", upd,"TABLE_CABINET_NO = ?",	new String[] { edboxid });
						}
					}
					Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
				} else {
					String savestatic = cursor.getString(cursor.getColumnIndex("istatic"));
					helper.update("RecordData",	cv,"username = ? and usernumber = ? and assetsnumber=?",new String[] { username, userid, assetsid });
					if (currentbox != null) {
						if (currentbox.getTABLE_CABINET_NO() != null
								&& !currentbox.getTABLE_CABINET_NO().equals("")) {
							if (currentbox.getDATACOUNT() != null
									&& !currentbox.getDATACOUNT().equals("")) {
								if (currentbox.getDATACOUNT().equals("0")||currentbox.getDATACOUNT().equals("0/0")) {
									ContentValues upd = new ContentValues();
									if(finish)
										upd.put("DATACOUNT", "1" + "/" + "1");
									else
										upd.put("DATACOUNT", "0" + "/" + "1");
									//upd.put("DATACOUNT", "1" + "/" + "1");
									helper.update("TabBoxData", upd,"TABLE_CABINET_NO = ?",	new String[] { currentbox.getTABLE_CABINET_NO() });
									currentbox.setDATACOUNT("1" + "/" + "1");
								} else {
									if (currentbox.getDATACOUNT().contains("/")) {
										String[] arr = currentbox.getDATACOUNT().split("/");
										try {
											int first = Integer.parseInt(arr[0]) + 1;
											int sce = Integer.parseInt(arr[1]);
											if (savestatic.equals("1"))
												first = first - 1;
											ContentValues upd = new ContentValues();
											if(finish)
												upd.put("DATACOUNT", first + "/" + sce);
											else
												upd.put("DATACOUNT", (first-1) + "/" + sce);
											//upd.put("DATACOUNT", first + "/" + sce);
											if (first == sce)
												upd.put("STATIC", 1);
											else
												upd.put("STATIC", 0);
											helper.update("TabBoxData",upd,	"TABLE_CABINET_NO = ?",	new String[] { currentbox.getTABLE_CABINET_NO() });
											if(finish)
												currentbox.setDATACOUNT(first + "/"	+ sce);
											else
												currentbox.setDATACOUNT((first-1) + "/"	+ sce);
										} catch (Exception e) {
											// TODO: handle exception
										}
									}
								}
							}

						}
					} else {
						String edboxid = iboxzb.getText().toString();
						if (edboxid != null && !edboxid.equals("")) {
							ContentValues upd = new ContentValues();
							upd.put("TABLE_CABINET_NO", edboxid);
							if(finish)
								upd.put("DATACOUNT", "1" + "/" + "1");
							else
								upd.put("DATACOUNT", "0" + "/" + "1");
							//upd.put("DATACOUNT", "1" + "/" + "1");
							helper.update("TabBoxData", upd,"TABLE_CABINET_NO = ?",	new String[] { edboxid });
						}
					}
					Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
				}
				cursor.close();
				if(stopedit)
					InstallActivity.this.finish();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		havesave=false;
		try {
			Boolean qphoto = mPreferences.getBoolean("myqphoto", false);
			switch (requestCode) {
			case R.id.scan_nassetsid:
				if (resultCode == RESULT_OK) {
					Bundle bundle = data.getExtras();
					String result = bundle.getString("result");
					String qrclass = bundle.getString("name");
					if (qrclass.equals("QR_CODE")) {
						JSONObject info = null;
						try {
							info = new JSONObject(result);
						} catch (JSONException e) {
							Toast.makeText(InstallActivity.this,
									"这是什么？请对准系统标准二维码扫描！", Toast.LENGTH_SHORT);
						}
						if (info != null) {
							try {
								String nassct = info.getString("d");
								txt_nassetsid.setText(nassct);
							} catch (JSONException e) {
								Toast.makeText(InstallActivity.this,
										"这是什么？请对准系统标准二维码扫描！",
										Toast.LENGTH_SHORT);
							}
						}
					} 
					else
					{
						txt_nassetsid.setText(result);
					}
					//Toast.makeText(InstallActivity.this,"这是什么？请对准系统标准二维码扫描！", Toast.LENGTH_SHORT);
					Boolean mqwork = mPreferences.getBoolean("myqwork", false);
					if (mqwork) {
						String tempstr = txt_npic1.getContentDescription()
								.toString();
						if ((tempstr.equals("单表") || tempstr.equals("有功")|| tempstr.equals(""))
								&& first) {
							doClick(R.id.ncamera1);
							first = false;
						}
					}
				}
				break;
			case R.id.scan_oldtable:
				if (resultCode == RESULT_OK) {
					Bundle bundle = data.getExtras();
					String result = bundle.getString("result");
					setView("C", result);
				}
				break;
			case R.id.iscan_boxid:
				if (resultCode == RESULT_OK) {
					Bundle bundle = data.getExtras();
					String result = bundle.getString("result");
					String qrclass = bundle.getString("name");
					if (qrclass.equals("QR_CODE")) {
						Toast.makeText(InstallActivity.this, "请对准表箱条形码进行扫描",
								Toast.LENGTH_SHORT).show();
						return;
					}
					iboxzb.setText(result);
				}
				break;
			case R.id.scan_seal1:
				if (resultCode == RESULT_OK) 
					txt_seal1.setText(data.getExtras().getString("result"));
				break;
			case R.id.scan_seal2:
				if (resultCode == RESULT_OK) 
					txt_seal2.setText(data.getExtras().getString("result"));
				break;
			case R.id.scan_seal3:
				if (resultCode == RESULT_OK) 
					txt_seal3.setText(data.getExtras().getString("result"));
				break;
			case R.id.scan_seal4:
				if (resultCode == RESULT_OK) 
					txt_seal4.setText(data.getExtras().getString("result"));
				break;
			case R.id.scan_seal5:
				if (resultCode == RESULT_OK) 
					txt_seal5.setText(data.getExtras().getString("result"));
				break;
			case R.id.scan_seal6:
				if (resultCode == RESULT_OK) 
					txt_seal6.setText(data.getExtras().getString("result"));
				break;
			case R.id.scan_seal7:
				if (resultCode == RESULT_OK) 
					txt_seal7.setText(data.getExtras().getString("result"));
				break;
			case R.id.scan_seal8:
				if (resultCode == RESULT_OK) 
					txt_seal8.setText(data.getExtras().getString("result"));
				break;
			case R.id.scan_seal9:
				if (resultCode == RESULT_OK)
					txt_seal9.setText(data.getExtras().getString("result"));
				break;
			case R.id.ncamera1:
				if (resultCode == RESULT_OK) {
					receive(txt_npic1,camera1,delcamera1);
					int temp = ((FrameLayout) findViewById(R.id.nwgview)).getVisibility();
					if (temp == View.VISIBLE) {
						String strtmp4 = txt_npic4.getContentDescription().toString();
						if ((strtmp4.equals("无功")||strtmp4.equals(""))&&qphoto)
							doClick(R.id.ncamera4);
					} else {
						String strtmp2 = txt_npic2.getContentDescription().toString();
						if ((strtmp2.equals("双表")||strtmp2.equals(""))&&qphoto)
							doClick(R.id.ncamera2);
					}
				}
				break;
			case R.id.ncamera4:
				if (resultCode == RESULT_OK) {
					receive(txt_npic4,camera4,delcamera4);
					String strtmp2 = txt_npic2.getContentDescription()
							.toString();
					if ((strtmp2.equals("双表")||strtmp2.equals(""))&&qphoto)
						doClick(R.id.ncamera2);
				}
				break;
			case R.id.ncamera2:
				if (resultCode == RESULT_OK) {
					receive(txt_npic2,camera2,delcamera2);
					String strtmp3 = txt_npic3.getContentDescription().toString();
					if ((strtmp3.equals("整体")||strtmp3.equals(""))&&qphoto)
						doClick(R.id.ncamera3);
				}
				break;
			case R.id.ncamera3:
				if (resultCode == RESULT_OK) {
					receive(txt_npic3,camera3,delcamera3);
					Boolean mimgcheck = mPreferences.getBoolean("myimgcheck",false);
					if (mimgcheck)
						showImage(txt_npic3.getContentDescription().toString());
				}
				break;
			case R.id.nctcamerabtn1:
				if (resultCode == RESULT_OK) {
					receive(txt_ctnpic1,ctcamera1,delctcamera1);
					String strtmp = txt_ctnpic2.getContentDescription().toString().replace(" ", "");
					if ((strtmp.equals("CTB相")||strtmp.equals(""))&&qphoto)
						doClick(R.id.nctcamerabtn2);
				}
				break;
			case R.id.nctcamerabtn2:
				if (resultCode == RESULT_OK) {
					receive(txt_ctnpic2,ctcamera2,delctcamera2);
					String strtmp = txt_ctnpic3.getContentDescription().toString().replace(" ", "");
					if ((strtmp.equals("CTC相")||strtmp.equals(""))&&qphoto)
						doClick(R.id.nctcamerabtn3);
				}
				break;
			case R.id.nctcamerabtn3:
				if (resultCode == RESULT_OK) {
					receive(txt_ctnpic3,ctcamera3,delctcamera3);
					String strtmp = txt_ctnpic4.getContentDescription().toString().replace(" ", "");
					if ((strtmp.equals("CT合相")||strtmp.equals(""))&&qphoto)
						doClick(R.id.nctcamerabtn4);
				}
				break;
			case R.id.nctcamerabtn4:
				if (resultCode == RESULT_OK)
					receive(txt_ctnpic4,ctcamera4,delctcamera4);
				break;
			case R.id.ocamerabtn1:
				if (resultCode == RESULT_OK) {
					receive(txt_opic1,ocamera1,delocamera1);
					int temp = ((FrameLayout) findViewById(R.id.nwgview)).getVisibility();
					if (temp == View.VISIBLE) {
						String strtmp4 = txt_opic4.getContentDescription().toString();
						if ((strtmp4.equals("无功")||strtmp4.equals(""))&&qphoto)
							doClick(R.id.ocamerabtn4);
					} else {
						String nstrtmp2 = txt_opic2.getContentDescription()
								.toString();
						if ((nstrtmp2.equals("双表")||nstrtmp2.equals("||"))&&qphoto)
							doClick(R.id.ocamerabtn2);
					}
				}
				break;
			case R.id.ocamerabtn4:
				if (resultCode == RESULT_OK) {
					receive(txt_opic4,ocamera4,delocamera4);
					String nstrtmp2 = txt_opic2.getContentDescription().toString();
					if ((nstrtmp2.equals("双表")||nstrtmp2.equals(""))&&qphoto)
						doClick(R.id.ocamerabtn2);
				}
				break;
			case R.id.ocamerabtn2:
				if (resultCode == RESULT_OK) {
					receive(txt_opic2,ocamera2,delocamera2);
					String nstrtmp3 = txt_opic3.getContentDescription().toString();
					if ((nstrtmp3.equals("整体")||nstrtmp3.equals(""))&&qphoto)
						doClick(R.id.ocamerabtn3);
				}
				break;
			case R.id.ocamerabtn3:
				if (resultCode == RESULT_OK) {
					receive(txt_opic3,ocamera3,delocamera3);
					Boolean mimgcheck = mPreferences.getBoolean("myimgcheck",false);
					if (mimgcheck)
						showImage(txt_opic3.getContentDescription().toString());
				}
				break;
			case R.id.octcamerabtn1:
				if (resultCode == RESULT_OK) {
					receive(txt_ctopic1,ctocamera1,delctocamera1);
					String strtmp = txt_ctopic2.getContentDescription().toString().replace(" ", "");
					if ((strtmp.equals("CTB相")||strtmp.equals(""))&&qphoto)
						doClick(R.id.octcamerabtn2);
				}
				break;
			case R.id.octcamerabtn2:
				if (resultCode == RESULT_OK) {
					receive(txt_ctopic2,ctocamera2,delctocamera2);
					String strtmp = txt_ctopic3.getContentDescription().toString().replace(" ", "");
					if ((strtmp.equals("CTC相")||strtmp.equals(""))&&qphoto)
						doClick(R.id.octcamerabtn3);
				}
				break;
			case R.id.octcamerabtn3:
				if (resultCode == RESULT_OK) {
					receive(txt_ctopic3,ctocamera3,delctocamera3);
					String strtmp = txt_ctopic4.getContentDescription().toString().replace(" ", "");
					if ((strtmp.equals("CT合相")||strtmp.equals(""))&&qphoto)
						doClick(R.id.octcamerabtn4);
				}
				break;
			case R.id.octcamerabtn4:
				if (resultCode == RESULT_OK)
					receive(txt_ctopic4,ctocamera4,delctocamera4);
				break;
			case R.id.icamerabox:
				if (resultCode == RESULT_OK) 
					receive(txt_boxpic,boxcamera,null);
				break;

			case R.id.icamerabox2:
				if (resultCode == RESULT_OK)
					receive(txt_boxpic2,boxcamera2,null);
				break;

			case R.id.icamerabox3:
				if (resultCode == RESULT_OK) 
					receive(txt_boxpic3,boxcamera3,null);
				break;
			case R.id.scan_aseal1:
				if (resultCode == RESULT_OK) 
					eb_cseal1.setText(data.getExtras().getString("result"));
				break;
			case R.id.scan_aseal2:
				if (resultCode == RESULT_OK)
					eb_cseal2.setText(data.getExtras().getString("result"));
				break;
			case R.id.scan_aseal3:
				if (resultCode == RESULT_OK) 
					eb_cseal3.setText(data.getExtras().getString("result"));
				break;
			case R.id.scan_aseal4:
				if (resultCode == RESULT_OK) 
					eb_cseal4.setText(data.getExtras().getString("result"));
				break;
			case R.id.scan_aseal5:
				if (resultCode == RESULT_OK)
					eb_cseal5.setText(data.getExtras().getString("result"));
				break;
			case R.id.scan_aseal6:
				if (resultCode == RESULT_OK)
					eb_cseal6.setText(data.getExtras().getString("result"));
				break;
			case R.id.txt_amoreboxpic:
				if (resultCode == RESULT_OK)
					setgridViewData(boxAdapter,boxgridView,boxcontent);
				break;
			case R.id.txt_ntbmorepics:
				if (resultCode == RESULT_OK)
					setgridViewData(ntbAdapter,ntbgridView,ntbcontent);
				break;
			case R.id.txt_otbmorepics:
				if (resultCode == RESULT_OK)
					setgridViewData(otbAdapter,otbgridView,otbcontent);
				break;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	private void setgridViewData(ImgAdapter imgAdapter,MyGridView myGridView,List<ImgContent> slist)
	{
		String morepath = Environment.getExternalStorageDirectory()
				.getPath() + "/Photo/" + CurrentImg;
		// ysImg(path);
		Bitmap addbmp = Common.getImageThumbnail(morepath, 3);
		ImgContent con=new ImgContent();
		con.name=CurrentImg;
		con.icon=addbmp;
		slist.add(con);
		imgAdapter=new ImgAdapter(InstallActivity.this,slist,false);
		myGridView.setAdapter(imgAdapter);
        imgAdapter.notifyDataSetChanged();
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

	private void Init() {
		savedatabt = (Button) findViewById(R.id.isavebtn);
		savedatabt.setOnClickListener(this);
		txt_nassetsid = (EditText) findViewById(R.id.nvalue_assetsid);
		txt_nassetsid.setInputType(EditorInfo.TYPE_CLASS_PHONE);
		iboxzb = (EditText) findViewById(R.id.ied_boxzb);
		iboxzb.setInputType(EditorInfo.TYPE_CLASS_PHONE);
		
		scan_nassetsid = (ImageView) findViewById(R.id.scan_nassetsid);
		scan_nassetsid.setOnClickListener(this);
		scan_oldtable = (ImageView) findViewById(R.id.scan_oldtable);
		scan_oldtable.setOnClickListener(this);
		scan_box = (ImageView) findViewById(R.id.iscan_boxid);
		scan_box.setOnClickListener(this);
		
		txt_npic1 = (TextView) findViewById(R.id.npic1name);
		txt_npic2 = (TextView) findViewById(R.id.npic2name);
		txt_npic3 = (TextView) findViewById(R.id.npic3name);
		txt_npic4 = (TextView) findViewById(R.id.npic4name);
		txt_ctnpic1 = (TextView) findViewById(R.id.nctpic1name);
		txt_ctnpic2 = (TextView) findViewById(R.id.nctpic2name);
		txt_ctnpic3 = (TextView) findViewById(R.id.nctpic3name);
		txt_ctnpic4 = (TextView) findViewById(R.id.nctpic4name);
		
		txt_seal1 = (EditText) findViewById(R.id.value_seal1);
		txt_seal2 = (EditText) findViewById(R.id.value_seal2);
		txt_seal3 = (EditText) findViewById(R.id.value_seal3);
		txt_seal4 = (EditText) findViewById(R.id.value_seal4);
		txt_seal5 = (EditText) findViewById(R.id.value_seal5);
		txt_seal6 = (EditText) findViewById(R.id.value_seal6);
		txt_seal7 = (EditText) findViewById(R.id.value_seal7);
		txt_seal8 = (EditText) findViewById(R.id.value_seal8);
		txt_seal9 = (EditText) findViewById(R.id.value_seal9);

		eb_cseal1 = (EditText) findViewById(R.id.value_aseal1);
		eb_cseal2 = (EditText) findViewById(R.id.value_aseal2);
		eb_cseal3 = (EditText) findViewById(R.id.value_aseal3);
		eb_cseal4 = (EditText) findViewById(R.id.value_aseal4);
		eb_cseal5 = (EditText) findViewById(R.id.value_aseal5);
		eb_cseal6 = (EditText) findViewById(R.id.value_aseal6);

		txt_username = (TextView) findViewById(R.id.ivalue_username);
		txt_userid = (TextView) findViewById(R.id.ivalue_userid);
		txt_assetsid = (TextView) findViewById(R.id.ivalue_assetsid);
		txt_useraddress = (TextView) findViewById(R.id.ivalue_useraddress);
		txt_equipmentclass = (TextView) findViewById(R.id.ivalue_equipmentclass);
		txt_inposition=(EditText)findViewById(R.id.ivalue_inposition);
		txt_datatag=(EditText)findViewById(R.id.ivalue_datatag);
		txt_opic1 = (TextView) findViewById(R.id.opic1name);
		txt_opic2 = (TextView) findViewById(R.id.opic2name);
		txt_opic3 = (TextView) findViewById(R.id.opic3name);
		txt_opic4 = (TextView) findViewById(R.id.opic4name);
		txt_ctopic1 = (TextView) findViewById(R.id.octpic1name);
		txt_ctopic2 = (TextView) findViewById(R.id.octpic2name);
		txt_ctopic3 = (TextView) findViewById(R.id.octpic3name);
		txt_ctopic4 = (TextView) findViewById(R.id.octpic4name);
		txt_boxpic = (TextView) findViewById(R.id.iboxpicname);
		txt_boxpic2 = (TextView) findViewById(R.id.iboxpicname2);
		txt_boxpic3 = (TextView) findViewById(R.id.iboxpicname3);

		txt_readdata = (EditText) findViewById(R.id.oeditdata);
		txt_readdata.setInputType(EditorInfo.TYPE_CLASS_PHONE);
		txt_dwreaddata = (EditText) findViewById(R.id.oweditdata);
		txt_dwreaddata.setInputType(EditorInfo.TYPE_CLASS_PHONE);
		txt_nreaddata = (EditText) findViewById(R.id.neditdata);
		txt_nreaddata.setInputType(EditorInfo.TYPE_CLASS_PHONE);
		txt_wreaddata = (EditText) findViewById(R.id.weditdata);
		txt_wreaddata.setInputType(EditorInfo.TYPE_CLASS_PHONE);

		ocamera1 = (ImageView) findViewById(R.id.ocamerabtn1);
		ocamera1.setOnClickListener(this);
		ocamera1.setOnLongClickListener(this);
		ocamera2 = (ImageView) findViewById(R.id.ocamerabtn2);
		ocamera2.setOnClickListener(this);
		ocamera2.setOnLongClickListener(this);
		ocamera3 = (ImageView) findViewById(R.id.ocamerabtn3);
		ocamera3.setOnClickListener(this);
		ocamera3.setOnLongClickListener(this);
		ocamera4 = (ImageView) findViewById(R.id.ocamerabtn4);
		ocamera4.setOnClickListener(this);
		ocamera4.setOnLongClickListener(this);

		ctocamera1 = (ImageView) findViewById(R.id.octcamerabtn1);
		ctocamera1.setOnClickListener(this);
		ctocamera1.setOnLongClickListener(this);
		ctocamera2 = (ImageView) findViewById(R.id.octcamerabtn2);
		ctocamera2.setOnClickListener(this);
		ctocamera2.setOnLongClickListener(this);
		ctocamera3 = (ImageView) findViewById(R.id.octcamerabtn3);
		ctocamera3.setOnClickListener(this);
		ctocamera3.setOnLongClickListener(this);
		ctocamera4 = (ImageView) findViewById(R.id.octcamerabtn4);
		ctocamera4.setOnClickListener(this);
		ctocamera4.setOnLongClickListener(this);

		camera1 = (ImageView) findViewById(R.id.ncamera1);
		camera1.setOnClickListener(this);
		camera1.setOnLongClickListener(this);
		camera2 = (ImageView) findViewById(R.id.ncamera2);
		camera2.setOnClickListener(this);
		camera2.setOnLongClickListener(this);
		camera3 = (ImageView) findViewById(R.id.ncamera3);
		camera3.setOnClickListener(this);
		camera3.setOnLongClickListener(this);
		camera4 = (ImageView) findViewById(R.id.ncamera4);
		camera4.setOnClickListener(this);
		camera4.setOnLongClickListener(this);

		ctcamera1 = (ImageView) findViewById(R.id.nctcamerabtn1);
		ctcamera1.setOnClickListener(this);
		ctcamera1.setOnLongClickListener(this);
		ctcamera2 = (ImageView) findViewById(R.id.nctcamerabtn2);
		ctcamera2.setOnClickListener(this);
		ctcamera2.setOnLongClickListener(this);
		ctcamera3 = (ImageView) findViewById(R.id.nctcamerabtn3);
		ctcamera3.setOnClickListener(this);
		ctcamera3.setOnLongClickListener(this);
		ctcamera4 = (ImageView) findViewById(R.id.nctcamerabtn4);
		ctcamera4.setOnClickListener(this);
		ctcamera4.setOnLongClickListener(this);

		boxcamera = (ImageView) findViewById(R.id.icamerabox);
		boxcamera.setOnClickListener(this);
		boxcamera.setOnLongClickListener(this);
		boxcamera2 = (ImageView) findViewById(R.id.icamerabox2);
		boxcamera2.setOnClickListener(this);
		boxcamera2.setOnLongClickListener(this);
		boxcamera3 = (ImageView) findViewById(R.id.icamerabox3);
		boxcamera3.setOnClickListener(this);
		boxcamera3.setOnLongClickListener(this);
		
		delocamera1 = (ImageView) findViewById(R.id.delocamerabtn1);
		delocamera1.setOnClickListener(this);
		delocamera2 = (ImageView) findViewById(R.id.delocamerabtn2);
		delocamera2.setOnClickListener(this);
		delocamera3 = (ImageView) findViewById(R.id.delocamerabtn3);
		delocamera3.setOnClickListener(this);
		delocamera4 = (ImageView) findViewById(R.id.delocamerabtn4);
		delocamera4.setOnClickListener(this);
		delcamera1 = (ImageView) findViewById(R.id.delncamera1);
		delcamera1.setOnClickListener(this);
		delcamera2 = (ImageView) findViewById(R.id.delncamera2);
		delcamera2.setOnClickListener(this);
		delcamera3 = (ImageView) findViewById(R.id.delncamera3);
		delcamera3.setOnClickListener(this);
		delcamera4 = (ImageView) findViewById(R.id.delncamera4);
		delcamera4.setOnClickListener(this);
		delctocamera1 = (ImageView) findViewById(R.id.deloctcamerabtn1);
		delctocamera1.setOnClickListener(this);
		delctocamera2 = (ImageView) findViewById(R.id.deloctcamerabtn2);
		delctocamera2.setOnClickListener(this);
		delctocamera3 = (ImageView) findViewById(R.id.deloctcamerabtn3);
		delctocamera3.setOnClickListener(this);
		delctocamera4 = (ImageView) findViewById(R.id.deloctcamerabtn4);
		delctocamera4.setOnClickListener(this);
		delctcamera1 = (ImageView) findViewById(R.id.delnctcamerabtn1);
		delctcamera1.setOnClickListener(this);
		delctcamera2 = (ImageView) findViewById(R.id.delnctcamerabtn2);
		delctcamera2.setOnClickListener(this);
		delctcamera3 = (ImageView) findViewById(R.id.delnctcamerabtn3);
		delctcamera3.setOnClickListener(this);
		delctcamera4 = (ImageView) findViewById(R.id.delnctcamerabtn4);
		delctcamera4.setOnClickListener(this);
		
		scan_seal1 = (ImageView) findViewById(R.id.scan_seal1);
		scan_seal1.setOnClickListener(this);
		scan_seal2 = (ImageView) findViewById(R.id.scan_seal2);
		scan_seal2.setOnClickListener(this);
		scan_seal3 = (ImageView) findViewById(R.id.scan_seal3);
		scan_seal3.setOnClickListener(this);
		scan_seal4 = (ImageView) findViewById(R.id.scan_seal4);
		scan_seal4.setOnClickListener(this);
		scan_seal5 = (ImageView) findViewById(R.id.scan_seal5);
		scan_seal5.setOnClickListener(this);
		scan_seal6 = (ImageView) findViewById(R.id.scan_seal6);
		scan_seal6.setOnClickListener(this);
		scan_seal7 = (ImageView) findViewById(R.id.scan_seal7);
		scan_seal7.setOnClickListener(this);
		scan_seal8 = (ImageView) findViewById(R.id.scan_seal8);
		scan_seal8.setOnClickListener(this);
		scan_seal9 = (ImageView) findViewById(R.id.scan_seal9);
		scan_seal9.setOnClickListener(this);

		scan_boxseal1 = (ImageView) findViewById(R.id.scan_aseal1);
		scan_boxseal1.setOnClickListener(this);
		scan_boxseal2 = (ImageView) findViewById(R.id.scan_aseal2);
		scan_boxseal2.setOnClickListener(this);
		scan_boxseal3 = (ImageView) findViewById(R.id.scan_aseal3);
		scan_boxseal3.setOnClickListener(this);
		scan_boxseal4 = (ImageView) findViewById(R.id.scan_aseal4);
		scan_boxseal4.setOnClickListener(this);
		scan_boxseal5 = (ImageView) findViewById(R.id.scan_aseal5);
		scan_boxseal5.setOnClickListener(this);
		scan_boxseal6 = (ImageView) findViewById(R.id.scan_aseal6);
		scan_boxseal6.setOnClickListener(this);
		
		ntbmoretbpics=(TextView)findViewById(R.id.txt_ntbmorepics);
		ntbmoretbpics.setOnClickListener(this);
		ntbgridView = (MyGridView) findViewById(R.id.ngridtbView);
		ntbAdapter=new ImgAdapter(InstallActivity.this,ntbcontent,false);
		ntbgridView.setAdapter(ntbAdapter);
		setntbGridViewOnclick();
		ntbgridView.setOnItemLongClickListener(new OnItemLongClickListener() {// 设置事件监听(长按)
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				Boolean ntbisShowDelete=ntbAdapter.getShowDelete(position);
				if (ntbisShowDelete) {
                    ntbisShowDelete = false;
                    setntbGridViewOnclick();
                } else {
                    ntbisShowDelete = true;
                    ntbgridView.setOnItemClickListener(null);
                }
                Log.d("TAG","onItemLongClicked");
                ntbAdapter.setShowDelete(position,ntbisShowDelete);
				return true;
			}
		});
		otbmoretbpics=(TextView)findViewById(R.id.txt_otbmorepics);
		otbmoretbpics.setOnClickListener(this);
		otbgridView = (MyGridView) findViewById(R.id.ogridtbView);
		otbAdapter=new ImgAdapter(InstallActivity.this,otbcontent,false);
		otbgridView.setAdapter(otbAdapter);
		setotbGridViewOnclick();
		otbgridView.setOnItemLongClickListener(new OnItemLongClickListener() {// 设置事件监听(长按)
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				Boolean otbisShowDelete=otbAdapter.getShowDelete(position);
				if (otbisShowDelete) {
                    otbisShowDelete = false;
                    setotbGridViewOnclick();
                } else {
                    otbisShowDelete = true;
                    otbgridView.setOnItemClickListener(null);
                }
                Log.d("TAG","onItemLongClicked");
                otbAdapter.setShowDelete(position,otbisShowDelete);
				return true;
			}
		});

		boxmoretbpics=(TextView)findViewById(R.id.txt_amoreboxpic);
		boxmoretbpics.setOnClickListener(this);
		boxgridView = (MyGridView) findViewById(R.id.agridboxView);
		boxAdapter=new ImgAdapter(InstallActivity.this,boxcontent,false);
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
	
	private void setotbGridViewOnclick() {
		otbgridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Boolean otbisShowDelete=otbAdapter.getShowDelete(position);
				if(!otbisShowDelete)
				{
					File file = new File(Environment.getExternalStorageDirectory()
							.getPath() + "/Photo/" + otbcontent.get(position).name);
					Intent intent = new Intent();
					intent.setAction(android.content.Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.fromFile(file), "image/*");
					startActivity(intent);
				}
			}
		});
	}
	private void setntbGridViewOnclick() {
		ntbgridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Boolean ntbisShowDelete=ntbAdapter.getShowDelete(position);
				if(!ntbisShowDelete)
				{
					File file = new File(Environment.getExternalStorageDirectory()
							.getPath() + "/Photo/" + ntbcontent.get(position).name);
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

	private void setView(String action, String m_data) {
		if (m_data.equals(""))
			return;
		if (action.equals("A")) {
			JSONObject info = null;
			try {
				info = new JSONObject(m_data);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (info != null) {
				try {
					String nassct = info.getString("d");
					txt_nassetsid.setText(nassct);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {
			new Thread(new Runnable() {
				@Override
				public void run() {
					AnalysisResult analysis = new AnalysisResult();
					if (data.equals(""))
						return;
					TableData tabledata = analysis.getData(data);
					DBHelper helper = new DBHelper(mcontext);
					try {
						Cursor cursor = helper.query("RecordData",
										new String[] { "username,usernumber,assetsnumber,useraddress,equipmentclass,BIN_POSITION_NUMBER,TAG,readdata,wreaddata,ameterspic,tmeterspic,allpic,n_assetsnumber,n_ameterspic,n_tmeterspic,n_allpic,n_readdata,n_wreaddata,seal1,seal2,seal3,seal4,seal5,seal6,seal7,seal8,seal9,"
												+ "dstatic,istatic,static,TABLE_CABINET_NO,OCT_SINGLE_PICTURES,OCT_DOUBLE_PICTURES,OCT_WHOLE_PICTURES,NCT_SINGLE_PICTURES,NCT_DOUBLE_PICTURES,NCT_WHOLE_PICTURES,ROUND_CT,NEW_ROUND_CT,NEW_REACTIVE_PICTURES,OLD_REACTIVE_PICTURES,NEW_MORE_PICTURES,OLD_MORE_PICTURES,MORE_SEAL_NUMBER" },
										"username = ? and usernumber = ? and assetsnumber=?",
										new String[] { tabledata.getUserName(),
												tabledata.getUserId(),
												tabledata.getAssetsId() },
										null, null, null);
						if (cursor != null) {
							if (cursor.moveToFirst() == false) {
								Message doingnow = new Message();
								doingnow.what = 0;
								doingnow.obj = tabledata;
								handler.sendMessage(doingnow);
							} else {
								if (currentbox == null) {
									if (cursor.getString(cursor
											.getColumnIndex("TABLE_CABINET_NO")) != null
											&& !cursor
													.getString(
															cursor.getColumnIndex("TABLE_CABINET_NO"))
													.equals("")) {
										currentboxno = cursor.getString(cursor
												.getColumnIndex("TABLE_CABINET_NO"));
										currentbox = getBoxData(currentboxno);
									}
								}
								record = new TableData();
								record.setUserName(cursor.getString(cursor.getColumnIndex("username")));
								record.setUserId(cursor.getString(cursor.getColumnIndex("usernumber")));
								record.setAssetsId(cursor.getString(cursor.getColumnIndex("assetsnumber")));
								record.setUserAddress(cursor.getString(cursor.getColumnIndex("useraddress")));
								record.setEquipmentClass(cursor.getString(cursor.getColumnIndex("equipmentclass")));
								record.setBIN_POSITION_NUMBER(cursor.getString(cursor.getColumnIndex("BIN_POSITION_NUMBER")));
								record.setDATATAG(cursor.getString(cursor.getColumnIndex("TAG")));
								record.setReaddata(cursor.getString(cursor.getColumnIndex("readdata")));
								record.setMdReaddata(cursor.getString(cursor.getColumnIndex("wreaddata")));
								record.setAmeterspic(cursor.getString(cursor.getColumnIndex("ameterspic")));
								record.setTeterspic(cursor.getString(cursor.getColumnIndex("tmeterspic")));
								record.setAllpic(cursor.getString(cursor.getColumnIndex("allpic")));
								record.setN_assetsnumber(cursor.getString(cursor.getColumnIndex("n_assetsnumber")));
								record.setN_ameterspic(cursor.getString(cursor.getColumnIndex("n_ameterspic")));
								record.setN_tmeterspic(cursor.getString(cursor.getColumnIndex("n_tmeterspic")));
								record.setN_allpic(cursor.getString(cursor.getColumnIndex("n_allpic")));
								record.setN_readdata(cursor.getString(cursor.getColumnIndex("n_readdata")));
								record.setN_wreaddata(cursor.getString(cursor.getColumnIndex("n_wreaddata")));
								record.setSealId1(cursor.getString(cursor.getColumnIndex("seal1")));
								record.setSealId2(cursor.getString(cursor.getColumnIndex("seal2")));
								record.setSealId3(cursor.getString(cursor.getColumnIndex("seal3")));
								record.setSealId4(cursor.getString(cursor.getColumnIndex("seal4")));
								record.setSealId5(cursor.getString(cursor.getColumnIndex("seal5")));
								record.setSealId6(cursor.getString(cursor.getColumnIndex("seal6")));
								record.setSealId7(cursor.getString(cursor.getColumnIndex("seal7")));
								record.setSealId8(cursor.getString(cursor.getColumnIndex("seal8")));
								record.setSealId9(cursor.getString(cursor.getColumnIndex("seal9")));
								record.setDstatic(cursor.getString(cursor.getColumnIndex("dstatic")));
								record.setIstatic(cursor.getString(cursor.getColumnIndex("istatic")));
								record.setStatic(cursor.getString(cursor.getColumnIndex("static")));
								record.setOCT_SINGLE_PICTURES(cursor.getString(cursor.getColumnIndex("OCT_SINGLE_PICTURES")));
								record.setOCT_DOUBLE_PICTURES(cursor.getString(cursor.getColumnIndex("OCT_DOUBLE_PICTURES")));
								record.setOCT_WHOLE_PICTURES(cursor.getString(cursor.getColumnIndex("OCT_WHOLE_PICTURES")));
								record.setNCT_SINGLE_PICTURES(cursor.getString(cursor.getColumnIndex("NCT_SINGLE_PICTURES")));
								record.setNCT_DOUBLE_PICTURES(cursor.getString(cursor.getColumnIndex("NCT_DOUBLE_PICTURES")));
								record.setNCT_WHOLE_PICTURES(cursor.getString(cursor.getColumnIndex("NCT_WHOLE_PICTURES")));
								record.setROUND_CT(cursor.getString(cursor.getColumnIndex("ROUND_CT")));
								record.setNEW_ROUND_CT(cursor.getString(cursor.getColumnIndex("NEW_ROUND_CT")));
								record.setNEW_REACTIVE_PICTURES(cursor.getString(cursor.getColumnIndex("NEW_REACTIVE_PICTURES")));
								record.setOLD_REACTIVE_PICTURES(cursor.getString(cursor.getColumnIndex("OLD_REACTIVE_PICTURES")));
								record.setNEW_MORE_PICTURES(cursor.getString(cursor.getColumnIndex("NEW_MORE_PICTURES")));
								record.setOLD_MORE_PICTURES(cursor.getString(cursor.getColumnIndex("OLD_MORE_PICTURES")));
								record.setMORE_SEAL_NUMBER(cursor.getString(cursor.getColumnIndex("MORE_SEAL_NUMBER")));

								Message doingnow = new Message();
								doingnow.what = TABLE_DATA_CODE;
								doingnow.obj = record;
								handler.sendMessage(doingnow);

								String p1 = record.getN_ameterspic();
								String p2 = record.getN_tmeterspic();
								String p3 = record.getN_allpic();
								String p4 = record.getNEW_REACTIVE_PICTURES();
								if (p1 != null && !p1.equals(""))
									sendpic(txt_npic1,camera1, p1);
								if (p4 != null && !p4.equals(""))
									sendpic(txt_npic4,camera4, p4);
								if (p2 != null && !p2.equals(""))
									sendpic(txt_npic2,camera2, p2);
								if (p3 != null && !p3.equals(""))
									sendpic(txt_npic3,camera3, p3);

								String ctp1 = record.getNCT_SINGLE_PICTURES();
								String ctp2 = record.getNCT_DOUBLE_PICTURES();
								String ctp3 = record.getNCT_WHOLE_PICTURES();
								String ctp4 = record.getNEW_ROUND_CT();
								if (ctp1 != null && !ctp1.equals(""))
									sendpic(txt_ctnpic1,ctcamera1, ctp1);
								if (ctp2 != null && !ctp2.equals(""))
									sendpic(txt_ctnpic2,ctcamera2, ctp2);
								if (ctp3 != null && !ctp3.equals(""))
									sendpic(txt_ctnpic3,ctcamera3, ctp3);
								if (ctp4 != null && !ctp4.equals(""))
									sendpic(txt_ctnpic4,ctcamera4, ctp4);

								String op1 = record.getAmeterspic();
								String op2 = record.getTeterspic();
								String op3 = record.getAllpic();
								String op4 = record.getOLD_REACTIVE_PICTURES();
								if (op1 != null && !op1.equals(""))
									sendpic(txt_opic1,ocamera1, op1);
								if (op4 != null && !op4.equals(""))
									sendpic(txt_opic4,ocamera4, op4);
								if (op2 != null && !op2.equals(""))
									sendpic(txt_opic2,ocamera2, op2);
								if (op3 != null && !op3.equals(""))
									sendpic(txt_opic3,ocamera3, op3);

								String ctop1 = record.getOCT_SINGLE_PICTURES();
								String ctop2 = record.getOCT_DOUBLE_PICTURES();
								String ctop3 = record.getOCT_WHOLE_PICTURES();
								String ctop4 = record.getROUND_CT();
								if (ctop1 != null && !ctop1.equals(""))
									sendpic(txt_ctopic1,ctocamera1, ctop1);
								if (ctop2 != null && !ctop2.equals(""))
									sendpic(txt_ctopic4,ctocamera4, ctop2);
								if (ctop3 != null && !ctop3.equals(""))
									sendpic(txt_ctopic3,ctocamera3, ctop3);
								if (ctop4 != null && !ctop4.equals(""))
									sendpic(txt_ctopic4,ctocamera4, ctop4);
								
								if(record.getNEW_MORE_PICTURES()!=null&&!record.getNEW_MORE_PICTURES().equals(""))
								{
									String[] arr=record.getNEW_MORE_PICTURES().split(",");
									if(arr.length>0)
									{
										for(int i=0;i<arr.length;i++)
										{
											String path = Environment.getExternalStorageDirectory().getPath() + "/Photo/" + arr[i];
											// ysImg(path);
											Bitmap addbmp = Common.getImageThumbnail(path, 3);
											if(addbmp!=null)
											{
												ImgContent con=new ImgContent();
												con.name=arr[i];
												con.icon=addbmp;
												ntbcontent.add(con);
											}
										}
										Message txthd = new Message();
										txthd.what = LIST_DATA_CODE;
										txthd.obj=1;
										handler.sendMessage(txthd);
									}
								}
								if(record.getOLD_MORE_PICTURES()!=null&&!record.getOLD_MORE_PICTURES().equals(""))
								{
									String[] arr=record.getOLD_MORE_PICTURES().split(",");
									if(arr.length>0)
									{
										for(int i=0;i<arr.length;i++)
										{
											String path = Environment.getExternalStorageDirectory().getPath() + "/Photo/" + arr[i];
											// ysImg(path);
											Bitmap addbmp = Common.getImageThumbnail(path, 3);
											if(addbmp!=null){
												ImgContent con=new ImgContent();
												con.name=arr[i];
												con.icon=addbmp;
												otbcontent.add(con);
											}
										}
										Message txthd = new Message();
										txthd.what = LIST_DATA_CODE;
										txthd.obj=2;
										handler.sendMessage(txthd);
									}
								}
							}
							cursor.close();

							if (currentbox != null) {
								if (currentbox.getTABLE_CABINET_NO() != null
										&& !currentbox.getTABLE_CABINET_NO()
												.equals("")) {
									Message txthd = new Message();
									txthd.what = BOX_DATA_CODE;
									txthd.obj = currentbox;
									handler.sendMessage(txthd);
								}
								if (currentbox.getPICTURES() != null
										&& !currentbox.getPICTURES().equals(""))
									sendpic(txt_boxpic,boxcamera,currentbox.getPICTURES());
								if (currentbox.getPICTURES2() != null
										&& !currentbox.getPICTURES2().equals(""))
									sendpic(txt_boxpic2,boxcamera2,currentbox.getPICTURES2());
								if (currentbox.getPICTURES3() != null
										&& !currentbox.getPICTURES3().equals(""))
									sendpic(txt_boxpic3,boxcamera3,currentbox.getPICTURES3());
								if (currentbox.getMORE_PICTURES() != null&& !currentbox.getMORE_PICTURES().equals("")) {
									String[] arr = currentbox.getMORE_PICTURES().split(",");
									if (arr.length > 0) {
										for (int i = 0; i < arr.length; i++) {
											String path = Environment.getExternalStorageDirectory().getPath() + "/Photo/" + arr[i];
											// ysImg(path);
											Bitmap addbmp = Common.getImageThumbnail(path, 3);
											if(addbmp!=null){
												ImgContent con=new ImgContent();
												con.name=arr[i];
												con.icon=addbmp;
												boxcontent.add(con);
											}
										}
										Message txthd = new Message();
										txthd.what = LIST_DATA_CODE;
										txthd.obj=3;
										handler.sendMessage(txthd);
									}
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
	}

	protected void sendpic(TextView txtview,ImageView imgview, String filename) {
		String path = Environment.getExternalStorageDirectory().getPath()
				+ "/Photo/" + filename;
		Bitmap bitmap = Common.getImageThumbnail(path, 3);
		if (bitmap != null) {
			PHOMessageData data=new PHOMessageData();
			data.imgview=imgview;
			data.txtview=txtview;
			data.bmpname=filename;
			data.bmp=bitmap;
			Message phomessage = new Message();
			phomessage.what = PHO_DATA_CODE;
			phomessage.obj = data;
			handler.sendMessage(phomessage);
		}
	}
}
