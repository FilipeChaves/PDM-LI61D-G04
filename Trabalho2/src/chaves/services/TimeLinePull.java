package chaves.services;

import java.util.List;
import winterwell.jtwitter.Status;
import winterwell.jtwitter.Twitter;
//import chaves.android.IRefreshTimeLine;
import chaves.android.MyApplication;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class TimeLinePull extends Service {
	
	static TimeLinePull _instance = null;
	static final int DELAY = 3000;
	private boolean _runFlag = false;
	private static final String TAG = "TimeLinePull";
	private Updater _updater;
	private MyApplication _application;
//	private IRefreshTimeLine _callback;
//	
//	public  void setCallback(IRefreshTimeLine callback){
//		_callback = callback;
//	}
	
	public synchronized static TimeLinePull getInstance(){
		if(_instance != null)
			return _instance;
		_instance = new TimeLinePull();
		_instance.onCreate();
		Log.i(TAG,"getInstance");
		return _instance.getInstance(); 
	}
	

	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate(){
		super.onCreate();
		_instance = this;
		_application = (MyApplication)getApplication();
		_updater = new Updater();
		Log.i(TAG,"onCreate");
	}
	
	@Override
	public int onStartCommand(Intent intent,int flags, int startId)
	{
		super.onStartCommand(intent, flags, startId);
		
		_runFlag = true;
		_updater.start();
		_application.setServiceRunning(true);
		
		Log.i(TAG,"onStartCommand");
		return START_STICKY;
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		
		_runFlag = false;
		_updater.interrupt();
		_updater = null;
		_application.setServiceRunning(false);
		Log.i(TAG,"onDestroy");
	}
	
	public List<Twitter.Status> getTimeLine(){
		return _updater.getTimeLine();
	}
	
	
	public class Updater extends Thread{
		private List<Twitter.Status> _timeline;
		
		public List<Twitter.Status> getTimeLine(){
			if(_application == null)
				_application = (MyApplication)getApplication();
			try{
				_timeline = _application.getTwitter().getHomeTimeline();
			}
			catch(TwitterException e){
				Log.e(TAG,"Failed to connect to twitter");
			}
			return _timeline;
		}
		
		public Updater(){
			super("Service-TimeLinePull");
		}
		
		public void run(){
			TimeLinePull timeLineService = TimeLinePull.this;
			while(timeLineService._runFlag)
			{
				Log.i(TAG, "TimeLine running");
				try{
					try{
						_timeline = _application.getTwitter().getHomeTimeline();
					}
					catch(TwitterException e){
						Log.e(TAG,"Failed to connect to twitter");
					}
					 
			          for (Twitter.Status status : _timeline) { // 
			            Log.d(TAG, String.format("%s: %s", status.user.name, status.text)); // 
			          }
					Log.i(TAG, "TimeLine ran");
					Thread.sleep(DELAY);
				}
				catch(InterruptedException e){
					timeLineService._runFlag = false;
				}
			}
			
		}
		
	}

}
