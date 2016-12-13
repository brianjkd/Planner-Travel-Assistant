package team1028.plannertravelassistant;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import me.everything.providers.android.calendar.Event;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
	public static final String TAG = "MainActivity"; // TODO add description

	// API and state for location
    private GoogleApiClient googleApiClient;
    private Location lastLocation = null;
    private LocationRequest locationRequest;
	Location lastKnownLocation;

	// State for events and display
    ArrayList<Event> events = new ArrayList<Event>(); // events from user's calendar
	private float totalTravelTime = 0; // the total travel time in minutes for all events
	ArrayList<GeoLocation> eventLocations = new ArrayList<GeoLocation>(); // list of locations obtained from events list

	// TODO are these ok?
	// The indices for the projection array above.
	private static final int PROJECTION_ID_INDEX = 0;
	private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
	private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
	private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;

	// TODO is this used???
	// calendar stuff
	// Projection array. Creating indices for this array instead of doing
	// dynamic lookups improves performance.
	public static final String[] EVENT_PROJECTION = new String[] {
			CalendarContract.Calendars._ID,                           // 0
			CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
			CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
			CalendarContract.Calendars.OWNER_ACCOUNT                  // 3
	};

    // Location Permissions variables
    private static final int REQUEST_CODE = 201;
    //private static final int READ_CALENDAR = 202;
    private static String[] PERMISSIONS_ARRAY = {
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_CALENDAR
    };

	// Expandable List View stuffs
	ExpandableListAdapter listAdapter;
	ExpandableListView expListView;
	// Child data in format of header title, child title

	// TODO remove?
    // this comment is a test for the project build

    // Verify relevant permissions
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

    // Receive data from other services (such as location or calendar change)
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: we got a message"); // Log actions

            // TODO add descriptive comments
            EventListWrapper eventListWrapper = (EventListWrapper) intent.getSerializableExtra("events");
            ArrayList<Event> receivedEvents = eventListWrapper.getEvents();

	        // Update total travel time
			float receivedTotalTravelTime = intent.getFloatExtra("totalTravelTime", 0);
			if (receivedTotalTravelTime > 0){ // we actually got something
				totalTravelTime = receivedTotalTravelTime;
				Log.d(TAG, "totalTravelTime : " + totalTravelTime);
			}

	        // Get location
			Bundle b = intent.getBundleExtra("userLocation");
			Location receivedLastKnownLoc = b.getParcelable("userLocation");

			if (receivedLastKnownLoc != null){
				lastKnownLocation = receivedLastKnownLoc;
				Log.d(TAG, "onReceive: got a last known user gps coord" + lastKnownLocation.getLatitude());
			}

            // Check that location list is valid
            if (receivedEvents != null && receivedEvents.size() > 0) {
                Log.d(TAG, "onReceive: We got a list of " + receivedEvents.size() + " locations from MyServiceIntent");
                events = receivedEvents;
				for (Event e : events){
                    Log.d(TAG, e.title);
                }
            } else {
                Log.d(TAG, "onReceive: We did not get a list of locations from MyServiceIntent");
            }

	        // Update view
	        updateView();
        }
    };

	/**
	 * Update view of events and status
	 */
	private void updateView() {
		// Update travel time
		TextView travelTimeView = (TextView)findViewById(R.id.textTravelTime);

		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(1);

		String travelTimeText = "Travel Time: " + df.format(totalTravelTime) + " minutes";
		travelTimeView.setText(travelTimeText);

		// Update events list title
		TextView eventsListTitle = (TextView)findViewById(R.id.textEventsNum);
		String eventsListText = events.size() + " Events";
		eventsListTitle.setText(eventsListText);

		// Adapter to display Expandable List View
		ExpandableListAdapter thisAdapter = new ExpandableListAdapter(this);

		// Update list of events TODO @whoever is doing UI: Fix this or use ListView -Brian
		for (Event e : events) {
			expListView.setAdapter(thisAdapter);

			// Check for null events list
			if (e == null) {
				Log.d(TAG, "Event list is null");
				return;
			}

			// Add event to list
			int newIndex = thisAdapter.addGroup(e.title);
			ArrayList<String> details = new ArrayList<String>();
			if (e.title != null) {
				Log.d(TAG, "Event " + e.title + " is being added");
				details.add(e.title);
			}

			// Display description (if > 2 characters)
			if (e.description != null && e.description.length() > 2) {
				Log.d(TAG, "Description: " + e.description);
				details.add(e.description);
			}

			// Display location
			if (e.eventLocation != null) {
				Log.d(TAG, "Location: " + e.eventLocation);
				details.add(e.eventLocation);
			}

			// Add start time
			if (e.dTStart != 0) {
				GregorianCalendar calendar = new GregorianCalendar();
				calendar.setTimeInMillis(e.dTStart);
				details.add(calendar.getTime().toString());
			}

			// Add end time
			if (e.dTend != 0) {
				GregorianCalendar calendar = new GregorianCalendar();
				calendar.setTimeInMillis(e.dTend);
				details.add(calendar.getTime().toString());
			}

			// Display entire Event in view
			thisAdapter.addChildren(newIndex, details);

			// OLD CODE TODO remove
//				if (e == null) {
//					int newIndex = thisAdapter.addGroup(e.title);
//					ArrayList<String> details = new ArrayList<String>();
//					if(e.title != null) {
//						Log.d(TAG, "Event " + e.title + " is being added");
//						details.add(e.title);
//					}
//					if(e.description != null && e.description.length() > 2){
//						Log.d(TAG, "Description: " + e.description);
//						details.add(e.description);
//					}
//					if(e.eventLocation != null){
//						Log.d(TAG, "Location: " + e.eventLocation);
//						details.add(e.eventLocation);
//
//					}
//					if(e.dTStart != 0){
//						GregorianCalendar calendar = new GregorianCalendar();
//						calendar.setTimeInMillis(e.dTStart);
//						details.add(calendar.getTime().toString());
//					}
//					if(e.dTend != 0){
//						GregorianCalendar calendar = new GregorianCalendar();
//						calendar.setTimeInMillis(e.dTend);
//						details.add(calendar.getTime().toString());
//					}
//					thisAdapter.addChildren(newIndex, details);
//				}
//				else{
//					Log.d(TAG, "Event list is null");
//				}
//				expListView.setAdapter(thisAdapter);
		}
	}

	// Perform essential functions on startup
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

	    // Prepare API client
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

	    // Setup View and ForegroundService
        setContentView(R.layout.activity_main);
        Intent i = new Intent(this, ForegroundService.class);

	    // Sync button
        Button sync = (Button) findViewById(R.id.btnSync);
        sync.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, MyServiceIntent.class);
                Bundle b = new Bundle();

	            // Update last location if appropriate
                if (lastLocation != null) {
                    b.putParcelable("Location", lastLocation);
                    i.putExtra("Location", b);
                    MainActivity.this.startService(i); // start the service intent
                } else {
                    Toast.makeText(getApplicationContext(), "We could not retrieve your current location", Toast.LENGTH_LONG).show();
                }
            }
        });

	    // Stop button
		Button stop = (Button) findViewById(R.id.btnStop);
		stop.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				stopService(v);
			}
		});

		// Ask for permission if needed
        verifyLocationPermissions(this);

	    // Handle details for Expandable List
	    expListView = (ExpandableListView)findViewById(R.id.viewExpandList); // Get list view
	    listAdapter = new ExpandableListAdapter(this);

	    // Error checking TODO is it still needed (if no events)?
	    if (expListView == null) {
		    System.out.println("Error: expListView is null!");
		    return;
	    }

	    // Setup View of expandable list
	    expListView.setAdapter(listAdapter);

        this.startService(i); // TODO where to put this?
	    updateView(); // Just to get initial display of no info
    }

	/**
	 * Open map when appropriate button is clicked
	 * @param view Button clicked
	 */
	public void openMapActivity(View view) {
		Intent mapIntent = new Intent(this, MapActivity.class);

		// Add locations of events
		ArrayList<String> locations = new ArrayList<>();
		for (Event e: this.events){
			locations.add(e.eventLocation);
		}

		// TODO add extras?
		Log.d(TAG, "Sending message to map activity with list of event locations");
		mapIntent.putStringArrayListExtra("locations", locations);

		// Display location, if available
		if (lastKnownLocation != null) {
			Log.d(TAG, "openMapActivity: sending lastKnownLocation");
			Bundle b = new Bundle();
			b.putParcelable("userLocation", lastKnownLocation);
			mapIntent.putExtra("userLocation", b);
		}

		startActivity(mapIntent);
	}

	// Stop any listeners to preserve energy
	public void stopService(View view) {
		Intent stop = new Intent(this, ForegroundService.class);
		stop.putExtra("stop", true);
		this.startService(stop); // TODO where to put this?
		Toast.makeText(getApplicationContext(), "Stopping background service", Toast.LENGTH_LONG).show();
	}

	// Stop receiver to save energy
    public void onPause(){
        super.onPause();
        // unregister message Receiver when app is not focused
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

	// Restart receiver after pausing
    public void onResume() { // TODO save instance state?
        super.onResume();
        // listen for intents with the filter "newLocation"
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("locations"));
    }

	// Reconnect APIs
	@Override
	public void onStart() {
		googleApiClient.connect();
		super.onStart();
	}

	// Disconnect from APIs
	@Override
	public void onStop() {
		googleApiClient.disconnect();
		super.onStop();
	}

	// Get location
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // TODO handle runtime permission rather than wrapping in a try catch
        try {
	        Location temp = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

	        // Null checking
            if (temp != null) {
                lastLocation = temp;
            }
        } catch (SecurityException e){}
    }

	// Functions from parent class
    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}
}