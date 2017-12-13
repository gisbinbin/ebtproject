package com.rm.until;

import java.util.List;

import com.rm.ebtapp.R;
import com.rm.model.TableBox;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TableAdapter extends BaseAdapter {

	private List<TableBox> list;
	private LayoutInflater inflater;
	//private View[] itemViews;
	
	public TableAdapter(Context context, List<TableBox> list){
		this.list = list;
		//inflater = LayoutInflater.from(context);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		//itemViews=null;
		//itemViews = new View[list.size()];
		//for (int i = 0; i < list.size(); i++) {
		//	itemViews[i] = makeview(i,null);
		//}
	}
	
	@Override
	public int getCount() {
		int ret = 0;
		if(list!=null){
			ret = list.size();
		}
		return ret;
	}

	@Override
	public Object getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}
	
//	private View makeview(int position,View convertView)
//	{
//		TableBox tablebox = (TableBox) this.getItem(position);
//		if(convertView == null)
//		{
//			convertView= inflater.inflate(R.layout.table_item, null);
//			TextView txtid =  (TextView) convertView.findViewById(R.id.text_id);
//			String id=tablebox.getID()+"";
//			txtid.setText(id);
//			txtid.setTextSize(13);
//			TextView boxid = (TextView) convertView.findViewById(R.id.tablebox_id);
//			boxid.setText(tablebox.getTABLE_CABINET_NO());
//			boxid.setTextSize(13);
//			TextView createtime = (TextView) convertView.findViewById(R.id.tablebox_createtime);
//			createtime.setText(tablebox.getHLY_CREATE_DATE());
//			createtime.setTextSize(13);	
//			TextView dstatic = (TextView) convertView.findViewById(R.id.tablebox_static);
//			dstatic.setText(tablebox.getDATACOUNT());
//			dstatic.setTextSize(13);	
//		}		
//		return convertView;
//	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
//		if (convertView == null)
//			return itemViews[position];
//		return itemViews[position];
			
		TableBox tablebox=list.get(position);
		ViewHolder viewHolder;
		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.table_item, null);
			viewHolder.Id = (TextView) convertView.findViewById(R.id.text_id);
			viewHolder.boxid = (TextView) convertView.findViewById(R.id.tablebox_id);
			viewHolder.createtime = (TextView) convertView.findViewById(R.id.tablebox_createtime);
			viewHolder.ztstatic =  (TextView) convertView.findViewById(R.id.tablebox_static);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		String id=tablebox.getID()+"";
		viewHolder.Id.setText(id);
		viewHolder.Id.setTextSize(13);
		viewHolder.boxid.setText(tablebox.getTABLE_CABINET_NO());
		viewHolder.boxid.setTextSize(13);
		viewHolder.createtime.setText(tablebox.getHLY_CREATE_DATE());
		viewHolder.createtime.setTextSize(13);
		viewHolder.ztstatic.setText(tablebox.getDATACOUNT());
		viewHolder.ztstatic.setTextSize(13);		
		return convertView;
	}
	
	public static class ViewHolder{
		public TextView Id;
		public TextView boxid;
		public TextView createtime;
		public TextView ztstatic;
	}
}
