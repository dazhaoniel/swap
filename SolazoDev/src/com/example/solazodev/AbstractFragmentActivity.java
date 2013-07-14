package com.example.solazodev;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

/**
 * Created by danielzhao on 7/10/13.
 */
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