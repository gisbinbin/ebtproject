package com.rm.until;

import org.json.JSONException;
import org.json.JSONObject;

import com.rm.model.TableData;

public class AnalysisResult {
	public TableData getData(String result) {
		TableData data=new TableData();
		JSONObject info = null;
		try {
			info = new JSONObject(result);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (info != null) {
			try {
				data.setAction(info.getString("a"));
				data.setUserName(info.getString("b"));
				data.setUserId(info.getString("c"));
				data.setAssetsId(info.getString("d"));
//				data.setUserAddress(info.getString("e"));
				data.setEquipmentClass(info.getString("e"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return data;
	}
}
