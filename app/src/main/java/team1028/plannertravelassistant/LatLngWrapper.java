package team1028.plannertravelassistant;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Wrapper to handle latitude and longitude details
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
