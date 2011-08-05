package com.example.dbsample;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;


import java.util.ArrayList;
import java.util.List;

public class DBBase {

   private static final String DATABASE_NAME = "dbexample.db";
   private static final int DATABASE_VERSION = 4;
   private static final String EXPENDITURE_TABLE = "expenditure";
   private static final String SETTINGS_TABLE = "defalut_setting";

   private Context context;
   private SQLiteDatabase db;

   private SQLiteStatement insertStmt;

   private static final String INSERT = "insert into " + EXPENDITURE_TABLE + "(reason, amount) values (?, ?)";




   public DBBase(Context context) {
      this.context = context;
      OpenHelper openHelper = new OpenHelper(this.context);
      this.db = openHelper.getWritableDatabase();
      this.insertStmt = this.db.compileStatement(INSERT);
   }
   
   public SQLiteDatabase getDb() {
      return this.db;
   }

   public long insert(String reason, double amount) {
	   this.insertStmt.bindString(1, reason + amount);

	      return this.insertStmt.executeInsert();

   }
   
   public void deleteAll() {
      this.db.delete(EXPENDITURE_TABLE, null, null);
   }

   public List<String> selectAll() {
      List<String> list = new ArrayList<String>();
      Cursor cursor = this.db.query(EXPENDITURE_TABLE, new String[] { "reason", "amount" }, null, null, null, null, "id desc");
      if (cursor.moveToFirst()) {
         do {
            list.add(cursor.getString(0)); 
         } while (cursor.moveToNext());
      }
      if (cursor != null && !cursor.isClosed()) {
         cursor.close();
      }
      return list;
   }
   
   public List<String> get_balance_amount() {
	   List<String> list = new ArrayList<String>();;
	   
	      Cursor cursor = this.db.query(EXPENDITURE_TABLE, new String[] { "sum(amount) as e_amount" }, null, null, null, null, null);
	      if (cursor.moveToFirst()) {
	         do {
	        	 list.add(cursor.getString(cursor.getColumnIndex("e_amount"))); 
	         } while (cursor.moveToNext());
	      }
	      if (cursor != null && !cursor.isClosed()) {
	         cursor.close();
	      }
	      return list;
	   }
   
   public List<String> AllSettings() {
	      List<String> list = new ArrayList<String>();
	      Cursor cursor = this.db.query(SETTINGS_TABLE, new String[] { "setting_key", "setting_value" }, null, null, null, null, "id desc");
	      if (cursor.moveToFirst()) {
	         do {
	            list.add(cursor.getString(cursor.getColumnIndex("setting_key")) + ":"+ cursor.getString(cursor.getColumnIndex("setting_value"))); 
	         } while (cursor.moveToNext());
	      }
	      if (cursor != null && !cursor.isClosed()) {
	         cursor.close();
	      }
	      return list;
	   }
   
   public long save_settings(String settings_key, double settings_value) {
	   long row = 0;
	   ContentValues contentvalue = new ContentValues();
		   Cursor c = this.db.query(SETTINGS_TABLE, new String[] {"setting_key"}, "setting_key=?", new String[]{settings_key}, null, null, null);
		   
		   if(c.getCount() > 0){
			   contentvalue.put("setting_value", settings_value);
			   this.db.update(SETTINGS_TABLE, contentvalue, "setting_key=?", new String[]{settings_key});
		   }else{			   
			   contentvalue.put("setting_key", settings_key);
			   contentvalue.put("setting_value", settings_value);
			   this.db.insert(SETTINGS_TABLE, null, contentvalue);
			   		  
		   }
		   return row;
	   
   }
   

   private static class OpenHelper extends SQLiteOpenHelper {

      OpenHelper(Context context) {
         super(context, DATABASE_NAME, null, DATABASE_VERSION);
      }

      @Override
      public void onCreate(SQLiteDatabase db) {
         db.execSQL("CREATE TABLE " + EXPENDITURE_TABLE + " (id INTEGER PRIMARY KEY, reason TEXT, amount NUMERIC)");
         db.execSQL("CREATE TABLE " + SETTINGS_TABLE + " (id INTEGER PRIMARY KEY, setting_key TEXT, setting_value NUMERIC)");
      }

      @Override
      public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
         
         db.execSQL("DROP TABLE IF EXISTS " + EXPENDITURE_TABLE);
         db.execSQL("DROP TABLE IF EXISTS " + SETTINGS_TABLE);
         onCreate(db);
      }
   }
}