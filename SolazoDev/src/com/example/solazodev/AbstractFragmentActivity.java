package com.example.solazodev;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.location.Location;


public abstract class AbstractFragmentActivity extends SherlockFragmentActivity {

    protected Solazo mSolazo;
    private LocationService locationService;
    private Intent serviceIntent;

    public AbstractFragmentActivity() {
        super();
    }

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        mSolazo = Solazo.getInstance();

        serviceIntent = new Intent(this, LocationService.class);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Starting the service makes it stick, regardless of bindings
        startService(serviceIntent);
        // Bind to the service
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onPause() {
        super.onPause();
        stopService(serviceIntent);

        // Unbind from the service
        unbindService(serviceConnection);
    }

    public Boolean refreshCurrentLocation(Solazo solazo) {
        if( solazo.getLocation() != null || locationService.refreshCurrentLocation(solazo)) {
            return true;
        } else {
            int count = 0;

            while ( solazo.getLocation() == null && count < solazo.LOCATION_SERVICE_TIME_OUT_COUNT ) {
                try {
                    wait(10000);
                } catch (Exception ignored) {}

                count++;
    //            return true;
            }

            if ( solazo.getLocation() != null )
                return true;
            return false;
        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            locationService = ((LocationService.LocationBinder) service)
                    .getService();
            locationService.startTracking();
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            locationService.stopTracking();
            locationService = null;
        }
    };

    public void showProgressBarIndeterminate() {
        setSupportProgressBarIndeterminateVisibility( true );
    }

    public void hideProgressBarIndeterminate() {
        setSupportProgressBarIndeterminateVisibility( false );
    }
}