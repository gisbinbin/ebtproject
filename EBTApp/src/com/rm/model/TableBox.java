package com.rm.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class TableBox implements Serializable {
	public TableBox(){	}
	public TableBox(int id,String number,String time)
	{
		this.ID=id;
		this.TABLE_CABINET_NO=number;
		this.HLY_CREATE_DATE=time;
	}
	int ID;
	private String PICTURES;
	private String PICTURES2;
	private String PICTURES3;
	private String MORE_PICTURES;
	private String TABLE_CABINET_NO;
	private String HLY_CREATE_DATE;
	private String HLY_EDIT_MAN;
	private String BOX_CLASS;
	private String BOX_VILLAGE;
	private String DATASTATIC;
	private String DATACOUNT;
	private String DATATSTATIC;
	private String SEAL_NUMBER;
	private String SEAL_NUMBER2;
	private String SEAL_NUMBER3;
	private String SEAL_NUMBER4;
	private String SEAL_NUMBER5;
	private String SEAL_NUMBER6;
	public final int getID() {
		return ID;
	}
	public final void setID(int iD) {
		ID = iD;
	}
	public final String getPICTURES() {
		return PICTURES;
	}
	public final  void setPICTURES(String pICTURES) {
		PICTURES = pICTURES;
	}
	public final  String getTABLE_CABINET_NO() {
		return TABLE_CABINET_NO;
	}
	public final  void setTABLE_CABINET_NO(String tABLE_CABINET_NO) {
		TABLE_CABINET_NO = tABLE_CABINET_NO;
	}
	public final  String getHLY_CREATE_DATE() {
		return HLY_CREATE_DATE;
	}
	public final  void setHLY_CREATE_DATE(String hLY_CREATE_DATE) {
		HLY_CREATE_DATE = hLY_CREATE_DATE;
	}
	public final  String getHLY_EDIT_MAN() {
		return HLY_EDIT_MAN;
	}
	public final  void setHLY_EDIT_MAN(String hLY_EDIT_MAN) {
		HLY_EDIT_MAN = hLY_EDIT_MAN;
	}
	public final  String getBOX_CLASS() {
		return BOX_CLASS;
	}
	public final  void setBOX_CLASS(String bOX_CLASS) {
		BOX_CLASS = bOX_CLASS;
	}
	public final  String getBOX_VILLAGE() {
		return BOX_VILLAGE;
	}
	public final void setBOX_VILLAGE(String bOX_VILLAGE) {
		BOX_VILLAGE = bOX_VILLAGE;
	}
	public final  String getDATASTATIC() {
		return DATASTATIC;
	}
	public final void setDATASTATIC(String dATASTATIC) {
		DATASTATIC = dATASTATIC;
	}
	public final  String getDATACOUNT() {
		return DATACOUNT;
	}
	public final void setDATACOUNT(String dATACOUNT) {
		DATACOUNT = dATACOUNT;
	}
	public final  String getDATATSTATIC() {
		return DATATSTATIC;
	}
	public final void setDATATSTATIC(String dATATSTATIC) {
		DATATSTATIC = dATATSTATIC;
	}
	public String getSEAL_NUMBER() {
		return SEAL_NUMBER;
	}
	public void setSEAL_NUMBER(String sEAL_NUMBER) {
		SEAL_NUMBER = sEAL_NUMBER;
	}
	public String getSEAL_NUMBER2() {
		return SEAL_NUMBER2;
	}
	public void setSEAL_NUMBER2(String sEAL_NUMBER2) {
		SEAL_NUMBER2 = sEAL_NUMBER2;
	}
	public String getSEAL_NUMBER3() {
		return SEAL_NUMBER3;
	}
	public void setSEAL_NUMBER3(String sEAL_NUMBER3) {
		SEAL_NUMBER3 = sEAL_NUMBER3;
	}
	public String getPICTURES2() {
		return PICTURES2;
	}
	public void setPICTURES2(String pICTURES2) {
		PICTURES2 = pICTURES2;
	}
	public String getPICTURES3() {
		return PICTURES3;
	}
	public void setPICTURES3(String pICTURES3) {
		PICTURES3 = pICTURES3;
	}
	public String getSEAL_NUMBER4() {
		return SEAL_NUMBER4;
	}
	public void setSEAL_NUMBER4(String sEAL_NUMBER4) {
		SEAL_NUMBER4 = sEAL_NUMBER4;
	}
	public String getSEAL_NUMBER5() {
		return SEAL_NUMBER5;
	}
	public void setSEAL_NUMBER5(String sEAL_NUMBER5) {
		SEAL_NUMBER5 = sEAL_NUMBER5;
	}
	public String getSEAL_NUMBER6() {
		return SEAL_NUMBER6;
	}
	public void setSEAL_NUMBER6(String sEAL_NUMBER6) {
		SEAL_NUMBER6 = sEAL_NUMBER6;
	}
	public String getMORE_PICTURES() {
		return MORE_PICTURES;
	}
	public void setMORE_PICTURES(String mORE_PICTURES) {
		MORE_PICTURES = mORE_PICTURES;
	}
	
}
