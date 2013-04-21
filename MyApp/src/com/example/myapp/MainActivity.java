package com.example.myapp;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.google.android.maps.GeoPoint;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

	GPSValidate connection;
	double currLong;
	double currLat;
	TextView textView;
	public static String add;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		connection = new GPSValidate(this);


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
	            textView = (TextView) findViewById(R.id.TextView2);
	            textView.setText(add);
	            textView.setTextSize(25);
	       //     Toast.makeText(getBaseContext(), add, Toast.LENGTH_SHORT).show();
	         } catch (IOException e) {                
	                e.printStackTrace();
	         }
		}
	}

	public void onClick(View view){
		
		//GeoPoint myGeoPoint = new GeoPoint((int)currLat,(int)currLong);
		Intent intent = new Intent(this,Validate.class);
		EditText editText = (EditText) findViewById(R.id.editText1);
		String destination = editText.getText().toString();
		intent.putExtra("Destination", destination);
		double[] myCood = {currLat,currLong};
		intent.putExtra("myCood",myCood);
		startActivity(intent);
	}

}
