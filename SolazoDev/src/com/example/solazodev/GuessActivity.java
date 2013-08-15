package com.example.solazodev;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.example.solazodev.utils.GatewayConnectionUtils;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import android.widget.Toast;
import android.location.Location;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.util.Log;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

public class GuessActivity extends SherlockFragmentActivity {

    private static final String LOGTAG = "com.example.solazodev.GuessActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fm = getSupportFragmentManager();

        if (fm.findFragmentById(android.R.id.content) == null) {
            GuessFragment guessResults = new GuessFragment();
            fm.beginTransaction().add(android.R.id.content, guessResults).commit();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public static class GuessFragment extends SherlockFragment {
        private Solazo mSolazo;

        private Location mLocation;

        private AsyncTask<String, Void, String> mGuessTask;

        // Handles to UI widgets
        private ProgressBar mActivityIndicator;

        private TextView display_temperature;
        private TextView display_humidity;
        private TextView display_station;
        private TextView display_distance;
        private TextView display_current_location;
        private TextView display_last_update;
        private ImageView display_outlook;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mSolazo = Solazo.getInstance();

            if (mSolazo != null) {
                mLocation = mSolazo.getLocation();
                mGuessTask = new GuessTask().execute();
            } else {
                try {
                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
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
            display_outlook = (ImageView) view.findViewById(R.id.image_outlook);
            display_last_update = (TextView) view.findViewById(R.id.guess_last_update);

            return view;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            mActivityIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        public void onDestroy() {
        if (mGuessTask != null && mGuessTask.getStatus() == AsyncTask.Status.RUNNING) {
            mGuessTask.cancel(true);

            mGuessTask = null;
        }
            super.onDestroy();
        }

        @Override
        public void onResume() {
            super.onResume();
        }

        private class GuessTask extends AsyncTask<String, Void, String> {

            private JSONArray jArray;
            private String result;
            private InputStream inputStream;
            private StringBuilder stringBuilder;
            private BufferedReader bufferedReader;

            @Override
            protected void onPreExecute() {}

            @Override
            protected String doInBackground(String... params) {
                Double lat = mLocation.getLatitude();
                Double longt = mLocation.getLongitude();

                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("c_latitude", lat.toString()));
                nameValuePairs.add(new BasicNameValuePair("c_longitude", longt.toString()));

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(GatewayConnectionUtils.getGuessURL());

                try {
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    HttpResponse response = httpclient.execute(httppost);

                    HttpEntity entity = response.getEntity();

                    inputStream = entity.getContent();

                    bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"), 8);

                    stringBuilder = new StringBuilder();

                    stringBuilder.append(bufferedReader.readLine() + "\n");

                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line + "\n");
                    }

                    inputStream.close();
                    bufferedReader.close();

                    result = stringBuilder.toString();

                } catch (Exception e) {
                    Log.i(LOGTAG, "Error in Http Request: " + e.toString());
                }

                return result;
            }

            @Override
            protected void onPostExecute(String result) {

                mGuessTask = null;

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

                    String result_address = mSolazo.getAddress();
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date();
//                    Toast.makeText(getActivity(), result_address, Toast.LENGTH_LONG).show();


                    display_temperature.setText(temp[0] + "F");
                    display_humidity.setText(humid[0] + "% Humidity");
                    display_station.setText("Closest Station: " + noaa_station[0]);
                    display_distance.setText("Miles from you: " + noaa_distance[0]);
                    display_current_location.setText(result_address);
                    display_last_update.setText("Last Update: " + dateFormat.format(date).toString());

                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Oops...something went wrong :(", Toast.LENGTH_LONG).show();
                    Log.i(LOGTAG, "Error in Post Execute: " + e.toString());
                }

            }
        }
    }
}
