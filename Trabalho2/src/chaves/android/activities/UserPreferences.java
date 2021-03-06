package chaves.android.activities;

import chaves.android.R;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;

public class UserPreferences extends PreferenceActivity {

	private String TAG;
	
	private void init(){
		TAG = getResources().getString(R.string.hello);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		init();
		addPreferencesFromResource(R.xml.prefs);
		Log.i(TAG, "UserPreferencesActivity.onCreate()");
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		Log.i(TAG, "UserPreferencesActivity.onStart()");
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, " UserPreferencesActivity.onResume()");
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Log.i(TAG, "UserPreferencesActivity.onPause()");
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		Log.i(TAG, "UserPreferencesActivity.onStop()");
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "UserPreferencesActivity.onDestroy()");
	}
	
	
	
}
