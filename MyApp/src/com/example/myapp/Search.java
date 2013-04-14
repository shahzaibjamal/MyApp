package com.example.myapp;

import java.util.List;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.location.Location;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.widget.TextView;

public class Search extends Activity {

	//MapView mapView;
	double currLong;
	double currLat;
	Location location;
	GeoPoint geoPoint;
	GooglePlaces googlePlaces;
	PlacesList nearPlaces;
	GoogleMap  map;
	LatLng myPos;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		
		Intent intent = getIntent();
		
	/*	String message = intent.getStringExtra("Destination");
	
		TextView textView = new TextView(this);
		textView.setTextSize(40);
		textView.setText(message);
		
		setContentView(textView);*/
		currLong = Double.parseDouble(intent.getStringExtra("longitude"));
		currLat = Double.parseDouble(intent.getStringExtra("latitude"));

/*		googlePlaces = new GooglePlaces();
		
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
		//mapView = (MapView)findViewById(R.id.mapView);	
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		myPos = new LatLng(currLat,currLong);
		Marker myMarker = map.addMarker(new MarkerOptions().position(myPos).title("You!!"));
		
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(myPos, 15));
		map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		return true;
	}


}
