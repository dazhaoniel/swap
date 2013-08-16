package org.solazo.swap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class SubmitActivity extends Activity {

	private JSONArray jArray;
	private String result = null;
	private InputStream is = null;
	private StringBuilder sb = null;

	private LocationService locationService;
	private Intent serviceIntent;

	private static final String LOGTAG = "PostActivity";

	private double latitude;
	private double longitude;
	EditText edit_temp;
	EditText edit_humid;
	TextView display_message;
	
	String message_temp = null;
	String message_humid = null;


	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_submit);
		serviceIntent = new Intent(this, LocationService.class);

		edit_temp = (EditText) findViewById(R.id.editTemperature);
		edit_humid = (EditText) findViewById(R.id.editHumidity);

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
		message_temp = edit_temp.getText().toString();
		message_humid = edit_humid.getText().toString();

		if (loc == null) {
			// Do nothing
		} else {
			latitude = loc.getLatitude();
			longitude = loc.getLongitude();

			new PostTask().execute();
			edit_temp.setText(null);
			edit_humid.setText(null);
		}
	}

	private class PostTask extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			// Get Date Format
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date();
			
			// Get Device UUID
			final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
			final String tmDevice, tmSerial, androidId;
		    tmDevice = "" + tm.getDeviceId();
		    tmSerial = "" + tm.getSimSerialNumber();
		    androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

		    UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
		    String userId = deviceUuid.toString();
			
			// Send Values
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("c_latitude", Double
					.toString(latitude)));
			nameValuePairs.add(new BasicNameValuePair("c_longitude", Double
					.toString(longitude)));
			nameValuePairs.add(new BasicNameValuePair("c_user_temp", message_temp) );
			nameValuePairs.add(new BasicNameValuePair("c_user_humid", message_humid) );
			nameValuePairs.add(new BasicNameValuePair("c_date_time", dateFormat.format(date).toString() ) );
			nameValuePairs.add(new BasicNameValuePair("c_user_id", userId) );

			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(
					"");

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

			@SuppressWarnings("unused")
			String postMessage = null;

			try {

				jArray = new JSONArray(result);
				JSONObject json_data = null;

				for (int i = 0; i < jArray.length(); i++) {
					json_data = jArray.getJSONObject(i);
					postMessage = json_data.getString("test");
				}

				Toast.makeText(getBaseContext(),
						"Thank you, that was super helpful :)", Toast.LENGTH_LONG)
						.show();

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
