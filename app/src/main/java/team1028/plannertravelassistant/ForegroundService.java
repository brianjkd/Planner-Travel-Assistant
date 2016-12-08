package team1028.plannertravelassistant;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class ForegroundService extends Service {
    private static final String TAG = "ForegroundService";
    private static final int LOCATION_INTERVAL = 8 * 60 * 1000; // 8 minutes
    private static final float LOCATION_DISTANCE = 500f; // 500 meters

    private final long WAIT_TIME = 30 * 1000; // sleepy time for the thread

    SomeThread R1; // TODO describe

    // TODO use Fused Location Services rather than MyLocationListener
    public LocationManager locationManager;
    public MyLocationListener listener;

    // only called once, when a service has not been created
    @Override
    public void onCreate() {
        super.onCreate();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = new MyLocationListener(this.getApplicationContext());

        startListener();

        R1 = new SomeThread( "planner travel assistant thread");
        R1.start();
    }

	/**
     * TODO add descriptive comments
     */
    public void startListener() {
        Log.d(TAG, "startListener: ");
        try {
	        locationManager.removeUpdates(listener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, listener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, listener);
        } catch (SecurityException e) {
	        // TODO add catch
	        e.printStackTrace();
        }
    }

    // called multiple times potentially;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // create our notification and start this service as a foreground service
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pendingIntent)
                .setAutoCancel(false)
                .setColor(Color.BLUE)
                .setContentTitle("Planner Travel Assistant");

        builder.setSmallIcon(R.drawable.fg_service);
        int notificationID = 489543289; // TODO why is this hard-coded?
        Notification notification = builder.build();
        notificationManager.notify(notificationID, notification);
        startForeground(notificationID, notification);
        // end setting up as foreground service

        super.onStartCommand(intent, flags, startId);
        return START_NOT_STICKY;
    }

    // TODO use alarm manager instead of a thread to launch small IntentServices
    // we can't do this on the service's main thread
    class SomeThread implements Runnable {
        private Thread t;
        private String threadName;
        private boolean stopFlag = false;

        SomeThread(String name) {
            threadName = name;
            Log.d(TAG, "Creating " +  "thread");
        }

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
                    b.putParcelable("Location", listener.getLocation());
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
            if (t == null) // only allow start to be called once per thread instance
            {
                t = new Thread(this, threadName);
                t.start ();
            }
        }

        void stop() {
            Log.d(TAG, "Stopping thread " + threadName);
            stopFlag = true;
        }
    }

	// TODO add descriptive comments
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        R1.stop(); // stop repeating MyServiceIntent
        try {
            locationManager.removeUpdates(listener);
        } catch (SecurityException e) {}
    }

}