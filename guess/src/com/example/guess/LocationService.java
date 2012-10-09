package com.example.guess;


import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class LocationService extends Service implements LocationListener {

    private static final String LOGTAG = "LocationService";
    
    private LocationManager manager;
    private Location currentLocation;
    private boolean locationChanged = false;
    
    /* Service Setup Methods */
    @Override
    public void onCreate() {
        manager = (LocationManager)getSystemService(LOCATION_SERVICE);
        Log.i(LOGTAG, "Location Service Running...");
    }
    
    public void startTracking() {
        if(!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return;
        }
        Toast.makeText(this, "Refreshing Location Service...", Toast.LENGTH_SHORT).show();
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, this);
    }
    
    public void stopTracking() {
//        Toast.makeText(this, "Stopping Location Service", Toast.LENGTH_SHORT).show();
        manager.removeUpdates(this);
    }
    
    @Override
    public void onDestroy() {
        manager.removeUpdates(this);
        Log.i(LOGTAG, "Location Service Stopped...");
    }
    
    /* Service Access Methods */
    public class LocationBinder extends Binder {
        LocationService getService() {
            return LocationService.this;
        }
    }
    
    private final IBinder binder = new LocationBinder();
    
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
    
    public Location getLocation() {
        return currentLocation;
    }
    
    /* LocationListener Methods */
    @Override
    public void onLocationChanged(Location location) {
    	currentLocation = location;
    	setLocationChanged(true);
        Log.i("LocationService", "Adding new location");
    }

    @Override
    public void onProviderDisabled(String provider) { }

    @Override
    public void onProviderEnabled(String provider) { }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { }

	public boolean isLocationChanged() {
		return locationChanged;
	}

	public void setLocationChanged(boolean locationChanged) {
		this.locationChanged = locationChanged;
	}
}
