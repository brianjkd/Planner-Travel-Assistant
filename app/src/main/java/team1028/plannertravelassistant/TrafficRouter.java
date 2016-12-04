package team1028.plannertravelassistant;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Class to calculate distances and times between Locations. Uses JSON (not XML)!
 * Created by Maddy on 12/4/2016.
 */
class TrafficRouter {
	// Store Locations as Strings (can be name, place ID, or lat/long)
	private ArrayList<String> origins; // String list of origins
	private ArrayList<String> destinations; // String list of destinations

	// Basic URL for distance/time requests
	private final String DIST_MAT_URL = "https://maps.googleapis.com/maps/api/distancematrix/json?";

	// Setup
	private final String API_KEY = "AIzaSyBl51dawLzyjI2ZK_i-ZONlL2-rEeKFhts"; // Really really really bad, but who cares
	private final String DEFAULT_UNITS = "imperial";

	// TODO handle malformed data - handle errors
	public TrafficRouter(ArrayList<String> orig, ArrayList<String> dest) {
		this.origins = orig;
		this.destinations = dest;
	}

	public double totalTravelTime(String units, String arrivalTime) {
		double totalTime = -1; // Error output is -1
		String request = formRequest(units, arrivalTime);

		// Attempt to make request
		try {
			String jsonResult = RestCalls.doGet(request);
		} catch (IOException e) {
			return -1; // TODO handle errors
		}

		// TODO parse JSON result
		return totalTime;
	}

	/**
	 * Turns parameters into a request string for Google Distance Matrix
	 * @param units Units (metric or imperial)
	 * @param arrivalTime Requested arrival time (in Google API form - seconds since midnight on Jan 1, 1970)
	 * @return Request URL to send
	 */
	private String formRequest(String units, String arrivalTime) {
		String requestURL = DIST_MAT_URL; // Request URL to add onto
		String origReqs = origins.get(0); // TODO filter events - see if the place exists
		String destReqs = destinations.get(0);

		if (units == null || units.equals("")) {
			units = DEFAULT_UNITS;
		}

		// Append additional origins to the request (start with second origin to reduce if statements)
		for (int i = 1; i < origins.size(); i++) {
			// First origin is already added, so all others are appended with "|"
			// TODO check for valid location!
			origReqs += "|" + origins.get(i); // TODO handle place_id
		}

		// Append additional destinations to the request (start with second destination)
		for (int i = 1; i < destinations.size(); i++) {
			// First destination is already added, so all others are appended with "|"
			destReqs += "|" + destinations.get(i);
		}

		// Combine all params into the request string
		requestURL += "units=" + units; // Units comes first, so has no "&" at beginning
		requestURL += "&origins=" + origReqs;
		requestURL += "&destinations=" + destReqs;
		requestURL += "&key=" + API_KEY;

		return requestURL;
	}
}
