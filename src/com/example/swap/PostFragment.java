package com.example.swap;

import android.app.Fragment;
import android.os.Bundle;
//import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
//import android.widget.Toast;

public class PostFragment extends Fragment {

	// private Forecast forecast;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return (LinearLayout) inflater.inflate(R.layout.activity_post,
				container, false);
	}

/*	protected class Post extends LocationActivity {

		public void sendForecast() {
			EditText editTemperature = (EditText) findViewById(R.id.edit_temperature);
			if( editTemperature != null ) {
				String temp_msg = editTemperature.getText().toString();
				( Toast.makeText( this, temp_msg, Toast.LENGTH_LONG) ).show();
			} else {
				( Toast.makeText( this, "Enter a Temperature Value...", Toast.LENGTH_LONG) ).show();
			}
		}
	}*/

}
