package com.example.dbsample;

import java.util.List;



import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import android.widget.AdapterView.OnItemSelectedListener;
import android.content.Intent;


public class DailyExpenditure extends Activity {
    /** Called when the activity is first created. */
	
	private Spinner expenditure_type;
	private EditText amount_put, new_expenditure_type;
	private Button save_button;
	private TextView out_text;
	private ArrayAdapter<String> adapter;
	
	 
	private DBBase db_base;
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        this.db_base = new DBBase(getApplicationContext());

        this.expenditure_type = (Spinner) this.findViewById(R.id.expenditure_type);
        this.amount_put = (EditText) this.findViewById(R.id.amount_put);
        this.save_button = (Button) this.findViewById(R.id.save_button);
        this.out_text = (TextView) this.findViewById(R.id.out_text);

        this.new_expenditure_type = (EditText) findViewById(R.id.new_expenditure_type);
        
        this.new_expenditure_type.setVisibility(View.GONE);

        
        
        Button setting_button = (Button) findViewById(R.id.setting);
        setting_button.setOnClickListener(new OnClickListener() {
            public void onClick(final View v) {                   
                startActivity(new Intent(v.getContext(), Settings.class));
            }
        });
        
        Spinner spinner = (Spinner) findViewById(R.id.expenditure_type);
        
        this.adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1, db_base.ExpenditureTypes());

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if(arg0.getItemAtPosition(arg2).toString() == "Others"){
					new_expenditure_type.setVisibility(View.VISIBLE);
				}
				else{
					new_expenditure_type.setVisibility(View.GONE);
				}
				
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				
				
			}
           

        });

        

        new OnLoadTask().execute();
        this.save_button.setOnClickListener(new OnClickListener() {
        	public void onClick(final View v) {
        	  String expenditure_type = DailyExpenditure.this.expenditure_type.getSelectedItem().toString();
        	  if (DailyExpenditure.this.expenditure_type.getSelectedItem().toString() == "Others"){
        		  expenditure_type = DailyExpenditure.this.new_expenditure_type.getText().toString();
        		  String[] new_expenditure =  new String[] {
        				  expenditure_type
        			   };        		  
        		  DailyExpenditure.this.db_base.insert_into_expenditure(new_expenditure, getApplicationContext());
        	  }
              new OnSaveTask().execute(expenditure_type, DailyExpenditure.this.amount_put.getText().toString());
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

	         DailyExpenditure.this.out_text.setText(result);
	         Spinner spinner = (Spinner) findViewById(R.id.expenditure_type);
	         DailyExpenditure.this.adapter = new ArrayAdapter<String>(DailyExpenditure.this, android.R.layout.simple_spinner_item, android.R.id.text1, db_base.ExpenditureTypes());

	         spinner.setAdapter(adapter);

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


	      protected void onPostExecute(final String result) {
	         if (this.dialog.isShowing()) {
	            this.dialog.dismiss();
	         }
	         new OnLoadTask().execute();
	      }
		
	}
	
	
	 

}