package com.example.dbsample;




import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.os.AsyncTask;
import android.os.Bundle;

public class Settings extends Activity {
	
	private EditText base_amount;
	private Button save_setting;
	private DBBase db_base;
	private TextView out_text;
	
//	public void onClick(final View v) {
//		
//		
//	}
	@Override
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.settings);
	        
	        this.db_base = new DBBase(getApplicationContext());
	        
	        this.base_amount = (EditText) this.findViewById(R.id.base_amount);
	        this.save_setting = (Button) this.findViewById(R.id.save_setting1);
	        this.out_text = (TextView) this.findViewById(R.id.out_text);
	        new OnLoadTask().execute();
	        this.save_setting.setOnClickListener(new OnClickListener() {
	        	public void onClick(final View v) {
	              new OnSaveTask().execute(Settings.this.base_amount.getText().toString());
	              
	            }
	         });
	 }
	
	
	private class OnLoadTask extends AsyncTask<String, Void, String>{
		 private final ProgressDialog dialog = new ProgressDialog(Settings.this);
		 
		 protected void onPreExecute() {
	         this.dialog.setMessage("Selecting data...");
	         this.dialog.show();
	      }

	     
		 protected String doInBackground(final String... args) {
	         List<String> names = Settings.this.db_base.AllSettings();
	         StringBuilder sb = new StringBuilder();
	         for (String name : names) {
	            sb.append(name + "\n");
	         }
	         return sb.toString();
	      }

	 
	      protected void onPostExecute(final String result) {
	         if (this.dialog.isShowing()) {
	            this.dialog.dismiss();
	         }
	         
	         Settings.this.out_text.setText(result);
	         
	      }
		
	}
	
	private class OnSaveTask extends AsyncTask<String, Void, String>{
		private final ProgressDialog dialog = new ProgressDialog(Settings.this);
		 
		 protected void onPreExecute() {
	         this.dialog.setMessage("inserting data...");
	         this.dialog.show();
	      }


	      protected String doInBackground(final String... args) {
	    	  double amount = Double.valueOf(args[0]);
	    	  Settings.this.db_base.save_settings("base_amount", amount);
	    	  return null;
	      }

	      // can use UI thread here
	      protected void onPostExecute(final String result) {
	    	  this.dialog.setMessage("settings updated...");
	    	  
	          this.dialog.dismiss();
	         
	         new OnLoadTask().execute();
	      }
		
	}
	
}
