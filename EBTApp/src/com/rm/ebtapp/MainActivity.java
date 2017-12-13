package com.rm.ebtapp;

import java.util.ArrayList;
import java.util.List;

import com.rm.db.DBHelper;
import com.rm.model.TableBox;
import com.rm.model.TableData;
import com.rm.serverces.EBTServerces;
import com.rm.until.AnalysisResult;
import com.rm.until.Common;
import com.rm.until.ListViewAdapter;
import com.rm.until.PopupList;
import com.rm.until.TableAdapter;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {
	private String TAG = "MainActivity";
	public SQLiteDatabase db;
	private final DBHelper helper = new DBHelper(this);
	private final static int SCANNIN_GREQUEST_CODE = 1;
	private final static int Disassem_GREQUEST_CODE = 2;
	private final static int USERLOGIN_GREQUEST_CODE = 3;
	private final static int TABLEBOX_GREQUEST_CODE = 4;
	private SharedPreferences mPreferences;
	private float mLastX;
	private float mLastY;
	private Button left_but, right_but;
	private TextView zcount, txtwwc, txtywc, txtgj, title;
	private EditText edtxt;
	private ImageView imgbutton, addrecord, userlogin;
	private ListView listView, tableListView;
	private List<TableData> mlistInfo = new ArrayList<TableData>();
	private List<TableData> mlistTemp = new ArrayList<TableData>();
	private List<TableBox> tablist;
	private List<String> popupMenuItemList = new ArrayList<String>();
	private ListViewAdapter adapter;
	private TableAdapter tableadapter;
	private LinearLayout reltitle;
	private ViewPager viewPager;
	private List<View> views = new ArrayList<View>();
	private PagerAdapter pageadapter;
	private TableBox currentbox = null;
	private String currentboxno = "";
	private Boolean clickin = false;
	private String currentpage = "boxpage";
	private String currentdata = "wwc";
	private String userName = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_main);

		db = helper.getWritableDatabase();

		ImageView mButton = (ImageView) findViewById(R.id.testbutton);
		mButton.setOnClickListener(this);
		title = (TextView) findViewById(R.id.textview_title);
		reltitle = (LinearLayout) findViewById(R.id.button_title);
		addrecord = (ImageView) findViewById(R.id.addtool);
		addrecord.setOnClickListener(this);
		left_but = (Button) findViewById(R.id.left_btn);
		left_but.setOnClickListener(this);
		right_but = (Button) findViewById(R.id.right_btn);
		right_but.setOnClickListener(this);
		imgbutton = (ImageView) findViewById(R.id.img_seting);
		imgbutton.setVisibility(View.VISIBLE);
		imgbutton.setOnClickListener(this);
		zcount = (TextView) findViewById(R.id.zcount);

		userlogin = (ImageView) findViewById(R.id.img_user);
		userlogin.setOnClickListener(this);

		edtxt = (EditText) findViewById(R.id.edtxt);
		edtxt.setInputType(EditorInfo.TYPE_CLASS_PHONE);
		edtxt.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				Log.d(TAG, "afterTextChanged");
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				Log.d(TAG, "beforeTextChanged:" + s + "-" + start + "-" + count
						+ "-" + after);
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				Log.d(TAG, "onTextChanged:" + s + "-" + "-" + start + "-"
						+ before + "-" + count);
				mlistTemp.clear();
				for (int i = 0; i < mlistInfo.size(); i++) {
					TableData td = mlistInfo.get(i);
					if (td.getUserId().contains(s))
						mlistTemp.add(td);
				}
				adapter = new ListViewAdapter(MainActivity.this, mlistTemp);
				listView.setAdapter(adapter);
				adapter.notifyDataSetChanged();
			}
		});
		InitView();

		mPreferences = getSharedPreferences("APPSETTING", Context.MODE_PRIVATE);

		Statistics();
		
		if(!Common.isServiceWork(MainActivity.this,"com.rm.serverces.EBTServerces"))
    	{
    		Intent service = new Intent(MainActivity.this,EBTServerces.class);
    		service.setAction("com.rm.serverces.ACTION_EBTSERVICE");
    		MainActivity.this.startService(service);
    	} 
	}

	private void Statistics() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				List<TableBox> boxlistInfo = new ArrayList<TableBox>();
				DBHelper helper = new DBHelper(MainActivity.this);
				try {
					Cursor cursor = helper.query("TabBoxData", null, null,
							null, null, null, null);
					if (cursor != null) {
						boxlistInfo.clear();
						while (cursor.moveToNext()) {
							TableBox data = new TableBox();
							data.setTABLE_CABINET_NO(cursor.getString(cursor
									.getColumnIndex("TABLE_CABINET_NO")));
							data.setPICTURES(cursor.getString(cursor
									.getColumnIndex("DATACOUNT")));
							boxlistInfo.add(data);
						}
					}
					cursor.close();
					for (int i = 0; i < boxlistInfo.size(); i++) {
						String boxid = boxlistInfo.get(i).getTABLE_CABINET_NO();
						String fz = quertyCount(true,boxid);
						String fm = quertyCount(null,boxid);
						ContentValues upd = new ContentValues();
						upd.put("DATACOUNT", fz + "/" + fm);
						helper.update("TabBoxData", upd,
								"TABLE_CABINET_NO = ?",
								new String[] { boxid });
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}).start();
	}

	private void reload() {
		tablist = getBoxsData();
		tableadapter = new TableAdapter(MainActivity.this, tablist);
		tableListView.invalidate();
		tableListView.setAdapter(tableadapter);
		if (tablist.size() - 2 > -1)
			tableListView.setSelection(tablist.size() - 2);
		tableadapter.notifyDataSetChanged();

		Statistics();
	}

	@SuppressWarnings("deprecation")
	private void InitView() {
		viewPager = (ViewPager) findViewById(R.id.viewFlipper);
		LayoutInflater inflater = (LayoutInflater) MainActivity.this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View tableview = inflater.inflate(R.layout.table_view, null);
		View boxview = inflater.inflate(R.layout.box_view, null);
		views.add(boxview);
		views.add(tableview);
		pageadapter = new PagerAdapter() {
			@Override
			public int getCount() {
				return views.size();
			}

			@Override
			public boolean isViewFromObject(View view, Object object) {
				return view == object;
			}

			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				View view = views.get(position);
				container.addView(view);
				return view;
			}

			@Override
			public void destroyItem(ViewGroup container, int position,
					Object object) {
				container.removeView(views.get(position));
			}
		};
		viewPager.setAdapter(pageadapter);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				if (position == 0) {
					currentpage = "boxpage";
					reltitle.setVisibility(View.GONE);
					title.setVisibility(View.VISIBLE);
					title.setText("表箱信息");
					// title.setGravity(Gravity.CENTER_VERTICAL);
					changeTab(0);
					clickin = false;
				} else {
					title.setVisibility(View.GONE);
					reltitle.setVisibility(View.VISIBLE);
					if (!clickin) {
						currentbox = null;
						currentboxno = "";
						clickin = false;
					}
					changeTab(1);
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});

		txtwwc = (TextView) tableview.findViewById(R.id.wwc);
		txtywc = (TextView) tableview.findViewById(R.id.ywc);
		txtgj = (TextView) tableview.findViewById(R.id.gj);
		listView = (ListView) tableview.findViewById(R.id.dataview);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TableData getObject = mlistInfo.get(position);
				String rowdata = "{\"a\":\"D\",\"b\":\""
						+ getObject.getUserName() + "\",\"c\":\""
						+ getObject.getUserId() + "\",\"d\":\""
						+ getObject.getAssetsId() + "\"}";
				Intent Install_intent = new Intent(MainActivity.this,
						InstallActivity.class);
				Bundle databundle = new Bundle();
				databundle.putSerializable("boxdata", currentbox);
				Install_intent.putExtras(databundle);
				Install_intent.putExtra("action", "D");
				Install_intent.putExtra("data", rowdata);
				startActivity(Install_intent);
			}
		});

		// 设置表格标题的背景颜色
		ViewGroup tableTitle = (ViewGroup) boxview
				.findViewById(R.id.table_title);
		tableTitle.setBackgroundColor(Color.rgb(229, 247, 255));
		tableListView = (ListView) boxview.findViewById(R.id.boxdataview);
		tableListView.setItemsCanFocus(false);
		tableListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				currentbox = tablist.get(position);
				if (currentbox.getTABLE_CABINET_NO() != null
						&& !currentbox.getTABLE_CABINET_NO().equals("")) {
					currentboxno = currentbox.getTABLE_CABINET_NO();
					clickin = true;
				}
				changeTab(1);
			}
		});
		tableListView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent ev) {
				switch (ev.getAction()) {
				case MotionEvent.ACTION_DOWN:
					mLastX = ev.getX();
					mLastY = ev.getY();
					break;
				case MotionEvent.ACTION_MOVE:
					break;
				case MotionEvent.ACTION_UP:
					if ((Math.abs(mLastX - ev.getX()) > Math.abs(mLastY
							- ev.getY()) + 10)
							&& (mLastX - ev.getX() > 0)) {
						currentbox = null;
						currentboxno = "";
						changeTab(1);
						return true;
					}
					break;
				case MotionEvent.ACTION_CANCEL:
					break;
				}
				return false;
			}
		});
		setPopUpWind(tableListView);
	}

	private void setPopUpWind(View anchorView) {
		popupMenuItemList.add("编辑");
		popupMenuItemList.add("删除");
		PopupList popupList = new PopupList();
		popupList.init(MainActivity.this, anchorView, popupMenuItemList,
				new PopupList.OnPopupListClickListener() {
					@Override
					public void onPopupListClick(View contextView,
							int contextPosition, int position) {
						if (position == 0) {

						} else if (position == 1) {

						} else
							Toast.makeText(MainActivity.this,
									contextPosition + "," + position,
									Toast.LENGTH_LONG).show();
					}
				});
		ImageView indicator = new ImageView(MainActivity.this);
		indicator.setImageResource(R.drawable.popuplist_default_arrow);
		popupList.setIndicatorView(indicator);
		popupList.setIndicatorSize(popupList.dp2px(16), popupList.dp2px(8));
	}

	private List<TableBox> getBoxsData() {
		List<TableBox> resultdata = new ArrayList<TableBox>();
		DBHelper helper = new DBHelper(this);
		try {
			Cursor cursor = helper
					.query("TabBoxData",
							new String[] { "_id,TABLE_CABINET_NO,HLY_CREATE_DATE,PICTURES,PICTURES2,PICTURES3,MORE_PICTURES,DATACOUNT,SEAL_NUMBER,SEAL_NUMBER2,SEAL_NUMBER3,SEAL_NUMBER4,SEAL_NUMBER5,SEAL_NUMBER6" },
							null, null, null, null, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					TableBox tb = new TableBox();
					tb.setID(cursor.getInt(cursor.getColumnIndex("_id")));
					tb.setTABLE_CABINET_NO(cursor.getString(cursor
							.getColumnIndex("TABLE_CABINET_NO")));
					tb.setHLY_CREATE_DATE(cursor.getString(cursor
							.getColumnIndex("HLY_CREATE_DATE")));
					tb.setPICTURES(cursor.getString(cursor
							.getColumnIndex("PICTURES")));
					tb.setPICTURES2(cursor.getString(cursor
							.getColumnIndex("PICTURES2")));
					tb.setPICTURES3(cursor.getString(cursor
							.getColumnIndex("PICTURES3")));
					tb.setMORE_PICTURES(cursor.getString(cursor
							.getColumnIndex("MORE_PICTURES")));
					tb.setDATACOUNT(cursor.getString(cursor
							.getColumnIndex("DATACOUNT")));
					tb.setSEAL_NUMBER(cursor.getString(cursor
							.getColumnIndex("SEAL_NUMBER")));
					tb.setSEAL_NUMBER2(cursor.getString(cursor
							.getColumnIndex("SEAL_NUMBER2")));
					tb.setSEAL_NUMBER3(cursor.getString(cursor
							.getColumnIndex("SEAL_NUMBER3")));
					tb.setSEAL_NUMBER4(cursor.getString(cursor
							.getColumnIndex("SEAL_NUMBER4")));
					tb.setSEAL_NUMBER5(cursor.getString(cursor
							.getColumnIndex("SEAL_NUMBER5")));
					tb.setSEAL_NUMBER6(cursor.getString(cursor
							.getColumnIndex("SEAL_NUMBER6")));
					resultdata.add(tb);
				}
				cursor.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultdata;
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

	@Override
	protected void onResume() {
		try {
			if (currentpage.equals("tablepage")) {
				if (currentdata.equals("wwc"))
					doclick(R.id.left_btn);
				else
					doclick(R.id.right_btn);
			} else {
				tablist = getBoxsData();
				tableadapter = new TableAdapter(this, tablist);
				tableListView.invalidate();
				tableListView.setAdapter(tableadapter);
				tableadapter.notifyDataSetChanged();
			}
			userName = mPreferences.getString("userName", "");
			if (userName.equals(""))
				userlogin.setImageResource(R.drawable.offline_user);
			else
				userlogin.setImageResource(R.drawable.online_user);
			if(currentbox != null)
			{
				currentbox = getBoxData(currentbox.getTABLE_CABINET_NO());
			}
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
		super.onResume();
	}

	private String quertyCount(Boolean isfinish, String boxid) {
		String result = "10";
		try {
			String select = "select Count(*) as C from RecordData";
			Cursor cursor = null;
			if (boxid == null || boxid.equals("")) {
				if (isfinish == null) {
					select = "select Count(*) as C from RecordData";
					cursor = db.rawQuery(select, null);
				} else {
					if (isfinish)
						select = "select Count(*) as C from RecordData where dstatic=? and istatic=? order by _id";
					else
						select = "select Count(*) as C from RecordData where dstatic<>? or istatic<>? order by _id";
					cursor = db.rawQuery(select, new String[] { "1", "1" });
				}
			} else {
				if (isfinish == null) {
					select = "select Count(*) as C from RecordData where TABLE_CABINET_NO=?";
					cursor = db.rawQuery(select, new String[] { boxid });
				} else {
					if (isfinish)
						select = "select Count(*) as C from RecordData where dstatic=? and istatic=? and TABLE_CABINET_NO=? order by _id";
					else
						select = "select Count(*) as C from RecordData where (dstatic<>? or istatic<>?) and TABLE_CABINET_NO=? order by _id";
					cursor = db.rawQuery(select, new String[] { "1", "1",boxid });
				}
			}
			if (cursor != null) {
				while (cursor.moveToNext()) {
					if (isfinish == null) {
						result = cursor.getString(cursor.getColumnIndex("C"));
					} else {
						if (isfinish)
							result = cursor.getString(cursor
									.getColumnIndex("C"));
						else
							result = cursor.getString(cursor
									.getColumnIndex("C"));
					}

				}
				cursor.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private void quertyData(Boolean isfinish) {
		try {
			String select = "select * from RecordData";
			if (isfinish) {
				if (currentboxno.equals(""))
					select = "select * from RecordData where dstatic=? and istatic=? order by _id";
				else
					select = "select * from RecordData where dstatic=? and istatic=? and TABLE_CABINET_NO=? order by _id";
			} else {
				if (currentboxno.equals(""))
					select = "select * from RecordData where dstatic<>? or istatic<>? order by _id";
				else
					select = "select * from RecordData where dstatic<>? or istatic<>? and TABLE_CABINET_NO=? order by _id";
			}
			Cursor cursor = null;
			if (currentboxno.equals(""))
				cursor = db.rawQuery(select, new String[] { "1", "1" });
			else
				cursor = db.rawQuery(select, new String[] { "1", "1",
						currentboxno });
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
					data.setN_assetsnumber(cursor.getString(cursor
							.getColumnIndex("n_assetsnumber")));
					data.setN_ameterspic(cursor.getString(cursor
							.getColumnIndex("n_ameterspic")));
					data.setN_tmeterspic(cursor.getString(cursor
							.getColumnIndex("n_tmeterspic")));
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
					if (data != null)
						mlistInfo.add(data);
				}
				cursor.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case SCANNIN_GREQUEST_CODE:
			if (resultCode == RESULT_OK) {
				AnalysisResult analysis = new AnalysisResult();
				Bundle bundle = data.getExtras();
				String result = bundle.getString("result");
				String name = bundle.getString("name");
				if (name.equals("QR_CODE")) {
					TableData record = analysis.getData(result);
					if (record.getAction() != null
							&& record.getAction().equals("A")) {
						Intent Install_intent = new Intent(MainActivity.this,
								InstallActivity.class);

						Bundle databundle = new Bundle();
						databundle.putSerializable("boxdata", currentbox);
						Install_intent.putExtras(databundle);
						Install_intent.putExtra("action", record.getAction());
						Install_intent.putExtra("data", result);
						startActivity(Install_intent);
					} else if (record.getAction() != null
							&& record.getAction().equals("C")) {
						if (hasContent(record.getUserName(),
								record.getUserId(), record.getAssetsId())) {
							Intent Install_intent = new Intent(
									MainActivity.this, InstallActivity.class);
							Bundle databundle = new Bundle();
							databundle.putSerializable("boxdata", currentbox);
							Install_intent.putExtras(databundle);
							Install_intent.putExtra("action",
									record.getAction());
							Install_intent.putExtra("data", result);
							startActivity(Install_intent);
						} else {
							Intent disassemble_intent = new Intent(
									MainActivity.this,
									DisassembleActivity.class);
							disassemble_intent.putExtra("boxdata", currentbox);
							disassemble_intent.putExtra("data", result);
							if(currentbox!=null){
								String fm = quertyCount(null,currentbox.getTABLE_CABINET_NO());
								disassemble_intent.putExtra("loc",Integer.valueOf(fm)+1);
							}
							startActivityForResult(disassemble_intent,
									Disassem_GREQUEST_CODE);
						}
					} else {
						String message = "非系统标准二维码！";
						Toast.makeText(this, message, message.length()).show();
					}
				} else {
					// Toast.makeText(this, "条形码：" + result,
					// Toast.LENGTH_SHORT).show();
					// currentboxno=result;
					Intent box_intent = new Intent(MainActivity.this,
							TabBoxActivity.class);
					box_intent.putExtra("action", "scan");
					box_intent.putExtra("data", result);
					startActivityForResult(box_intent, TABLEBOX_GREQUEST_CODE);
				}

			}
			break;
		case Disassem_GREQUEST_CODE:
			if (resultCode == RESULT_OK) {
				Boolean mqwork = mPreferences.getBoolean("myqwork", false);
				Boolean qphoto = mPreferences.getBoolean("myqphoto", false);
				if (mqwork||qphoto) {
					currentbox = getBoxData(currentboxno);
					Intent intent = new Intent();
					intent.setClass(MainActivity.this, QRScanActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.putExtra("zoomto", 0);
					startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
				}
			}
			break;
		case TABLEBOX_GREQUEST_CODE:
			if (resultCode == RESULT_OK) {
				currentboxno = data.getStringExtra("result").toString();
				currentbox = getBoxData(currentboxno);
				if (currentbox.getTABLE_CABINET_NO() != null
						&& !currentbox.getTABLE_CABINET_NO().equals("")) {
					currentboxno = currentbox.getTABLE_CABINET_NO();
					clickin = true;
				}
				changeTab(1);
				
				Boolean mqwork = mPreferences.getBoolean("myqwork", false);
				if (mqwork) {
					Intent intent = new Intent();
					intent.setClass(MainActivity.this, QRScanActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.putExtra("zoomto", 0);
					startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
				}
			}
			break;
		}
	}

	private Boolean hasContent(String username, String userid, String assetsid) {
		Boolean result = false;
		DBHelper helper = new DBHelper(this);
		try {
			Cursor cursor = helper.query("RecordData",
					new String[] { "_id,username,usernumber,assetsnumber" },
					"username = ? and usernumber = ? and assetsnumber=?",
					new String[] { username, userid, assetsid }, null, null,
					null);
			if (cursor != null) {
				if (cursor.moveToFirst() == false) {
					result = false;
				} else {
					result = true;
				}
				cursor.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public void onClick(View arg0) {
		doclick(arg0.getId());
	}

	@SuppressWarnings("deprecation")
	private void doclick(int viewid) {
		switch (viewid) {
		case R.id.testbutton:
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, QRScanActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra("zoomto", 0);
			startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
			break;
		case R.id.img_user:
			Intent loginintent = new Intent();
			loginintent.setClass(MainActivity.this, LoginActivity.class);
			loginintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(loginintent, USERLOGIN_GREQUEST_CODE);
			break;
		case R.id.left_btn:
			left_but.setBackgroundResource(R.drawable.switch_button_left_checked);
			left_but.setTextColor(getResources().getColorStateList(R.color.backcolor));
			right_but.setBackgroundResource(R.drawable.switch_button_right);
			right_but.setTextColor(getResources().getColorStateList(R.color.white));
			if (currentpage.equals("tablepage")) {
				quertyData(false);
				adapter = new ListViewAdapter(MainActivity.this, mlistInfo);
				listView.setAdapter(adapter);
				adapter.notifyDataSetChanged();
				currentdata = "wwc";
				if (currentboxno.equals(""))
					setDatastctic();
			} else if (currentpage.equals("boxpage")) {
			} else {
			}
			break;
		case R.id.right_btn:
			left_but.setBackgroundResource(R.drawable.switch_button_left);
			left_but.setTextColor(getResources().getColorStateList(	R.color.white));
			right_but.setBackgroundResource(R.drawable.switch_button_right_checked);
			right_but.setTextColor(getResources().getColorStateList(R.color.backcolor));
			if (currentpage.equals("tablepage")) {
				quertyData(true);
				adapter = new ListViewAdapter(MainActivity.this, mlistInfo);
				listView.setAdapter(adapter);
				adapter.notifyDataSetChanged();
				currentdata = "ywc";

				if (currentboxno.equals(""))
					setDatastctic();
			} else if (currentpage.equals("boxpage")) {
			} else {
			}
			break;
		case R.id.img_seting:
			Intent Seting_intent = new Intent(MainActivity.this,
					SetingActivity.class);
			startActivity(Seting_intent);
			break;
		case R.id.addtool:
			if (currentpage.equals("boxpage")) {
				Intent box_intent = new Intent(MainActivity.this,
						TabBoxActivity.class);
				box_intent.putExtra("action", "add");
				startActivity(box_intent);
			} else if (currentpage.equals("tablepage")) {
				Intent add_intent = new Intent(MainActivity.this,
						DisassembleActivity.class);
				add_intent.putExtra("boxdata", currentbox);
				add_intent.putExtra("data", "");
				if(currentbox!=null){
					String fm = quertyCount(null,currentbox.getTABLE_CABINET_NO());
					add_intent.putExtra("loc",Integer.valueOf(fm)+1);
				}
				startActivity(add_intent);
			} else {
			}
			break;
		default:
			break;
		}
	}

	private void setDatastctic() {
		String fz = quertyCount(false,null);
		String fm = quertyCount(null,null);
		int ywccount = 0;
		try {
			ywccount = Integer.parseInt(fm) - Integer.parseInt(fz);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		zcount.setText(ywccount + "/" + fm);
		txtwwc.setText("未完成：" + fz);
		txtywc.setText("已完成：" + ywccount);
		txtgj.setText("共计：" + fm);
	}

	private void changeTab(int tab) {
		if (tab == 0) {
			currentbox=null;
			viewPager.setCurrentItem(0);
			currentpage = "boxpage";
			reload();
		} else if (tab == 1) {
			viewPager.setCurrentItem(1);
			currentpage = "tablepage";
			if (currentboxno.equals("")) {
				txtwwc.setVisibility(View.VISIBLE);
				txtgj.setVisibility(View.VISIBLE);
				if (currentdata.equals("wwc"))
					doclick(R.id.left_btn);
				else
					doclick(R.id.right_btn);
			} else {
				txtwwc.setVisibility(View.GONE);
				txtgj.setVisibility(View.GONE);
				txtywc.setText("当前表箱编号：" + currentboxno);
				if (currentdata.equals("wwc"))
					doclick(R.id.left_btn);
				else
					doclick(R.id.right_btn);
			}
		} else {
		}
	}
}
