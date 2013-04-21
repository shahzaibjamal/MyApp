package com.example.myapp;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
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
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;

public class Search extends Activity {

	double currLong;
	double currLat;
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
		
		double[] myCood = intent.getExtras().getDoubleArray("myCood");
		double[] destinationCood = intent.getExtras().getDoubleArray("destinationCood");
		currLat = myCood[0];
		currLong = myCood[1];
		
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
		LatLng desPos = new LatLng(destinationCood[0],destinationCood[1]);
		
		Marker myMarker = map.addMarker(new MarkerOptions().position(myPos).title("You!!"));
		Marker desMarker = map.addMarker(new MarkerOptions().position(desPos).title("destination!!"));
		
		String test = makeURL(currLat,currLong,destinationCood[0],destinationCood[1]);
		Log.d("url", test);
		
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
	
	 public String makeURL (double sourcelat, double sourcelog, double destlat, double destlog ){
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
	        urlString.append("&sensor=true&mode=driving&alternatives=true");
	        return urlString.toString();
	 }
	 
	 public void drawPath(String  result) {

		    try {
		            //Tranform the string into a json object
		           final JSONObject json = new JSONObject(result);
		           JSONArray routeArray = json.getJSONArray("routes");
		           JSONObject routes = routeArray.getJSONObject(0);
		           JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
		           String encodedString = overviewPolylines.getString("points");
		           List<LatLng> list = decodePoly(encodedString);

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
}
