package com.example.swap;

import android.os.Bundle;
import android.os.IBinder;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.view.Menu;

public class MainActivity extends Activity {

	public static Context appContext;
	LocationService locationService;
	Intent serviceIntent;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_main);

		// ActionBar gets initiated
		ActionBar actionBar = getActionBar();
		// Tell the ActionBar we want to use Tabs.
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		String label1 = getResources().getString(R.string.label1);
		Tab tab = actionBar.newTab();
		tab.setText(label1);
		TabListener<GuessFragment> tl = new TabListener<GuessFragment>(this, label1, GuessFragment.class);
		tab.setTabListener(tl);
		actionBar.addTab(tab);

		String label2 = getResources().getString(R.string.label2);
		tab = actionBar.newTab();
		tab.setText(label2);
		TabListener<PostFragment> tl2 = new TabListener<PostFragment>(this, label2, PostFragment.class);
		tab.setTabListener(tl2);
		actionBar.addTab(tab);

		String label3 = getResources().getString(R.string.label3);
		tab = actionBar.newTab();
		tab.setText(label3);
		TabListener<AboutFragment> tl3 = new TabListener<AboutFragment>(this, label3, AboutFragment.class);
		tab.setTabListener(tl3);
		actionBar.addTab(tab);
		
		serviceIntent = new Intent(this, LocationService.class);
//		locationService.startService(serviceIntent);
	}

	private class TabListener<T extends Fragment> implements ActionBar.TabListener {
		private Fragment mFragment;
		private final Activity mActivity;
		private final String mTag;
		private final Class<T> mClass;

		/**
		 * Constructor used each time a new tab is created.
		 * 
		 * @param activity
		 *            The host Activity, used to instantiate the fragment
		 * @param tag
		 *            The identifier tag for the fragment
		 * @param clz
		 *            The fragment's Class, used to instantiate the fragment
		 */
		public TabListener(Activity activity, String tag, Class<T> clz) {
			mActivity = activity;
			mTag = tag;
			mClass = clz;
		}

		@SuppressLint("NewApi")
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			// Check if the fragment is already initialized
			if (mFragment == null) {
				// If not, instantiate and add it to the activity
				mFragment = Fragment.instantiate(mActivity, mClass.getName());
				ft.add(android.R.id.content, mFragment, mTag);
			} else {
				// If it exists, simply attach it in order to show it
				ft.attach(mFragment);
			}
		}

		@SuppressLint("NewApi")
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			if (mFragment != null) {
				// Detach the fragment, because another one is being attached
				ft.detach(mFragment);
			}
		}

		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			// User selected the already selected tab. Usually do nothing.
		}
	}
	
	private ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            locationService = ((LocationService.LocationBinder)service).getService();
//            updateStatus();
        }

        public void onServiceDisconnected(ComponentName className) {
            locationService = null;
        }
    };
    
	public void onResume() {
		super.onResume();
		// Starting the service 
		locationService.startService(serviceIntent);
		// Bind to the service
		locationService.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
	}
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
}
