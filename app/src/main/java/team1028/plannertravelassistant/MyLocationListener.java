package team1028.plannertravelassistant;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

class MyLocationListener implements LocationListener {
    private static final int TWO_MINUTES = 1000 * 60 * 2; // TODO did I rename this correctly?
    private static final String TAG = "MyLocationListener";
    private Location PREV_BEST_LOCATION = null;

    private Context context;



    public Location getLocation(){
        return this.PREV_BEST_LOCATION;
    }

    MyLocationListener(Context context) {
        super();
        this.context = context;
    }

	/**
	 * Tell if new location is better than current best
	 * @param location New location
	 * @param currentBestLocation Current best location
	 * @return True if new Location is better, false otherwise
	 */
    private boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            return true; // A new location is always better than no location
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int)(location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 75;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

	/**
	 * Tell if two Location providers are the same
	 * @param provider1 Location Provider 1
	 * @param provider2 Location Provider 2
	 * @return True if Providers are the same, false otherwise
	 */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

	/**
	 * Update activity if new best Location is found
	 * @param loc New Location
	 */
    public void onLocationChanged(final Location loc) {
        if (isBetterLocation(loc, PREV_BEST_LOCATION)) {
            PREV_BEST_LOCATION = loc;
            // send the new location to the Main Activity
            // which is listening
            //sendMessageToActivity(PREV_BEST_LOCATION);
        }
    }

	// TODO what are these for?
    public void onProviderDisabled(String provider) {
    }

    public void onProviderEnabled(String provider) {
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {

    }
}