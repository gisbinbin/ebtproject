package com.rm.until;

import java.io.File;
import java.util.List;

import com.rm.ebtapp.R;
import com.rm.model.ImgContent;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ImgAdapter extends BaseAdapter {
	private LayoutInflater inflater;
    private List<ImgContent> data;
    private int clickItemIndex=-1;//根据这个变量来辨识选中的current值
    private Boolean IsshowName=false;

    public ImgAdapter(Context context,List<ImgContent> data,Boolean IsshowName) {
        inflater=LayoutInflater.from(context);
        this.data=data;
        this.IsshowName=IsshowName;
    }

    public void setShowDelete(int postion,boolean isShowDelete){
        //this.isShowDelete=isShowDelete;
    	data.get(postion).showdel=isShowDelete;
        notifyDataSetChanged();
    }
    public boolean getShowDelete(int postion){
        return data.get(postion).showdel;
    }
    public void setClickItemIndex(int postion){
        this.clickItemIndex=postion;
    }
    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @SuppressLint("InflateParams")
	@Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        if(convertView==null){
            convertView=inflater.inflate(R.layout.imgitem,null);
            viewHolder=new ViewHolder();
            viewHolder.name= (TextView) convertView.findViewById(R.id.id_name);
            viewHolder.icon= (ImageView) convertView.findViewById(R.id.imageView1);
            viewHolder.dele= (ImageView) convertView.findViewById(R.id.id_imv_dele);
            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }
        if(!IsshowName){
        	viewHolder.name.setVisibility(View.GONE);
        }
        else
        {
        	if(data.get(position).showname!=null)
        	{
        		viewHolder.name.setText(data.get(position).showname.toString());
        		viewHolder.name.setVisibility(View.GONE);
        	}
        }
        viewHolder.icon.setAdjustViewBounds(true);
		viewHolder.icon.setImageBitmap((Bitmap) data.get(position).icon);
        viewHolder.icon.setClickable(false);
        viewHolder.dele.setVisibility(data.get(position).showdel?View.VISIBLE:View.GONE);
        if(viewHolder.dele.getVisibility()==View.VISIBLE) {
        	viewHolder.dele.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("TAG", "onClick: "+clickItemIndex);
                    File file = new File(Environment.getExternalStorageDirectory()
    						.getPath() + "/Photo/" + data.get(position).name);
                    file.delete();
                    data.remove(position);
                    //Util.removeListEmlement(icons, (Integer) icons.get(clickItemIndex));
                    //Util.removeListEmlement(names, names.get(clickItemIndex).toString());
                    notifyDataSetChanged();
                }
            });
        }
        return convertView;
    }
    class ViewHolder{
        TextView name;
        ImageView icon;
        ImageView dele;
    }
}
    
