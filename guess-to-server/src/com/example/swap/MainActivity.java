package com.example.swap;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
 
@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity {
    // TabSpec Names
    private static final String GUESS_SPEC = "Now";
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
 
        TabHost tabHost = getTabHost();
 
        // Guess Tab
        TabSpec guessSpec = tabHost.newTabSpec(GUESS_SPEC);
        // Tab Icon
        guessSpec.setIndicator(GUESS_SPEC);
        Intent guessIntent = new Intent(this, GuessActivity.class);
        // Tab Content
        guessSpec.setContent(guessIntent);
 
        // Adding all TabSpec to TabHost
        tabHost.addTab(guessSpec); 
    }
}