package chaves.android;

import java.util.List;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.Status;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.util.Log;
import chaves.services.TimeLinePull;

public class MyApplication extends Application implements OnSharedPreferenceChangeListener{
	/*Application não deve ter estado, só dados para reduzir o custo entre activities do mesmo processo*/
	private Twitter _twit;
	private String TAG;
	private final String DEFAULT_LIST_MAX_SIZE = "10";
	private final String DEFAULT_TWIT_MAX_SIZE = "140";
	private SharedPreferences prefs;
	private boolean _serviceIsRuning;
	private List<Status> _timelineList;
	private TimelineActivity _timeline;
	
	public boolean isServiceRunning(){
		return _serviceIsRuning;
	}
	
	public void setServiceRunning(boolean state){
		_serviceIsRuning = state;
	}
	
	public void setTimeline(TimelineActivity t){
		_timeline = t;
	}

	public void setStatusList(List<Status> list){
		_timelineList = list;
		if(_timeline != null)
			_timeline.refreshTimeline(list);
	}
	
	public List<Status> getStatusList(){
		Log.i("App", "getAtatusList");
		return _timelineList;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		TAG = getResources().getString(R.string.hello);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.registerOnSharedPreferenceChangeListener(this);
		createAccount(prefs);
		Log.i(TAG, "MyApplication.onCreate()");
	}
	
	@Override
	public void onTerminate(){
		super.onTerminate();
		stopService(new Intent(this, TimeLinePull.class));
		Log.i(TAG,"onTerminate");
	}
	
	private void createAccount(SharedPreferences prefs) {
		if((!prefs.contains(getString(R.string.userKey))) || 
				(!prefs.contains(getString(R.string.passKey))))
			inflatePreferences();
	}
	private void createAccount() {
		createAccount(PreferenceManager.getDefaultSharedPreferences(this));
	}

	private void inflatePreferences() {
		Intent i = new Intent(this, UserPreferencesActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
	}

	public Twitter getTwitter(){
		Log.i(TAG, "MyApplication.getTwitter()");
		openAccount(false);
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
		String user = prefs.getString(getString(R.string.userKey), ""), 
				pass = prefs.getString(getString(R.string.passKey), "");
		if(pass.equals("") || user.equals(""))
			return;
		if(_twit == null || !urlChanged)
			_twit = new Twitter(user,pass);
		_twit.setAPIRootUrl(prefs.getString(getString(R.string.urlKey), getString(R.string.defaultUrl)));
	}

	public int getListMaxSize() {
		return Integer.parseInt(prefs.getString(getString(R.string.nTwitsKey), DEFAULT_LIST_MAX_SIZE));
	}
	
	public int getTweetMaxSize() {
		return Integer.parseInt(prefs.getString(getString(R.string.nCharsKey), DEFAULT_TWIT_MAX_SIZE));
	}

	public boolean setTwitter() {
		createAccount();
		return true;
	}

}
