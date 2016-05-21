package wychoniu.bybus;

import android.location.Location;

public class BusStop {
    public String name;
    public String direction;
    public double latitude;
    public double longitude;
    public int line;
    Location location = new Location("Point");

    public BusStop(String n, double lat, double longi, int l, String dir){
        name = n;
        latitude = lat;
        longitude = longi;
        line = l;
        direction = dir;
        location.setLatitude(latitude);
        location.setLongitude(longitude);
    }
}
