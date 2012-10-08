package com.example.swap;


import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;


public class LocationService extends Service implements LocationListener {

	private static final String LOGTAG = "LocationService";
	
	private LocationManager manager;
    private Location currentLocation;
    
    String selectedProvider;
    
    
    @Override
    public void onCreate() {
        manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Log.i(LOGTAG, "Location Service Running...");
    }
    

    public void startService() {
        if(!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //Ask the user to enable GPS
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Location Manager");
            builder.setMessage("We would like to use your location, but GPS is currently disabled.\n"
                    +"Would you like to change these settings now?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Launch settings, allowing user to make a change
                    Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(i);
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //No location service, no Activity
                    return;
                }
            });
            builder.create().show();
        	return;
        }
        //Get a cached location, if it exists
        // manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        //Register for updates
        int minTime = 5000;
        float minDistance = 0;
        Toast.makeText(this, "Starting Location Service", Toast.LENGTH_SHORT).show();
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, this);
    }
    
    public void stopService() {
        Toast.makeText(this, "Stopping Location Service", Toast.LENGTH_SHORT).show();
        manager.removeUpdates(this);
    }
    
    @Override
    public void onDestroy(){
    	manager.removeUpdates(this);
    	Log.i(LOGTAG, "Location Service Stopped...");
    }
    
    private void updateDisplay() {
        if(currentLocation == null) {
            Log.i(LOGTAG, "Determining Your Location...");
        } else {
            Log.i(LOGTAG, String.format("Your Location:\n%.2f, %.2f",
                    currentLocation.getLatitude(),
                    currentLocation.getLongitude()));
        }
    }

    /* Service Access Methods */
    public class LocationBinder extends Binder {
        LocationService getService() {
            return LocationService.this;
        }
    }
    
    private final IBinder binder = (IBinder) new LocationBinder();
    
	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	@Override
	public void onLocationChanged(Location location) {
		currentLocation = location;
		Log.i("LocationService", "Update Location");
		updateDisplay();
	}

	@Override
	public void onProviderDisabled(String provider) {}

	@Override
	public void onProviderEnabled(String provider) {}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {}
}
