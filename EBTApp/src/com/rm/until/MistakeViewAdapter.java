package com.rm.until;

import java.util.List;

import com.rm.ebtapp.R;
import com.rm.model.MistakeRecord;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MistakeViewAdapter extends BaseAdapter {
	View[] itemViews;

	public MistakeViewAdapter(Context context,List<MistakeRecord> mlistInfo) {
		// TODO Auto-generated constructor stub
		itemViews = null;
		itemViews = new View[mlistInfo.size()];
		for (int i = 0; i < mlistInfo.size(); i++) {
			MistakeRecord getInfo = (MistakeRecord) mlistInfo.get(i);
			itemViews[i] = makeItemView(context,getInfo.getUSER_NO(),getInfo.getUSER_NAME(), getInfo.getASSET_TAG(),getInfo.getPROBLEM(), getInfo.getSUBMITSTATIC());
		}
	}

	public int getCount() {
		return itemViews.length;
	}

	public View getItem(int position) {
		return itemViews[position];
	}

	public long getItemId(int position) {
		return position;
	}

	// 绘制Item的函数
	@SuppressLint("InflateParams")
	private View makeItemView(Context context,String usernumberstr, String username,String assetsnumberstr,String reason, String ZT) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View itemView = inflater.inflate(R.layout.mistakeitem, null);
		TextView assetsnumbers = (TextView) itemView.findViewById(R.id.miskassetsnumber);
		TextView txtzt = (TextView) itemView.findViewById(R.id.miskissubmit);
		assetsnumbers.setText("资产编号：" + assetsnumberstr);
		TextView text = (TextView) itemView.findViewById(R.id.miskusername);
		text.setText("用户名称：" + username);
		TextView txtreason = (TextView) itemView.findViewById(R.id.miskaddress);
		txtreason.setText("补拍原因：" + reason);
		TextView txtusernumber = (TextView) itemView.findViewById(R.id.miskusernumber);
		txtusernumber.setText("用户编号：" + usernumberstr);
		if (ZT!=null&&ZT.equals("1")) {
			txtzt.setVisibility(View.VISIBLE);
		}
		return itemView;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null)
			return itemViews[position];
		return itemViews[position];
	}
}
