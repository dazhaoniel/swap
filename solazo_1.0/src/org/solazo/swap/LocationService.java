package org.solazo.swap;

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
	private Location gpsLocation;
	private Location networkLocation;

	/* Service Setup Methods */
	@Override
	public void onCreate() {
		manager = (LocationManager) getSystemService(LOCATION_SERVICE);
		Log.i(LOGTAG, "Location Service Running...");
	}

	public void startTracking() {
		final boolean gpsEnabled = manager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		final boolean networkEnabled = manager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		if (!gpsEnabled && !networkEnabled) {
			Toast.makeText(this,
					"Please enable Location Service to use this app...",
					Toast.LENGTH_LONG).show();
			return;
		}
		Toast.makeText(this, "Determining Current Location...",
				Toast.LENGTH_LONG).show();

		networkLocation = requestUpdatesFromProvider(LocationManager.NETWORK_PROVIDER);
		gpsLocation = requestUpdatesFromProvider(LocationManager.GPS_PROVIDER);

		if (gpsLocation != null && networkLocation != null) {
			currentLocation = getBetterLocation(gpsLocation, networkLocation);
		} else if (gpsLocation != null) {
			currentLocation = gpsLocation;
		} else if (networkLocation != null) {
			currentLocation = networkLocation;
		}
	}

	private Location requestUpdatesFromProvider(final String provider) {
		Location location = null;
		if (manager.isProviderEnabled(provider)) {
			manager.requestLocationUpdates(provider, 60000, 0, this);
		} else {
			location = manager.getLastKnownLocation(provider);
		}
		return location;
	}

	public void stopTracking() {
		manager.removeUpdates(this);
	}

	@Override
	public void onDestroy() {
		manager.removeUpdates(this);
		Log.i(LOGTAG, "Location Service Stopped...");
	}

	public Location getLocation() {
		return this.currentLocation;
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

	/* LocationListener Methods */
	@Override
	public void onLocationChanged(Location location) {
		this.currentLocation = location;
		Log.i(LOGTAG, "Adding new location");
		Log.i(LOGTAG,
				currentLocation.getLatitude() + ", "
						+ currentLocation.getLongitude());
		Toast.makeText(
				this,
				String.format(currentLocation.getLatitude() + ", "
						+ currentLocation.getLongitude()), Toast.LENGTH_SHORT)
				.show();
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	protected Location getBetterLocation(Location newLocation,
			Location currentBestLocation) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return newLocation;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = newLocation.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > 120000;
		boolean isSignificantlyOlder = timeDelta < -120000;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use
		// the new location
		// because the user has likely moved.
		if (isSignificantlyNewer) {
			return newLocation;
			// If the new location is more than two minutes older, it must be
			// worse
		} else if (isSignificantlyOlder) {
			return currentBestLocation;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (newLocation.getAccuracy() - currentBestLocation
				.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(newLocation.getProvider(),
				currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and
		// accuracy
		if (isMoreAccurate) {
			return newLocation;
		} else if (isNewer && !isLessAccurate) {
			return newLocation;
		} else if (isNewer && !isSignificantlyLessAccurate
				&& isFromSameProvider) {
			return newLocation;
		}
		return currentBestLocation;
	}

	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}

}