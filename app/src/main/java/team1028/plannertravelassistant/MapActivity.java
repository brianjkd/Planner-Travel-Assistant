package team1028.plannertravelassistant;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ExpandableListView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static team1028.plannertravelassistant.MainActivity.verifyLocationPermissions;

/**
 * Class to handle map view
 * Created by Maddy on 12/9/2016.
 */
public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
	// Expandable List View stuffs
	ExpandableListAdapter listAdapter;
	ExpandableListView expListView;

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
		listAdapter.addGroup("No Events");
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
	}
}
