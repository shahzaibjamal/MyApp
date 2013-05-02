package com.example.myapp;

import java.io.IOException;
import java.util.ArrayList;
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
	ArrayList<Match> indexes = new ArrayList<Match>();
	Match match;
	TextView textViewBusID;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_validate);
		Intent intent = getIntent();
		String destination = intent.getStringExtra("Destination");
		myCood = intent.getExtras().getDoubleArray("myCood");
		textView = (TextView)findViewById(R.id.textView3);
		textViewBusID = (TextView)findViewById(R.id.textView4);
		
		SearchLocation s = new SearchLocation(destination,this);
		s.execute();
		try {
			Match sss = s.get();
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
		textViewBusID.setText("Bus ID = "+match.getBusID());
		textViewBusID.setTextSize(25);
		textView.setText(destination);
		textView.setTextSize(15);
	}

	public void validateOnClick(View view){
		Intent intent = new Intent(this,Search.class);
		intent.putExtra("myCood",myCood);
		intent.putExtra("destinationCood", destinationCood);
		intent.putExtra("match", match);
		startActivity(intent);	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.validate, menu);
		return true;
	}

	 private class SearchLocation extends AsyncTask<Void, Void, Match>{
		    private ProgressDialog progressDialog;

		    DataTest d1;
		    String destination;
		    Cursor cursor;
		    Context context;
		    ArrayList<String> bus_name;
		    SearchLocation(String destination, Context context){
		    	this.destination = destination;
		    	result = "";
		    	this.context = context;
		    	this.bus_name = new ArrayList<String>();
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
		    protected Match doInBackground(Void... params) {
		    	d1 = new DataTest(Validate.this);

		    	bus_name.add("B-1");
		    	bus_name.add("B-2");
		    	bus_name.add("B-4");
		    	bus_name.add("B-5");
		    	bus_name.add("B-7");
		    	bus_name.add("B-8");
		    	bus_name.add("B-10");
		    	bus_name.add("B-11");
		    	bus_name.add("B-12");
		    	bus_name.add("B-12A");
		    	bus_name.add("B-14");
		    	bus_name.add("B-16");
		    	bus_name.add("B-17");
		    	bus_name.add("B-18");
		    	bus_name.add("B-19");
		    	bus_name.add("B-20");
		    	bus_name.add("B-21");
		    	bus_name.add("B-22");
		    	bus_name.add("B-23");
		    	bus_name.add("B-24");
		    	bus_name.add("B-26");
		    	bus_name.add("B-28");
		    	bus_name.add("B-33");
		    	bus_name.add("B-37-49-A");
		    	bus_name.add("B-41");
		    	bus_name.add("B-42");
		    	bus_name.add("B-43");
		    	bus_name.add("B-49");
		    	bus_name.add("B-51");
		    	bus_name.add("B-53");
		    	bus_name.add("B-54");
		    	bus_name.add("B-56");
		    	
		    	
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
//		    	int index = -1;
		    	SQLiteDatabase db = d1.openDataBase();
		    	long size = DatabaseUtils.queryNumEntries(db,"route");
			    //	System.out.println(size);
			    	
//			    	Match match;
			    	String temp1 = destination.toLowerCase();
			    	destinationLength = destination.length();
			   
			    	String index = null;
			    	String busID = null;
			    	
			    	boolean flag = false;
			    	
		    	for(int i = 1;i<bus_name.size();i++){
		    		cursor = db.query("route", new String[] { "_id","bus_number","source","destination","lat","long"}, "_id=?",
		    				new String[]{String.valueOf(i)}, null, null, null, null);
		    		int distance = -1;
			    	double max = -5;
			    	double div = -.01;
		    		
		    		while(cursor.moveToNext()){
		    			String a = cursor.getString(2);
		    			
		    			String temp2 = a.toLowerCase();
		    			distance = computeLevenshteinDistance(temp1,temp2);
		    						
		    			dataLength = a.length();
		    			
	    				if(distance != 0){
	    					div = (dataLength-distance);
	    					div = div/distance*dataLength;
	    				}

	    				if(distance == 0){
	    					index = cursor.getString(0);
	    					busID = cursor.getString(1);
	    					match = new Match(index,busID,max);
		    				indexes.add(match);
	    					
		    			}else if(destinationLength < dataLength  && max < div){ 
		    				max = div;
		    				index = cursor.getString(0);
	    					busID = cursor.getString(1);
	    					flag = true;
	    					//System.out.println(busID + " location " + a+ " index = "+ index);
						}
		    		}
		    		
		    		if(flag){
		    			match = new Match(index,busID,max);
        				indexes.add(match);
        				flag = false;
		    		}		    	
		    		
		    	}
		    	
		    	double tempLat;
				double tempLong;	    	
				int indexLast = -1;
		    	String indexFinish = null;
		    	
		    	double max = -5;
		    	double tempMax;
		    	
		    	for(int z = 0;z<indexes.size();z++){

		    		cursor = db.query("route", new String[] { "_id","bus_number","source","destination","lat","long"}, "bus_number=?",
							new String[]{String.valueOf(indexes.get(z).getBusID())}, null, null, null, null);
		    		
			    	double minLat=5.0;
			    	double minLong = 5.0;

		    		tempMax = indexes.get(z).div;
    				//System.out.println(tempMax +" tempMax"+indexes.get(z).getBusID()+  " busid");
		    		if(max < tempMax){
    					flag = true;
    					max = tempMax;
    					System.out.println(max+ " only max");
    				}
    				
		    		while(cursor.moveToNext()){			
		    			if(cursor.getString(4)!=null){
		    				tempLat = Math.abs(Double.parseDouble(cursor.getString(4))-myCood[0]);	
		    				tempLong = Math.abs(Double.parseDouble(cursor.getString(5))-myCood[1]);
		    					
		    					if(tempLat < minLat && tempLong < minLong && flag){
		    						minLat = tempLat;
		    						minLong = tempLong;
		    			
		    						indexFinish = cursor.getString(0);
		    						indexes.get(z).setIndexFinish(indexFinish);
		    						indexLast = z;
		    						System.out.println(max +" max "+ indexFinish);
		    					}
		    				
		    			}
				    }
		    		flag = false;
	
		    	}
		    	
//		    	System.out.println(indexLast + " "+ indexes.get(indexLast).getIndexFinish());
		    	match = indexes.get(indexLast);
		    	System.out.println(match.getBusID()+" start "+match.getIndexStart() + " finish "+ match.getIndexFinish());
		    	return match;

	
		    
		    
		    }
		    @Override
		    protected void onPostExecute(Match result1) {
		        progressDialog.dismiss();
//		        result = result1;
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