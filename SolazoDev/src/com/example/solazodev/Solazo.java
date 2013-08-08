package com.example.solazodev;

import android.location.Location;

/**
 * Created by danielzhao on 7/10/13.
 */
public class Solazo {

    private static Solazo INSTANCE = new Solazo();

    private Location mCurrentLocation;
    private String mAddress;
//    private LocationActivity mLocationActivity;
//    public final int LOCATION_SERVICE_TIME_OUT_COUNT = 6;


    private Solazo() {
//        mLocationActivity = new LocationActivity();
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
//
//    public void startLocationUpdates() {
//        mLocationService.startTracking();
//    }
//
//    public void stopLocationUpdates() {
//        mLocationService.stopTracking();
//    }
}
