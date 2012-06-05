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
		Message msg = Message.obtain();
		msg.obj = s;

		if(!Utils.haveInternet( this ) || app.getCount() == 0){
			Toast.makeText(this, "adicionei ao pendingStatus", Toast.LENGTH_LONG).show();
			app.addToPendingStatus(s);
		}
		else{
			LinkedList<String> l = app.getPendingStatus();
			for(String message : l){
				Toast.makeText(this, "forStatus PublishService", Toast.LENGTH_LONG).show();
				msg.obj = message;
				h.sendMessage(msg);
			}
			app.removePendingStatus();
			h.sendMessage(msg);
		}
		return 1;
	}

}
