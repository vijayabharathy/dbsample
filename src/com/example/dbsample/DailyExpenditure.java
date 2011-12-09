package com.example.dbsample;

import java.util.List;



import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import android.widget.AdapterView.OnItemSelectedListener;
import android.content.DialogInterface;
import android.content.Intent;


public class DailyExpenditure extends Activity {
    /** Called when the activity is first created. */
	
	private Spinner expenditure_type;
	private EditText amount_put, new_expenditure_type;
	private Button save_button;
	
	private ArrayAdapter<String> adapter;
	private TableLayout tableLayout1;
	private Button delete_button;
	 
	private DBBase db_base;
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        this.db_base = new DBBase(getApplicationContext());

        this.expenditure_type = (Spinner) this.findViewById(R.id.expenditure_type);
        this.amount_put = (EditText) this.findViewById(R.id.amount_put);
        this.save_button = (Button) this.findViewById(R.id.save_button);
        
        
        

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
        	  boolean valid = true;	
        	  String expenditure_type = DailyExpenditure.this.expenditure_type.getSelectedItem().toString();
        	  if (DailyExpenditure.this.expenditure_type.getSelectedItem().toString() == "Others"){
        		  expenditure_type = DailyExpenditure.this.new_expenditure_type.getText().toString();
        		  String[] new_expenditure =  new String[] {
        				  expenditure_type
        			   };   
        		  if (DailyExpenditure.this.db_base.valid_expenditure_type(expenditure_type)){
        			  DailyExpenditure.this.db_base.insert_into_expenditure(new_expenditure, getApplicationContext());
        		  }
        		  else
        		  {
        			  AlertDialog alertDialog = new AlertDialog.Builder(DailyExpenditure.this).create();
        			   alertDialog.setMessage("Already you have this expenditure type !");
        			   alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
        				      public void onClick(DialogInterface dialog, int which) {
        				    	  new_expenditure_type.setVisibility(View.VISIBLE);
        				 
        				    } }); 
        			   alertDialog.show();
        			   valid = false;
        			
        		  } 
        	  }
        	  if (valid){
              new OnSaveTask().execute(expenditure_type, DailyExpenditure.this.amount_put.getText().toString());
        	  }
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
	         
	         expendituresList(result);
	         
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
	
	private void expendituresList(String result) {
		TableLayout tl = (TableLayout)findViewById(R.id.tableLayout1);
		tl.removeAllViews();
        TableRow tableRow;
        TextView textView;

        String[] rows = result.split("\n");
        for (Integer i = 0; i < rows.length; i++) {
       	 tableRow = new TableRow(getApplicationContext());
       	 
       	 	String[] cols = rows[i].split(";");
       	 	for(Integer j = 0; j < (cols.length -1); j++) {
       	 			 
	        		 textView = new TextView(getApplicationContext());
	        		 textView.setText(cols[j]);		        		 
	        		 textView.setPadding(5, 10, 5, 10);
	        		 tableRow.addView(textView);
	        		 
	        		 
       	 	}
       	 Button button = new Button(this);
       	 final Integer row_id = Integer.parseInt(cols[cols.length - 1]);
         button.setId(row_id);
         button.setOnClickListener(new OnClickListener(){
        	 public void onClick(final View  v) {
        		 DailyExpenditure.this.db_base.delete_from_exp(row_id);
        		 new OnLoadTask().execute();
        	 }
         });         
         button.setText("Delete");
         button.setMinimumHeight(5);
         button.setMinimumWidth(50);
         tableRow.addView(button);
       	 	
       	 tl.addView(tableRow);
       	 }
        long sum_of_expenditure = DailyExpenditure.this.db_base.amount_spend();
    	int base_amount = Integer.parseInt(DailyExpenditure.this.db_base.get_key_from_setting("base_amount"));
        for (Integer i = 0; i < 1; i++) {
        	
        	String exp_sum = sum_of_expenditure + "";
          	 tableRow = new TableRow(getApplicationContext());
          	
       		 textView = new TextView(getApplicationContext());
       		 
       		 textView.setText("Total = " + exp_sum);		        		 
       		 textView.setPadding(5, 10, 5, 10);
       		 tableRow.addView(textView);
       		
       		textView = new TextView(getApplicationContext());
      		 textView.setText("BALANCE  = "  + (base_amount - sum_of_expenditure));		        		 
      		 textView.setPadding(5, 10, 5, 10);
      		 tableRow.addView(textView);
      		tl.addView(tableRow);
        }   	
        
	}
	
	 public long get_balance_amount(){
		 long sum_of_expenditure = DailyExpenditure.this.db_base.amount_spend();
	     int base_amount = Integer.parseInt(DailyExpenditure.this.db_base.get_key_from_setting("base_amount"));
	     return base_amount - sum_of_expenditure;
	 }
	 
}