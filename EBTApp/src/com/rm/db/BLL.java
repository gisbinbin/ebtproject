package com.rm.db;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.rm.model.MistakeRecord;

public class BLL {
	public static List<MistakeRecord> JSONArrayToMistakeRecord(JSONArray prorecords)
	{
		List<MistakeRecord> list=new ArrayList<MistakeRecord>();
		for(int i=0;i<prorecords.length();i++)
		{
			JSONObject temp;
			try {
				temp = prorecords.getJSONObject(i);
				MistakeRecord record=new MistakeRecord();
				record.setUSER_NAME(temp.getString("USER_NAME"));
				record.setUSER_NO(temp.getString("USER_NO"));
				record.setADDRESS(temp.getString("ADDRESS"));
				record.setDEVICE_CLASS(temp.getString("DEVICE_CLASS"));
				record.setASSET_TAG(temp.getString("ASSET_TAG"));
				record.setNEW_ASSET_TAG(temp.getString("NEW_ASSET_TAG"));
				record.setPROBLEM(temp.getString("PROBLEM"));
				record.setDESCRIBE(temp.getString("CONTENT"));
				record.setWORKSTATIC("0");
				record.setSUBMITSTATIC("0");
				list.add(record);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return list;
	}

	public static void insertMistake(Context context,MistakeRecord mistakeRecord) {
		DBHelper helper = new DBHelper(context);
		ContentValues values=new ContentValues();
		values.put("USER_NAME", mistakeRecord.getUSER_NAME());
		values.put("USER_NO", mistakeRecord.getUSER_NO());
		values.put("ADDRESS", mistakeRecord.getADDRESS());
		values.put("DEVICE_CLASS", mistakeRecord.getDEVICE_CLASS());
		values.put("ASSET_TAG", mistakeRecord.getASSET_TAG());
		values.put("NEW_ASSET_TAG", mistakeRecord.getNEW_ASSET_TAG());
		values.put("PROBLEM", mistakeRecord.getPROBLEM());
		values.put("DESCRIBE", mistakeRecord.getDESCRIBE());
		values.put("WORKSTATIC", mistakeRecord.getWORKSTATIC());
		values.put("SUBMITSTATIC", mistakeRecord.getSUBMITSTATIC());
		helper.insert(values, "BPIMG");
	}

	public static List<MistakeRecord> getMistakeRecord(Context context,int type) {
		List<MistakeRecord> result=new ArrayList<MistakeRecord>();
		DBHelper helper = new DBHelper(context);
		Cursor cursor;
		if(type<0)
			cursor = helper.query("BPIMG", null, null,null, null, null, null);
		else
			cursor = helper.query("BPIMG", null, "WORKSTATIC = ?", new String[] {String.valueOf(type)}, null, null, null);
		if (cursor != null) {
			result.clear();
			while (cursor.moveToNext()) {
				MistakeRecord data = new MistakeRecord();
				data.setUSER_NAME(cursor.getString(cursor.getColumnIndex("USER_NAME")));
				data.setUSER_NO(cursor.getString(cursor.getColumnIndex("USER_NO")));
				data.setADDRESS(cursor.getString(cursor.getColumnIndex("ADDRESS")));
				data.setDEVICE_CLASS(cursor.getString(cursor.getColumnIndex("DEVICE_CLASS")));
				data.setASSET_TAG(cursor.getString(cursor.getColumnIndex("ASSET_TAG")));
				data.setNEW_ASSET_TAG(cursor.getString(cursor.getColumnIndex("NEW_ASSET_TAG")));
				data.setPROBLEM(cursor.getString(cursor.getColumnIndex("PROBLEM")));
				data.setDESCRIBE(cursor.getString(cursor.getColumnIndex("DESCRIBE")));
				data.setPICDATA(cursor.getString(cursor.getColumnIndex("PICDATA")));
				data.setTAG(cursor.getString(cursor.getColumnIndex("TAG")));
				data.setWORKSTATIC(cursor.getString(cursor.getColumnIndex("WORKSTATIC")));
				data.setSUBMITSTATIC(cursor.getString(cursor.getColumnIndex("SUBMITSTATIC")));
				result.add(data);
			}
		}
		cursor.close();
		return result;
	}

	public static Boolean updateMistake(Context context,MistakeRecord mistakeRecord) {
		DBHelper helper = new DBHelper(context);
		try {
			ContentValues values=new ContentValues();
			values.put("USER_NAME", mistakeRecord.getUSER_NAME());
			values.put("USER_NO", mistakeRecord.getUSER_NO());
			values.put("ASSET_TAG", mistakeRecord.getASSET_TAG());
			values.put("NEW_ASSET_TAG", mistakeRecord.getNEW_ASSET_TAG());
			values.put("WORKSTATIC", mistakeRecord.getWORKSTATIC());
			if(mistakeRecord.getPICDATA()!=null&&!mistakeRecord.getPICDATA().equals(""))
				values.put("PICDATA", mistakeRecord.getPICDATA());
			helper.update("BPIMG", values, "USER_NO = ? and ASSET_TAG=?",	new String[] { mistakeRecord.getUSER_NO(),mistakeRecord.getASSET_TAG() });
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
