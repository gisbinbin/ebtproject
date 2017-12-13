package com.rm.ebtapp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener {
	String userName="";
	String passWord="";
	EditText edusername,edpassword;
	Button loginbut;
	private SharedPreferences mPreferences;
	private Editor mEditor;
	private TextView forgetpassword;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_login);
		((ImageView) findViewById(R.id.img_user)).setVisibility(View.GONE);
		((LinearLayout)findViewById(R.id.button_title)).setVisibility(View.GONE);
		TextView title = (TextView) findViewById(R.id.textview_title);
		title.setText("用户登入");
		title.setVisibility(View.VISIBLE);
		
		mPreferences = getSharedPreferences("APPSETTING", Context.MODE_PRIVATE);
		mEditor = mPreferences.edit();
		
		edusername=(EditText)findViewById(R.id.accountEdittext);
		edusername.setOnClickListener(this);
		edpassword=(EditText)findViewById(R.id.pwdEdittext);
		edpassword.setOnClickListener(this);
		loginbut=(Button)findViewById(R.id.Loginbutton);
		loginbut.setOnClickListener(this);
		forgetpassword=(TextView)findViewById(R.id.sound_help);
		forgetpassword.setOnClickListener(this);
		
		userName = mPreferences.getString("userCode", "");
		passWord = mPreferences.getString("password", "");
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		if(userName!="")
			edusername.setText(userName);
		if(passWord!="")
			edpassword.setText(passWord);
	}

	@Override
	public void onClick(View arg0) {
		int viewid = arg0.getId();
		switch (viewid) {
		case R.id.accountEdittext:
			break;
		case R.id.pwdEdittext:
			break;
		case R.id.sound_help:
			mEditor.putString("password", "");
			passWord="";
			edpassword.setText(passWord);
			mEditor.commit();
			break;
		case R.id.Loginbutton:
			userName=edusername.getText().toString();
			passWord=edpassword.getText().toString();
			if(userName.equals("")||passWord.equals(""))
			{
				Toast.makeText(LoginActivity.this, "用户名密码不能为空！",
						Toast.LENGTH_LONG).show();
				return;
			}
			else
				Userlogin();
			break;
		}
	}
	
	private void Userlogin()
	{
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String request = post("http://112.74.72.137:8181/ebtg/j_acegi_security_check",userName,passWord);//http://112.74.72.137:8100/v2/j_acegi_security_check",userName,passWord);
					JSONObject info = null;
					try {
						info = new JSONObject(request);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					if (info != null) {
						try {
							String resu = info.getString("success");
							if (resu.equals("true")) {
								Message msg = new Message();
								msg.what = 2;
								msg.obj = info.getString("result");
								handler.sendMessage(msg);
							} else
							{
								Message msg = new Message();
								msg.what = 1;
								msg.obj = info.getString("errorMsg");
								handler.sendMessage(msg);
							}
								return;
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
					Message msg = new Message();
					msg.what = 1;
					msg.obj = "登入异常！";//e.toString();
					handler.sendMessage(msg);
				}
			}
		}).start();
	}
	
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				try {
					msg.obj = URLDecoder.decode(msg.obj + "", "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				break;
			case 1:
				Toast.makeText(LoginActivity.this, msg.obj.toString(),
						Toast.LENGTH_LONG).show();
				break;
			case 2:
				JSONObject result = null;
				try {
					String data=msg.obj.toString();
					result = new JSONObject(data);
					String myuserName = result.getString("userName");
					String myuserdoce = result.getString("userCode");
					String mypassword = result.getString("password");
					String userid=result.getString("userid");
					String deptPid=result.getString("deptPid");
					String deptPcode=result.getString("deptPcode");
					mEditor.putString("userName", myuserName);
					mEditor.putString("userCode", myuserdoce);
					mEditor.putString("userid", userid);
					mEditor.putString("deptPid", deptPid);
					mEditor.putString("deptPcode", deptPcode);
					mEditor.putString("password", mypassword);
					mEditor.commit();
					LoginActivity.this.finish();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			default:
				break;
			}
		}
	};
	
	/**
     * 通过拼接的方式构造请求内容，实现参数传输以及文件传输
     * 
     * @param url Service net address
     * @param params text content
     * @param files pictures
     * @return String result of Service response
     * @throws IOException
     */
    public static String post(String url, String username,String password)
            throws IOException {
        String BOUNDARY = java.util.UUID.randomUUID().toString();
        String PREFIX = "--", LINEND = "\r\n";
        String CHARSET = "UTF-8";

        URL uri = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
        conn.setReadTimeout(300 * 1000); // 缓存的最长时间
        //conn.setDoInput(true);// 允许输入
        //conn.setDoOutput(true);// 允许输出
        //conn.setUseCaches(false); // 不允许使用缓存
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Charsert", "UTF-8");
        conn.setRequestProperty("Accept", "application/json, text/javascript, */*;q=0.01");
        conn.setRequestProperty("Accept-Language", "en-GB,en;q=0.5");
        conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
        conn.setRequestProperty("httpClientFrom", "mobile");
        conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
        conn.setRequestProperty("connection", "keep-alive");

        // 首先组拼文本类型的参数
        StringBuilder sb = new StringBuilder();
        sb.append("j_username="+username+"&j_password="+password+"&isEnableKaptcha=false");

        DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());
        outStream.write(sb.toString().getBytes());
        
        // 请求结束标志
        byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
        outStream.write(end_data);
        outStream.flush();
        // 得到响应码
        int res = conn.getResponseCode();
        InputStream in = conn.getInputStream();
        StringBuilder sb2 = new StringBuilder();
        if (res == 200) {
        	BufferedReader reader = new BufferedReader(new InputStreamReader(in,CHARSET));
            String line = "";
            while ((line = reader.readLine()) != null) {
            	sb2.append(line);
            }
        }
        outStream.close();
        conn.disconnect();
        return sb2.toString();
    }
}
