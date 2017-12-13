package com.rm.model;

public class TableData {
	private String mAction;//操作类型（安装：install、拆卸：disassemble）
	private String mUserName;//用户名
	private String mUserId;//用户编号
	private String mAssetsId;//资产编号
	private String mUserAddress;//用户地址
	private String mEquipmentClass;//设备类型
	private String BIN_POSITION_NUMBER;//箱柜内位置
	private String DATATAG;
	private String mReaddata;//旧表读数
	private String mdReaddata;//旧无功读数
	private String mAmeterspic;//单表图片
	private String mTeterspic;//双表图片
	private String mAllpic;//双表图片
	private String mn_assetsnumber;//新表资产编号
	private String mn_ameterspic;//新表单表图
	private String mn_tmeterspic;//新表双表图
	private String mn_allpic;//新表双表图
	private String mn_readdata;//新表读数
	private String mn_wreaddata;//新表无功读数
	private String mSealId1;//封条号1
	private String mSealId2;//封条号2
	private String mSealId3;//封条号3
	private String mSealId4;//封条号4
	private String mSealId5;//封条号5
	private String mSealId6;//封条号6
	private String mSealId7;//封条号7
	private String mSealId8;//封条号8
	private String mSealId9;//封条号9
	private String mdstatic;//拆表状态
	private String mistatic;//装表状态
	private String mstatic;//数据状态
	private String mdtime;//拆表时间
	private String mitime;//安装时间
	private String TABLE_CABINET_NO;
	private String OCT_SINGLE_PICTURES;
	private String OCT_DOUBLE_PICTURES;
	private String OCT_WHOLE_PICTURES;
	private String NCT_SINGLE_PICTURES;
	private String NCT_DOUBLE_PICTURES;
	private String NCT_WHOLE_PICTURES;
	private String ROUND_CT;
	private String NEW_ROUND_CT;
	private String OLD_REACTIVE_PICTURES;
	private String NEW_REACTIVE_PICTURES;
	private String OLD_MORE_PICTURES;//更多旧表图片(多张用逗号分割)
	private String NEW_MORE_PICTURES;//更多新表图片(多张用逗号分割)
	private String MORE_SEAL_NUMBER;//更多封印信息
	public String getAction() {
		return mAction;
	}
	public void setAction(String mAction) {
		this.mAction = mAction;
	}
	public String getUserId() {
		return mUserId;
	}
	public void setUserId(String mUserId) {
		this.mUserId = mUserId;
	}
	public String getAssetsId() {
		return mAssetsId;
	}
	public void setAssetsId(String mAssetsId) {
		this.mAssetsId = mAssetsId;
	}
	public String getUserName() {
		return mUserName;
	}
	public void setUserName(String mUserName) {
		this.mUserName = mUserName;
	}
	public String getUserAddress() {
		return mUserAddress;
	}
	public void setUserAddress(String mUserAddress) {
		this.mUserAddress = mUserAddress;
	}
	public String getEquipmentClass() {
		return mEquipmentClass;
	}
	public void setEquipmentClass(String mEquipmentClass) {
		this.mEquipmentClass = mEquipmentClass;
	}
	public String getReaddata() {
		return mReaddata;
	}
	public void setReaddata(String mReaddata) {
		this.mReaddata = mReaddata;
	}
	public String getAmeterspic() {
		return mAmeterspic;
	}
	public void setAmeterspic(String mAmeterspic) {
		this.mAmeterspic = mAmeterspic;
	}
	public String getTeterspic() {
		return mTeterspic;
	}
	public void setTeterspic(String mTeterspic) {
		this.mTeterspic = mTeterspic;
	}
	public String getN_assetsnumber() {
		return mn_assetsnumber;
	}
	public void setN_assetsnumber(String mn_assetsnumber) {
		this.mn_assetsnumber = mn_assetsnumber;
	}
	public String getN_ameterspic() {
		return mn_ameterspic;
	}
	public void setN_ameterspic(String mn_ameterspic) {
		this.mn_ameterspic = mn_ameterspic;
	}
	public String getN_tmeterspic() {
		return mn_tmeterspic;
	}
	public void setN_tmeterspic(String mn_tmeterspic) {
		this.mn_tmeterspic = mn_tmeterspic;
	}
	public String getN_readdata() {
		return mn_readdata;
	}
	public void setN_readdata(String mn_readdata) {
		this.mn_readdata = mn_readdata;
	}
	public String getN_wreaddata() {
		return mn_wreaddata;
	}
	public void setN_wreaddata(String mn_wreaddata) {
		this.mn_wreaddata = mn_wreaddata;
	}
	public String getSealId2() {
		return mSealId2;
	}
	public void setSealId2(String mSealId2) {
		this.mSealId2 = mSealId2;
	}
	public String getSealId1() {
		return mSealId1;
	}
	public void setSealId1(String mSealId1) {
		this.mSealId1 = mSealId1;
	}
	public String getSealId3() {
		return mSealId3;
	}
	public void setSealId3(String mSealId3) {
		this.mSealId3 = mSealId3;
	}
	public String getDstatic() {
		return mdstatic;
	}
	public void setDstatic(String mdstatic) {
		this.mdstatic = mdstatic;
	}
	public String getIstatic() {
		return mistatic;
	}
	public void setIstatic(String mistatic) {
		this.mistatic = mistatic;
	}
	public String getStatic() {
		return mstatic;
	}
	public void setStatic(String mstatic) {
		this.mstatic = mstatic;
	}
	public String getN_allpic() {
		return mn_allpic;
	}
	public void setN_allpic(String mn_allpic) {
		this.mn_allpic = mn_allpic;
	}
	public String getAllpic() {
		return mAllpic;
	}
	public void setAllpic(String mAllpic) {
		this.mAllpic = mAllpic;
	}
	public String getMdtime() {
		return mdtime;
	}
	public void setMdtime(String mdtime) {
		this.mdtime = mdtime;
	}
	public String getMitime() {
		return mitime;
	}
	public void setMitime(String mitime) {
		this.mitime = mitime;
	}
	public String getTABLE_CABINET_NO() {
		return TABLE_CABINET_NO;
	}
	public void setTABLE_CABINET_NO(String tABLE_CABINET_NO) {
		TABLE_CABINET_NO = tABLE_CABINET_NO;
	}
	public String getOCT_SINGLE_PICTURES() {
		return OCT_SINGLE_PICTURES;
	}
	public void setOCT_SINGLE_PICTURES(String oCT_SINGLE_PICTURES) {
		OCT_SINGLE_PICTURES = oCT_SINGLE_PICTURES;
	}
	public String getOCT_DOUBLE_PICTURES() {
		return OCT_DOUBLE_PICTURES;
	}
	public void setOCT_DOUBLE_PICTURES(String oCT_DOUBLE_PICTURES) {
		OCT_DOUBLE_PICTURES = oCT_DOUBLE_PICTURES;
	}
	public String getOCT_WHOLE_PICTURES() {
		return OCT_WHOLE_PICTURES;
	}
	public void setOCT_WHOLE_PICTURES(String oCT_WHOLE_PICTURES) {
		OCT_WHOLE_PICTURES = oCT_WHOLE_PICTURES;
	}
	public String getNCT_SINGLE_PICTURES() {
		return NCT_SINGLE_PICTURES;
	}
	public void setNCT_SINGLE_PICTURES(String nCT_SINGLE_PICTURES) {
		NCT_SINGLE_PICTURES = nCT_SINGLE_PICTURES;
	}
	public String getNCT_DOUBLE_PICTURES() {
		return NCT_DOUBLE_PICTURES;
	}
	public void setNCT_DOUBLE_PICTURES(String nCT_DOUBLE_PICTURES) {
		NCT_DOUBLE_PICTURES = nCT_DOUBLE_PICTURES;
	}
	public String getNCT_WHOLE_PICTURES() {
		return NCT_WHOLE_PICTURES;
	}
	public void setNCT_WHOLE_PICTURES(String nCT_WHOLE_PICTURES) {
		NCT_WHOLE_PICTURES = nCT_WHOLE_PICTURES;
	}
	public String getMdReaddata() {
		return mdReaddata;
	}
	public void setMdReaddata(String mdReaddata) {
		this.mdReaddata = mdReaddata;
	}
	public String getROUND_CT() {
		return ROUND_CT;
	}
	public void setROUND_CT(String rOUND_CT) {
		ROUND_CT = rOUND_CT;
	}
	public String getNEW_ROUND_CT() {
		return NEW_ROUND_CT;
	}
	public void setNEW_ROUND_CT(String nEW_ROUND_CT) {
		NEW_ROUND_CT = nEW_ROUND_CT;
	}
	public String getOLD_REACTIVE_PICTURES() {
		return OLD_REACTIVE_PICTURES;
	}
	public void setOLD_REACTIVE_PICTURES(String oLD_REACTIVE_PICTURES) {
		OLD_REACTIVE_PICTURES = oLD_REACTIVE_PICTURES;
	}
	public String getNEW_REACTIVE_PICTURES() {
		return NEW_REACTIVE_PICTURES;
	}
	public void setNEW_REACTIVE_PICTURES(String nEW_REACTIVE_PICTURES) {
		NEW_REACTIVE_PICTURES = nEW_REACTIVE_PICTURES;
	}
	public String getSealId4() {
		return mSealId4;
	}
	public void setSealId4(String mSealId4) {
		this.mSealId4 = mSealId4;
	}
	public String getSealId5() {
		return mSealId5;
	}
	public void setSealId5(String mSealId5) {
		this.mSealId5 = mSealId5;
	}
	public String getSealId6() {
		return mSealId6;
	}
	public void setSealId6(String mSealId6) {
		this.mSealId6 = mSealId6;
	}
	public String getSealId7() {
		return mSealId7;
	}
	public void setSealId7(String mSealId7) {
		this.mSealId7 = mSealId7;
	}
	public String getSealId8() {
		return mSealId8;
	}
	public void setSealId8(String mSealId8) {
		this.mSealId8 = mSealId8;
	}
	public String getSealId9() {
		return mSealId9;
	}
	public void setSealId9(String mSealId9) {
		this.mSealId9 = mSealId9;
	}
	public String getOLD_MORE_PICTURES() {
		return OLD_MORE_PICTURES;
	}
	public void setOLD_MORE_PICTURES(String oLD_MORE_PICTURES) {
		OLD_MORE_PICTURES = oLD_MORE_PICTURES;
	}
	public String getNEW_MORE_PICTURES() {
		return NEW_MORE_PICTURES;
	}
	public void setNEW_MORE_PICTURES(String nEW_MORE_PICTURES) {
		NEW_MORE_PICTURES = nEW_MORE_PICTURES;
	}
	public String getMORE_SEAL_NUMBER() {
		return MORE_SEAL_NUMBER;
	}
	public void setMORE_SEAL_NUMBER(String mORE_SEAL_NUMBER) {
		MORE_SEAL_NUMBER = mORE_SEAL_NUMBER;
	}
	public String getBIN_POSITION_NUMBER() {
		return BIN_POSITION_NUMBER;
	}
	public void setBIN_POSITION_NUMBER(String bIN_POSITION_NUMBER) {
		BIN_POSITION_NUMBER = bIN_POSITION_NUMBER;
	}
	public String getDATATAG() {
		return DATATAG;
	}
	public void setDATATAG(String dATATAG) {
		DATATAG = dATATAG;
	}
}
