package chaves.android;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.Twitter.Status;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import chaves.android.activities.Timeline;
import chaves.android.activities.UserPreferences;
import chaves.android.activities.UserStatus;
import chaves.android.database.StatusDataSource;
import chaves.android.model.StatusDTO;
import chaves.android.services.TimelinePull;

/* Application não deve ter estado, só dados para reduzir o custo entre activities do mesmo processo */
public class YambaApplication extends Application implements OnSharedPreferenceChangeListener{
	
	private Twitter _tweet;
	private final String TAG = "YambaApplication";
	private final String DEFAULT_LIST_MAX_SIZE = "10";
	private final String DEFAULT_TWEET_MAX_SIZE = "140";
	private SharedPreferences prefs;
	private UserStatus _userStatusActivity;
	private boolean _userStatusButtonIsDisable;
	private boolean _timeLineServiceIsRuning;
	private boolean _autoRefresh;
	private boolean _wifi;
	/* Array utilizados para ler/escrever no mapa */
	private String[] _from;
	/* Array com a descrição dos tempos no ecrã da timeline */
	private String[] _timeAgo;
	private int _delay;
	private Timeline _timelineActivity;
	private TimelinePull.Updater _timelineServiceThread;
	private LinkedList<Map<String,String>> _timelineData;
	private LinkedList<String> _pendingStatus;
	private StatusDataSource _dataSource;
	
	/* Ultimo acesso à base de dados */
	private Date _lastAccess;
	@Override
	public void onCreate() {
		super.onCreate();
		/* Inicialização dos Arrays de Strings presentes na classe Utils, e que também serão usados na TimelineActivity */
		Utils.init(_from = new String[]{ getString(R.string.imgKey), getString(R.string.titleKey), 
				getString(R.string.descrKey), getString(R.string.publishTimeKey) , getString(R.string.idKey)},
				_timeAgo = new String[]{getString(R.string.hours), getString(R.string.minutes),getString(R.string.seconds)});
		_pendingStatus = new LinkedList<String>();
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.registerOnSharedPreferenceChangeListener(this);
				
		_wifi = Utils.haveInternet(this);
		
		IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
		
		registerReceiver(new ConnectivityReceiver(),filter);
		
		createAccount(prefs);
		_dataSource = new StatusDataSource(this);
		_dataSource.open();
		
		Log.i(TAG, "onCreate");
	}
	
	/** Guarda a instância actual de Timeline
	 * de modo a que quando haja uma modificação na lista _timeline
	 * a aplicação possa comunicar essa modificação
	 * */
	public void setTimeLineActivity(Timeline timeline){
//		if(timeline == null)
//			sendTwitterNotification();
		_timelineActivity = timeline;
		Log.i(TAG, "setTimeLineActivity");
	}
	
	public StatusDataSource getDataSource() {
		return _dataSource;
	}
	public LinkedList<StatusDTO> getAllStatus(){
		return _dataSource.getAllStatus();
	}

	public void setButtonEnable(boolean enable){
		_userStatusButtonIsDisable = enable;
		Log.i(TAG, "setButtonEnable");
	}
	
