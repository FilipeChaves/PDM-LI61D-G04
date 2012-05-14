package chaves.android;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

import winterwell.jtwitter.Status;
import winterwell.jtwitter.Twitter;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import chaves.android.activities.Timeline;
import chaves.android.activities.UserPreferences;
import chaves.android.activities.UserStatus;
import chaves.android.services.TimelinePull;

/* Application n�o deve ter estado, s� dados para reduzir o custo entre activities do mesmo processo */
public class YambaApplication extends Application implements OnSharedPreferenceChangeListener{
	
	private Twitter _tweet;
	private final String TAG = "YambaApplication";
	private final String DEFAULT_LIST_MAX_SIZE = "10";
	private final String DEFAULT_TWIT_MAX_SIZE = "140";
	private SharedPreferences prefs;
	private UserStatus _userStatusActivity;
	private boolean _userStatusButtonIsDisable;
	private boolean _timeLineServiceIsRuning;
	private boolean _autoRefresh;
	/* Array utilizados para ler/escrever no mapa */
	private String[] _from;
	/* Array com a descri��o dos tempos no ecr� da timeline */
	private String[] _timeAgo;
	private int _delay;
	private Timeline _timelineActivity;
	private TimelinePull.Updater _timelineServiceThread;
	private LinkedList<Map<String,String>> _timelineData;
	
	/** Guarda a inst�ncia actual de Timeline
	 * de modo a que quando haja uma modifica��o na lista _timeline
	 * a aplica��o possa comunicar essa modifica��o
	 * */
	public void setTimeLineActivity(Timeline timeline){
		_timelineActivity = timeline;
		Log.i(TAG, "setTimeLineActivity");
	}
	
	public void setButtonEnable(boolean enable){
		_userStatusButtonIsDisable = enable;
		Log.i(TAG, "setButtonEnable");
	}
	
	/** Guarda a inst�ncia actual de UserStatus
	 * de modo a que se o utilizador rodar o ecr� enquanto o bot�o est� disable
	 * possa coloca-lo como Enable
	 * */
	public void setUserStatusActivity(UserStatus userStatus){
		UserStatus old = _userStatusActivity;
		_userStatusActivity = userStatus;
		if(old == null && userStatus != null && _userStatusButtonIsDisable){
			userStatus.setButtonEnable();
			_userStatusButtonIsDisable = false;
		}
		Log.i(TAG, "setUserStatusActivity");
	}
	
	/**
	 * M�todo utilizado para colocar em mem�ria a lista de Tweets
	 * Se estiver a ser exibida a TimelineActivity esta � automaticamente actualizada
	 * */
	public void setTimeLinedata(final ArrayList<Map<String,String>> list){
		Log.i(TAG, "setTimeLinedata()");
		if(_timelineData == null)
			_timelineData = new LinkedList<Map<String,String>>();
		else
			_timelineData.clear();
		
		for(int i = 0; i < list.size() && i < getListMaxSize(); ++i){
			_timelineData.add(list.get(i));
		}
		
		if(_timelineActivity != null)
		{
			final boolean refresh = _autoRefresh;
			Thread thread = (new Thread(){
				@Override
				public void run(){
					_timelineActivity.runOnUiThread(new Runnable() { // O truque est� aqui
					    public void run() {
					    	_timelineActivity.refreshTimeline(list);
					    	if(!refresh)
					    		stopService(new Intent(_timelineActivity, TimelinePull.class));
					    }
					});	
				}
			});
			thread.start();
		}
	}
	
	public LinkedList<Map<String,String>> getTimeLinedata(){
		return _timelineData;
	}
	
	public void setServiceThread(TimelinePull.Updater t){
		_timelineServiceThread = t;
	}
	
	public boolean getAutoRefresh(){
		return _autoRefresh;
	}
	
	public boolean isTimeLineServiceRunning(){
		Log.i(TAG, "isServiceRunning");
		return _timeLineServiceIsRuning;
	}
	
	public void setTimeLineServiceRunning(boolean state){
		Log.i(TAG, "setServiceRunning");
		_timeLineServiceIsRuning = state;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		/* Inicializa��o dos Arrays de Strings presentes na classe Utils, e que tamb�m ser�o usados na TimelineActivity */
		Utils.init(_from = new String[]{ getString(R.string.imgKey), getString(R.string.titleKey), 
				getString(R.string.descrKey), getString(R.string.publishTimeKey) , getString(R.string.idKey)},
				_timeAgo = new String[]{getString(R.string.hours), getString(R.string.minutes)});
		/*Verifica��o do estado da Internet, se n�o existir conectividade � mostrada uma mensagem e o programa acaba*/
		if(!Utils.haveInternet( this )){
			Toast.makeText(this, R.string.internetDown, Toast.LENGTH_LONG).show();
			onTerminate();
		}
		
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.registerOnSharedPreferenceChangeListener(this);
		
		createAccount(prefs);
		Log.i(TAG, "onCreate");
	}
	
	@Override
	public void onTerminate(){
		super.onTerminate();
		stopService(new Intent(this, TimelinePull.class));
		Log.i(TAG,"onTerminate");
	}
	
	private void createAccount(SharedPreferences prefs) {
		if((!prefs.contains(getString(R.string.userKey))) || 
				(!prefs.contains(getString(R.string.passKey))))
			inflatePreferences();
		_autoRefresh = prefs.getBoolean(getString(R.string.autoRKey), true);
		_delay = Integer.parseInt(prefs.getString(getString(R.string.delayKey), "100"));
		if(_timeLineServiceIsRuning && _timelineServiceThread.isRunning()){
			_timelineServiceThread.interrupt();
		}
	}


	private void inflatePreferences() {
		Intent i = new Intent(this, UserPreferences.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
	}

	public Twitter getTwitter(){
		Log.i(TAG, "MyApplication.getTwitter()");
		if(_tweet == null)
			openAccount(false);
		return _tweet;
	}
	
	public void onSharedPreferenceChanged(SharedPreferences s, String key) {
		if(key == getString(R.string.autoRKey)){
			_autoRefresh = s.getBoolean(key, false);
			return;
		}
		if(key == getString(R.string.delayKey)){
			_delay = Integer.parseInt(s.getString(key, "100"));
			return;
		}
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
		if(_tweet == null || !urlChanged)
			_tweet = new Twitter(user,pass);
		_tweet.setAPIRootUrl(prefs.getString(getString(R.string.urlKey), getString(R.string.defaultUrl)));
	}

	public int getListMaxSize() {
		return Integer.parseInt(prefs.getString(getString(R.string.nTwitsKey), DEFAULT_LIST_MAX_SIZE));
	}
	
	public int getTweetMaxSize() {
		return Integer.parseInt(prefs.getString(getString(R.string.nCharsKey), DEFAULT_TWIT_MAX_SIZE));
	}

	public long getDelay() {
		return _delay*1000;
	}

	public void refreshTimeline() {
		if(!isTimeLineServiceRunning())
			startService(new Intent(_timelineActivity, TimelinePull.class));
		else if(_timelineServiceThread != null)
			_timelineServiceThread.interrupt();
	}

	public void newStatus(Status status) {
		_timelineData.addFirst(Utils.getMap(status));
	}

	public String[] getFrom() {
		return _from;
	}		
	
	public String[] getTimeAgo() {
		return _timeAgo;
	}
	
}
