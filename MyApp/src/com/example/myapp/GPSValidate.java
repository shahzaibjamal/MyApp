package com.example.myapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

public class GPSValidate extends Service implements LocationListener{

	
	boolean isGPSEnabled = false;
	boolean isNetworkEnabled = false;
	private boolean canGetLocation = false;
	
	Location location = null;
	private double longitude;
	private double latitude;
	
	Context context;
	
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; 

	private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; 
	
	LocationManager locationManager;
	
	public GPSValidate(Context context){
		this.context = context;
		getLocation();
	}

	private Location getLocation(){
		
		try{
			locationManager = (LocationManager)context.getSystemService(LOCATION_SERVICE);
			
			isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			
			isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			
			if(!(isGPSEnabled || isNetworkEnabled)){
				
				
			}else{
				if(isNetworkEnabled){
					locationManager.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER,MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
					if (locationManager != null) {
						location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);	
						if (location != null) {
							latitude = location.getLatitude();
							longitude = location.getLongitude();
						}
					}
				}
				
				if(isGPSEnabled){
					locationManager.requestLocationUpdates(
							LocationManager.GPS_PROVIDER,MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
					if (locationManager != null) {
						location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
						if (location != null) {
							latitude = location.getLatitude();
							longitude = location.getLongitude();
						}
					}

				}
				canGetLocation = true;
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return location;
	}
	
	public void stopUsingGPS() {
		if (locationManager != null) {
			locationManager.removeUpdates(GPSValidate.this);
		}
	}

	public double getLatitude() {
		if (location != null) {
			latitude = location.getLatitude();
		}

		return latitude;
	}

	public double getLongitude() {
		if (location != null) {
			longitude = location.getLongitude();
		}

		return longitude;
	}

	public boolean canGetLocation() {
		return this.canGetLocation;
	}
	
	
	
	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
