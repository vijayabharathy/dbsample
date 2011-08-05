package com.example.dbsample;

import android.app.Application;
//import android.util.Log;

public class DBWapper extends Application {

//   public static final String APP_NAME = "DbSample";

//public static final String getDBBase = null;  
   
   private DBBase db_base;


   
   @Override
   public void onCreate() {
      super.onCreate();
//      Log.d(APP_NAME, "APPLICATION onCreate");
      this.db_base = new DBBase(this);      
   }
   
   @Override
   public void onTerminate() {
//      Log.d(APP_NAME, "APPLICATION onTerminate");      
      super.onTerminate();      
   }

   public DBBase getDbBase() {
      return this.db_base;
   }

   public void setDataHelper(DBBase db_base) {
      this.db_base = db_base;
   }
}
