package com.example.myapp;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

	GPSValidate connection;
	double currLong;
	double currLat;
	TextView textView;
	public static String add;
	String result;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		connection = new GPSValidate(this);
		
		Thread t1 = new Thread(new db());
		t1.start();
//		try {
//			t1.join();
//		} catch (InterruptedException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		
		
		
		if(connection.canGetLocation()){
			currLong = connection.getLongitude();
			currLat = connection.getLatitude();

			Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
	
			try {
	        	List<Address> addresses = geoCoder.getFromLocation(currLat,currLong, 2);
	        	
	        	add = "Your Location\n";
	            if (addresses.size() > 0) 
	            {
	                for(int j = 1;j<addresses.size();j++)
	                	for (int i=0; i<addresses.get(j).getMaxAddressLineIndex();i++){
	                		add += addresses.get(j).getAddressLine(i) + "\n";
	                		
	                	}
	            }
	            EditText t = (EditText) findViewById(R.id.editText1);
	          //  t.setText("Regal");
	            t.setTextColor(Color.rgb(255,255,255));
	            textView = (TextView) findViewById(R.id.TextView2);
	            textView.setText(add);
	            textView.setTextSize(20);
	            textView.setTextColor(Color.rgb(255,255,255));
	       //     Toast.makeText(getBaseContext(), add, Toast.LENGTH_SHORT).show();
	         } catch (IOException e) {                
	                e.printStackTrace();
	         }
		}
	}

	public void onClick(View view){
		
		Intent intent = new Intent(this,Validate.class);
		
		EditText editText = (EditText) findViewById(R.id.editText1);
		String destination = editText.getText().toString();
		intent.putExtra("Destination", destination);
		double[] myCood = {currLat,currLong};
		intent.putExtra("myCood",myCood);
		startActivity(intent);
	}

	
	public class db implements Runnable{

		DataTest d1;
		SQLiteDatabase db;
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			d1 = new DataTest(MainActivity.this);
			
			
			try {
				d1.createDataBase();
				System.out.println("Database created");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
