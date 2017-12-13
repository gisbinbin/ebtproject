package com.rm.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class MistakeRecord implements Serializable  {
	private String USER_NAME;		//用户名
	private String USER_NO;			//用户编号
	private String ADDRESS;			//地址
	private String DEVICE_CLASS;	//设备类型
	private String ASSET_TAG;		//旧资产编号
	private String NEW_ASSET_TAG;	//新资产编号
	private String PROBLEM;			//问题
	private String DESCRIBE;		//问题描述
	private String PICDATA;			//图片数据（与问题对应）
	private String TAG;				//备注
	private String WORKSTATIC;		//工作状态
	private String SUBMITSTATIC;	//提交状态

	public String getUSER_NAME() {
		return USER_NAME;
	}

	public void setUSER_NAME(String uSER_NAME) {
		USER_NAME = uSER_NAME;
	}

	public String getUSER_NO() {
		return USER_NO;
	}

	public void setUSER_NO(String uSER_NO) {
		USER_NO = uSER_NO;
	}

	public String getADDRESS() {
		return ADDRESS;
	}

	public void setADDRESS(String aDDRESS) {
		ADDRESS = aDDRESS;
	}

	public String getDEVICE_CLASS() {
		return DEVICE_CLASS;
	}

	public void setDEVICE_CLASS(String dEVICE_CLASS) {
		DEVICE_CLASS = dEVICE_CLASS;
	}

	public String getASSET_TAG() {
		return ASSET_TAG;
	}

	public void setASSET_TAG(String aSSET_TAG) {
		ASSET_TAG = aSSET_TAG;
	}

	public String getNEW_ASSET_TAG() {
		return NEW_ASSET_TAG;
	}

	public void setNEW_ASSET_TAG(String nEW_ASSET_TAG) {
		NEW_ASSET_TAG = nEW_ASSET_TAG;
	}

	public String getPROBLEM() {
		return PROBLEM;
	}

	public void setPROBLEM(String pROBLEM) {
		PROBLEM = pROBLEM;
	}

	public String getDESCRIBE() {
		return DESCRIBE;
	}

	public void setDESCRIBE(String dESCRIBE) {
		DESCRIBE = dESCRIBE;
	}

	public String getPICDATA() {
		return PICDATA;
	}

	public void setPICDATA(String pICDATA) {
		PICDATA = pICDATA;
	}

	public String getTAG() {
		return TAG;
	}

	public void setTAG(String tAG) {
		TAG = tAG;
	}

	public String getWORKSTATIC() {
		return WORKSTATIC;
	}

	public void setWORKSTATIC(String wORKSTATIC) {
		WORKSTATIC = wORKSTATIC;
	}

	public String getSUBMITSTATIC() {
		return SUBMITSTATIC;
	}

	public void setSUBMITSTATIC(String sUBMITSTATIC) {
		SUBMITSTATIC = sUBMITSTATIC;
	}
}
