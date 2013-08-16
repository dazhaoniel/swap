package org.solazo.swap;

//import android.location.Address;
import android.location.Location;

/**
 * Created by Daniel Zhao on 7/10/13.
 */
public class Solazo {

    private static Solazo INSTANCE = new Solazo();

    private Location mCurrentLocation;
    private String mAddress;


    private Solazo() {
    }

    public static synchronized Solazo getInstance() {
        return INSTANCE;
    }

    public Location getLocation() {
        return mCurrentLocation;
    }

    public String getAddress(){
        return mAddress;
    }

    public void setCurrentLocation(Location loc) {
        this.mCurrentLocation = loc;
    }

    public void setAddress( String address ) {
        this.mAddress = address;
    }
}
