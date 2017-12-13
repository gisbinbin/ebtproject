package com.rm.ebtapp;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.rm.db.BLL;
import com.rm.db.DBHelper;
import com.rm.model.ImgContent;
import com.rm.model.MistakeRecord;
import com.rm.model.TableBox;
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
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

public class BPPicActivity extends Activity implements OnClickListener {
	private final static int SAVE_DATA_CODE = 1000;
	private String TAG = "BPPicActivity";
	private SQLiteDatabase db;
	private final DBHelper helper = new DBHelper(this);
	private SharedPreferences sp = null;
	private MistakeRecord crurrentRecord;
	
	private List<ImgContent> imgs=new ArrayList<ImgContent>();
    private ImgAdapter mainAdapter;
	
	private Boolean havesave=true;
	
	private MyGridView gridView;
	private EditText username,usernumber,oassetsid,nassetsid;
	private TextView bpcontent,bpaddress;
	private ImageView camera;
	private Button savebut;
	
	private String CurrentImg="";
	private String[] CurrentProArr;
	
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler(){
    	@Override
		public void handleMessage(Message msg){
    		if(msg.what==SAVE_DATA_CODE)
    			savedata(false);
    	}
    };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_bppic);
		db = helper.getWritableDatabase();
        sp = this.getSharedPreferences("APPSETTING", Context.MODE_PRIVATE);
        
		TextView title = (TextView) findViewById(R.id.textview_title);
		title.setText("信息补录");

		findViewById(R.id.img_user).setVisibility(View.GONE);
		Button backbutton=(Button)findViewById(R.id.button_back);
		backbutton.setVisibility(View.VISIBLE);
		backbutton.setOnClickListener(this);
		Intent intent = BPPicActivity.this.getIntent();
		crurrentRecord= (MistakeRecord) intent.getSerializableExtra("data");
		
		init();
	}

	private void init() {
		camera=(ImageView)findViewById(R.id.bpcamerabtn);
		camera.setOnClickListener(this);
		savebut=(Button)findViewById(R.id.bpsavebtn);
		savebut.setOnClickListener(this);
		username=(EditText)findViewById(R.id.bp_username);
		usernumber=(EditText)findViewById(R.id.bp_usernumber);
		oassetsid=(EditText)findViewById(R.id.bpovalue_assetsid);
		nassetsid=(EditText)findViewById(R.id.bpnvalue_assetsid);
		bpaddress=(TextView)findViewById(R.id.bp_address);
		bpcontent=(TextView)findViewById(R.id.bpmiaosu);
		gridView = (MyGridView) findViewById(R.id.bpgridtbView);
        setGridViewOnclick();
		gridView.setOnItemLongClickListener(new OnItemLongClickListener() {// 设置事件监听(长按)
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				Boolean isShowDelete=mainAdapter.getShowDelete(position);
				if (isShowDelete) {
                    isShowDelete = false;
                } else {
                    isShowDelete = true;
                }
                Log.d("TAG","onItemLongClicked");
                mainAdapter.setShowDelete(position,isShowDelete);
				return true;
			}
		});
		if(crurrentRecord!=null)
		{
			username.setText(crurrentRecord.getUSER_NAME());
			usernumber.setText(crurrentRecord.getUSER_NO());
			oassetsid.setText(crurrentRecord.getASSET_TAG());
			nassetsid.setText(crurrentRecord.getNEW_ASSET_TAG());
			bpaddress.setText(crurrentRecord.getADDRESS());
			bpcontent.setText(crurrentRecord.getPROBLEM());
			if(crurrentRecord.getPROBLEM()!=null&&!crurrentRecord.getPROBLEM().equals(""))
				CurrentProArr=crurrentRecord.getPROBLEM().split(",");
			if(crurrentRecord.getPICDATA()!=null&&!crurrentRecord.getPICDATA().equals(""))
			{
				String[] arr=crurrentRecord.getPICDATA().split(",");
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
						if(CurrentProArr!=null&&i<CurrentProArr.length)
							con.showname=CurrentProArr[i];
						imgs.add(con);
					}
				}
				mainAdapter=new ImgAdapter(BPPicActivity.this,imgs,true);
		        gridView.setAdapter(mainAdapter);
				mainAdapter.notifyDataSetChanged();;
			}
		}
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
					readPic(imgs.get(position).name);
				}
			}
		});
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

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.button_back:
			onBackPressed();
			break;
		case R.id.bpcamerabtn:
			startCamera(R.id.bpcamerabtn);
			break;
		case R.id.bpsavebtn:
			savedata(true);
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		havesave=false;
		try {
			switch (requestCode) {
			case R.id.bpcamerabtn:
				if (resultCode == RESULT_OK) {
					String path = Environment.getExternalStorageDirectory().getPath() + "/Photo/" + CurrentImg;
					Bitmap addbmp = Common.getImageThumbnail(path, 3);
					ImgContent content=new ImgContent();
					content.name=CurrentImg;
					content.icon=addbmp;
					int L=imgs.size();
					if(CurrentProArr!=null&&L<CurrentProArr.length)
					{
						Boolean hascontent=false;
						for(int i=0;i<CurrentProArr.length;i++)
						{
							hascontent=false;
							for(int j=0;j<L;j++)
							{
								if(CurrentProArr[i].equals(imgs.get(j).showname))
								{
									hascontent=true;
								}
							}
							if(!hascontent){
								content.showname=CurrentProArr[i];
								break;
							}
						}
					}
					imgs.add(content);
					mainAdapter=new ImgAdapter(BPPicActivity.this,imgs,true);
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
	public void onResume() {
		super.onResume();
		registerHomeKeyReceiver();
	}
	@Override
    protected void onPause() {
        unregisterHomeKeyReceiver();
        super.onPause();
    }
	
	private void unregisterHomeKeyReceiver() {
		try {
			if (null != mHomeKeyEventReceiver) {
				BPPicActivity.this.unregisterReceiver(mHomeKeyEventReceiver);
	        }
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void registerHomeKeyReceiver() {
		final IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
		BPPicActivity.this.registerReceiver(mHomeKeyEventReceiver, homeFilter);
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
									BPPicActivity.this.finish();
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
			BPPicActivity.this.finish();
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
	
	private void savedata(Boolean toclose)
	{
//		username,usernumber,oassetsid,nassetsid;
//		TextView bpcontent;
		String user_name = username.getText().toString();
		if (user_name.equals("")) {
			Toast.makeText(this, "用户名为空", Toast.LENGTH_SHORT).show();
		}
		String user_number = usernumber.getText().toString();
		if (user_number.equals("")) {
			Toast.makeText(this, "用户编号为空", Toast.LENGTH_SHORT).show();
			return;
		}
		String myoassetsid = oassetsid.getText().toString();
		if (myoassetsid.equals("")) {
			Toast.makeText(this, "资产编号为空", Toast.LENGTH_SHORT).show();
			return;
		}
		String mynassetsid = nassetsid.getText().toString();
		if (mynassetsid==null||mynassetsid.equals("")) {
			Toast.makeText(this, "新资产编号为空", Toast.LENGTH_SHORT).show();
		}
		String mycontent= bpcontent.getText().toString();
		if (mycontent==null||mycontent.equals("")) {
			mycontent="";
		}
		if(crurrentRecord==null)
			crurrentRecord=new MistakeRecord();
		crurrentRecord.setUSER_NO(user_number);
		crurrentRecord.setUSER_NAME(user_name);
		crurrentRecord.setASSET_TAG(myoassetsid);
		crurrentRecord.setNEW_ASSET_TAG(mynassetsid);
		if(imgs.size()>0)
		{
			String otherpics="";
			for(int i=0;i<imgs.size();i++)
			{
				otherpics=otherpics+imgs.get(i).name;
				if(i<imgs.size()-1)
					otherpics=otherpics+",";
			}
			crurrentRecord.setPICDATA(otherpics);
		}
		int size=mycontent.split(",").length;
		if(size<=imgs.size())
			crurrentRecord.setWORKSTATIC("1");
		else
			crurrentRecord.setWORKSTATIC("0");
			
		if(BLL.updateMistake(BPPicActivity.this,crurrentRecord))
			Toast.makeText(this, "保存成功！", Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(this, "保存失败！", Toast.LENGTH_SHORT).show();
		
		havesave=true;
		if(toclose)
			BPPicActivity.this.finish();
	}
}
