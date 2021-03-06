package team1028.plannertravelassistant;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

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

	private static final String TAG = "TrafficRouter";

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

	// Get expected travel time from requested arrival time and travel mode
	String getTrafficJson(String units, String arrivalTime, String mode, String trafficModel) {
		String jsonResult;

		// Check for units (default is imperial for this app)
		if (units == null || units.equals("")) {
			units = DEFAULT_UNITS;
		}

		// Form request
		String request = formRequest(units, arrivalTime, mode, trafficModel);

		// Attempt to make request
		try {
			jsonResult = RestCalls.doGet(request);
		} catch (IOException e) {
			return null; // TODO handle errors
		}

		// TODO parse JSON result
		return jsonResult;
	}

	/**
	 * Turns parameters into a request string for Google Distance Matrix
	 * @param units Units (metric or imperial)
	 * @param arrivalTime Requested arrival time (in Google API form - seconds since midnight on Jan 1, 1970)
	 * @return Request URL to send
	 */
	private String formRequest(String units, String arrivalTime, String mode, String trafficModel) {
		String requestURL = DIST_MAT_URL; // Request URL to add onto
		String origReqs = "";// = origins.get(0); // TODO filter events - see if the place exists
		String destReqs = "";// = destinations.get(0);

		// Append additional origins to the request (start with second origin to reduce if statements)
		for (int i = 0; i < origins.size(); i++) {
			// First origin is already added, so all others are appended with "|"
			// TODO check for valid location!
			origReqs += "|" + origins.get(i); // TODO handle place_id
		}

		// Append additional destinations to the request (start with second destination)
		for (int i = 0; i < destinations.size(); i++) {
			// First destination is already added, so all others are appended with "|"
			destReqs += "|" + destinations.get(i);
		}

		// Combine all params into the request string
		requestURL += "units=" + units; // Units comes first, so has no "&" at beginning

		// Add arrival time, if specified TODO more checks
		if (arrivalTime != null && !arrivalTime.equals("")) {
			requestURL +="&arrival_time=" + arrivalTime;
		}

		// TODO for some reason this causes errors
//		if (trafficModel != null && !trafficModel.equals("")) {
//			requestURL += "&traffic_model=" + trafficModel;
//		}

		if (mode != null && !mode.equals("")) {
			requestURL += "&mode=" + mode;
		}

		// TODO add mode, traffic model

		requestURL += "&origins=" + origReqs;
		requestURL += "&destinations=" + destReqs;
		requestURL += "&key=" + API_KEY;

		return requestURL;
	}

	// Calculate travel duration in seconds for a matrix of origin/destination
	float parseTravelDuration(String json) {
		float duration = 0; // the travel duration
		try {
			int rowSize = new JSONObject(json)
					.getJSONArray("rows").length();

			for (int i = 0; i < rowSize; i++) {
				int elementSize = new JSONObject(json)
						.getJSONArray("rows")
						.getJSONObject(i)
						.getJSONArray ("elements").length();

				if (rowSize == elementSize) {
					JSONObject jsonObject = new JSONObject(json)
							.getJSONArray("rows")
							.getJSONObject(i)
							.getJSONArray ("elements")
							.getJSONObject(i);
					String status  = jsonObject.get("status").toString();

					if (status.equals("OK")) {
						JSONObject jsonDurationObject = jsonObject.getJSONObject("duration");
						// Get event duration in seconds
						duration += Float.parseFloat( jsonDurationObject.get("value").toString());
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			Log.d(TAG, "parseTravelDurationFailed: " + json);
			return duration;
		}
		return duration;
	}

/* TODO remove this?
	// this method returns the travel duration in seconds for the first origin/destination
	public float parseTravelDuration(String json){
		float duration = -1; // the travel duration
		try {
			JSONObject jsonObject = new JSONObject(json)
					.getJSONArray("rows")
					.getJSONObject(0)
					.getJSONArray ("elements")
					.getJSONObject(0);
			String status  = jsonObject.get("status").toString();

			if (status.equals("OK")) {
				JSONObject jsonDurationObject = jsonObject.getJSONObject("duration");
				// Get event duration in seconds
				duration = Float.parseFloat( jsonDurationObject.get("value").toString());
			}
		} catch (JSONException e) {
			e.printStackTrace();
			Log.d(TAG, "parseTravelDurationFailed: " + json);
			return duration;
		}
		return duration;
	}*/
}