package com.rm.ebtapp;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import com.rm.config.SysConfig;
import com.rm.db.BLL;
import com.rm.db.DBHelper;
import com.rm.model.MistakeRecord;
import com.rm.model.TableBox;
import com.rm.model.TableData;
import com.rm.until.MySwitch;
import com.rm.until.MySwitch.OnSwitchChangedListener;
import com.rm.until.UploadUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SetingActivity extends Activity implements OnClickListener,
		OnSwitchChangedListener {
	private final int USERLOGIN_GREQUEST_CODE = 0;
	private final int GETVALUE = 0;
	private final int POSTVALUE = 1;
	private final int succeed = 2;
	private final int MYMESSAGE = 3;
	private final int success = 4;
	private final int IMGSUBMITSUCCESS = 5;
	private final int SUBMIT_BPDATA_CODE = 6;
	private int submitdata = 0;
	private SharedPreferences mPreferences;
	private Editor mEditor;
	private String userName = "";
	private String userID = "";
	private String ENTERPRISE_ID = "";
	RelativeLayout rel_upload, rel_uploadimg, rel_clear,rel_bppic;
	ImageView img_upload, img_uploadimg, img_clear,img_bppic;
	TextView txt_upload, txt_uploadimg, txt_clear,txt_bppic, doingnow, submitimg;
	Button btn_close;
	MySwitch myswith, datacheck_Switch, imgcheck_Switch, qwork_Switch,qphoto_Switch;

	private Notification mNotification;
	private NotificationManager mNotificationManager;

	List<TableData> mlistInfo = new ArrayList<TableData>();
	List<TableBox> boxlistInfo = new ArrayList<TableBox>();
	List<MistakeRecord> mistakelistInfo = new ArrayList<MistakeRecord>();
	List<String> imglist = new ArrayList<String>();

	Boolean submiting = false;

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GETVALUE:
				try {
					msg.obj = URLDecoder.decode(msg.obj + "", "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				break;
			case POSTVALUE:
				Toast.makeText(SetingActivity.this, msg.obj.toString(),
						Toast.LENGTH_LONG).show();
				break;
			case succeed:
				TableData temptb = (TableData) msg.obj;
				update(temptb);
				break;
			case success:
				TableBox tempbox = (TableBox) msg.obj;
				if (tempbox != null && tempbox.getTABLE_CABINET_NO() != null
						&& tempbox.getTABLE_CABINET_NO().equals("")) {
					DBHelper helper = new DBHelper(SetingActivity.this);
					try {
						ContentValues upd = new ContentValues();
						upd.put("TSTATIC", "1");
						helper.update("TabBoxData", upd,
								"TABLE_CABINET_NO = ?",
								new String[] { tempbox.getTABLE_CABINET_NO() });
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				break;
			case MYMESSAGE:
				doingnow.setText(msg.obj.toString());
				break;
			case 10:
				doingnow.setText(msg.obj.toString());
				break;
			case IMGSUBMITSUCCESS:
				submitimg.setText(msg.obj.toString());
				break;
			case SUBMIT_BPDATA_CODE:
				MistakeRecord record=(MistakeRecord) msg.obj;
				if (record != null && record.getUSER_NO() != null&& !record.getUSER_NO().equals("")
						&& record.getASSET_TAG() != null&& !record.getASSET_TAG().equals("")) {
					DBHelper helper = new DBHelper(SetingActivity.this);
					try {
						ContentValues upd = new ContentValues();
						upd.put("SUBMITSTATIC", "1");
						helper.update("BPIMG", upd,"USER_NO = ? and ASSET_TAG=?",	new String[] { record.getUSER_NO(),record.getASSET_TAG()  });
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
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
		setContentView(R.layout.layout_seting);
		((ImageView) findViewById(R.id.img_user)).setVisibility(View.GONE);
		findViewById(R.id.button_title).setVisibility(View.GONE);
		TextView title = (TextView) findViewById(R.id.textview_title);
		title.setVisibility(View.VISIBLE);
		title.setText("设置");
		Button mButtonBack = (Button) findViewById(R.id.button_back);
		mButtonBack.setVisibility(View.VISIBLE);
		mButtonBack.setOnClickListener(this);

		mPreferences = getSharedPreferences("APPSETTING", Context.MODE_PRIVATE);
		mEditor = mPreferences.edit();

		rel_upload = (RelativeLayout) findViewById(R.id.rel_upload);
		rel_upload.setOnClickListener(this);
		img_upload = (ImageView) findViewById(R.id.img_upload);
		img_upload.setOnClickListener(this);
		txt_upload = (TextView) findViewById(R.id.txt_upload);
		txt_upload.setOnClickListener(this);

		rel_uploadimg = (RelativeLayout) findViewById(R.id.rel_uploadimg);
		rel_uploadimg.setOnClickListener(this);
		img_uploadimg = (ImageView) findViewById(R.id.img_uploadimg);
		img_uploadimg.setOnClickListener(this);
		txt_uploadimg = (TextView) findViewById(R.id.txt_uploadimg);
		txt_uploadimg.setOnClickListener(this);

		rel_clear = (RelativeLayout) findViewById(R.id.rel_clear);
		rel_clear.setOnClickListener(this);
		img_clear = (ImageView) findViewById(R.id.img_clear);
		img_clear.setOnClickListener(this);
		txt_clear = (TextView) findViewById(R.id.txt_clear);
		txt_clear.setOnClickListener(this);
		rel_bppic = (RelativeLayout) findViewById(R.id.rel_bppic);
		rel_bppic.setOnClickListener(this);
		img_bppic = (ImageView) findViewById(R.id.img_bppic);
		img_bppic.setOnClickListener(this);
		txt_bppic = (TextView) findViewById(R.id.txt_bppic);
		txt_bppic.setOnClickListener(this);
		btn_close = (Button) findViewById(R.id.closeseting);
		btn_close.setOnClickListener(this);
		myswith = (MySwitch) findViewById(R.id.light_Switch);
		myswith.setOnSwitchChangedListener(this);
		qwork_Switch = (MySwitch) findViewById(R.id.qwork_Switch);
		qwork_Switch.setOnSwitchChangedListener(this);
		qphoto_Switch = (MySwitch) findViewById(R.id.qphoto_Switch);
		qphoto_Switch.setOnSwitchChangedListener(this);
		datacheck_Switch = (MySwitch) findViewById(R.id.datacheck_Switch);
		datacheck_Switch.setOnSwitchChangedListener(this);
		imgcheck_Switch = (MySwitch) findViewById(R.id.imgcheck_Switch);
		imgcheck_Switch.setOnSwitchChangedListener(this);
		doingnow = (TextView) findViewById(R.id.txt_doing);
		submitimg = (TextView) findViewById(R.id.txt_submitimg);

		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mNotification = new NotificationCompat.Builder(this)
				.setSmallIcon(R.drawable.upload).setContentTitle("上传")
				.setContentText("正在上传数据……").build();

		myswith.setStatus(mPreferences.getBoolean("mylight", false));
		imgcheck_Switch.setStatus(mPreferences.getBoolean("myimgcheck", false));
		datacheck_Switch.setStatus(mPreferences
				.getBoolean("mydatacheck", false));
		qwork_Switch.setStatus(mPreferences.getBoolean("myqwork", false));
		qphoto_Switch.setStatus(mPreferences.getBoolean("myqphoto", false));
		selectData();

		userName = mPreferences.getString("userName", "");
		userID = mPreferences.getString("userid", "");
		ENTERPRISE_ID = mPreferences.getString("deptPid", "");
		
		setfailImageinfo();
	}
	
	@Override
	public void onResume()
	{
		userName = mPreferences.getString("userName", "");
		userID = mPreferences.getString("userid", "");
		ENTERPRISE_ID = mPreferences.getString("deptPid", "");
		super.onResume();
	}

	@Override
	public void onSwitchChanged(MySwitch obj, int status) {
		switch (obj.getId()) {
		case R.id.light_Switch:
			boolean b_light = false;
			if (status == 0)
				b_light = false;
			else if (status == 1)
				b_light = true;
			else {
			}
			mEditor.putBoolean("mylight", b_light);
			mEditor.commit();
			break;
		case R.id.qwork_Switch:
			boolean b_qwork = false;
			if (status == 0)
				b_qwork = false;
			else if (status == 1) {
				b_qwork = true;
			} else {
			}
			mEditor.putBoolean("myqwork", b_qwork);
			mEditor.commit();
			break;
		case R.id.qphoto_Switch:
			boolean b_qphoto = false;
			if (status == 0)
				b_qphoto = false;
			else if (status == 1) {
				b_qphoto = true;
			} else {
			}
			mEditor.putBoolean("myqphoto", b_qphoto);
			mEditor.commit();
			break;
		case R.id.datacheck_Switch:
			boolean b_datacheck = false;
			if (status == 0)
				b_datacheck = false;
			else if (status == 1)
				b_datacheck = true;
			else {
			}
			mEditor.putBoolean("mydatacheck", b_datacheck);
			mEditor.commit();
			break;
		case R.id.imgcheck_Switch:
			boolean b_imgcheck = false;
			if (status == 0)
				b_imgcheck = false;
			else if (status == 1)
				b_imgcheck = true;
			else {
			}
			mEditor.putBoolean("myimgcheck", b_imgcheck);
			mEditor.commit();
			break;
		}
	}
	
	private void setfailImageinfo() {
		getfailImglist();
		if (imglist.size() < 1) {
			submitimg.setText("暂无提交失败照片！");
		}
		else
			submitimg.setText(imglist.size()+"张照片提交失败，请重新提交。");
	}

	protected void update(TableData temptb) {
		ContentValues cv = new ContentValues();
		cv.put("static", 1);
		DBHelper helper = new DBHelper(this);
		try {
			helper.update("RecordData", cv,
					"username = ? and usernumber = ? and assetsnumber=?",
					new String[] { temptb.getUserName(), temptb.getUserId(),
							temptb.getAssetsId() });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onBackPressed() {
		if (submiting) {
			new AlertDialog.Builder(this)
					.setTitle("正在提交数据，确认退出吗？")
					.setIcon(R.drawable.upload)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									SetingActivity.this.finish();
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
			SetingActivity.this.finish();
	}

	@Override
	public void onClick(View arg0) {
		int viewid = arg0.getId();
		switch (viewid) {
		case R.id.closeseting:
		case R.id.button_back:
			if (submiting) {
				new AlertDialog.Builder(this)
						.setTitle("正在提交数据，确认退出吗？")
						.setIcon(R.drawable.upload)
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										SetingActivity.this.finish();
									}
								})
						.setNegativeButton("返回",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,	int which) {}
								}).show();
			} else
				SetingActivity.this.finish();
			break;
		case R.id.rel_upload:
		case R.id.img_upload:
		case R.id.txt_upload:
			userName = mPreferences.getString("userName", "");
			if (userName.equals("")) {
				new AlertDialog.Builder(SetingActivity.this)
						.setTitle("系统提示")
						.setMessage("请先登陆才能提交数据 ！")
						.setPositiveButton("确定", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										Intent loginintent = new Intent();
										loginintent.setClass(
												SetingActivity.this,
												LoginActivity.class);
										loginintent
												.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
										startActivityForResult(loginintent,
												USERLOGIN_GREQUEST_CODE);
									}
								})
						.setNegativeButton("返回",new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										Log.i("alertdialog", " 请保存数据！");
									}
								}).show();
				return;
			} else {
				new AlertDialog.Builder(SetingActivity.this)
						.setTitle("选择删除项")
						.setIcon(R.drawable.upload)
						.setSingleChoiceItems(
								new String[] { "提交所有数据", "提交已完成数据", "提交补拍数据" }, 0,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										switch (which) {
										case 0:
											submitdata = 0;
											uploaddata();
											break;
										case 1:
											submitdata = 1;
											uploaddata();
											break;
										case 2:
											uploadBPdata();
											break;
										default:
											break;
										}
										dialog.dismiss();
									}
								}).setNegativeButton("取消", null).show();
			}
			break;
		case R.id.rel_clear:
		case R.id.img_clear:
		case R.id.txt_clear:
			cleardata();
			break;
		case R.id.rel_bppic:
		case R.id.img_bppic:
		case R.id.txt_bppic:
			Intent loginintent = new Intent();
			loginintent.setClass(SetingActivity.this, BPDataActivity.class);
			loginintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(loginintent, USERLOGIN_GREQUEST_CODE);
			break;
		case R.id.rel_uploadimg:
		case R.id.img_uploadimg:
		case R.id.txt_uploadimg:
			submitfailImg();
			break;
		}
	}

	private void submitfailImg() {
		if (!submiting) {
			submiting = true;
			getfailImglist();
			if (imglist.size() < 1) {
				submitimg.setText("暂无提交失败照片！");
			} else {
				new Thread(new Runnable() {
					@Override
					public void run() {
						mNotificationManager.notify(0, mNotification);
						Message mydoingnow = new Message();
						mydoingnow.what = IMGSUBMITSUCCESS;
						mydoingnow.obj = "正在提交图片数据……";
						handler.sendMessage(mydoingnow);
						for (int i = 0; i < imglist.size(); i++) {
							Message progrom = new Message();
							progrom.what = IMGSUBMITSUCCESS;
							progrom.obj = "正在提交图片数据：" + i + "/"
									+ boxlistInfo.size();
							handler.sendMessage(progrom);
							Boolean issuccess = submitImgfunction(imglist.get(i));
							if (issuccess) {
								DBHelper helper = new DBHelper(
										SetingActivity.this);
								helper.delete("FailImg", "PICTURENAME = ?",
										new String[] { imglist.get(i) });
							}
						}

						mNotificationManager.cancelAll();
						Message doednow = new Message();
						doednow.what = MYMESSAGE;
						doednow.obj = "提交数据完成";
						handler.sendMessage(doednow);
					}
				}).start();
			}
			submiting = false;
		}
	}

	private void getfailImglist() {
		DBHelper helper = new DBHelper(this);
		try {
			Cursor cursor = helper.query("FailImg", null, null, null, null,
					null, null);
			if (cursor != null) {
				imglist.clear();
				while (cursor.moveToNext()) {
					String filename = cursor.getString(cursor
							.getColumnIndex("PICTURENAME"));
					if (filename != null && !filename.equals("")) {
						imglist.add(filename);
					}
				}
			}
			cursor.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void cleardata() {
		new AlertDialog.Builder(SetingActivity.this)
				.setTitle("选择删除项")
				.setIcon(R.drawable.delete_24px)
				.setSingleChoiceItems(new String[] { "删除已提交数据", "删除所有数据" }, 0,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								switch (which) {
								case 0:
									deleteData(0);
									break;
								case 1:
									deleteData(1);
									break;
								default:
									break;
								}
								dialog.dismiss();
							}
						}).setNegativeButton("取消", null).show();
	}

	private void deleteData(int whice) {
		DBHelper helper = new DBHelper(this);
		try {
			String st = "1";
			Cursor cursor = null;
			if (whice == 0) {
				cursor = helper.query("RecordData", null, "static = ?",
						new String[] { st }, null, null, null);
			} else {
				cursor = helper.query("RecordData", null, null, null, null,
						null, null);
				helper.delete("TabBoxData", null, null);
				helper.delete("FailImg", null, null);
			}
			if (cursor != null) {
				mlistInfo.clear();
				while (cursor.moveToNext()) {
					TableData data = new TableData();
					data.setUserName(cursor.getString(cursor
							.getColumnIndex("username")));
					data.setUserId(cursor.getString(cursor
							.getColumnIndex("usernumber")));
					data.setAssetsId(cursor.getString(cursor
							.getColumnIndex("assetsnumber")));
					data.setUserAddress(cursor.getString(cursor
							.getColumnIndex("useraddress")));
					data.setEquipmentClass(cursor.getString(cursor
							.getColumnIndex("equipmentclass")));
					data.setReaddata(cursor.getString(cursor
							.getColumnIndex("readdata")));
					data.setMdReaddata(cursor.getString(cursor
							.getColumnIndex("wreaddata")));
					data.setAmeterspic(cursor.getString(cursor.getColumnIndex("ameterspic")));
					data.setTeterspic(cursor.getString(cursor.getColumnIndex("tmeterspic")));
					data.setAllpic(cursor.getString(cursor.getColumnIndex("allpic")));
					data.setOLD_REACTIVE_PICTURES(cursor.getString(cursor.getColumnIndex("OLD_REACTIVE_PICTURES")));
					data.setN_assetsnumber(cursor.getString(cursor.getColumnIndex("n_assetsnumber")));
					data.setN_ameterspic(cursor.getString(cursor.getColumnIndex("n_ameterspic")));
					data.setN_tmeterspic(cursor.getString(cursor.getColumnIndex("n_tmeterspic")));
					data.setN_allpic(cursor.getString(cursor.getColumnIndex("n_allpic")));
					data.setNEW_REACTIVE_PICTURES(cursor.getString(cursor.getColumnIndex("NEW_REACTIVE_PICTURES")));
					
					data.setNCT_SINGLE_PICTURES(cursor.getString(cursor.getColumnIndex("NCT_SINGLE_PICTURES")));
					data.setNCT_DOUBLE_PICTURES(cursor.getString(cursor.getColumnIndex("NCT_SINGLE_PICTURES")));
					data.setNCT_WHOLE_PICTURES(cursor.getString(cursor.getColumnIndex("NCT_WHOLE_PICTURES")));
					data.setNEW_ROUND_CT(cursor.getString(cursor.getColumnIndex("NEW_ROUND_CT")));
					data.setOCT_SINGLE_PICTURES(cursor.getString(cursor.getColumnIndex("OCT_SINGLE_PICTURES")));
					data.setOCT_DOUBLE_PICTURES(cursor.getString(cursor.getColumnIndex("OCT_SINGLE_PICTURES")));
					data.setOCT_WHOLE_PICTURES(cursor.getString(cursor.getColumnIndex("OCT_WHOLE_PICTURES")));
					data.setROUND_CT(cursor.getString(cursor.getColumnIndex("ROUND_CT")));
					
					
					data.setN_readdata(cursor.getString(cursor
							.getColumnIndex("n_readdata")));
					data.setN_wreaddata(cursor.getString(cursor
							.getColumnIndex("n_wreaddata")));
					data.setSealId1(cursor.getString(cursor
							.getColumnIndex("seal1")));
					data.setSealId2(cursor.getString(cursor
							.getColumnIndex("seal2")));
					data.setSealId3(cursor.getString(cursor
							.getColumnIndex("seal3")));
					data.setMdtime(cursor.getString(cursor
							.getColumnIndex("dtime")));
					data.setMitime(cursor.getString(cursor
							.getColumnIndex("itime")));
					data.setDstatic(cursor.getString(cursor
							.getColumnIndex("dstatic")));
					data.setIstatic(cursor.getString(cursor
							.getColumnIndex("istatic")));
					data.setStatic(cursor.getString(cursor
							.getColumnIndex("static")));
					mlistInfo.add(data);
				}
				cursor.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (int r = 0; r < mlistInfo.size(); r++) {
			TableData temptb = mlistInfo.get(r);
			List<String> Imglist=new ArrayList<String>();
			if (temptb.getAmeterspic() != null&&!temptb.getAmeterspic().equals(""))
					Imglist.add(temptb.getAmeterspic());
			if (temptb.getTeterspic() != null&&!temptb.getTeterspic().equals("")) 
					Imglist.add(temptb.getTeterspic());
			if (temptb.getAllpic() != null&&!temptb.getAllpic().equals("")) 
					Imglist.add(temptb.getAllpic());
			if (temptb.getOLD_REACTIVE_PICTURES() != null&&!temptb.getOLD_REACTIVE_PICTURES().equals("")) 
				Imglist.add(temptb.getOLD_REACTIVE_PICTURES());
			if (temptb.getN_ameterspic() != null&&!temptb.getN_ameterspic().equals(""))
					Imglist.add(temptb.getN_ameterspic());
			if (temptb.getN_tmeterspic() != null && !temptb.getN_tmeterspic().equals(""))
					Imglist.add(temptb.getN_tmeterspic());
			if (temptb.getN_allpic() != null&& !temptb.getN_allpic().equals(""))
					Imglist.add(temptb.getN_allpic());
			if (temptb.getNEW_REACTIVE_PICTURES() != null&&!temptb.getNEW_REACTIVE_PICTURES().equals("")) 
				Imglist.add(temptb.getNEW_REACTIVE_PICTURES());
			
			if(temptb.getNCT_SINGLE_PICTURES()!=null&& !temptb.getNCT_SINGLE_PICTURES().equals(""))
				Imglist.add(temptb.getNCT_SINGLE_PICTURES());
			if(temptb.getNCT_DOUBLE_PICTURES()!=null&& !temptb.getNCT_DOUBLE_PICTURES().equals(""))
				Imglist.add(temptb.getNCT_DOUBLE_PICTURES());
			if(temptb.getNCT_WHOLE_PICTURES()!=null&& !temptb.getNCT_WHOLE_PICTURES().equals(""))
				Imglist.add(temptb.getNCT_WHOLE_PICTURES());
			if(temptb.getNEW_ROUND_CT()!=null&& !temptb.getNEW_ROUND_CT().equals(""))
				Imglist.add(temptb.getNEW_ROUND_CT());
			
			if(temptb.getOCT_SINGLE_PICTURES()!=null&& !temptb.getOCT_SINGLE_PICTURES().equals(""))
				Imglist.add(temptb.getOCT_SINGLE_PICTURES());
			if(temptb.getOCT_DOUBLE_PICTURES()!=null&& !temptb.getOCT_DOUBLE_PICTURES().equals(""))
				Imglist.add(temptb.getOCT_DOUBLE_PICTURES());
			if(temptb.getOCT_WHOLE_PICTURES()!=null&& !temptb.getOCT_WHOLE_PICTURES().equals(""))
				Imglist.add(temptb.getOCT_WHOLE_PICTURES());
			if(temptb.getROUND_CT()!=null&& !temptb.getROUND_CT().equals(""))
				Imglist.add(temptb.getROUND_CT());
				
			deleteImg(Imglist);
			helper.delete("RecordData",
					"username = ? and usernumber = ? and assetsnumber=?",
					new String[] { temptb.getUserName(), temptb.getUserId(),
							temptb.getAssetsId() });
		}
	}
	
	private Boolean deleteImg(List<String> Imglist)
	{
		try {
			for(int i=0;i<Imglist.size();i++)
			{
				if (!Imglist.get(i).equals("")) {
					String mFilePath5 = Environment
							.getExternalStorageDirectory().getPath()
							+ "/Photo/" + Imglist.get(i);
					File file5 = new File(mFilePath5);
					file5.delete();
				}
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@SuppressLint("SimpleDateFormat")
	private String getNowDateTime(String strFormat) {
		if (strFormat.equals("")) {
			strFormat = "yyyy-MM-dd HH:mm:ss";
		}
		Date now = new Date();
		SimpleDateFormat df = new SimpleDateFormat(strFormat);
		return df.format(now);
	}

	private void selectData() {
		DBHelper helper = new DBHelper(this);
		try {
			String st = "0";
			Cursor cursor = helper.query("RecordData", null, "static = ?",
					new String[] { st }, null, null, null);
			if (cursor != null) {
				mlistInfo.clear();
				while (cursor.moveToNext()) {
					TableData data = new TableData();
					data.setUserName(cursor.getString(cursor
							.getColumnIndex("username")));
					data.setUserId(cursor.getString(cursor
							.getColumnIndex("usernumber")));
					data.setAssetsId(cursor.getString(cursor
							.getColumnIndex("assetsnumber")));
					data.setUserAddress(cursor.getString(cursor
							.getColumnIndex("useraddress")));
					data.setEquipmentClass(cursor.getString(cursor
							.getColumnIndex("equipmentclass")));
					data.setReaddata(cursor.getString(cursor
							.getColumnIndex("readdata")));
					data.setMdReaddata(cursor.getString(cursor
							.getColumnIndex("wreaddata")));
					data.setAmeterspic(cursor.getString(cursor
							.getColumnIndex("ameterspic")));
					data.setTeterspic(cursor.getString(cursor
							.getColumnIndex("tmeterspic")));
					data.setAllpic(cursor.getString(cursor
							.getColumnIndex("allpic")));
					data.setN_assetsnumber(cursor.getString(cursor
							.getColumnIndex("n_assetsnumber")));
					data.setN_ameterspic(cursor.getString(cursor
							.getColumnIndex("n_ameterspic")));
					data.setN_tmeterspic(cursor.getString(cursor
							.getColumnIndex("n_tmeterspic")));
					data.setN_allpic(cursor.getString(cursor
							.getColumnIndex("n_allpic")));
					data.setN_readdata(cursor.getString(cursor
							.getColumnIndex("n_readdata")));
					data.setN_wreaddata(cursor.getString(cursor
							.getColumnIndex("n_wreaddata")));
					data.setSealId1(cursor.getString(cursor
							.getColumnIndex("seal1")));
					data.setSealId2(cursor.getString(cursor
							.getColumnIndex("seal2")));
					data.setMdtime(cursor.getString(cursor
							.getColumnIndex("dtime")));
					data.setMitime(cursor.getString(cursor
							.getColumnIndex("itime")));
					data.setDstatic(cursor.getString(cursor
							.getColumnIndex("dstatic")));
					data.setIstatic(cursor.getString(cursor
							.getColumnIndex("istatic")));
					data.setStatic(cursor.getString(cursor
							.getColumnIndex("static")));
					mlistInfo.add(data);
				}
				cursor.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (mlistInfo.size() < 1) {
			doingnow.setText("暂无数据需要提交");
			return;
		} else
			doingnow.setText("部分数据需要提交");
	}

	private void uploaddata() {
		rel_upload.setClickable(false);
		img_upload.setClickable(false);
		txt_upload.setClickable(false);
		submiting = true;
		DBHelper helper = new DBHelper(this);
		try {
			String st = "0";
			Cursor cursor = null;
			Cursor boxcursor = null;
			if (submitdata == 0) {
				cursor = helper.query("RecordData", null, "static = ?",
						new String[] { st }, null, null, null);
				boxcursor = helper.query("TabBoxData", null, null, null, null,
						null, null);
			} else {
				cursor = helper.query("RecordData", null,
						"static = ? and dstatic=? and istatic=?", new String[] {
								st, "1", "1" }, null, null, null);
				boxcursor = helper.query("TabBoxData", null, null, null, null,
						null, null);
			}
			if (boxcursor != null) {
				boxlistInfo.clear();
				while (boxcursor.moveToNext()) {
					TableBox data = new TableBox();
					data.setTABLE_CABINET_NO(boxcursor.getString(boxcursor
							.getColumnIndex("TABLE_CABINET_NO")));
					data.setPICTURES(boxcursor.getString(boxcursor
							.getColumnIndex("PICTURES")));
					data.setPICTURES2(boxcursor.getString(boxcursor
							.getColumnIndex("PICTURES2")));
					data.setPICTURES3(boxcursor.getString(boxcursor
							.getColumnIndex("PICTURES3")));
					data.setMORE_PICTURES(boxcursor.getString(boxcursor
							.getColumnIndex("MORE_PICTURES")));
					data.setHLY_CREATE_DATE(boxcursor.getString(boxcursor
							.getColumnIndex("HLY_CREATE_DATE")));
					data.setSEAL_NUMBER(boxcursor.getString(boxcursor
							.getColumnIndex("SEAL_NUMBER")));
					data.setSEAL_NUMBER2(boxcursor.getString(boxcursor
							.getColumnIndex("SEAL_NUMBER2")));
					data.setSEAL_NUMBER3(boxcursor.getString(boxcursor
							.getColumnIndex("SEAL_NUMBER3")));
					data.setSEAL_NUMBER4(boxcursor.getString(boxcursor
							.getColumnIndex("SEAL_NUMBER4")));
					data.setSEAL_NUMBER5(boxcursor.getString(boxcursor
							.getColumnIndex("SEAL_NUMBER5")));
					data.setSEAL_NUMBER6(boxcursor.getString(boxcursor
							.getColumnIndex("SEAL_NUMBER6")));
					boxlistInfo.add(data);
				}
				boxcursor.close();
			}
			if (cursor != null) {
				mlistInfo.clear();
				while (cursor.moveToNext()) {
					TableData data = new TableData();
					data.setUserName(cursor.getString(cursor
							.getColumnIndex("username")));
					data.setUserId(cursor.getString(cursor
							.getColumnIndex("usernumber")));
					data.setAssetsId(cursor.getString(cursor
							.getColumnIndex("assetsnumber")));
					data.setUserAddress(cursor.getString(cursor
							.getColumnIndex("useraddress")));
					data.setEquipmentClass(cursor.getString(cursor
							.getColumnIndex("equipmentclass")));
					data.setBIN_POSITION_NUMBER(cursor.getString(cursor
							.getColumnIndex("BIN_POSITION_NUMBER")));
					data.setDATATAG(cursor.getString(cursor
							.getColumnIndex("TAG")));
					data.setReaddata(cursor.getString(cursor
							.getColumnIndex("readdata")));
					data.setMdReaddata(cursor.getString(cursor
							.getColumnIndex("wreaddata")));
					data.setAmeterspic(cursor.getString(cursor
							.getColumnIndex("ameterspic")));
					data.setTeterspic(cursor.getString(cursor
							.getColumnIndex("tmeterspic")));
					data.setAllpic(cursor.getString(cursor
							.getColumnIndex("allpic")));
					data.setOLD_MORE_PICTURES(cursor.getString(cursor
							.getColumnIndex("OLD_MORE_PICTURES")));
					data.setOLD_REACTIVE_PICTURES(cursor.getString(cursor
							.getColumnIndex("OLD_REACTIVE_PICTURES")));
					data.setN_assetsnumber(cursor.getString(cursor
							.getColumnIndex("n_assetsnumber")));
					data.setN_ameterspic(cursor.getString(cursor
							.getColumnIndex("n_ameterspic")));
					data.setN_tmeterspic(cursor.getString(cursor
							.getColumnIndex("n_tmeterspic")));
					data.setN_allpic(cursor.getString(cursor
							.getColumnIndex("n_allpic")));
					data.setNEW_MORE_PICTURES(cursor.getString(cursor
							.getColumnIndex("NEW_MORE_PICTURES")));
					data.setNEW_REACTIVE_PICTURES(cursor.getString(cursor
							.getColumnIndex("NEW_REACTIVE_PICTURES")));
					data.setOCT_SINGLE_PICTURES(cursor.getString(cursor
							.getColumnIndex("OCT_SINGLE_PICTURES")));
					data.setOCT_DOUBLE_PICTURES(cursor.getString(cursor
							.getColumnIndex("OCT_DOUBLE_PICTURES")));
					data.setOCT_WHOLE_PICTURES(cursor.getString(cursor
							.getColumnIndex("OCT_WHOLE_PICTURES")));
					data.setROUND_CT(cursor.getString(cursor
							.getColumnIndex("ROUND_CT")));
					data.setNCT_SINGLE_PICTURES(cursor.getString(cursor
							.getColumnIndex("NCT_SINGLE_PICTURES")));
					data.setNCT_DOUBLE_PICTURES(cursor.getString(cursor
							.getColumnIndex("NCT_DOUBLE_PICTURES")));
					data.setNCT_WHOLE_PICTURES(cursor.getString(cursor
							.getColumnIndex("NCT_WHOLE_PICTURES")));
					data.setNEW_ROUND_CT(cursor.getString(cursor
							.getColumnIndex("NEW_ROUND_CT")));
					data.setN_readdata(cursor.getString(cursor
							.getColumnIndex("n_readdata")));
					data.setN_wreaddata(cursor.getString(cursor
							.getColumnIndex("n_wreaddata")));
					data.setSealId1(cursor.getString(cursor
							.getColumnIndex("seal1")));
					data.setSealId2(cursor.getString(cursor
							.getColumnIndex("seal2")));
					data.setSealId3(cursor.getString(cursor
							.getColumnIndex("seal3")));
					data.setSealId4(cursor.getString(cursor
							.getColumnIndex("seal4")));
					data.setSealId5(cursor.getString(cursor
							.getColumnIndex("seal5")));
					data.setSealId6(cursor.getString(cursor
							.getColumnIndex("seal6")));
					data.setSealId7(cursor.getString(cursor
							.getColumnIndex("seal7")));
					data.setSealId8(cursor.getString(cursor
							.getColumnIndex("seal8")));
					data.setSealId9(cursor.getString(cursor
							.getColumnIndex("seal9")));
					data.setMdtime(cursor.getString(cursor
							.getColumnIndex("dtime")));
					data.setMitime(cursor.getString(cursor
							.getColumnIndex("itime")));
					data.setDstatic(cursor.getString(cursor
							.getColumnIndex("dstatic")));
					data.setIstatic(cursor.getString(cursor
							.getColumnIndex("istatic")));
					data.setStatic(cursor.getString(cursor
							.getColumnIndex("static")));
					data.setTABLE_CABINET_NO(cursor.getString(cursor
							.getColumnIndex("TABLE_CABINET_NO")));
					mlistInfo.add(data);
				}
				cursor.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (mlistInfo.size() < 1 && boxlistInfo.size() < 1) {
			Toast.makeText(SetingActivity.this, "所以数据均已提交！", Toast.LENGTH_LONG).show();
			submiting = false;
			return;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				mNotificationManager.notify(0, mNotification);
				Message mydoingnow = new Message();
				mydoingnow.what = MYMESSAGE;
				mydoingnow.obj = "正在提交数据……";
				handler.sendMessage(mydoingnow);
				for (int i = 0; i < boxlistInfo.size(); i++) {
					Message progrom = new Message();
					progrom.what = 10;
					progrom.obj = "正在提交表箱数据：" + i + "/" + boxlistInfo.size();
					handler.sendMessage(progrom);
					TableBox tempdata = boxlistInfo.get(i);
					final Map<String, String> params = new HashMap<String, String>();
					String TABLE_CABINET_NO = tempdata.getTABLE_CABINET_NO();
					if (tempdata.getTABLE_CABINET_NO() == null)
						TABLE_CABINET_NO = "";
					params.put("TABLE_CABINET_NO", TABLE_CABINET_NO);
					String PICTURES = tempdata.getPICTURES();
					if (tempdata.getPICTURES() == null)
						PICTURES = "";
					params.put("PICTURES", PICTURES);
					String PICTURES2 = tempdata.getPICTURES2();
					if (tempdata.getPICTURES2() == null)
						PICTURES2 = "";
					params.put("PICTURES2", PICTURES2);
					String PICTURES3 = tempdata.getPICTURES3();
					if (tempdata.getPICTURES3() == null)
						PICTURES3 = "";
					params.put("PICTURES3", PICTURES3);
					String MORE_PICTURES = tempdata.getMORE_PICTURES();
					if (tempdata.getMORE_PICTURES() == null)
						MORE_PICTURES = "";
					params.put("MORE_PICTURES", MORE_PICTURES);
					String HLY_CREATE_DATE = tempdata.getHLY_CREATE_DATE();
					if (tempdata.getHLY_CREATE_DATE() == null)
						HLY_CREATE_DATE = "";
					params.put("HLY_CREATE_DATE", HLY_CREATE_DATE);
					String SEAL_NUMBER = tempdata.getSEAL_NUMBER();
					if (tempdata.getSEAL_NUMBER() == null)
						SEAL_NUMBER = "";
					params.put("SEAL_NUMBER", SEAL_NUMBER);
					String SEAL_NUMBER2 = tempdata.getSEAL_NUMBER2();
					if (tempdata.getSEAL_NUMBER2() == null)
						SEAL_NUMBER2 = "";
					params.put("SEAL_NUMBER2", SEAL_NUMBER2);
					String SEAL_NUMBER3 = tempdata.getSEAL_NUMBER3();
					if (tempdata.getSEAL_NUMBER3() == null)
						SEAL_NUMBER3 = "";
					params.put("SEAL_NUMBER3", SEAL_NUMBER3);
					String SEAL_NUMBER4 = tempdata.getSEAL_NUMBER4();
					if (tempdata.getSEAL_NUMBER4() == null)
						SEAL_NUMBER4 = "";
					params.put("SEAL_NUMBER4", SEAL_NUMBER4);
					String SEAL_NUMBER5 = tempdata.getSEAL_NUMBER5();
					if (tempdata.getSEAL_NUMBER5() == null)
						SEAL_NUMBER5 = "";
					params.put("SEAL_NUMBER5", SEAL_NUMBER5);
					String SEAL_NUMBER6 = tempdata.getSEAL_NUMBER6();
					if (tempdata.getSEAL_NUMBER6() == null)
						SEAL_NUMBER6 = "";
					params.put("SEAL_NUMBER6", SEAL_NUMBER6);
					String HLY_CREATE_MAN = userName;
					params.put("HLY_CREATE_MAN", HLY_CREATE_MAN);
					params.put("HLY_CREATE_MAN_ID", userID);

					String request = "";

					if (tempdata.getPICTURES() != null) {
						if (!tempdata.getPICTURES().equals("")) {
							Boolean success = submitImgfunction(tempdata.getPICTURES());
							if (!success)
								insertfailImg(tempdata.getPICTURES());
						}
					}
					if (tempdata.getPICTURES2() != null) {
						if (!tempdata.getPICTURES2().equals("")) {
							Boolean success = submitImgfunction(tempdata.getPICTURES2());
							if (!success)
								insertfailImg(tempdata.getPICTURES2());
						}
					}
					if (tempdata.getPICTURES3() != null) {
						if (!tempdata.getPICTURES3().equals("")) {
							Boolean success = submitImgfunction(tempdata.getPICTURES3());
							if (!success)
								insertfailImg(tempdata.getPICTURES3());
						}
					}
					//提交表箱其他图片
					if (tempdata.getMORE_PICTURES() != null
							&& !tempdata.getMORE_PICTURES().equals("")) {
						String[] arr=tempdata.getMORE_PICTURES().split(",");
						for(int l=0;l<arr.length;l++)
						{
							Boolean success = submitImgfunction(arr[l]);
							if (!success)
								insertfailImg(arr[l]);
						}
					}
					
					final Map<String, File> files = new HashMap<String, File>();
					try {
						request = UploadUtil.post(SysConfig.BOXDATAPOSTURL,params, files);
						JSONObject info = null;
						try {
							info = new JSONObject(request);
						} catch (JSONException e) {
							e.printStackTrace();
							continue;
						}
						if (info != null) {
							try {
								String resu = info.getString("success");
								if (resu.equals("true")) {
									Message msg = new Message();
									msg.what = success;
									msg.obj = tempdata;
									handler.sendMessage(msg);
								} else {
									Message err = new Message();
									err.what = POSTVALUE;
									err.obj = "提交记录失败！";//info.getString("errorMsg");
									handler.sendMessage(err);
								}
							} catch (JSONException e) {
								e.printStackTrace();
								continue;
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
						Message msg = new Message();
						msg.what = POSTVALUE;
						msg.obj = "当前数据提交异常！";//e.toString();
						handler.sendMessage(msg);
						continue;
					}
				}

				for (int i = 0; i < mlistInfo.size(); i++) {
					Message progrom = new Message();
					progrom.what = 10;
					progrom.obj = "正在提交表数据：" + i + "/" + mlistInfo.size();
					handler.sendMessage(progrom);
					TableData temptb = mlistInfo.get(i);
					final Map<String, String> params = new HashMap<String, String>();
					if (temptb.getUserName() == null) {
					}
					//params.put("USER_NAME", myusername);
					String usernumber = temptb.getUserId();
					if (temptb.getUserId() == null) {
						usernumber = "";
						continue;
					}
					params.put("USER_NO", usernumber);
					String assetsnumber = temptb.getAssetsId();
					if (temptb.getAssetsId() == null)
						assetsnumber = "";
					params.put("ASSET_TAG", assetsnumber);
					String readdata = temptb.getReaddata();
					String wreaddata = temptb.getMdReaddata();
					if (temptb.getReaddata() == null)
						readdata = "";
					if (temptb.getMdReaddata() == null)
						wreaddata = "";
					params.put("TOTAL_ACTIVE_POWER", readdata);
					params.put("TOTAL_POSITIVE_REACTIVE_POWER", wreaddata);
					params.put("PRINCIPAL", userName);
					params.put("PRINCIPAL_ID", userID);
					String ameterspic = temptb.getAmeterspic();
					if (temptb.getAmeterspic() == null)
						ameterspic = "";
					params.put("OLD_SINGLE_PICTURES", ameterspic);
					String tmeterspic = temptb.getTeterspic();
					if (temptb.getTeterspic() == null)
						tmeterspic = "";
					params.put("OLD_DOUBLE_PICTURES", tmeterspic);
					String allpic = temptb.getAllpic();
					if (temptb.getAllpic() == null)
						allpic = "";
					params.put("OLD_WHOLE_PICTURES", allpic);
					String OLD_MORE_PICTURES = temptb.getOLD_MORE_PICTURES();
					if (temptb.getOLD_MORE_PICTURES() == null)
						OLD_MORE_PICTURES = "";
					params.put("OLD_MORE_PICTURES", OLD_MORE_PICTURES);
					String OLD_REACTIVE_PICTURES = temptb.getOLD_REACTIVE_PICTURES();
					if (temptb.getOLD_REACTIVE_PICTURES() == null)
						OLD_REACTIVE_PICTURES = "";
					params.put("OLD_REACTIVE_PICTURES", OLD_REACTIVE_PICTURES);
					
					String n_assetsnumber = temptb.getN_assetsnumber();
					if (temptb.getN_assetsnumber() == null)
						n_assetsnumber = "";
					params.put("NEW_ASSET_TAG", n_assetsnumber);
					String n_ameterspic = temptb.getN_ameterspic();
					if (temptb.getN_ameterspic() == null)
						n_ameterspic = "";
					params.put("NEW_SINGLE_PICTURES", n_ameterspic);
					String n_tmeterspic = temptb.getN_tmeterspic();
					if (temptb.getN_tmeterspic() == null)
						n_tmeterspic = "";
					params.put("NEW_DOUBLE_PICTURES", n_tmeterspic);
					String n_allpic = temptb.getN_allpic();
					if (temptb.getN_allpic() == null)
						n_allpic = "";
					params.put("NEW_WHOLE_PICTURES", n_allpic);
					String NEW_MORE_PICTURES = temptb.getNEW_MORE_PICTURES();
					if (temptb.getNEW_MORE_PICTURES() == null)
						NEW_MORE_PICTURES = "";
					params.put("NEW_MORE_PICTURES", NEW_MORE_PICTURES);
					String NEW_REACTIVE_PICTURES = temptb.getNEW_REACTIVE_PICTURES();
					if (temptb.getNEW_REACTIVE_PICTURES() == null)
						NEW_REACTIVE_PICTURES = "";
					params.put("NEW_REACTIVE_PICTURES", NEW_REACTIVE_PICTURES);
					
					String BIN_POSITION_NUMBER=temptb.getBIN_POSITION_NUMBER();
					if (temptb.getBIN_POSITION_NUMBER() == null)
						BIN_POSITION_NUMBER = "1";
					params.put("BIN_POSITION_NUMBER", BIN_POSITION_NUMBER);
					String LOADING_CAUSE=temptb.getDATATAG();
					if (temptb.getDATATAG() == null)
						LOADING_CAUSE = "";
					params.put("LOADING_CAUSE", LOADING_CAUSE);
					
					String n_readdata = temptb.getN_readdata();
					if (temptb.getN_readdata() == null)
						n_readdata = "";
					params.put("NEW_TOTAL_ACTIVE_POWER", n_readdata);
					String n_wreaddata = temptb.getN_wreaddata();
					if (temptb.getN_wreaddata() == null)
						n_wreaddata = "";
					params.put("NEW_TOTAL_POSITIVE_REACTIVE", n_wreaddata);
					String seal1 = temptb.getSealId1();
					if (temptb.getSealId1() == null)
						seal1 = "";
					params.put("SEAL_NUMBER", seal1);
					String seal2 = temptb.getSealId2();
					if (temptb.getSealId2() == null)
						seal2 = "";
					params.put("SEAL_NUMBER2", seal2);
					
					String seal3 = temptb.getSealId3();
					if (temptb.getSealId3() == null)
						seal3 = "";
					params.put("SEAL_NUMBER3", seal3);
					
					String seal4 = temptb.getSealId4();
					if (temptb.getSealId4() == null)
						seal4 = "";
					params.put("SEAL_NUMBER4", seal4);
					String seal5 = temptb.getSealId5();
					if (temptb.getSealId5() == null)
						seal5 = "";
					params.put("SEAL_NUMBER5", seal5);
					
					String seal6 = temptb.getSealId6();
					if (temptb.getSealId6() == null)
						seal6 = "";
					params.put("SEAL_NUMBER6", seal6);
					String seal7 = temptb.getSealId7();
					if (temptb.getSealId7() == null)
						seal7 = "";
					params.put("SEAL_NUMBER7", seal7);
					String seal8 = temptb.getSealId8();
					if (temptb.getSealId8() == null)
						seal8 = "";
					params.put("SEAL_NUMBER8", seal8);
					String seal9 = temptb.getSealId9();
					if (temptb.getSealId9() == null)
						seal9 = "";
					params.put("SEAL_NUMBER9", seal9);
					
					String dstatic = temptb.getDstatic();
					if (temptb.getDstatic() == null)
						dstatic = "";

					String UNLOADING_DATE = temptb.getMitime();
					if (temptb.getMitime() == null)
						UNLOADING_DATE = getNowDateTime("");
					else
						UNLOADING_DATE = UNLOADING_DATE + " 00:00:00";
					params.put("UNLOADING_DATE", UNLOADING_DATE);
					String LOADING_DATE = temptb.getMdtime();
					if (temptb.getMdtime() == null)
						LOADING_DATE = getNowDateTime("");
					else
						LOADING_DATE = LOADING_DATE + " 00:00:00";
					params.put("LOADING_DATE", LOADING_DATE);

					String TABLE_CABINET_NO = temptb.getTABLE_CABINET_NO();
					if (temptb.getTABLE_CABINET_NO() == null)
						TABLE_CABINET_NO = "";
					params.put("TABLE_CABINET_NO", TABLE_CABINET_NO);

					params.put("dstatic", dstatic);
					String istatic = temptb.getIstatic();
					if (temptb.getIstatic() == null)
						dstatic = "";
					params.put("istatic", istatic);
					if (dstatic.equals("1") && dstatic.equals("1")) {
						params.put("MARK", "已装拆");
					} else if (dstatic.equals("1") && !dstatic.equals("1")) {
						params.put("MARK", "已拆未装");
					} else
						params.put("MARK", "未装拆");
					String statics = temptb.getStatic();
					if (temptb.getStatic() == null)
						statics = "";
					params.put("static", statics);

					String NCT_SINGLE_PICTURES = temptb
							.getNCT_SINGLE_PICTURES();
					if (temptb.getNCT_SINGLE_PICTURES() == null)
						NCT_SINGLE_PICTURES = "";
					params.put("NCT_SINGLE_PICTURES", NCT_SINGLE_PICTURES);
					String NCT_DOUBLE_PICTURES = temptb
							.getNCT_DOUBLE_PICTURES();
					if (temptb.getNCT_DOUBLE_PICTURES() == null)
						NCT_DOUBLE_PICTURES = "";
					params.put("NCT_DOUBLE_PICTURES", NCT_DOUBLE_PICTURES);
					String NCT_WHOLE_PICTURES = temptb.getNCT_WHOLE_PICTURES();
					if (temptb.getNCT_WHOLE_PICTURES() == null)
						NCT_WHOLE_PICTURES = "";
					params.put("NCT_WHOLE_PICTURES", NCT_WHOLE_PICTURES);
					String ROUND_CT = temptb.getROUND_CT();
					if (temptb.getROUND_CT() == null)
						ROUND_CT = "";
					params.put("ROUND_CT", ROUND_CT);
					String OCT_SINGLE_PICTURES = temptb
							.getOCT_SINGLE_PICTURES();
					if (temptb.getOCT_SINGLE_PICTURES() == null)
						OCT_SINGLE_PICTURES = "";
					params.put("OCT_SINGLE_PICTURES", OCT_SINGLE_PICTURES);
					String OCT_DOUBLE_PICTURES = temptb
							.getOCT_DOUBLE_PICTURES();
					if (temptb.getOCT_DOUBLE_PICTURES() == null)
						OCT_DOUBLE_PICTURES = "";
					params.put("OCT_DOUBLE_PICTURES", OCT_DOUBLE_PICTURES);
					String OCT_WHOLE_PICTURES = temptb.getOCT_WHOLE_PICTURES();
					if (temptb.getOCT_WHOLE_PICTURES() == null)
						OCT_WHOLE_PICTURES = "";
					params.put("OCT_WHOLE_PICTURES", OCT_WHOLE_PICTURES);
					String NEW_ROUND_CT = temptb.getNEW_ROUND_CT();
					if (temptb.getNEW_ROUND_CT() == null)
						NEW_ROUND_CT = "";
					params.put("NEW_ROUND_CT", NEW_ROUND_CT);

					String request;
					
					// 提交图片
					if (temptb.getAmeterspic() != null
							&& !temptb.getAmeterspic().equals("")) {
						Boolean success = submitImgfunction(temptb.getAmeterspic());
						if (!success)
							insertfailImg(temptb.getAmeterspic());
					}
					if (temptb.getTeterspic() != null
							&& !temptb.getTeterspic().equals("")) {
						Boolean success = submitImgfunction(temptb.getTeterspic());
						if (!success)
							insertfailImg(temptb.getTeterspic());
					}
					if (temptb.getAllpic() != null
							&& !temptb.getAllpic().equals("")) {
						Boolean success = submitImgfunction(temptb.getAllpic());
						if (!success)
							insertfailImg(temptb.getAllpic());
					}
					if (temptb.getOLD_REACTIVE_PICTURES() != null
							&& !temptb.getOLD_REACTIVE_PICTURES().equals("")) {
						Boolean success = submitImgfunction(temptb.getOLD_REACTIVE_PICTURES());
						if (!success)
							insertfailImg(temptb.getOLD_REACTIVE_PICTURES());
					}
					//提交旧表其他图片
					if (temptb.getOLD_MORE_PICTURES() != null
							&& !temptb.getOLD_MORE_PICTURES().equals("")) {
						String[] arr=temptb.getOLD_MORE_PICTURES().split(",");
						for(int l=0;l<arr.length;l++)
						{
							Boolean success = submitImgfunction(arr[l]);
							if (!success)
								insertfailImg(arr[l]);
						}
					}
					if (temptb.getN_ameterspic() != null
							&& !temptb.getN_ameterspic().equals("")) {
						Boolean success = submitImgfunction(temptb.getN_ameterspic());
						if (!success)
							insertfailImg(temptb.getN_ameterspic());
					}
					if (temptb.getN_tmeterspic() != null
							&& !temptb.getN_tmeterspic().equals("")) {
						Boolean success = submitImgfunction(temptb.getN_tmeterspic());
						if (!success)
							insertfailImg(temptb.getN_tmeterspic());
					}
					if (temptb.getN_allpic() != null
							&& !temptb.getN_allpic().equals("")) {
						Boolean success = submitImgfunction(temptb.getN_allpic());
						if (!success)
							insertfailImg(temptb.getN_allpic());
					}
					if (temptb.getNEW_REACTIVE_PICTURES() != null
							&& !temptb.getNEW_REACTIVE_PICTURES().equals("")) {
						Boolean success = submitImgfunction(temptb.getNEW_REACTIVE_PICTURES());
						if (!success)
							insertfailImg(temptb.getNEW_REACTIVE_PICTURES());
					}
					//提交新表其他图片
					if (temptb.getNEW_MORE_PICTURES() != null
							&& !temptb.getNEW_MORE_PICTURES().equals("")) {
						String[] arr=temptb.getNEW_MORE_PICTURES().split(",");
						for(int l=0;l<arr.length;l++)
						{
							Boolean success = submitImgfunction(arr[l]);
							if (!success)
								insertfailImg(arr[l]);
						}
					}
					// 提交CT图片
					if (temptb.getOCT_SINGLE_PICTURES() != null
							&& !temptb.getOCT_SINGLE_PICTURES().equals("")&& !temptb.getOCT_SINGLE_PICTURES().equals("CTA相")) {
						Boolean success = submitImgfunction(temptb.getOCT_SINGLE_PICTURES());
						if (!success)
							insertfailImg(temptb.getOCT_SINGLE_PICTURES());
					}
					if (temptb.getOCT_DOUBLE_PICTURES() != null
							&& !temptb.getOCT_DOUBLE_PICTURES().equals("")&& !temptb.getOCT_SINGLE_PICTURES().equals("CTB相")) {
						Boolean success = submitImgfunction(temptb.getOCT_DOUBLE_PICTURES());
						if (!success)
							insertfailImg(temptb.getOCT_DOUBLE_PICTURES());
					}
					if (temptb.getOCT_WHOLE_PICTURES() != null
							&& !temptb.getOCT_WHOLE_PICTURES().equals("")&& !temptb.getOCT_SINGLE_PICTURES().equals("CTC相")) {
						Boolean success = submitImgfunction(temptb.getOCT_WHOLE_PICTURES());
						if (!success)
							insertfailImg(temptb.getOCT_WHOLE_PICTURES());
					}
					if (temptb.getROUND_CT() != null
							&& !temptb.getROUND_CT().equals("")&& !temptb.getOCT_SINGLE_PICTURES().equals("CT合相")) {
						Boolean success = submitImgfunction(temptb.getROUND_CT());
						if (!success)
							insertfailImg(temptb.getROUND_CT());
					}
					if (temptb.getNCT_SINGLE_PICTURES() != null
							&& !temptb.getNCT_SINGLE_PICTURES().equals("")&& !temptb.getOCT_SINGLE_PICTURES().equals("CTA相")) {
						Boolean success = submitImgfunction(temptb.getNCT_SINGLE_PICTURES());
						if (!success)
							insertfailImg(temptb.getNCT_SINGLE_PICTURES());
					}
					if (temptb.getNCT_DOUBLE_PICTURES() != null
							&& !temptb.getNCT_DOUBLE_PICTURES().equals("")&& !temptb.getOCT_SINGLE_PICTURES().equals("CTB相")) {
						Boolean success = submitImgfunction(temptb.getNCT_DOUBLE_PICTURES());
						if (!success)
							insertfailImg(temptb.getNCT_DOUBLE_PICTURES());
					}
					if (temptb.getNCT_WHOLE_PICTURES() != null
							&& !temptb.getNCT_WHOLE_PICTURES().equals("")&& !temptb.getOCT_SINGLE_PICTURES().equals("CTC相")) {
						Boolean success = submitImgfunction(temptb.getNCT_WHOLE_PICTURES());
						if (!success)
							insertfailImg(temptb.getNCT_WHOLE_PICTURES());
					}
					if (temptb.getNEW_ROUND_CT() != null
							&& !temptb.getNEW_ROUND_CT().equals("")&& !temptb.getOCT_SINGLE_PICTURES().equals("CT合相")) {
						Boolean success = submitImgfunction(temptb.getNEW_ROUND_CT());
						if (!success)
							insertfailImg(temptb.getNEW_ROUND_CT());
					}					
					
					final Map<String, File> files = new HashMap<String, File>();
					try {
						request = UploadUtil.post(SysConfig.TABDATAPOSTURL,	params, files);
						JSONObject info = null;
						try {
							info = new JSONObject(request);
						} catch (JSONException e) {
							e.printStackTrace();
							continue;
						}
						if (info != null) {
							try {
								String resu = info.getString("success");
								if (resu.equals("true")) {
									Message msg = new Message();
									msg.what = succeed;
									msg.obj = temptb;
									handler.sendMessage(msg);
								} else {
									Message err = new Message();
									err.what = POSTVALUE;
									err.obj = "提交数据记录失败！";//info.getString("errorMsg");
									handler.sendMessage(err);
									continue;
								}
							} catch (JSONException e) {
								e.printStackTrace();
								continue;
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
						Message msg = new Message();
						msg.what = POSTVALUE;
						msg.obj = "单前数据提交异常！";//e.toString();
						handler.sendMessage(msg);
						continue;
					}
					
					
				}
				
				mNotificationManager.cancelAll();
				Message doednow = new Message();
				doednow.what = MYMESSAGE;
				doednow.obj = "提交数据完成";
				handler.sendMessage(doednow);
				rel_upload.setClickable(true);
				img_upload.setClickable(true);
				txt_upload.setClickable(true);
				submiting = false;
			}
		}).start();
	}

	private void uploadBPdata()
	{
		//USER_NO    ASSET_TAG     CONTENT  STATE  PROBLEM    HLY_CREATE_MAN  HLY_CREATE_MAN_ID
		rel_upload.setClickable(false);
		img_upload.setClickable(false);
		txt_upload.setClickable(false);
		submiting = true;
		mistakelistInfo=BLL.getMistakeRecord(SetingActivity.this,-1);
		if (mistakelistInfo.size() < 1 && mistakelistInfo.size() < 1) {
			Toast.makeText(SetingActivity.this, "无补拍数据需要提交！", Toast.LENGTH_LONG).show();
			submiting = false;
			return;
		}
		for(int l=mistakelistInfo.size()-1;l>=0;l--)
		{
			MistakeRecord temp=mistakelistInfo.get(l);
			if(temp.getPICDATA()==null||temp.getPICDATA().equals("")){
				mistakelistInfo.remove(l);
				continue;
			}
			if(temp.getSUBMITSTATIC()!=null&&temp.getSUBMITSTATIC().equals("1")){
				mistakelistInfo.remove(l);
				continue;
			}
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				mNotificationManager.notify(0, mNotification);
				Message mydoingnow = new Message();
				mydoingnow.what = MYMESSAGE;
				mydoingnow.obj = "正在提交补拍数据……";
				handler.sendMessage(mydoingnow);
				for (int i = 0; i < mistakelistInfo.size(); i++) {
					Message progrom = new Message();
					progrom.what = 10;
					progrom.obj = "正在提交补拍数据：" + i + "/" + mistakelistInfo.size();
					handler.sendMessage(progrom);
					String request = "";
					final Map<String, File> files = new HashMap<String, File>();
					MistakeRecord tempdata = mistakelistInfo.get(i);
					if (tempdata.getPICDATA() != null&& !tempdata.getPICDATA().equals("")) {
						String[] arr=tempdata.getPICDATA().split(",");
						for(int l=0;l<arr.length;l++)
						{
							Boolean success = submitImgfunction(arr[l]);
							if (!success)
							{
								insertfailImg(arr[l]);
								continue;
							}
						}
					}
					else
						continue;
					
					final Map<String, String> tbparams = new HashMap<String, String>();
					String USER_NO = tempdata.getUSER_NO();
					if (USER_NO == null||USER_NO.equals(""))
						continue;
					String ASSET_TAG = tempdata.getASSET_TAG();
					if (ASSET_TAG == null||ASSET_TAG.equals(""))
						continue;
					String bpstatic="已完成";
					String MisTakeStr=tempdata.getPROBLEM();
					String Picdata=tempdata.getPICDATA();
					String[] proNames=MisTakeStr.split(",");
					String[] picNames=Picdata.split(",");
					if(picNames.length>=proNames.length)
						bpstatic="已完成";
					else
						bpstatic="未完成";
					tbparams.put("USER_NO", USER_NO);
					tbparams.put("ASSET_TAG", ASSET_TAG);
					int count=0;
					for(int j=0;j<proNames.length;j++){
						if (j < picNames.length) {
							if (proNames[j].equals("旧单表") || proNames[j].equals("旧有功表")) {
								tbparams.put("OLD_SINGLE_PICTURES", picNames[j]);
								count=count+1;
							} else if (proNames[j].equals("旧无功表")) {
								tbparams.put("OLD_REACTIVE_PICTURES",picNames[j]);
								count=count+1;
							} else if (proNames[j].equals("旧双表")) {
								tbparams.put("OLD_DOUBLE_PICTURES", picNames[j]);
								count=count+1;
							} else if (proNames[j].equals("旧整体")) {
								tbparams.put("OLD_WHOLE_PICTURES", picNames[j]);
								count=count+1;
							} else if (proNames[j].equals("旧CTA相")) {
								tbparams.put("OCT_SINGLE_PICTURES", picNames[j]);
								count=count+1;
							} else if (proNames[j].equals("旧CTB相")) {
								tbparams.put("OCT_DOUBLE_PICTURES", picNames[j]);
								count=count+1;
							} else if (proNames[j].equals("旧CTC相")) {
								tbparams.put("OCT_WHOLE_PICTURES", picNames[j]);
								count=count+1;
							} else if (proNames[j].equals("旧CT合相")) {
								tbparams.put("ROUND_CT", picNames[j]);
								count=count+1;
							} else if (proNames[j].equals("新单表")|| proNames[j].equals("新有功表")) {
								tbparams.put("NEW_SINGLE_PICTURES", picNames[j]);
								count=count+1;
							} else if (proNames[j].equals("新无功表")) {
								tbparams.put("NEW_REACTIVE_PICTURES",picNames[j]);
								count=count+1;
							} else if (proNames[j].equals("新双表")) {
								tbparams.put("NEW_DOUBLE_PICTURES", picNames[j]);
								count=count+1;
							} else if (proNames[j].equals("新整体")) {
								tbparams.put("NEW_WHOLE_PICTURES", picNames[j]);
								count=count+1;
							} else if (proNames[j].equals("新CTA相")) {
								tbparams.put("NCT_SINGLE_PICTURES", picNames[j]);
								count=count+1;
							} else if (proNames[j].equals("新CTB相")) {
								tbparams.put("NCT_DOUBLE_PICTURES", picNames[j]);
								count=count+1;
							} else if (proNames[j].equals("新CTC相")) {
								tbparams.put("NCT_WHOLE_PICTURES", picNames[j]);
								count=count+1;
							} else if (proNames[j].equals("新CT合相")) {
								tbparams.put("NEW_ROUND_CT", picNames[j]);
								count=count+1;
							} 
//							else if (proNames[j].equals("表箱(中)")) {
//								tbparams.put("PICTURES", picNames[j]);
//							} else if (proNames[j].equals(("表箱(右)")) {
//								tbparams.put("PICTURES2", picNames[j]);
//							} else if (proNames[j].equals("表箱(其它)")) {
//								tbparams.put("PICTURES3", picNames[j]);
//							} 
							else {
							}
						}
					}
					if(count==0)
						continue;
					try {
						request = UploadUtil.post(SysConfig.TABDATAPOSTURL,	tbparams, files);
						JSONObject info = null;
						try {
							info = new JSONObject(request);
						} catch (JSONException e) {
							e.printStackTrace();
							continue;
						}
						if (info != null) {
							try {
								String resu = info.getString("success");
								if (!resu.equals("true")) {
									continue;
								}
							} catch (JSONException e) {
								continue;
							}
						}
					} catch (IOException e) {
						continue;
					}
					
					
					
					final Map<String, String> params = new HashMap<String, String>();
					params.put("USER_NO", USER_NO);
					params.put("ASSET_TAG", ASSET_TAG);
					params.put("STATE",bpstatic);
					try {
						request = UploadUtil.post(SysConfig.PROUPDATEDATAURL,params, files);
						JSONObject info = null;
						try {
							info = new JSONObject(request);
						} catch (JSONException e) {
							e.printStackTrace();
							continue;
						}
						if (info != null) {
							try {
								String resu = info.getString("success");
								if (resu.equals("true")) {
									Message msg = new Message();
									msg.what = SUBMIT_BPDATA_CODE;
									msg.obj = tempdata;
									handler.sendMessage(msg);
								} else {
									Message err = new Message();
									err.what = POSTVALUE;
									err.obj = "提交记录失败！";//info.getString("errorMsg");
									handler.sendMessage(err);
								}
							} catch (JSONException e) {
								e.printStackTrace();
								continue;
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
						Message msg = new Message();
						msg.what = POSTVALUE;
						msg.obj = "当前数据提交异常！";//e.toString();
						handler.sendMessage(msg);
						continue;
					}
					
					
				}
				
				mNotificationManager.cancelAll();
				Message doednow = new Message();
				doednow.what = MYMESSAGE;
				doednow.obj = "提交数据完成";
				handler.sendMessage(doednow);
				rel_upload.setClickable(true);
				img_upload.setClickable(true);
				txt_upload.setClickable(true);
				submiting = false;
			}
		}).start();
	}
	
	protected void insertfailImg(String failName) {
		ContentValues cv = new ContentValues();
		cv.put("PICTURENAME", failName);
		DBHelper helper = new DBHelper(this);
		try {
			Cursor cursor = helper.query("FailImg",
					new String[] { "_id,PICTURENAME" }, "PICTURENAME = ?",
					new String[] { failName }, null, null, null);
			if (cursor != null) {
				if (cursor.moveToFirst() == false) {
					helper.insert(cv, "FailImg");
				}
			}
			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Boolean submitImgfunction(String filename) {
		try {
			String mFilePath0 = Environment.getExternalStorageDirectory().getPath() + "/Photo/" + filename;
			File file0 = new File(mFilePath0);
			if(file0.exists())
			{
				return submitImgbpoint(filename,true);
			}
			else
				return false;
//			request = UploadUtil.postImge(
//					"http://112.74.72.137:8282/ReceiveServices.ashx", file0);
//			if (request.equals("succeed"))
//				return true;
//			else
//				return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private Boolean submitImgbpoint(String filename,Boolean isCommit) throws IOException {
		try {
			String mFilePath0 = Environment.getExternalStorageDirectory()
					.getPath() + "/Photo/" + filename;
			File file0 = new File(mFilePath0);
			long offset=0;
			long filelong=file0.length();
			int L=(int)(filelong/(1024*50))+1;
			for(int i=0;i<L;i++)
			{
				offset=1024*50*i;
				long uploadsize=1024*50;
				if((offset+uploadsize)>filelong)
					uploadsize=filelong-offset;
				if(offset>0)
					isCommit=false;
				long result = UploadUtil.dpointpostImge(filename,SysConfig.IMGPOSTURL, file0,isCommit,offset,uploadsize);
				while (result!=-1) {
					isCommit=false;
					if(result==0)
						isCommit=true;
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					result = UploadUtil.dpointpostImge(filename,SysConfig.IMGPOSTURL, file0,isCommit,result,uploadsize);
				}
			}
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
		
	}
}
