package org.solazo.swap;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	public void startGuessActivity(View view) {
		Intent intent = new Intent(this, GuessActivity.class);
		startActivity(intent);
	}

	public void startSubmitActivity(View view) {
		Intent intent = new Intent(this, SubmitActivity.class);
		startActivity(intent);
	}
	
	public void startAboutActivity(View view) {
		Intent intent = new Intent(this, AboutActivity.class);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
}

