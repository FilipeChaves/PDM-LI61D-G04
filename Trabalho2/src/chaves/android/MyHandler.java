package chaves.android;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

public class MyHandler extends HandlerThread{

	Handler hdlr = null;
	Looper looper;
	
	public MyHandler(String name) {
		super(name);
	}
	
	@Override
	public void run() {
		Looper.prepare();
		looper = Looper.myLooper();
//		hdlr = new Handler(){
//			
//			@Override
//			public void handleMessage(Message msg) {
//				super.handleMessage(msg);
//				PublishService.postMessage(msg);
//			}
//		};
		Looper.loop();
	}
	
	public Handler getHandler() {
		return hdlr;
	}
}
