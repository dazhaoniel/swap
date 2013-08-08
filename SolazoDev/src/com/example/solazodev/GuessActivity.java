package com.example.solazodev;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.widget.Toast;
import android.location.Location;
import android.net.ParseException;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import java.util.ArrayList;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

public class GuessActivity extends SherlockFragment {

    private Solazo mSolazo;

    private Location mLocation;

    private JSONArray jArray;
    private String result = null;
    private InputStream is = null;
    private StringBuilder sb = null;


    // Handles to UI widgets
    private ProgressBar mActivityIndicator;

    private TextView display_temperature;
    private TextView display_humidity;
    private TextView display_station;
    private TextView display_distance;
    private TextView display_current_location;

    private AsyncTask<String, Void, String> mGuessTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSolazo = Solazo.getInstance();

        if ( mSolazo != null ) {
            mLocation = mSolazo.getLocation();
            mGuessTask = new GuessTask().execute();
        } else {
            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

//        FragmentManager fm = getSupportFragmentManager();
//
//        if (fm.findFragmentById(android.R.id.content) == null) {
//            GuessFragment guessResults = new GuessFragment();
//            fm.beginTransaction().add(android.R.id.content, guessResults).commit();
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_guess, container, false);

        // Get handles to the UI view objects
        mActivityIndicator = (ProgressBar) view.findViewById(R.id.progressBar);

        display_temperature = (TextView) view.findViewById(R.id.guess_temperature);
        display_humidity = (TextView) view.findViewById(R.id.guess_humidity);
        display_station = (TextView) view.findViewById(R.id.guess_station);
        display_distance = (TextView) view.findViewById(R.id.guess_distance);
        display_current_location = (TextView) view.findViewById(R.id.guess_current_location);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        if (mGuessTask != null && mGuessTask.getStatus() == AsyncTask.Status.RUNNING) {
            mGuessTask.cancel(true);

            mGuessTask = null;
        }

        super.onDestroy();
    }

    private class GuessTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
//            showProgressBarIndeterminate();
        }

        @Override
        protected String doInBackground(String... params) {
            Location loc = mLocation;

            Double lat = loc.getLatitude(); //"40.778982";//
            Double longt = loc.getLongitude(); //"-74.128876";//

            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("c_latitude", lat.toString()));
            nameValuePairs.add(new BasicNameValuePair("c_longitude", longt.toString()));

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(
                    "http://solazo.org/scripts/guess.php");

            // URL Encoding the POST Parameters
            try {
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            } catch (Exception e) {
//                Log.i(LOGTAG,
//                        "Error in URL Encoding the POST Parameters: "
//                                + e.toString());
            }

            // Making Http Request

            try {
                HttpResponse response = httpclient.execute(httppost);
                // Writing response to log
//                Log.i(LOGTAG, "Http Request Response: " + response.toString());

                HttpEntity entity = response.getEntity();
                is = entity.getContent();
            } catch (ClientProtocolException e) {
//                Log.i(LOGTAG, "Error in Http Request " + e.toString());
            } catch (IOException e) {
//                Log.i(LOGTAG, "Error in Http Request " + e.toString());
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
//                Log.i(LOGTAG, "Error converting result " + e.toString());
            }

            return result;


        } // end do in background

        @Override
        protected void onPostExecute(String result) {

            // Turn off the progress bar
            mActivityIndicator.setVisibility(View.GONE);

            // paring data
            String[] temp = new String[5];
            String[] humid = new String[5];
            String[] noaa_station = new String[5];
            String[] noaa_distance = new String[5];
            String[] noaa_temp = new String[5];
            String[] noaa_humid = new String[5];


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

                String the_results = "Temperature: " + temp[0]
                        + " F \n Humidity: " + humid[0] + "% \n "
                        + "Closest Station: " + noaa_station[0] + "\n "
                        + "Miles from you: " + noaa_distance[0];

//                Toast.makeText( getActivity(), the_results, Toast.LENGTH_LONG).show();

                display_temperature.setText(temp[0] + "F");
                display_humidity.setText(humid[0] + "% Humidity");
                display_station.setText("Closest Station: " + noaa_station[0]);
                display_distance.setText("Miles from you: " + noaa_distance[0]);
//                display_current_location.setText();

            } catch (JSONException e1) {
                Toast.makeText(getActivity(), "Oops...something went wrong :(", Toast.LENGTH_LONG).show();
            } catch (ParseException e1) {
                e1.printStackTrace();

            }

        }
    }
}
