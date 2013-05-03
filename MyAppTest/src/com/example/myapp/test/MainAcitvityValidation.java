package com.example.myapp.test;

import com.example.myapp.GPSValidate;
import com.example.myapp.MainActivity;

import android.location.Location;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.TextView;
import com.example.myapp.R;

public class MainAcitvityValidation extends
		ActivityInstrumentationTestCase2<MainActivity> {
	MainActivity mainActivity;
	TextView textView;
	
	public MainAcitvityValidation() {
		super(MainActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
//		Intent intent = Intent.parseUri("com.example.myapp", Intent.URI_INTENT_SCHEME);
	//	setActivityIntent(intent);

		mainActivity = getActivity();
		textView = (TextView)mainActivity.findViewById(R.id.TextView2);
	}
	
	public void testLocationAddress(){
		String a = textView.getText().toString();
	//	a = a + "2";
		
		String b = MainActivity.add;
		
		assertTrue("Field should be equal", a.equals(b));
		
	}

	public void testLocation(){
	
		GPSValidate testGPS = new GPSValidate(mainActivity);
		Location location = new Location(testGPS.getLocation());
	
		assertTrue("Coordinates overFlow",location.getLongitude()<180.01 || location.getLongitude()>-180.01
				&& location.getLatitude()<90.01 || location.getLatitude()>-.01);
		
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}

}
