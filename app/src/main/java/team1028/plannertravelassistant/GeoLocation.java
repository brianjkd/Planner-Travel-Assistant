package team1028.plannertravelassistant;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Miya on 12/9/2016.
 */

public class GeoLocation {

    LatLng latitude;
    LatLng longitude;

    GeoLocation (LatLng latitude, LatLng longitude)
    {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    LatLng getLatitude(){ return latitude; }
    LatLng getLongitude(){ return longitude; }
    void setLatitude(LatLng lat){ this.latitude = lat;}
    void setLongitude(LatLng lon){ this.longitude = lon;}
}
