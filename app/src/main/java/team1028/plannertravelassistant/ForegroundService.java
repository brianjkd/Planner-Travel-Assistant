package team1028.plannertravelassistant;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class ForegroundService extends Service implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final String TAG = "ForegroundService";

    private final long WAIT_TIME = 30 * 1000; // sleepy time for the thread

    // Information for location retrieval
    private GoogleApiClient googleApiClient;
    private Location lastLocation = null;
    private LocationRequest locationRequest;

    SomeThread R1; // TODO describe

    // Setup the service (only called on startup)
    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: ");

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        R1 = new SomeThread( "planner travel assistant thread");
        R1.start();
        super.onCreate();
    }


    // Restore information after pause (potentially called multiple times)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        googleApiClient.connect();

        // create our notification and start this service as a foreground service
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pendingIntent)
                .setAutoCancel(false)
                .setColor(Color.BLUE)
                .setContentTitle("Planner Travel Assistant");

	    // Setup notification (allows it to run in background) TODO check this comment
        builder.setSmallIcon(R.drawable.fg_service);
        int notificationID = 489543289; // TODO why is this hard-coded?
        Notification notification = builder.build();
        notificationManager.notify(notificationID, notification);
        startForeground(notificationID, notification);
        // end setting up as foreground service

        super.onStartCommand(intent, flags, startId);

	    // Tell if service should pause
        boolean stop = intent.getBooleanExtra("stop", false);
        if (stop) {
            Log.d(TAG, "stopping service");
            stopForeground(true);
            stopSelf();
        }

        return START_NOT_STICKY;
    }

	// Start service to get location updates
    protected void startLocationUpdates() {
        Log.d(TAG, "startLocationUpdates: Called start location updates");
        try {
	        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, locationRequest, this);
        } catch (SecurityException e) {}
    }

	// Perform essential functions when connection is achieved
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(TAG, "onConnected: ");
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000 * 60);
        locationRequest.setFastestInterval(1000 * 30);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        startLocationUpdates();
        // TODO handle runtime permission rather than wrapping in a try catch
        try { Location temp = LocationServices.FusedLocationApi.getLastLocation(
                googleApiClient);
            if (temp != null) {
                lastLocation = temp;
            }
        } catch (SecurityException e){}
    }

	// Update last location when changed
    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged: ");
        lastLocation = location;
    }

    // TODO use alarm manager instead of a thread to launch small IntentServices
	// TODO describe what this does
    // we can't do this on the service's main thread
    class SomeThread implements Runnable {
	    // Setup thread
        private Thread t;
        private String threadName;
        private boolean stopFlag = false;

        SomeThread(String name) {
            threadName = name;
            Log.d(TAG, "Creating " +  "thread");
        }

	    // Run thread - wait for location updates
        public void run() {
            Log.d(TAG, "running thread" );
            try {
                while (true) {
                    if (stopFlag){
                        Log.d(TAG, "Actually stopping thread: ");
                        return;
                    }
                    Thread.sleep(WAIT_TIME);
                    // launch a ServiceIntent
                    Intent i = new Intent(ForegroundService.this, MyServiceIntent.class);

                    Bundle b = new Bundle();

                    b.putParcelable("Location", lastLocation);
                    i.putExtra("Location", b);
                    ForegroundService.this.startService(i); // start the service intent
                }
            } catch (InterruptedException e) {
                Log.d(TAG, "Thread " +  threadName + " interrupted.");
            }
            Log.d(TAG, "Thread " +  threadName + " exiting.");
        }

        void start () {
            Log.d(TAG, "Starting thread " + threadName);
	        // only allow start to be called once per thread instance
            if (t == null) {
                t = new Thread(this, threadName);
                t.start ();
            }
        }

        void stop() {
            Log.d(TAG, "Stopping thread " + threadName);
            stopFlag = true;
        }
    }

	// Clean up when app is stopped
    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        R1.stop(); // stop repeating MyServiceIntent
        LocationServices.FusedLocationApi.removeLocationUpdates(
                googleApiClient, this);
        googleApiClient.disconnect();
        super.onDestroy();
    }

	// Functions from parent class
	@Override
	public void onConnectionSuspended(int i) {}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}