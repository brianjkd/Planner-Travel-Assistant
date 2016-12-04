package team1028.plannertravelassistant;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
	public static final String TAG = "MainActivity"; // TODO add description

	Location curLocation; // Store current location

    // Location Permissions variables
    private static final int ACCESS_FINE_LOCATION = 201;
    private static String[] PERMISSIONS_LOCATION = {
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    // permission verification method.
    public static void verifyLocationPermissions(Activity activity) {
        // Check if we have read or write permission
        int fineLocationPermission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (fineLocationPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_LOCATION,
                    ACCESS_FINE_LOCATION
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

        verifyLocationPermissions(this);

        this.startService(i);
    }

    public void onPause() { // TODO save instance state?
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

}
