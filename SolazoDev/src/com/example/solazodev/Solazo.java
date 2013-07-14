package com.example.solazodev;

import android.location.Location;

/**
 * Created by danielzhao on 7/10/13.
 */
public class Solazo {

    private static final Solazo INSTANCE = new Solazo();
    private Location mCurrentLocation;
    private LocationService mLocationService;
    public final int LOCATION_SERVICE_TIME_OUT_COUNT = 6;


    private Solazo() {
        mLocationService = new LocationService();
    }

    public static synchronized Solazo getInstance() {
        return INSTANCE;
    }

    public Location getLocation() {
        return mCurrentLocation;
    }

    public void setCurrentLocation(Location loc) {
        this.mCurrentLocation = loc;
    }

    public void startLocationUpdates() {
        mLocationService.startTracking();
    }

    public void stopLocationUpdates() {
        mLocationService.stopTracking();
    }


}
