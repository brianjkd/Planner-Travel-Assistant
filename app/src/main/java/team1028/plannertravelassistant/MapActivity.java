package team1028.plannertravelassistant;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import static team1028.plannertravelassistant.MainActivity.verifyLocationPermissions;

/**
 * Class to handle map view
 * Created by Maddy on 12/9/2016.
 */
public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
	public static final String TAG = "MapActivity"; // TODO add description

	ArrayList<String> locations = new ArrayList<String>(); // events from user's calendar
	Location lastKnownLocation = null;

	ArrayList<LatLng> coordinates;
//	ArrayList<String> testStrings = new ArrayList<String>(); // TODO remove
	ArrayList<LatLng> geoLocations = new ArrayList<LatLng>();

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);


		// let's get the list of events sent over from the MainActivity
		Intent intent = getIntent(); //
		this.locations = intent.getStringArrayListExtra("locations");
		if (this.locations != null){
			for (String l : this.locations){
				Log.d(TAG, "onCreate: event from main " + l);
			}
		}

		Bundle b = intent.getBundleExtra("userLocation");
		// check that the bundle exists
		// if it does, get the user's current location assuming it exists
		if (b != null)
		{
			Location receivedLastKnownLoc = b.getParcelable("userLocation");
			if (receivedLastKnownLoc != null){
				lastKnownLocation = receivedLastKnownLoc;
				Log.d(TAG, "onCreate: we got the user's location" + lastKnownLocation.getLatitude());
			}
		}
		else
		{
			// if the user attempts to open the map when the bundle is empty, alert
			Toast.makeText(getApplicationContext(), "Your location has not been set yet", Toast.LENGTH_LONG).show();
		}
		// Important!
		verifyLocationPermissions(this);

		// parse the location strings
		if (this.locations != null) {
			geoLocations = getLocationFromAddress(locations);
			if (lastKnownLocation != null) {
				// set the user's current location to be the first item in the list
				geoLocations.add(0, new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()));
			}
		}

		// Prep map
		SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager()
				.findFragmentById(R.id.mapView);
		mapFragment.getMapAsync(this);

		updateView();
	}

	/**
	 * Update View elements
	 */
	private void updateView() {
		// Update travel time
//		TextView travelTime = (TextView)findViewById(R.id.textTravelTimeMap);
//		String travelTimeString = "Travel Time: 0 min";
//		travelTime.setText(travelTimeString);

	}

	public void onPause() {
		super.onPause();
		// TODO unregister receiver???
	}

	public void onResume() {
		super.onResume();
	}

	/**
	 * This is where we can add markers or lines, add listeners or move the camera. In this case,
	 * we add a marker to Worcester, MA (WPI's location).
	 */
	@Override
	public void onMapReady(GoogleMap map) {

		// create the polyline with the retrieved locations
		map.addPolyline((new PolylineOptions()).addAll(geoLocations));
		// add a marker at each location point
		for (LatLng l : geoLocations)
			{
				map.addMarker(new MarkerOptions().position(l));
			}
		// check that the list is not empty before setting the camera at the user's location
		if (geoLocations.size() > 0)
		{
			map.moveCamera(CameraUpdateFactory.newLatLng(geoLocations.get(0)));
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(geoLocations.get(0), 13));
		}
		updateView();
	}

	/**
	 * Parses a list of event location strings to LatLng objects;
	 * returns a list of LatLng coordinates
	 * @param eventLocations List of locations as Strings
	 * @return List of latitudes and longitudes
     */
	public ArrayList<LatLng> getLocationFromAddress(ArrayList<String> eventLocations) {
		Geocoder coder = new Geocoder(this);
		coordinates = new ArrayList<LatLng>(); // TODO local or global?

		// TODO why are things hard-coded?
		// Convert locations to lat/long and add to list of coordinates
		for (String strAddress : eventLocations) {
			List<Address> address = new ArrayList<Address>(); // TODO can this use LatLng?

			// Convert String to coordinate
			try {
				address = coder.getFromLocationName(strAddress, 5);
			} catch (Exception e) {
				e.printStackTrace();
			}

			// Null checking of addresses
			if (address != null && address.size() > 0) {
				Address location = address.get(0);
				location.getLatitude();
				location.getLongitude();

				// Add to coordinates list
				coordinates.add(new LatLng(location.getLatitude(), location.getLongitude()));
			}
		}
		return coordinates;
	}
}
