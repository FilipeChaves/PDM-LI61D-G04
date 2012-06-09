package chaves.android.services;

import java.util.LinkedList;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import chaves.android.R;
import chaves.android.YambaApplication;

public class PublishService extends Service{
	
	private HandlerThread mh;
	private Handler h;
	private static YambaApplication app;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		app = (YambaApplication) getApplication();
		
		mh = new HandlerThread("PublishServiceThread");
		
		mh.start();
		
		h = new Handler(mh.getLooper()){
			@Override
	    	public void handleMessage(Message msg) {
				postMessage((msg.obj));
	    	}
		};
	}

	public void postMessage(Object msg) {
		app.newStatus(app.getTwitter().updateStatus(msg.toString()));
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mh.stop();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		boolean sucess = true;
		final String s = intent.getStringExtra("TwitterMessage");
		if(!app.internetState()){//mudar para ContentProvider
			app.addToPendingStatus(s);
		}
		else{
			LinkedList<String> l = app.getPendingStatus();
			if(l.size() > 0){
				for(String message : l){
					if(app.internetState()){
						send(message);
					}
					else{
						app.sendTwitterNotification(R.string.offlineError);
						sucess = false;
						break;
					}
				}
				if(sucess)
					app.sendTwitterNotification(R.string.offlineSuccess);
			}
			send(s);
		}
		return 1;
	}

	private void send(String message) {
		Message msg = Message.obtain();
		msg.obj = message;
		h.sendMessage(msg);
	}

}
