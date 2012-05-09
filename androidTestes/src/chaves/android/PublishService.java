package chaves.android;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.util.Log;

public class PublishService extends Service{
	
	public HandlerThread mh;
	public Handler h;
	public static MyApplication a;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		a = (MyApplication) getApplication();
		
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
	
//    class HandlerService extends Handler {
//    	public HandlerService() { super(mh.getLooper()); }
//        @Override
//    	public void handleMessage(Message msg) {
//    	   postMessage(((String)msg.obj));
//    	   stopSelf(msg.arg1);
//    	}	
//    }

	public void postMessage(Object msg) {
		a.getTwitter().updateStatus(msg.toString());
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
//		final String st = p.get("Twitter");
//		(new Thread(){
//			public void run(){
//				((MyApplication)getApplication()).getTwitter().updateStatus(st);
//			}
//		}).start();

}
