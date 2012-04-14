package chaves.android;

import winterwell.jtwitter.Twitter;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

public class MyApplication extends Application implements OnSharedPreferenceChangeListener{
	/*Application não deve ter estado, só dados para reduzir o custo entre activities do mesmo processo*/
	private Twitter _twit;
	private String TAG;

	@Override
	public void onCreate() {
		super.onCreate();
		TAG = getResources().getString(R.string.hello);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.registerOnSharedPreferenceChangeListener(this);
		createAccount(prefs);
		Log.i(TAG, "MyApplication.onCreate()");
	}
	
	private void createAccount(SharedPreferences prefs) {
		if(!prefs.contains(getString(R.string.userKey)) || !prefs.contains(getString(R.string.passKey)) || !prefs.contains(getString(R.string.urlKey)))
			inflatePreferences();
		openAccount(prefs, false);
	}

	private void inflatePreferences() {
		startActivity(new Intent(this, UserPreferencesActivity.class));
	}

	public Twitter getTwitter(){
		Log.i(TAG, "MyApplication.getTwitter()");
		
		return _twit;
	}
	
	public void onSharedPreferenceChanged(SharedPreferences s, String key) {
		Log.i(TAG, "MyApplication.onSharedPreferenceChanged()");
		boolean b = false;
		if(key.equals(getString(R.string.urlKey)))
			b = true;
		openAccount(s, b);
	}
	
	public void openAccount(SharedPreferences s, boolean urlChanged){
		if(!urlChanged)
			_twit = new Twitter(s.getString(getString(R.string.userKey), ""),
				s.getString(getString(R.string.passKey), ""));
		_twit.setAPIRootUrl(s.getString(getString(R.string.urlKey), getString(R.string.defaultUrl)));
	}
}
