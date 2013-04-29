package com.example.myapp;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.Menu;

public class Search extends Activity {

	double currLong;
	double currLat;
	GooglePlaces googlePlaces;
	PlacesList nearPlaces;
	GoogleMap  map;
	LatLng myPos;
	LatLng myCoodSearchPlace;
	ArrayList<String> wayPoints = new ArrayList<String>();
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		
		Intent intent = getIntent();
		
		double[] myCood = intent.getExtras().getDoubleArray("myCood");
		double[] destinationCood = intent.getExtras().getDoubleArray("destinationCood");
		currLat = myCood[0];
		currLong = myCood[1];
	
		
		myCoodSearchPlace = new LatLng(currLat,currLong);
		SearchPlace place = new SearchPlace(myCoodSearchPlace);
		place.execute();
		try {
			nearPlaces = place.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(nearPlaces!=null){
			currLat = nearPlaces.results.get(0).geometry.location.lat;
			currLong = nearPlaces.results.get(0).geometry.location.lng;
		}
	
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		myPos = new LatLng(currLat,currLong);
		
		LatLng desPos = new LatLng(destinationCood[0],destinationCood[1]);
		
		BitmapDescriptor yourImage = BitmapDescriptorFactory.fromResource(R.drawable.bluedot);

		
		Marker myMarker = map.addMarker(new MarkerOptions().position(myCoodSearchPlace).title("You").icon(yourImage));
		Marker BusMarker = map.addMarker(new MarkerOptions().position(myPos).title("Nearest BusStop"));
		Marker desMarker = map.addMarker(new MarkerOptions().position(desPos).title("destination!!"));

		for(int x = 0;x<=8;x++){
			String[] splits = wayPoints.get(x).split(",");
			LatLng wayPos = new LatLng(Double.valueOf(splits[1]),Double.valueOf(splits[2]));
			Marker wayPointMarker = map.addMarker(new MarkerOptions().position(wayPos).title(splits[0]));
		}
		
		String test = makeURL(currLat,currLong,destinationCood[0],destinationCood[1],wayPoints);
		
		new connectAsyncTask(test).execute();
		
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(myPos, 15));
		map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		return true;
	}
	
	 public String makeURL (double sourcelat, double sourcelog, double destlat, 
			 double destlog,ArrayList<String> wayPoints){
	        
		 StringBuilder urlString = new StringBuilder();
	        urlString.append("http://maps.googleapis.com/maps/api/directions/json");
		 	urlString.append("?origin=");// from
	        urlString.append(Double.toString(sourcelat));
	        urlString.append(",");
	        urlString
	                .append(Double.toString( sourcelog));
	        urlString.append("&destination=");// to
	        urlString
	                .append(Double.toString( destlat));
	        urlString.append(",");
	        urlString.append(Double.toString( destlog));
	        
	        
	        ///////////////////////////////////////////////
	        	urlString.append("&waypoints=");// to		//correct one but illegal url
	        for(int i = 0;i<=6;i++){
	        	String temp = wayPoints.get(i);
	        	String[] splits =  temp.split(",");
	        	urlString.append(splits[1]+","+splits[2]);
	        	if(i!=6)
	        		urlString.append("%7C");
	        }
	        ///////////////////////////////////////////////

	        urlString.append("&sensor=true&avoid=highways&mode=driving&alternatives=true");
	        String URL = null;
			try {
				URL = URLEncoder.encode(urlString.toString(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			String a = newURL + URL;
//			System.out.println(a);
//	        return a;
	        
	        return urlString.toString();
	 }
	 
	 public void drawPath(String result) {

		    try {
		            //Tranform the string into a json object
		           final JSONObject json = new JSONObject(result);
		           JSONArray routeArray = json.getJSONArray("routes");
		           JSONObject routes = routeArray.getJSONObject(0);
		           JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
		           String encodedString = overviewPolylines.getString("points");
		           List<LatLng> list = decodePoly(encodedString);

//		           System.out.println(myCood);
		           
		           LatLng myLatLng= myCoodSearchPlace;
	                LatLng dest1= list.get(0);
	                Polyline firstLine = map.addPolyline(new PolylineOptions()
	                .add(new LatLng(myLatLng.latitude, myLatLng.longitude), new LatLng(dest1.latitude,dest1.longitude))
	                .width(2)
	                .color(Color.BLUE).geodesic(true));
		           
		           
		           for(int z = 0; z<list.size()-1;z++){
		                LatLng src= list.get(z);
		                LatLng dest= list.get(z+1);
		                Polyline line = map.addPolyline(new PolylineOptions()
		                .add(new LatLng(src.latitude, src.longitude), new LatLng(dest.latitude,   dest.longitude))
		                .width(2)
		                .color(Color.BLUE).geodesic(true));
		            }
		    } 
		    catch (JSONException e) {
		    	e.printStackTrace();
		    }
		}
	
	 
	 private List<LatLng> decodePoly(String encoded) {

		    List<LatLng> poly = new ArrayList<LatLng>();
		    int index = 0, len = encoded.length();
		    int lat = 0, lng = 0;

		    while (index < len) {
		        int b, shift = 0, result = 0;
		        do {
		            b = encoded.charAt(index++) - 63;
		            result |= (b & 0x1f) << shift;
		            shift += 5;
		        } while (b >= 0x20);
		        int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
		        lat += dlat;

		        shift = 0;
		        result = 0;
		        do {
		            b = encoded.charAt(index++) - 63;
		            result |= (b & 0x1f) << shift;
		            shift += 5;
		        } while (b >= 0x20);
		        int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
		        lng += dlng;

		        LatLng p = new LatLng( (((double) lat / 1E5)),
		                 (((double) lng / 1E5) ));
		        poly.add(p);
		    }
		    return poly;
		}
	
	 private class connectAsyncTask extends AsyncTask<Void, Void, String>{
		    private ProgressDialog progressDialog;
		    String url;
		    connectAsyncTask(String urlPass){
		        url = urlPass;
		    }
		    @Override
		    protected void onPreExecute() {
		        // TODO Auto-generated method stub
		        super.onPreExecute();
		        progressDialog = new ProgressDialog(Search.this);
		        progressDialog.setMessage("Fetching route, Please wait...");
		        progressDialog.setIndeterminate(true);
		        progressDialog.show();
		    }
		    @Override
		    protected String doInBackground(Void... params) {
		        JSONParser jParser = new JSONParser();
		        String json = jParser.getJSONFromUrl(url);
		        return json;
		    }
		    @Override
		    protected void onPostExecute(String result) {
		        super.onPostExecute(result);   
		        progressDialog.hide();        
		        if(result!=null){
		            drawPath(result);
		        }
		    }
		}
	 
	 private class SearchPlace extends AsyncTask<Void, Void, PlacesList>{
		    private ProgressDialog progressDialog;
		    LatLng desCood;
		    PlacesList newList;
		    DataTest d1;
		    SQLiteDatabase db;
		    
		    SearchPlace(LatLng desCood){
		    	this.desCood = desCood;
		    	d1 = new DataTest(Search.this);
		    	db = d1.openDataBase();
		    }
		    @Override
		    protected void onPreExecute() {
		        // TODO Auto-generated method stub
		        super.onPreExecute();
		        progressDialog = new ProgressDialog(Search.this);
		        progressDialog.setMessage("Fetching route, Please wait...");
		        progressDialog.setIndeterminate(true);
		        progressDialog.show();
		    }
		    @Override
		    protected PlacesList doInBackground(Void... params) {
		    		googlePlaces = new GooglePlaces();
		    		Cursor cursor;
		    		
		    		long numRows = DatabaseUtils.queryNumEntries(db,"route");
		    		
		    		
		    		for(int i = 1;i<=numRows;i++){
		    			
		    			cursor = db.query("route", new String[] { "_id","bus_number","source","destination",
		    					"lat","long"}, "_id=?",
		    					new String[]{String.valueOf(i)}, null, null, null, null);
		    				if (cursor != null)
		    					cursor.moveToFirst();
		    				
		    				if(cursor.isNull(4) || cursor.isNull(5)){
		    					
		    				}else{
		    					String waypoint = cursor.getString(2)+","+cursor.getString(4)+"," + cursor.getString(5);
		    					wayPoints.add(waypoint);
		    				}
		    				
		    		}
		    		
		    		try {
		    			String types = "bus_station"; // Listing places only bus stations
					
					// Radius in meters - increase this value if you don't find any places
		    			double radius = 1000; // 1000 meters
					
					// get nearest places
		    			nearPlaces = googlePlaces.search(desCood.latitude,desCood.longitude, radius, types);
		    			newList = googlePlaces.search(desCood.latitude,desCood.longitude, radius, types);
		    			
					
		    		} catch (Exception e) {
		    			e.printStackTrace();
		    		}
		    		return newList;
		    }
		    @Override
		    protected void onPostExecute(PlacesList list) {
		    	nearPlaces = newList;
		    	db.close();
		        progressDialog.hide();

		    }
		}
	 
}
