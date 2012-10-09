package com.example.guess;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;

public class MainActivity extends Activity implements View.OnClickListener {
    
    Button enableButton, disableButton;
    TextView statusView;
    
    LocationService locationService;
    Location myLocation;
    Intent serviceIntent;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        enableButton = (Button)findViewById(R.id.refresh);
        enableButton.setOnClickListener(this);
        statusView = (TextView)findViewById(R.id.status);
        
        serviceIntent = new Intent(this, LocationService.class);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        //Starting the service makes it stick, regardless of bindings
        startService(serviceIntent);
        //Bind to the service
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }
    
    @Override
    public void onPause() {
        super.onPause();
        stopService(serviceIntent);
        
        //Unbind from the service
        unbindService(serviceConnection);
    }
    
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
        case R.id.refresh:
            locationService.startTracking();
            break;
        default:
            break;
        }
        updateStatus();
    }
    
    private void updateStatus() {
    	myLocation = locationService.getLocation();
    	if( myLocation != null ) {
    		statusView.setText(String.format("Current Location Guess - Latitude: " + myLocation.getLatitude() + ", Longitude: " + myLocation.getLongitude() ));
    	}
    }
    
    private ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            locationService = ((LocationService.LocationBinder)service).getService();
            updateStatus();
        }

        public void onServiceDisconnected(ComponentName className) {
            locationService = null;
        }
    };
}
