package com.example.dbsample;

import java.util.List;



import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;


public class DailyExpenditure extends Activity {
    /** Called when the activity is first created. */
	
	private EditText expenditure_type;
	private EditText amount_put;
	private Button save_button;
	private TextView out_text;
//	private TextView balance_amount;
	
	 
	private DBBase db_base;
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        this.db_base = new DBBase(getApplicationContext());
//        this.balance_amount = (TextView) this.findViewById(R.id.balance_amount);
        this.expenditure_type = (EditText) this.findViewById(R.id.expenditure_type);
        this.amount_put = (EditText) this.findViewById(R.id.amount_put);
        this.save_button = (Button) this.findViewById(R.id.save_button);
        this.out_text = (TextView) this.findViewById(R.id.out_text);
        Button setting_button = (Button) findViewById(R.id.setting);
        setting_button.setOnClickListener(new OnClickListener() {
            public void onClick(final View v) {                   
                startActivity(new Intent(v.getContext(), Settings.class));
            }
        });


        new OnLoadTask().execute();
        this.save_button.setOnClickListener(new OnClickListener() {
        	public void onClick(final View v) {
              new OnSaveTask().execute(DailyExpenditure.this.expenditure_type.getText().toString(), DailyExpenditure.this.amount_put.getText().toString());
              
            }
         });
        
    }
	
	
	public class OnLoadTask extends AsyncTask<String, Void, String>{
		 private final ProgressDialog dialog = new ProgressDialog(DailyExpenditure.this);
		 
		 protected void onPreExecute() {
	         this.dialog.setMessage("Selecting data...");
	         this.dialog.show();
	      }

	     
		 protected String doInBackground(final String... args) {
	         List<String> names = DailyExpenditure.this.db_base.selectAll();	         
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
//	         List<String> balance_amount = DBSample.this.db_base.get_balance_amount();
//	         StringBuilder sb = new StringBuilder();
//	         for (String name : balance_amount) {
//		            sb.append(name + "\n");
//		            
//		         }
//	         DBSample.this.balance_amount.setText(sb.toString());
	         DailyExpenditure.this.out_text.setText(result);
	         
	         DailyExpenditure.this.expenditure_type.setText("");
	         DailyExpenditure.this.amount_put.setText("");
	      }
		
	}
	private class OnSaveTask extends AsyncTask<String, Void, String>{
		private final ProgressDialog dialog = new ProgressDialog(DailyExpenditure.this);
		 
		 protected void onPreExecute() {
	         this.dialog.setMessage("inserting data...");
	         this.dialog.show();
	      }


	      protected String doInBackground(final String... args) {
	    	  double amount = Double.valueOf(args[1]);
	    	  DailyExpenditure.this.db_base.insert(args[0], amount);
	    	  return null;
	      }

	      // can use UI thread here
	      protected void onPostExecute(final String result) {
	         if (this.dialog.isShowing()) {
	            this.dialog.dismiss();
	         }
	         new OnLoadTask().execute();
	      }
		
	}
	
	
	 

}