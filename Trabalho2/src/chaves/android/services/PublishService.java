package chaves.android.services;

import chaves.android.YambaApplication;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;

public class PublishService extends Service{
	
	public HandlerThread mh;
	public Handler h;
	public static YambaApplication a;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		a = (YambaApplication) getApplication();
		
		mh = new HandlerThread("PublishServiceThread");
		
		mh.start();
		
		h = new Handler(mh.getLooper()){
			@Override
	    	public void handleMessage(Message msg) {
	    	   postMessage(((String)msg.obj));
	    	   stopSelf(msg.arg1);
	    	}
		};
	}

	public void postMessage(Object msg) {
		((YambaApplication) getApplication() ).newStatus(a.getTwitter().updateStatus(msg.toString()));
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mh.stop();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		
		final String s = intent.getStringExtra("TwitterMessage");
		Message msg = Message.obtain();
		msg.obj = s;
		
		h.sendMessage( msg );
		return 1;
	}

}
