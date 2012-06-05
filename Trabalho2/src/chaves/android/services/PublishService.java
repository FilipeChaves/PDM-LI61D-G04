package chaves.android.services;

import java.util.LinkedList;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;
import chaves.android.Utils;
import chaves.android.YambaApplication;

public class PublishService extends Service{
	
	public HandlerThread mh;
	public Handler h;
	public static YambaApplication app;
	private boolean _success = false;
	
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
		
		final String s = intent.getStringExtra("TwitterMessage");
		if(!Utils.haveInternet( this )){//mudar para ContentProvider
			app.addToPendingStatus(s);
		}
		else{
			Message msg;
			LinkedList<String> l = app.getPendingStatus();
			for(String message : l){
				msg = Message.obtain();
				msg.obj = message;
				h.sendMessage(msg);
			}
			msg = Message.obtain();
			app.removePendingStatus();
			msg.obj = s;
			h.sendMessage(msg);
		}
		return 1;
	}

}
