package org.solazodev;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.util.EntityUtils;
import org.json.simple.parser.JSONParser;
import org.solazodev.utils.DateUtils;
import org.solazodev.utils.GatewayConnectionUtils;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import android.widget.Toast;
import android.location.Location;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.solazodev.utils.WeatherConditionsUtils;

public class GuessActivity extends SherlockFragmentActivity {

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

    public static class GuessFragment extends SherlockFragment {
        private Solazo mSolazo;

        private Location mLocation;

        private AsyncTask<String, Void, String> mGuessTask;

        // Handles to UI widgets
        private ProgressBar mActivityIndicator;

        private RelativeLayout display_guess_content;

        private TextView display_temperature, display_humidity, display_station, display_distance, display_current_location, display_last_update, display_text_outlook;
        private ImageView display_outlook;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.activity_guess, container, false);

            // Get handles to the UI view objects
            mActivityIndicator = (ProgressBar) view.findViewById(R.id.progressBar);

            display_guess_content = (RelativeLayout) view.findViewById(R.id.guess_content);
            display_temperature = (TextView) view.findViewById(R.id.guess_temperature);
            display_humidity = (TextView) view.findViewById(R.id.guess_humidity);
            display_station = (TextView) view.findViewById(R.id.guess_station);
            display_distance = (TextView) view.findViewById(R.id.guess_distance);

            display_outlook = (ImageView) view.findViewById(R.id.image_outlook);
            display_text_outlook = (TextView) view.findViewById(R.id.guess_outlook);

            display_current_location = (TextView) view.findViewById(R.id.guess_current_location);
            display_last_update = (TextView) view.findViewById(R.id.guess_last_update);

            return view;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            mSolazo = Solazo.getInstance();

            display_guess_content.setVisibility(View.INVISIBLE);
            mActivityIndicator.setVisibility(View.VISIBLE);

            if (mSolazo != null) {
                mLocation = mSolazo.getLocation();
                mGuessTask = new GuessTask().execute();
            } else {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
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
            if (mSolazo == null) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
            super.onResume();
        }

        private class GuessTask extends AsyncTask<String, Void, String> {
            JSONObject json_data;

            @Override
            protected String doInBackground(String... params) {
                try {
                    if (isCancelled())
                        return null;

                    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("c_latitude", Double.toString(mLocation.getLatitude())));
                    nameValuePairs.add(new BasicNameValuePair("c_longitude", Double.toString(mLocation.getLongitude())));

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(GatewayConnectionUtils.getGuessURL());

                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);

                    StatusLine status = response.getStatusLine();

                    if (status.getStatusCode() == HttpStatus.SC_OK) {
                        return new String(EntityUtils.toByteArray(response.getEntity()), "ISO-8859-1");
                    }
                } catch (Exception ignored) {
                }

                return null;
            }

            @Override
            protected void onPostExecute(String result) {

                mGuessTask = null;
                // Turn off the progress bar
                display_guess_content.setVisibility(View.VISIBLE);
                mActivityIndicator.setVisibility(View.GONE);

                display_current_location.setText(getString(R.string.guess_current_location, mSolazo.getAddress()));
                display_last_update.setText(getString(R.string.guess_last_update, DateUtils.getDate()));

                String[] temp = new String[5];
                String[] humid = new String[5];
                String[] noaa_station = new String[5];
                String[] noaa_distance = new String[5];
                String[] noaa_outlook = new String[5];

                try {
                    JSONArray jArray = new JSONArray(result);

                    for (int i = 0; i < jArray.length(); i++) {
                        json_data = jArray.getJSONObject(i);
                        temp[i] = json_data.getString("temp");
                        humid[i] = json_data.getString("humid");
                        noaa_station[i] = json_data.getString("noaa_station");
                        noaa_distance[i] = json_data.getString("noaa_distance");
                        noaa_outlook[i] = json_data.getString("outlook");
                    }

                } catch (Exception e) {
                    Toast.makeText(getActivity(), R.string.oops, Toast.LENGTH_LONG).show();
                }
                display_temperature.setText(getString(R.string.guess_placeholder_temperature, temp[0]));
                display_humidity.setText(getString(R.string.guess_placeholder_humidity, humid[0]));
                display_station.setText(getString(R.string.guess_label_closest_station, noaa_station[0]));
                display_distance.setText(getString(R.string.guess_label_miles_from_you, noaa_distance[0]));
                display_text_outlook.setText(noaa_outlook[0]);
                display_outlook.setImageResource(WeatherConditionsUtils.getDrawablefromWeatherCondition(noaa_outlook[0])); // TODO
            }
        }
    }
}
