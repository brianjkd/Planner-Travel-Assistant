package team1028.plannertravelassistant;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

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


        for (Calendar c : calendars){
            List<Event> events = calendarProvider.getEvents(c.id).getList();
                for (Event e : events){
                    Log.d(TAG, "event name " +   e.title );
                }
        }
        
    }
}