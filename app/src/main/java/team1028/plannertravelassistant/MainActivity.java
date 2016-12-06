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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.util.ArrayList;

import static team1028.plannertravelassistant.R.id.mapView;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback{
	public static final String TAG = "MainActivity"; // TODO add description

	Location curLocation; // Store current location

    // Location Permissions variables
    private static final int REQUEST_CODE = 201;
    //private static final int READ_CALENDAR = 202;
    private static String[] PERMISSIONS_ARRAY = {
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_CALENDAR
    };
    ArrayAdapter<String> adapter;
    ArrayList<String> listItems=new ArrayList<String>();
    private ListView list;

    GoogleMap googleMap;

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

    // this receiver can receive data from the foreground service
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: we got a message"); // Log actions

	        // TODO add descriptive comments
            Bundle b = intent.getBundleExtra("Location");
            Location lastKnownLoc = b.getParcelable("Location");

	        // Check that location is valid
            if (lastKnownLoc != null) {
                Log.d(TAG, "onReceive: We got a location from service");
                curLocation = lastKnownLoc; // update the location field for this activity
            } else {
                Log.d(TAG, "onReceive: We did not get a location from service");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent i = new Intent(this, ForegroundService.class);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(mapView);
        mapFragment.getMapAsync(this);

        verifyLocationPermissions(this);

        // configure architecture so that this takes the event list from MyServiceIntent
        listItems.add("test 1");
        listItems.add("test 2");
        listItems.add("test 1");
        listItems.add("test 3");
        listItems.add("test 1");
        listItems.add("test 2");
        listItems.add("test 1");
        listItems.add("test 3");

        adapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                listItems);
        list = (ListView) findViewById(R.id.eventList);
        list.setAdapter(adapter);
        this.startService(i);
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
                mMessageReceiver, new IntentFilter("newLocation"));
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

    // The indices for the projection array above.
    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
    private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;

    @Override
    public void onMapReady(GoogleMap map) {

        /**
         * This is where we can add markers or lines, add listeners or move the camera. In this case,
         * we add a marker to Worcester, MA (WPI's location).
         */
        map.addMarker(new MarkerOptions().position(new LatLng(42.2722, -71.8038)).title("Marker"));
    }

}
