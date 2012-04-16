package chaves.android;

import winterwell.jtwitter.Twitter;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.util.Log;

public class MyApplication extends Application implements OnSharedPreferenceChangeListener{
	/*Application não deve ter estado, só dados para reduzir o custo entre activities do mesmo processo*/
	private Twitter _twit;
	private String TAG;
	private final String DEFAULT_LIST_MAX_SIZE = "10";
	private final String DEFAULT_TWIT_MAX_SIZE = "140";
	private SharedPreferences prefs;

	@Override
	public void onCreate() {
		super.onCreate();
		TAG = getResources().getString(R.string.hello);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.registerOnSharedPreferenceChangeListener(this);
		createAccount(prefs);
		Log.i(TAG, "MyApplication.onCreate()");
	}
	
	private void createAccount(SharedPreferences prefs) {
	//	if(!prefs.contains(getString(R.string.userKey)) || 
	//			!prefs.contains(getString(R.string.passKey)) || !prefs.contains(getString(R.string.urlKey)))
			inflatePreferences();
	}

	private void inflatePreferences() {
		Intent i = new Intent(this, UserPreferencesActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
	}

	public Twitter getTwitter(){
		Log.i(TAG, "MyApplication.getTwitter()");
		if(_twit == null) openAccount(false);
		return _twit;
	}
	
	public void onSharedPreferenceChanged(SharedPreferences s, String key) {
		if(key != getString(R.string.nCharsKey) || key != getString(R.string.nTwitsKey)) 
			return;
		prefs = s;
		Log.i(TAG, "MyApplication.onSharedPreferenceChanged()");
		boolean b = false;
		if(key.equals(getString(R.string.urlKey)))
			b = true;
		openAccount(b);
	}
	
	public void openAccount(boolean urlChanged){
		if(_twit == null || !urlChanged)
			_twit = new Twitter(prefs.getString(getString(R.string.userKey), ""),
					prefs.getString(getString(R.string.passKey), ""));
		_twit.setAPIRootUrl(prefs.getString(getString(R.string.urlKey), getString(R.string.defaultUrl)));
	}

	public int getListMaxSize() {
		return Integer.parseInt(prefs.getString(getString(R.string.nTwitsKey), DEFAULT_LIST_MAX_SIZE));
	}
	
	public int getTweetMaxSize() {
		return Integer.parseInt(prefs.getString(getString(R.string.nCharsKey), DEFAULT_TWIT_MAX_SIZE));
	}
}
