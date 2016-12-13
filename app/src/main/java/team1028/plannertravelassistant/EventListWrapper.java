package team1028.plannertravelassistant;


import java.io.Serializable;
import java.util.ArrayList;

import me.everything.providers.android.calendar.Event;

/**
 * Wrapper for list of events (allows for encapsulation)
 */
public class EventListWrapper implements Serializable {
    private ArrayList<Event> events; // List of events

    // Constructor
    public EventListWrapper(ArrayList<Event> data) {
        this.events = data;
    }

    // Get list of events
    public ArrayList<Event> getEvents() {
        return this.events;
    }
}