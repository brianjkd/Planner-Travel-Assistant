package team1028.plannertravelassistant;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
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

        //CalendarStuff cs = new CalendarStuff();
       // cs.readCalendarEvent(this);
/*
        CalendarContentResolver crs = new CalendarContentResolver(this);
        crs.getCalendars();*/
/*
        CalendarService cs = new CalendarService();
        cs.readCalendar(this, 7, 0);*/

        CalendarProvider calendarProvider = new CalendarProvider(this);
        List<Calendar> calendars = calendarProvider.getCalendars().getList();

        getFilteredList(calendarProvider, calendars);
    }

    protected void getFilteredList(CalendarProvider calendarProvider, List<Calendar> calendars)
    {
        for (Calendar c : calendars){
            List<Event> events = calendarProvider.getEvents(c.id).getList();
            String location = "";
            long currTime = System.currentTimeMillis();
            long msInTwelve = currTime + (60*60*12*1000);
            ArrayList<Event> filtered = new ArrayList<Event>();
            for (Event e : events){
                location = e.eventLocation;
                if (e.eventLocation != null && !e.eventLocation.isEmpty())
                {
                    Log.d(TAG, "event name " +   e.title );
                    Log.d(TAG, "event location " + e.eventLocation);
                    System.out.println("current time " + currTime);
                    System.out.println("twelve hours from now" + msInTwelve);
                    if (e.dTStart > currTime && e.dTStart < msInTwelve)
                    {
                        filtered.add(e);
                        System.out.println(location.length());
                        System.out.println("test "+ location);
                    }
                }
            }
        }
    }
}