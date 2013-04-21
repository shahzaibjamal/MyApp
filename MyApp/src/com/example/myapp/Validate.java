package com.example.myapp;

import java.util.List;
import java.util.Locale;

import com.google.android.maps.GeoPoint;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
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
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_validate);
		Intent intent = getIntent();
		String destination = intent.getStringExtra("Destination");
		myCood = intent.getExtras().getDoubleArray("myCood");
		textView = (TextView)findViewById(R.id.textView3);
		
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

/*	
		googlePlaces = new GooglePlaces();
		
		try {
			String types = "bus_station"; // Listing places only cafes, restaurants
			
			// Radius in meters - increase this value if you don't find any places
			double radius = 1000; // 1000 meters
			
			// get nearest places
			nearPlaces = googlePlaces.search(currLat,currLat, radius, types);
			

		} catch (Exception e) {
			e.printStackTrace();
		}

*/		
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

}