	/** Guarda a instância actual de UserStatus
	 * de modo a que se o utilizador rodar o ecrã enquanto o botão está disable
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
	 * Método utilizado para colocar em memória a lista de Tweets
	 * Se estiver a ser exibida a TimelineActivity esta é automaticamente actualizada
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
		
		if(_timelineActivity == null)
			sendTwitterNotification(R.string.notificationDescr);
		else
			refreshInUserThread();
	}
	
	private void refreshInUserThread() {
		final boolean refresh = _autoRefresh;
		Thread thread = (new Thread(){
			@Override
			public void run(){
				_timelineActivity.runOnUiThread(new Runnable() { // O truque está aqui
				    public void run() {
				    	_timelineActivity.refreshTimeline(_timelineData);
				    	if(!refresh)
				    		stopService(new Intent(_timelineActivity, TimelinePull.class));
				    }
				});	
			}
		});
		thread.start();
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
	public void onTerminate(){
		super.onTerminate();
		stopService(new Intent(this, TimelinePull.class));
		Log.i(TAG,"onTerminate");
	}
	
	private void createAccount(SharedPreferences prefs) {
		if((!prefs.contains(getString(R.string.userKey))) || 
				(!prefs.contains(getString(R.string.passKey))) ||
				(!prefs.contains(getString(R.string.passKey))) || (!prefs.contains(getString(R.string.urlKey))) )
			inflatePreferences();
		else
			openAccount(false);
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
		if(_tweet == null){
	//		openAccount(false);
			return null;
		}
		return _tweet;
	}
	
	public void onSharedPreferenceChanged(SharedPreferences s, String key) {
		if(key == getString(R.string.autoRKey)){
			_autoRefresh = s.getBoolean(key, false);
			return;
		}
		else if(key == getString(R.string.delayKey)){
			_delay = Integer.parseInt(s.getString(key, "1"));
			return;
		}
		if(key == getString(R.string.nTwitsKey)){
			int aux = 0;
			_timelineData.subList(0, ((aux = Integer.parseInt(s.getString(key, DEFAULT_LIST_MAX_SIZE)) ) == 0 ? Integer.parseInt(DEFAULT_LIST_MAX_SIZE) : aux ));
			refreshInUserThread();
			return;
		}
		else if(key == getString(R.string.nCharsKey))
			return;
		prefs = s;
		Log.i(TAG, "MyApplication.onSharedPreferenceChanged()");
		boolean b = false;
		if(key.equals(getString(R.string.urlKey)))
			b = true;
		openAccount(b);
	}
	
	static final String JTWITTER_OAUTH_KEY = "ZA8VpSCzYnGHtwWcmQoA";
	static final String JTWITTER_OAUTH_SECRET = "cXcqqiljyYYsyW2QMn3KPoiGivN4bydEt4ZquLmKA0A";
	
	public void openAccount(boolean urlChanged){
		String user = prefs.getString(getString(R.string.userKey), ""), 
				pass = prefs.getString(getString(R.string.passKey), "");
		if(pass.equals("") || user.equals(""))
			return;
		if(_wifi){
			if(_tweet == null || !urlChanged){
				_tweet = new Twitter(user, pass);
			}
			_tweet.setAPIRootUrl(prefs.getString(getString(R.string.urlKey), getString(R.string.defaultUrl)));
		}
		else
			Toast.makeText(this, "Please make sure you turn on WiFi so this app can comunicate with servers", Toast.LENGTH_LONG).show();
	}

	public int getListMaxSize() {
		return Integer.parseInt(prefs.getString(getString(R.string.nTwitsKey), DEFAULT_LIST_MAX_SIZE));
	}
	
	public int getTweetMaxSize() {
		return Integer.parseInt(prefs.getString(getString(R.string.nCharsKey), DEFAULT_TWEET_MAX_SIZE));
	}

	public long getDelay() {
		return _delay*1000*60;
	}

	public void refreshTimeline() {
		if(!isTimeLineServiceRunning())
			startService(new Intent(_timelineActivity, TimelinePull.class));
		else if(_timelineServiceThread != null)
			_timelineServiceThread.interrupt();
	}

	public void newStatus(Status status) {
		_timelineData.addFirst(Utils.getMap(_dataSource.createStatus(status)));
		refreshInUserThread();
	}

	public String[] getFrom() {
		return _from;
	}		
	
	public String[] getTimeAgo() {
		return _timeAgo;
	}
	
	public void setWiFiState(boolean state) {
		_wifi = state;
	}
	
	public LinkedList<String> getPendingStatus() {
		//		if(_pendingStatus == null)
//			return ContentProvider.getPendingStatus();
		return _pendingStatus;
	}
	
	public void setPendingStatus(LinkedList<String> statusList) {
		 _pendingStatus = statusList;
	}
	
	public void addToPendingStatus(String message) {		
		_pendingStatus.add(message);
	}
	
	public void removePendingStatus() {		
		_pendingStatus.clear();
	}

	public boolean internetState() {
		return _wifi;
	}
//	public static void notifyBar(Context context, String action) {
//		long when = System.currentTimeMillis();
//		Notification n  = new Notification(R.drawable.emo_im_happy, "PDM", when);
//		
//		n.flags |= Notification.FLAG_AUTO_CANCEL;
//		n.vibrate = new long[]{ 500, 500 };
//		
//		Intent it = new Intent(context, ReceiverActivity.class);
//		PendingIntent pi = PendingIntent.getActivity(context, 0, it, 0);
//		n.setLatestEventInfo(context, "Notificação de PDM", "action="+action, pi);
//		
//		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//		nm.notify(1, n);
//	}
	
	public Date getLastAccess(){
		return _lastAccess;
	}
	
	public void setCurrentTime(){
		_lastAccess = new Date(System.currentTimeMillis());
	}
	public void setCurrentTime(long createdAt) {
		_lastAccess=new Date(createdAt);	
	}
	
	public void sendTwitterNotification(int descr) {
		long when = System.currentTimeMillis();
		Notification n  = new Notification(R.drawable.twitter_logo, getString(R.string.app_name), when);
		
		n.flags |= Notification.FLAG_AUTO_CANCEL;
		n.vibrate = new long[]{ 500, 500 };
		
		Intent it = new Intent(this, Timeline.class);
		PendingIntent pi = PendingIntent.getActivity(this, 0, it, 0);
		n.setLatestEventInfo(this, getString(R.string.notification), getString(descr), pi);
		
		
		NotificationManager nm = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.notify(1, n);
	}

	
}
