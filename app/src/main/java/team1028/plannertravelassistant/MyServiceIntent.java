package team1028.plannertravelassistant;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import me.everything.providers.android.calendar.Calendar;
import me.everything.providers.android.calendar.CalendarProvider;
import me.everything.providers.android.calendar.Event;

public class MyServiceIntent extends IntentService {

    public static final String TAG = "MyServiceIntent";

    public MyServiceIntent(String name) {
        super(name);
    }

    public MyServiceIntent() {
        super("MyServiceIntent");
    }


    // push a notification to the user that will launch google navigation
    //
    private void pushNotification(Event e, String destination){
        // send notification to launch navigation application
        NotificationManager notificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        String navURL = "http://maps.google.com/maps?&daddr="+ destination;

        // touching the pushed notification will result in google navigation launching
        // with the user's current location and destination location as travel path
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse(navURL));

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentIntent(pendingIntent)
                .setAutoCancel(false)
                .setColor(Color.RED)
                .setVibrate(new long[] { 1000, 1000})
                .setContentTitle(e.title);

        builder.setSmallIcon(R.drawable.fg_service);
        // use the even'ts uuid as the notification id
        // to prevent notifying user multiple times for the same event
        int notificationID = (int) e.id;
        Notification notification = builder.build();
        notificationManager.notify(notificationID, notification);
    }

    @Override
    protected void onHandleIntent(Intent workIntent) { // do stuff when alarm starts this intent
        Log.d(TAG, "onHandleIntent: Do stuff");

        Bundle b = workIntent.getBundleExtra("Location");
        Location lastKnownLoc = b.getParcelable("Location");

        // Check that location is valid
        if (lastKnownLoc != null) {
            CalendarProvider calendarProvider = new CalendarProvider(this);
            List<Calendar> calendars = calendarProvider.getCalendars().getList();
            ArrayList<Event> filteredEvents = getFilteredList(calendarProvider, calendars);

            // the current user's location as the origin
            String userLoc = Double.toString(lastKnownLoc.getLatitude()) + "+,+" +
                    Double.toString(lastKnownLoc.getLongitude());

            for (Event e : filteredEvents){ // this loop stops after the first successful traffic lookup
                ArrayList<String> origins = new ArrayList<String>();
                ArrayList<String> destinations = new ArrayList<String>();

                String destination = e.eventLocation;
                destination = destination.replaceAll("\\s", "+"); // format for get request
                origins.add(userLoc);
                destinations.add(destination);

                // figure out the travel time from current location to destination
                TrafficRouter trafficRouter = new TrafficRouter(origins, destinations);
                String retJson = trafficRouter.getTrafficJson("imperial", null, "driving", "pessimistic");
                Log.d(TAG, "returned json: " + retJson);
                float travelTime = trafficRouter.parseTravelDuration(retJson);

                if (travelTime > 0 ){ // negative travel time means traffic lookup failed
                    Log.d(TAG, "onHandleIntent: travel time " + travelTime);

                    long currTime = System.currentTimeMillis();
                    long travelTimeMS = (long) travelTime * 1000; // travel time in milliseconds
                    long spareTime = 1000 * 60 * 10;// all events will ensure 10 minutes of spare time

                    // push a notification if the user needs to leave in order to make event
                    // on time with the specified spare time
                    if (currTime + spareTime + travelTimeMS > e.dTStart){
                        // we do not want to push this notification again. We will
                        // take advantage of saving to prefs to save the ids of pushed
                        // notifications so we only push them once

                        SharedPreferences prefs = this.getSharedPreferences("team1028.plannertravelassistant", Context.MODE_PRIVATE);
                        boolean alreadyPushed = prefs.contains(String.valueOf(e.id));

                        if (!alreadyPushed){
                            pushNotification(e, destination);
                            prefs.edit().putLong(String.valueOf(e.id), currTime).apply();
                        }
                        else {
                            Log.d(TAG, "onHandleIntent: Not pushing because user has already been notified about this event");
                        }
                    }
                    break; // stop the for loop, handled the next valid event
                }
                else {
                    Log.d(TAG, "onHandleIntent: traffic lookup failed");
                }
            }

                // send the filtered list of location names to the main activity so it can draw to
                // note, the locations sent to main activity have location strings but they may be invalid locations
                // use geocoder on main activity since the lat lon coordinates still need to be extracted
                ArrayList<String> locations = getLocations(filteredEvents);
                sendMessageToActivity(locations);
            }
            else {
                Log.d(TAG, "onHandleIntent: user's current location is null");
            }
        }


    protected ArrayList<Event> getFilteredList(CalendarProvider calendarProvider, List<Calendar> calendars)
    {
        ArrayList<Event> filtered = new ArrayList<Event>();
        for (Calendar c : calendars){
            List<Event> events = calendarProvider.getEvents(c.id).getList();
            long currTime = System.currentTimeMillis();
            long msInTwelve = currTime + (60*60*12*1000);
            for (Event e : events){
                if (e.eventLocation != null && !e.eventLocation.isEmpty())
                {
                    if (e.dTStart > currTime && e.dTStart < msInTwelve)
                    {
                        filtered.add(e);
                        Log.d(TAG, "getFilteredList: found event with location within 12 hours " + e.title);
                    }
                }
            }
        }

        // make sure that events are sorted by timestamp and not some other metric
        Collections.sort(filtered, new Comparator<Event>(){
            public int compare(Event e1, Event e2){
                if(e1.dTStart == e2.dTStart)
                    return 0;
                return e1.dTStart < e2.dTStart ? -1 : 1;
            }
        });


        return filtered;
    }


    protected ArrayList<String> getLocations(ArrayList<Event> events)
    {
        ArrayList<String> ret = new ArrayList<String>();

        for (Event e : events){
            ret.add(e.eventLocation);
        }
        return ret;
    }


    private void sendMessageToActivity(ArrayList<String> locations) {
        Log.d(TAG, "Sending message to main activity with list of locations as strings");
        // Create Event for change of Location
        Intent intent = new Intent("locations");
        intent.putStringArrayListExtra("locationList", locations);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


}