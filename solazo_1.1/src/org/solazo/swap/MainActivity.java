package org.solazo.swap;

import corg.solazo.swap.utils.AppState;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (AppState.getInstance(this).isOnline(this)) {
            Intent intent = new Intent(MainActivity.this, LocationActivity.class);
            startActivity(intent);
            finish();
        } else {
            setContentView(R.layout.activity_no_internet);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}