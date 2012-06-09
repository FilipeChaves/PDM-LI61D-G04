package chaves.android.services;

import winterwell.jtwitter.User;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import chaves.android.YambaApplication;
import chaves.android.activities.UserInfo;

public class UserInfoPull extends Service{
	
	public static final int UPDATE = 0;
	public static YambaApplication _app;
	
	Messenger _serviceMessenger = new Messenger(new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case UPDATE:
				sendResponde(msg, updateUserInfo());
				break;
			default: super.handleMessage(msg);
			}
		}

		private void sendResponde(Message msg, Bundle b) {
			try {
				Message mResp = Message.obtain();
				mResp.what=msg.what;
				mResp.setData(b);
				msg.replyTo.send(mResp);
			} catch (RemoteException e) { 
				Log.e("UserInfoPull","Error send resp. e="+e);
			}
			
		}
	});
	
	@Override
	public IBinder onBind(Intent intent) {
		log("onBind");
		return _serviceMessenger.getBinder();
	}
	
	protected Bundle updateUserInfo() {
		User user = _app.getTwitter().getHomeTimeline().get(0).getUser();
		
		Bundle b = new Bundle();
		b.putString(UserInfo.USERNAME, user.name);
		b.putString(UserInfo.USERIMAGE, user.profileImageUrl.toString());
		b.putInt(UserInfo.MSGNR, user.statusesCount);
		b.putInt(UserInfo.SUBSCRPNR, user.friendsCount);
		b.putInt(UserInfo.SUBSCRBNR, user.followersCount);
		return b;
	}

	public void onCreate() { 
		log("onCreate");	
		super.onCreate(); 
		_app = (YambaApplication) getApplication();
	}
	public int onStartCommand(Intent intent, int flags, int startId) {
		log("onStartCommand"); return super.onStartCommand(intent, flags, startId);	}
	public void onDestroy() { log("onDestroy"); super.onDestroy();	}
	public boolean onUnbind(Intent intent) { log("onUnbind"); return super.onUnbind(intent); }
	public void onRebind(Intent intent) { log("onRebind"); super.onRebind(intent); }
	private static void log(String txt) { Log.d("UserInfoPull", txt);}
}
