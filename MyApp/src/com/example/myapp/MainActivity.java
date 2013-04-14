package com.example.myapp;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

	GPSValidate connection;
	double currLong;
	double currLat;
	TextView textView;
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
	        	
	        	String add = "Your Location\n";
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
	//	Button button = (Button) findViewById(R.id.button1);
			Intent intent = new Intent(this,Search.class);
			EditText editText = (EditText) findViewById(R.id.editText1);
			String destination = editText.getText().toString();
			intent.putExtra("Destination", destination);
			intent.putExtra("longitude", String.valueOf(currLong));
			intent.putExtra("latitude", String.valueOf(currLat));
			startActivity(intent);
	}
	//@Override
	/*public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
*/
}
