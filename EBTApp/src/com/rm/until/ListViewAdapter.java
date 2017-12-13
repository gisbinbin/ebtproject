package com.rm.until;

import java.util.List;

import com.rm.ebtapp.R;
import com.rm.model.TableData;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListViewAdapter extends BaseAdapter {
	View[] itemViews;

	public ListViewAdapter(Context context,List<TableData> mlistInfo) {
		// TODO Auto-generated constructor stub
		itemViews = null;
		itemViews = new View[mlistInfo.size()];
		for (int i = 0; i < mlistInfo.size(); i++) {
			TableData getInfo = (TableData) mlistInfo.get(i);
			String time = getInfo.getMitime();
			if (time == null)
				time = getInfo.getMdtime();
			itemViews[i] = makeItemView(context,getInfo.getUserId(),
					getInfo.getUserName(), time, getInfo.getStatic());
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
	private View makeItemView(Context context,String assetsnumberstr, String strText,
			String addressstr, String ZT) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View itemView = inflater.inflate(R.layout.item, null);
		TextView assetsnumbers = (TextView) itemView.findViewById(R.id.myassetsnumber);
		TextView txtzt = (TextView) itemView.findViewById(R.id.issubmit);
		assetsnumbers.setText("用户编号：" + assetsnumberstr);
		TextView text = (TextView) itemView.findViewById(R.id.myusername);
		text.setText("用户名称：" + strText);
		TextView address = (TextView) itemView.findViewById(R.id.myaddress);
		address.setText("更换时间：" + addressstr);
		if (ZT.equals("1")) {
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
