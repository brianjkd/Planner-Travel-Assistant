package team1028.plannertravelassistant;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ExpandableListView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static team1028.plannertravelassistant.MainActivity.verifyLocationPermissions;

/**
 * Class to handle map view
 * Created by Maddy on 12/9/2016.
 */
public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
	// Expandable List View stuffs
	ExpandableListAdapter listAdapter;
	ExpandableListView expListView;
	List<String> listDataHeader;
	HashMap<String, List<String>> listDataChild;

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
		prepListData(); // Prepare data
		listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

		// Error checking
		if (expListView == null) {
			System.out.println("ERROR: expListView is null!");
			return;
		}

		expListView.setAdapter(listAdapter);
	}

	public void onPause() {
		super.onPause();
		// TODO unregister receiver???
	}

	public void onResume() {
		super.onResume();
	}

	/**
	 * Add data
	* TODO change - copied over from example
	*/
	private void prepListData() {
		listDataHeader = new ArrayList<String>();
		listDataChild = new HashMap<String, List<String>>();

		// Adding child data
		listDataHeader.add("Event 1");
		listDataHeader.add("Event 2");
		listDataHeader.add("Event 3");

		// Adding child data
		List<String> event1 = new ArrayList<String>();
		event1.add("time: 1am");

		List<String> event2 = new ArrayList<String>();
		event2.add("time: 2pm");
		event2.add("location: Worcester");

		List<String> event3 = new ArrayList<String>();
		event3.add("time: 9am");

		listDataChild.put(listDataHeader.get(0), event1); // Header, Child data
		listDataChild.put(listDataHeader.get(1), event2);
		listDataChild.put(listDataHeader.get(2), event3);
	}
	/**
	 * This is where we can add markers or lines, add listeners or move the camera. In this case,
	 * we add a marker to Worcester, MA (WPI's location).
	 */
	@Override
	public void onMapReady(GoogleMap map) {
		map.addMarker(new MarkerOptions().position(new LatLng(42.2722, -71.8038)).title("Marker"));
	}
}
