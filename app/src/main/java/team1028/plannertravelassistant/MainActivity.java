package team1028.plannertravelassistant;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
	public static final String TAG = "MainActivity"; // TODO add description

    ArrayList<String> locations = new ArrayList<String>(); // event locations as strings

	// TODO are these ok?
	// The indices for the projection array above.
	private static final int PROJECTION_ID_INDEX = 0;
	private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
	private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
	private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;

    // Location Permissions variables
    private static final int REQUEST_CODE = 201;
    //private static final int READ_CALENDAR = 202;
    private static String[] PERMISSIONS_ARRAY = {
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_CALENDAR
    };
	// TODO old way of doing the lists
//    ArrayAdapter<String> adapter;
//    ArrayList<String> listItems = new ArrayList<String>();
//    private ListView list;

	// Expandable List View stuffs
	ExpandableListAdapter listAdapter;
	ExpandableListView expListView;
	List<String> listDataHeader;
	HashMap<String, List<String>> listDataChild;

    // this comment is a test for the project build

    // permission verification method.
    public static void verifyLocationPermissions(Activity activity) {
        // Check if we have read or write permission

        int fineLocationPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
        int readCalendarPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_CALENDAR);
        if (fineLocationPermission != PackageManager.PERMISSION_GRANTED
                && readCalendarPermission != PackageManager.PERMISSION_GRANTED) {

            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_ARRAY,
                    REQUEST_CODE
            );
        }
    }

    // this receiver can receive data from other services
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: we got a message"); // Log actions

	        // TODO add descriptive comments
            ArrayList<String> receivedLocations = intent.getStringArrayListExtra("locationList");

	        // Check that location list is valid
            if (receivedLocations != null) {
                Log.d(TAG, "onReceive: We got a list of locations from MyServiceIntent");
                locations = receivedLocations;
                for (String l : locations){
                    Log.d(TAG, l);
                }
            } else {
                Log.d(TAG, "onReceive: We did not get a list of locations from MyServiceIntent");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent i = new Intent(this, ForegroundService.class);

	    // TODO move to MapActivity
//        SupportMapFragment mapFragment =
//                (SupportMapFragment) getSupportFragmentManager().findFragmentById(mapView);
//        mapFragment.getMapAsync(this);

        verifyLocationPermissions(this);

	    // TODO why is this here?
        // configure architecture so that this takes the event list from MyServiceIntent
//        listItems.add("test 1");
//        listItems.add("test 2");
//        listItems.add("test 1");
//        listItems.add("test 3");
//        listItems.add("test 1");
//        listItems.add("test 2");
//        listItems.add("test 1");
//        listItems.add("test 3");

//	    // TODO specify adapter type
//        adapter=new ArrayAdapter<String>(this,
//                android.R.layout.simple_list_item_1,
//                listItems);
//        list = (ListView) findViewById(R.id.eventList);
//        list.setAdapter(adapter);

	    // Handle details for Expandable List
	    expListView = (ExpandableListView)findViewById(R.id.viewExpandList); // Get list view
	    prepListData(); // Prepare data
	    listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

	    // Error checking
	    if (expListView == null) {
		    System.out.println("Error: expListView is null!");
		    return;
	    }

	    expListView.setAdapter(listAdapter);
        this.startService(i); // TODO where to put this?
    }

	/**
	 * Open map when button is clicked
	 * @param view Button clicked
	 */
	public void openMapActivity(View view) {
		Intent mapIntent = new Intent(this, MapActivity.class);

		// TODO add extras?

		startActivity(mapIntent);
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

    public void onPause(){
        super.onPause();
        // unregister message Receiver when app is not focused
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    public void onResume() { // TODO save instance state?
        super.onResume();
        // listen for intents with the filter "newLocation"
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("locations"));
    }

    // calendar stuff
    // Projection array. Creating indices for this array instead of doing
// dynamic lookups improves performance.
    public static final String[] EVENT_PROJECTION = new String[] {
            CalendarContract.Calendars._ID,                           // 0
            CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
            CalendarContract.Calendars.OWNER_ACCOUNT                  // 3
    };
}
