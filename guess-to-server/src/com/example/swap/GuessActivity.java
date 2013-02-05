package com.example.swap;

import java.io.IOException;
import org.apache.http.client.ClientProtocolException;
import java.util.ArrayList;
//import java.util.List;
//import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

// import everything you need
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
import android.widget.Toast;
//import android.view.Menu;

import java.io.BufferedReader;
//import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

//import org.apache.http.client.entity.UrlEncodedFormEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//import android.app.ListActivity;
//import android.location.Location;
import android.net.ParseException;
import android.util.Log;

@SuppressLint("NewApi")
public class GuessActivity extends Activity {

	JSONArray jArray;
	String result = null;
	InputStream is = null;
	StringBuilder sb = null;
	private static final String LOGTAG = "GuessActivity";

	// private Location location;

	/***********************************************************************
	 * here's long and latitude, just set your vars equal to these
	 ************************************************************************/
	double latitude = 44.943999;
	double longitude = -73.605117;

	/***********************************************************************/

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guess);

		new GuessTask().execute();
	}

	private class GuessTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
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
			// So far so good

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
				// output = json_data.getString("user_id");

				for (int i = 0; i < jArray.length(); i++) {
					json_data = jArray.getJSONObject(i);
					temp[i] = json_data.getString("temp");
					humid[i] = json_data.getString("humid");
					noaa_station[i] = json_data.getString("noaa_station");
					noaa_distance[i] = json_data.getString("noaa_distance");
					noaa_temp[i] = json_data.getString("noaa_temp");
					noaa_humid[i] = json_data.getString("noaa_humid");
					/****************
					 * ***************************************************
					 * display stuff, for debugging..
					 * ***************************
					 * **************************************
					 */
				}
				Toast.makeText(getBaseContext(), noaa_distance[0],
						Toast.LENGTH_LONG).show();
				/*****************************************************************
				 * ** display stuff
				 * *************************************************
				 * ***************** //Toast.makeText(getBaseContext(),
				 * Double.toString(sc_distance[0]) ,Toast.LENGTH_LONG).show();
				 * Toast.makeText(getBaseContext(), output[0]
				 * ,Toast.LENGTH_LONG).show();
				 * /*************************************
				 * ****************************
				 */
			} catch (JSONException e1) {
				Toast.makeText(getBaseContext(),
						"oops...something went wrong :(", Toast.LENGTH_LONG)
						.show();
			} catch (ParseException e1) {
				e1.printStackTrace();

			}

			return null;

		} // end onCreate
		/*
		 * @Override public boolean onCreateOptionsMenu(Menu menu) {
		 * getMenuInflater().inflate(R.menu.activity_guess, menu); return true;
		 * }
		 */
	}
} // end Activity
