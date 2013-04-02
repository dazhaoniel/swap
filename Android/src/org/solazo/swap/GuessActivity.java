package org.solazo.swap;

import java.io.IOException;
import org.apache.http.client.ClientProtocolException;
import java.util.ArrayList;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.net.ParseException;
import android.util.Log;

@SuppressLint("NewApi")
public class GuessActivity extends Activity {

	private JSONArray jArray;
	private String result = null;
	private InputStream is = null;
	private StringBuilder sb = null;

	private LocationService locationService;
	private Intent serviceIntent;

	private static final String LOGTAG = "GuessActivity";

	private double latitude;
	private double longitude;
	TextView display_temp;
	TextView display_humid;


	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guess);
		serviceIntent = new Intent(this, LocationService.class);
		/*
		Location loc = locationService.getLocation();
		while (loc == null) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		latitude = loc.getLatitude();
		longitude = loc.getLongitude();

		new GuessTask().execute();
		*/
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

	public void updateUIDisplay(View v) {
		Location loc = locationService.getLocation();

		if (loc == null) {
			// Do nothing
		} else {
			latitude = loc.getLatitude();
			longitude = loc.getLongitude();

			new GuessTask().execute();
		}
	}

	private class GuessTask extends AsyncTask<String, String, String> {

		// @Override
		@Override
		protected String doInBackground(String... params) {
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("c_latitude", Double
					.toString(latitude)));
			nameValuePairs.add(new BasicNameValuePair("c_longitude", Double
					.toString(longitude)));

			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(
					"http://solazo.org/scripts/guess.php");

			// URL Encoding the POST Parameters
			try {
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			} catch (Exception e) {
				Log.i(LOGTAG,
						"Error in URL Encoding the POST Parameters: "
								+ e.toString());
			}

			// Making Http Request

			try {
				HttpResponse response = httpclient.execute(httppost);
				// Writing response to log
				Log.i(LOGTAG, "Http Request Response: " + response.toString());

				HttpEntity entity = response.getEntity();
				is = entity.getContent();
			} catch (ClientProtocolException e) {
				Log.i(LOGTAG, "Error in Http Request " + e.toString());
			} catch (IOException e) {
				Log.i(LOGTAG, "Error in Http Request " + e.toString());
			}

			// convert response to string

			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is, "iso-8859-1"), 8);
				sb = new StringBuilder();
				sb.append(reader.readLine() + "\n");

				String line = "0";
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				is.close();
				result = sb.toString();
			} catch (Exception e) {
				Log.i(LOGTAG, "Error converting result " + e.toString());
			}

			return result;

		} // end do in background

		@Override
		protected void onPostExecute(String result) {

			// paring data

			/*******************************************************************
			 * variables for return values from servers, remember, php script
			 * can only print the values you need, no warnings, etc
			 *******************************************************************/

			String[] temp = new String[5];
			String[] humid = new String[5];
			String[] noaa_station = new String[5];
			String[] noaa_distance = new String[5];
			String[] noaa_temp = new String[5];
			String[] noaa_humid = new String[5];

			/***********************************************************************/

			try {

				jArray = new JSONArray(result);
				JSONObject json_data = null;

				for (int i = 0; i < jArray.length(); i++) {
					json_data = jArray.getJSONObject(i);
					temp[i] = json_data.getString("temp");
					humid[i] = json_data.getString("humid");
					noaa_station[i] = json_data.getString("noaa_station");
					noaa_distance[i] = json_data.getString("noaa_distance");
					noaa_temp[i] = json_data.getString("noaa_temp");
					noaa_humid[i] = json_data.getString("noaa_humid");
				}

				// Toast.makeText(getBaseContext(), noaa_station[0],
				// Toast.LENGTH_LONG).show();
				String the_results = "Temperature: " + temp[0]
						+ " degrees \n Humidity: " + humid[0] + "% \n "
						+ "Closest Station: " + noaa_station[0] + "\n"
						+ "Miles from you: " + noaa_distance[0];

				display_temp = (TextView) findViewById(R.id.status);
				display_temp.setText(the_results);

			} catch (JSONException e1) {
				Toast.makeText(getBaseContext(),
						"Oops...something went wrong :(", Toast.LENGTH_LONG)
						.show();
			} catch (ParseException e1) {
				e1.printStackTrace();

			}

		}
	}
}
