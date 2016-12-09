package team1028.plannertravelassistant;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.everything.providers.android.calendar.Event;

import static team1028.plannertravelassistant.MainActivity.verifyLocationPermissions;

/**
 * Class to handle map view
 * Created by Maddy on 12/9/2016.
 */
public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
	// Expandable List View stuffs
	ExpandableListAdapter listAdapter;
	ExpandableListView expListView;

	private static final LatLng MELBOURNE = new LatLng(-37.81319, 144.96298);

	private static final LatLng SYDNEY = new LatLng(-33.87365, 151.20689);

	private static final LatLng ADELAIDE = new LatLng(-34.92873, 138.59995);

	private static final LatLng PERTH = new LatLng(-31.95285, 115.85734);

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		// TODO handled in MainActivity?
//		Intent i = new Intent(this, ForegroundService.class);
//		startActivity(i);

		// Important!
		verifyLocationPermissions(this);

		// Prep map
		SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager()
				.findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);

		// Handle details for Expandable List
		expListView = (ExpandableListView)findViewById(R.id.viewExpandList); // Get list view
		listAdapter = new ExpandableListAdapter(this);

		// Error checking
		if (expListView == null) {
			System.out.println("ERROR: expListView is null!");
			return;
		}

		expListView.setAdapter(listAdapter);
		updateView();
	}

	/**
	 * Update View elements
	 */
	private void updateView() {
		// Update travel time
		TextView travelTime = (TextView)findViewById(R.id.textTravelTimeMap);
		String travelTimeString = "Travel Time: 0 min";
		travelTime.setText(travelTimeString);

		// TODO store Events list
//		// Update list of events TODO test
//		if (this.events.size() > 0) {
//			for (Event e : events) {
//				int newIndex = listAdapter.addGroup(e.displayName);
//
//				listAdapter.addChild(newIndex, e.description);
//				listAdapter.addChild(newIndex, e.eventLocation);
//				listAdapter.addChild(newIndex, e.duration);
//			}
//		}
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
		map.addMarker(new MarkerOptions().position(new LatLng(42.2722, -71.8038)).title("Marker"));
		map.addPolyline((new PolylineOptions()).add(MELBOURNE, ADELAIDE, PERTH));
		map.moveCamera(CameraUpdateFactory.newLatLng(MELBOURNE));

		updateView();
	}

	public LatLng getLocationFromAddress(ArrayList<String> eventLocations){
		Geocoder coder = new Geocoder(this);
		List<Address> address = new ArrayList<Address>();
		LatLng p1 = null;

		for (String strAddress : eventLocations) {
			try {
				try {
					address = coder.getFromLocationName(strAddress, 5);
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (address == null) {
					return null;
				}
				Address location = address.get(0);
				location.getLatitude();
				location.getLongitude();

				p1 = new LatLng(location.getLatitude(), location.getLongitude());

				return p1;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return p1;
	}

}
