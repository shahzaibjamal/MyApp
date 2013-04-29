package com.example.myapp;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import com.google.android.maps.GeoPoint;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class Validate extends Activity {

	TextView textView;
	double currLat;
	double currLong;
	GeoPoint MyGeopPoint;
	GeoPoint destinationGeopPoint;
	GooglePlaces googlePlaces;
	PlacesList nearPlaces;
	double[] myCood;
	double[] destinationCood;
	String result;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_validate);
		Intent intent = getIntent();
		String destination = intent.getStringExtra("Destination");
		myCood = intent.getExtras().getDoubleArray("myCood");
		textView = (TextView)findViewById(R.id.textView3);
		
//		String returned = intent.getStringExtra("result");
	
	//		Log.d("returned result", returned);
		SearchLocation s = new SearchLocation(destination,this);
		s.execute();
		try {
			String sss = s.get();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
			
		Log.d("values returned", result);
		
		Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
		
		if(!destination.equals("")){
			try{
				List<Address> address = geoCoder.getFromLocationName(destination, 3, 31.386468, 74.149475, 31.612457, 74.420013);
				
				if(address.size()>0){
					currLat = address.get(0).getLatitude();
					currLong = address.get(0).getLongitude();

					for(int i =0;i<address.size();i++)
						destination += "\n"+address.get(i).getFeatureName();
				}else{
					textView.setText("No results Found");
					return;
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	
		destinationCood = new double[2];
		destinationCood[0] = currLat;
		destinationCood[1] = currLong;		
		textView.setText(destination);
		textView.setTextSize(15);
	}
	
	public void validateOnClick(View view){
		Intent intent = new Intent(this,Search.class);
		intent.putExtra("myCood",myCood);
		intent.putExtra("destinationCood", destinationCood);
		startActivity(intent);	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.validate, menu);
		return true;
	}
	
	 private class SearchLocation extends AsyncTask<Void, Void, String>{
		    private ProgressDialog progressDialog;

		    DataTest d1;
		    String destination;
		    Cursor cursor;
		    Context context;
		    
		    SearchLocation(String destination, Context context){
		    	this.destination = destination;
		    	result = "";
		    	this.context = context;
		    }
		    
		    @Override
		    protected void onPreExecute() {
		        // TODO Auto-generated method stub
		        super.onPreExecute();
		        progressDialog = new ProgressDialog(Validate.this);
		        progressDialog.setMessage("Fetching Location, Please wait...");
		        progressDialog.setIndeterminate(true);
		        progressDialog.show();
		    }
		    
		    @Override
		    protected String doInBackground(Void... params) {
		    	d1 = new DataTest(Validate.this);
		    	
		    	int destinationLength;
		    	int dataLength;
		    	try {
					d1.createDataBase();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}catch(Exception e){
					e.printStackTrace();
				}
		    	int distance = -1;
		    	double max = -5;
		    	int index = -1;
		    	double div = -.01;
		    	SQLiteDatabase db = d1.openDataBase();
		    	//SQLiteDatabase db = d1.getInstance();
		    	
		    	
		    	
		    	System.out.println(DatabaseUtils.queryNumEntries(db,"route"));
		    	
		    	for(int i = 1;i<18;i++){
		    		cursor = db.query("route", new String[] { "_id","bus_number","source","destination"}, "_id=?",
		    				new String[]{String.valueOf(i)}, null, null, null, null);
		    		if (cursor != null)
		    			cursor.moveToFirst();
		    		
		    		String a = cursor.getString(2);
		    		String temp1 = destination.toLowerCase();
		    		String temp2 = a.toLowerCase();
		    		distance = computeLevenshteinDistance(temp1,temp2);
		    		
		    		destinationLength = destination.length();
		    		dataLength = a.length();
		    		
		    		if(distance != 0){
		    			div = (((dataLength-distance)/distance)*dataLength);
		    		}
		    		
		    		System.out.println(a + " location "+ distance+ " distance " + " div " +div);
		    		
		    		if(destinationLength < dataLength  && max < div){
		    			max = div;
		    			index = i;
		    			System.out.println(div + " location " + a);
		    		}
		    		if(distance == 0){
		    			index = i;
		    		}
		    	}
		    	
		    	if(index != -1){
		    		cursor = db.query("route", new String[] { "_id","bus_number","source","destination"}, "_id=?",
		    				new String[]{String.valueOf(index)}, null, null, null, null);
		    		if (cursor != null)
		    			cursor.moveToFirst();
		    		
		    		result = cursor.getString(2);
		    	}
		    	db.close();
		    	if(!result.equals("")){
		    		Log.d("return ","return value contained " + result);
		    		return result;
		    		
		    	}else{
		    		Log.d("not return","null");
		    		
		    		return null;
		    	}
		    }
		    @Override
		    protected void onPostExecute(String result1) {
		        progressDialog.dismiss();
		        result = result1;
		        super.onPostExecute(result1);   

		    }
		}
	 
	 private int minimum(int a, int b, int c) {
         return Math.min(Math.min(a, b), c);
	 }

	 public int computeLevenshteinDistance(CharSequence str1, CharSequence str2) {
         int[][] distance = new int[str1.length() + 1][str2.length() + 1];

         for (int i = 0; i <= str1.length(); i++)
                 distance[i][0] = i;
         for (int j = 1; j <= str2.length(); j++)
                 distance[0][j] = j;

         for (int i = 1; i <= str1.length(); i++)
                 for (int j = 1; j <= str2.length(); j++)
                         distance[i][j] = minimum(
                                         distance[i - 1][j] + 1,
                                         distance[i][j - 1] + 1,
                                         distance[i - 1][j - 1]
                                                         + ((str1.charAt(i - 1) == str2.charAt(j - 1)) ? 0
                                                                         : 1));

         return distance[str1.length()][str2.length()];
 }

	 
	 
	
	

}
