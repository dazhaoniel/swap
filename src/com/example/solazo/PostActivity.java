package com.example.solazo;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class PostActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_post, menu);
        return true;
    }
}
