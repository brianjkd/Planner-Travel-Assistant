package team1028.plannertravelassistant;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Miya on 12/9/2016.
 */

public class LatLngWrapper implements Serializable{
    private ArrayList<GeoLocation> coordinatesList ;
    public LatLngWrapper (ArrayList<GeoLocation> data){
        this.coordinatesList = data;
    }

    public ArrayList<GeoLocation> getCoordinatesList()
    {
        return this.coordinatesList;
    }
}
