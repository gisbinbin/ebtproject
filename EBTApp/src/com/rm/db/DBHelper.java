package com.rm.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	//数据库名称
    private static final String DB_NAME="dbdata.sqlite";
    private static final String CREATE_TBL="CREATE TABLE RecordData(_id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , " +
			"username VARCHAR,usernumber VARCHAR,assetsnumber VARCHAR,useraddress VARCHAR,equipmentclass VARCHAR,BIN_POSITION_NUMBER VARCHAR,readdata VARCHAR,wreaddata VARCHAR,dtime VARCHAR,"+
    		"n_assetsnumber VARCHAR,n_readdata VARCHAR,n_wreaddata VARCHAR,"+
			"seal1 VARCHAR,seal2 VARCHAR,seal3 VARCHAR,seal4 VARCHAR,seal5 VARCHAR,seal6 VARCHAR,seal7 VARCHAR,seal8 VARCHAR,seal9 VARCHAR,"+
			"itime VARCHAR,dstatic VARCHAR,istatic VARCHAR,static VARCHAR,"+
    		"ameterspic VARCHAR,tmeterspic VARCHAR,OLD_REACTIVE_PICTURES VARCHAR,allpic VARCHAR,n_ameterspic VARCHAR,n_tmeterspic VARCHAR,NEW_REACTIVE_PICTURES VARCHAR,n_allpic VARCHAR," +
			"OCT_SINGLE_PICTURES VARCHAR,OCT_DOUBLE_PICTURES VARCHAR,OCT_WHOLE_PICTURES VARCHAR,ROUND_CT VARCHAR,NCT_SINGLE_PICTURES VARCHAR,NCT_DOUBLE_PICTURES VARCHAR,NCT_WHOLE_PICTURES VARCHAR,NEW_ROUND_CT VARCHAR,"+
    		"NEW_MORE_PICTURES VARCHAR,OLD_MORE_PICTURES VARCHAR,MORE_SEAL_NUMBER VARCHAR,"+
			"TABLE_CABINET_NO VARCHAR,TAG VARCHAR)";
    
    private static final String CREATE_BPTBL="CREATE TABLE BPIMG(_id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , " +
			"USER_NAME VARCHAR,USER_NO VARCHAR,ADDRESS VARCHAR,DEVICE_CLASS VARCHAR,ASSET_TAG VARCHAR,NEW_ASSET_TAG VARCHAR,PROBLEM VARCHAR,"
    		+"DESCRIBE VARCHAR,PICDATA VARCHAR,TAG VARCHAR,WORKSTATIC VARCHAR,SUBMITSTATIC VARCHAR)";
    
    private static final String CREATE_TAB2="CREATE TABLE TabBoxData(_id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL ,PICTURES VARCHAR,PICTURES2 VARCHAR,PICTURES3 VARCHAR,MORE_PICTURES VARCHAR,TABLE_CABINET_NO VARCHAR,"
    		+"HLY_CREATE_DATE VARCHAR,HLY_EDIT_MAN VARCHAR,HLY_BOX_CLASS VARCHAR,HLY_BOX_VILLAGE VARCHAR,TSTATIC VARCHAR,STATIC VARCHAR,DATACOUNT VARCHAR,SEAL_NUMBER VARCHAR,SEAL_NUMBER2 VARCHAR,SEAL_NUMBER3 VARCHAR,SEAL_NUMBER4 VARCHAR,SEAL_NUMBER5 VARCHAR,SEAL_NUMBER6 VARCHAR,TAG VARCHAR)";
    
    private static final String CREATE_TAB3="CREATE TABLE FailImg(_id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL ,PICTURENAME VARCHAR)";
    
    private SQLiteDatabase db;

	public DBHelper(Context context) {
		super(context, DB_NAME, null, 2);
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		this.db=arg0;
		arg0.execSQL(CREATE_TBL);
		arg0.execSQL(CREATE_BPTBL);
		arg0.execSQL(CREATE_TAB2);
		arg0.execSQL(CREATE_TAB3);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		
	}
	
	/*
     * 插入方法
     */
    public void insert(ContentValues values,String tabName)
    {
        //获得SQLiteDatabase实例
        SQLiteDatabase db=getWritableDatabase();
        //插入
        db.insert(tabName, null, values);
        //关闭
        db.close();
    }
    /*
     * 查询方法
     */
    public Cursor query (String table,String[] columns, String selection,String[] selectionArgs, String groupBy,String having,String orderBy)
    {
        SQLiteDatabase db=getWritableDatabase();
        Cursor c=db.query(table, columns, selection, selectionArgs, groupBy, having,orderBy);
        return c;
    }
    /*
     * 更新方法
     */
    public int update (String table,ContentValues values, String whereClause,String[] whereArgs)
    {
        SQLiteDatabase db=getWritableDatabase();
        int i =db.update(table, values, whereClause, whereArgs);
        return i;
    }
    
    /*
     * 删除方法
     */
    public void del(String tabName,int id)
    {
        if(db==null)
        {
            //获得SQLiteDatabase实例
            db=getWritableDatabase();           
        }
        //执行删除
        db.delete(tabName, "_id=?", new String[]{String.valueOf(id)});     
    }
    public void delete(String tabName,String selection,String[] selectionArgs)
    {
        if(db==null)
        {
            //获得SQLiteDatabase实例
            db=getWritableDatabase();           
        }
        //执行删除
        db.delete(tabName, selection, selectionArgs);  
    }
    public void delall(String tableName)
    {
    	if(db==null)
        {
            //获得SQLiteDatabase实例
            db=getWritableDatabase();
        }
        //执行删除
        db.execSQL("delete from "+tableName);
        db.execSQL("UPDATE sqlite_sequence SET seq =0 WHERE name ='"+tableName+"'");
    }
    
    /*
     * 关闭数据库
     */
    public void colse()
    {
        if(db!=null)
        {
            db.close();
        }
    }
}
