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

   private static final String DATABASE_NAME = "moneymanager.db";
   private static final int DATABASE_VERSION = 9;
   private static final String EXPENDITURE_TABLE = "expenditure";
   private static final String SETTINGS_TABLE = "defalut_setting";
   private static final String EXPENDITURE_TYPE_TABLE = "expenditure_type";

   private Context context;
   private SQLiteDatabase db;

   private SQLiteStatement insertStmt;

   private static final String INSERT_TO_EXP = "insert into " + EXPENDITURE_TABLE + "(expenditure_type_id, amount) values (?, ?)";
   private static final String INSERT_TO_EXP_TYPE = "insert into " + EXPENDITURE_TYPE_TABLE + "(exp_type) values (?)";
   private static final String DELETE_FROM_EXP = "delete from " + EXPENDITURE_TABLE + " where id = ?";
   
   private static final String[] DEFAULT_EXP_TYPE =  new String[] {
	   "Food", "Travel", "House Rent", "Internet", "Telephone"
   };

   private OpenHelper open_helper;
   
   public DBBase(Context context) {
      this.context = context;
      OpenHelper openHelper = new OpenHelper(this.context);
      this.db = openHelper.getWritableDatabase();
      this.insertStmt = this.db.compileStatement(INSERT_TO_EXP);
      
   }
   
   public SQLiteDatabase getDb() {
      return this.db;
   }

   public long insert(String reason, double amount) {
	   this.insertStmt.bindDouble(2, amount);
	   this.insertStmt.bindString(1, reason);

	      return this.insertStmt.executeInsert();

   }
   
   public void deleteAll() {
      this.db.delete(EXPENDITURE_TABLE, null, null);
   }
   
   public void delete_from_exp(int exp_id){
	   SQLiteStatement stmt = db.compileStatement(DELETE_FROM_EXP);
 		  stmt.bindLong(1, exp_id);
 		  stmt.execute();
   
   }

   public List<String> selectAll() {
      List<String> list = new ArrayList<String>();
      Cursor cursor = this.db.query(EXPENDITURE_TABLE, new String[] { "expenditure_type_id", "amount", "id" }, null, null, null, null, "id desc");
      if (cursor.moveToFirst()) {
         do {
            list.add(cursor.getString(0) + ";" + cursor.getDouble(1) + ";" + cursor.getInt(2)); 
         } while (cursor.moveToNext());
      }
      if (cursor != null && !cursor.isClosed()) {
         cursor.close();
      }
      return list;
   }
   
   public List<String> get_balance_amount() {
	   List<String> list = new ArrayList<String>();
	   
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
   
   public List<String> ExpenditureTypes() {
	      List<String> list = new ArrayList<String>();
	      
	      Cursor cursor = this.db.query(EXPENDITURE_TYPE_TABLE, new String[] { "exp_type" }, null, null, null, null, "id desc");
	      if (cursor.moveToFirst()) {
	         do {
	            list.add(cursor.getString(cursor.getColumnIndex("exp_type"))); 
	         } while (cursor.moveToNext());
	      }
	      if (cursor != null && !cursor.isClosed()) {
	         cursor.close();
	      }
	      list.add("Others");
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
   
   public void insert_into_expenditure(String[] expenditure_type, Context context){
	  open_helper = new OpenHelper(context);
	  this.open_helper.insert_into_expenditure(expenditure_type, this.db);
      }
   
   public long amount_spend(){
	   long exp_amount = 0;


	   Cursor cursor = this.db.query(EXPENDITURE_TABLE, new String[] { "sum(amount) as f_amount"}, null, null, null, null, null);
	   if(cursor.moveToFirst()) {
		    exp_amount = cursor.getInt(0);
		}
	return exp_amount;
	  
	   
   }
  
   public String get_key_from_setting(String key_name){
	   String setting_value = "0";
	   Cursor c = this.db.query(SETTINGS_TABLE, new String[]{"setting_value"}, "setting_key=?", new String[]{key_name} , null, null, null);
	   if(c.moveToFirst()) {
		   setting_value = c.getString(c.getColumnIndex("setting_value"));
	   }
	   return setting_value;
   }

   public boolean valid_expenditure_type(String newExpenditure){
	   boolean valid = true;
	   Cursor c = this.db.query(EXPENDITURE_TYPE_TABLE,  new String[]{"exp_type"}, "exp_type=?", new String[]{newExpenditure} , null, null, null);
	   if(c.moveToFirst()) {
		   valid = false;
	   }
	   return valid;
   }
   
   private static class OpenHelper extends SQLiteOpenHelper {
	 
      OpenHelper(Context context) {
         super(context, DATABASE_NAME, null, DATABASE_VERSION);
      }

      @Override
      public void onCreate(SQLiteDatabase db) {
         db.execSQL("CREATE TABLE " + EXPENDITURE_TABLE + " (id INTEGER PRIMARY KEY, expenditure_type_id TEXT, amount NUMERIC)");
         db.execSQL("CREATE TABLE " + SETTINGS_TABLE + " (id INTEGER PRIMARY KEY, setting_key TEXT, setting_value NUMERIC)");
         db.execSQL("CREATE TABLE " + EXPENDITURE_TYPE_TABLE + " (id INTEGER PRIMARY KEY, exp_type TEXT)");
         insert_default_exp_type(db);
      }
      
      public void insert_default_exp_type(SQLiteDatabase db){    	      	  
    	  this.insert_into_expenditure(DEFAULT_EXP_TYPE, db);

      }
      
      
      public void insert_into_expenditure(String[] expenditure_type, SQLiteDatabase db){
   	   SQLiteStatement stmt = db.compileStatement(INSERT_TO_EXP_TYPE);
    	  for (int i = 0; i < expenditure_type.length; ++i) {    		  
    		  stmt.bindString(1, expenditure_type[i]);
    		  stmt.execute();
          }
   	   
   	   
      }

	@Override
      public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
         
         db.execSQL("DROP TABLE IF EXISTS " + EXPENDITURE_TABLE);
         db.execSQL("DROP TABLE IF EXISTS " + SETTINGS_TABLE);
         db.execSQL("DROP TABLE IF EXISTS " + EXPENDITURE_TYPE_TABLE);
         onCreate(db);
      }
      
         }
}