package org.solazo;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.solazo.utils.DateUtils;
import org.solazo.utils.GatewayConnectionUtils;

import java.util.ArrayList;
import java.util.UUID;

public class SubmitActivity extends SherlockFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fm = getSupportFragmentManager();

        if (fm.findFragmentById(android.R.id.content) == null) {
            SubmitFragment submitResults = new SubmitFragment();
            fm.beginTransaction().add(android.R.id.content, submitResults).commit();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public static class SubmitFragment extends SherlockFragment {
        private Solazo mSolazo;

        private Location mLocation;

        private AsyncTask<String, Void, String> mSubmitTask;

        private EditText edit_temp;
        private EditText edit_humid;
        private Button submit_button;

        private TextView submit_current_location;

        String message_temp = null;
        String message_humid = null;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            // Inflate the layout for this fragment
            View view = inflater.inflate(R.layout.activity_submit, container, false);

            edit_temp = (EditText) view.findViewById(R.id.editTemperature);
            edit_humid = (EditText) view.findViewById(R.id.editHumidity);
            submit_button = (Button) view.findViewById(R.id.button_submit);
            submit_current_location = (TextView) view.findViewById(R.id.guess_current_location);

            return view;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            mSolazo = Solazo.getInstance();
            if (mSolazo == null) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }

            submit_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSolazo != null) {
                        mLocation = mSolazo.getLocation();
                        updateUIDisplay();
                    }
                }
            });

            if (mSolazo.getAddress() != null) {
                submit_current_location.setText(getString(R.string.guess_current_location, mSolazo.getAddress()));
            } else {
                submit_current_location.setVisibility(View.INVISIBLE);
            }
        }

        public void updateUIDisplay() {
            message_temp = edit_temp.getText().toString();
            message_humid = edit_humid.getText().toString();

            mSubmitTask = new SubmitTask().execute();
            edit_temp.setText(null);
            edit_humid.setText(null);
        }

        private class SubmitTask extends AsyncTask<String, Void, String> {
            private JSONArray jArray;

            @Override
            protected String doInBackground(String... params) {
                try {
                    if (isCancelled())
                        return null;

                    // Send Values
                    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("c_latitude", Double
                            .toString(mLocation.getLatitude())));
                    nameValuePairs.add(new BasicNameValuePair("c_longitude", Double
                            .toString(mLocation.getLongitude())));
                    nameValuePairs.add(new BasicNameValuePair("c_user_temp", message_temp));
                    nameValuePairs.add(new BasicNameValuePair("c_user_humid", message_humid));
                    nameValuePairs.add(new BasicNameValuePair("c_date_time", DateUtils.getDate()));
                    nameValuePairs.add(new BasicNameValuePair("c_user_id", getUserID()));

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(GatewayConnectionUtils.getSubmitURL());

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

                mSubmitTask = null;

                try {
                    jArray = new JSONArray(result);
                    JSONObject json_data;

                    for (int i = 0; i < jArray.length(); i++) {
                        json_data = jArray.getJSONObject(i);
                        String postMessage = json_data.getString("test");
                    }
                    Toast.makeText(getActivity(), R.string.submit_thank_you, Toast.LENGTH_LONG).show();

                } catch (Exception e) {
                    Toast.makeText(getActivity(), R.string.oops, Toast.LENGTH_SHORT).show();
                }
            }

            private String getUserID() {
                // Get Device UUID
                final TelephonyManager tm = (TelephonyManager) getActivity().getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
                final String tmDevice, tmSerial, androidId;
                tmDevice = "" + tm.getDeviceId();
                tmSerial = "" + tm.getSimSerialNumber();
                androidId = "" + android.provider.Settings.Secure.getString(getActivity().getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

                UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
                return deviceUuid.toString();
            }
        }
    }
}
