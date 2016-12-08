package team1028.plannertravelassistant;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.ArrayList;
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

            if (filteredEvents.size() > 0){ // we have at least one event with a location
                ArrayList<String> origins = new ArrayList<String>();
                ArrayList<String> destinations = new ArrayList<String>();

                // the current user's location as the origin
                String userLoc = Double.toString(lastKnownLoc.getLatitude()) + "+,+" +
                        Double.toString(lastKnownLoc.getLongitude());
                origins.add(userLoc);
                destinations.add(filteredEvents.get(0).eventLocation);

                TrafficRouter trafficRouter = new TrafficRouter(origins, destinations);
                String retJson = trafficRouter.getTrafficJson("imperial", null, "driving", "pessimistic");

                float travelTime = trafficRouter.parseTravelDuration(retJson);
                if (travelTime > 0 ){ // negative travel time means traffic lookup failed
                    Log.d(TAG, "onHandleIntent: travel time " + travelTime);
                }
                else {
                    Log.d(TAG, "onHandleIntent: traffic lookup failed");
                }

                // send the filtered list of location names to the main activity so it can draw to screen
                ArrayList<String> locations = getLocations(filteredEvents);
                sendMessageToActivity(locations);
            }
            else {
                Log.d(TAG, "onHandleIntent: no events with location");
            }
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