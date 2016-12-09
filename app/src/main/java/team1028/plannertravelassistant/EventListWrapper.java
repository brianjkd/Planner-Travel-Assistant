package team1028.plannertravelassistant;


import java.io.Serializable;
import java.util.ArrayList;

import me.everything.providers.android.calendar.Event;

public class EventListWrapper implements Serializable {

    private ArrayList<Event> events;

    public EventListWrapper(ArrayList<Event> data) {
        this.events = data;
    }

    public ArrayList<Event> getEvents() {
        return this.events;
    }

}