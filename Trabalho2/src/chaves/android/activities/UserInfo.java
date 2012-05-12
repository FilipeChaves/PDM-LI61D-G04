package chaves.android.activities;

import java.io.IOException;
import java.net.MalformedURLException;

import chaves.android.R;
import chaves.android.Utils;
import chaves.android.R.id;
import chaves.android.R.layout;
import chaves.android.services.UserInfoPull;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

public class UserInfo extends SMActivity implements ServiceConnection {
	
	public ImageView _userImage;
	public TextView _userName;
	public TextView _msgNr;
	public TextView _subscriptionNr;
	public TextView _subscriberNr;
	private Messenger _activityMessenger = null;
	private Intent _intent = null;
	public static final String USERNAME = "userName";
	public static final String USERIMAGE = "userImage";
	public static final String MSGNR = "msgNr";
	public static final String SUBSCRPNR = "subscriptionNr";
	public static final String SUBSCRBNR = "subscriberNr";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i("UserInfo", "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.userinfo);
		initViews();
	}
	
	private void initViews() {
		_userImage = (ImageView) findViewById(R.id.imgDetail);
		_userName = (TextView) findViewById(R.id.user_id);		
		_msgNr = (TextView) findViewById(R.id.msg_number);
		_subscriptionNr = (TextView) findViewById(R.id.subscriptions_number);
		_subscriberNr = (TextView) findViewById(R.id.subscribers_number);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){	
		super.onCreateOptionsMenu(menu);
		menu.findItem(R.id.menu_icon_userinfo).setEnabled(false);
		return true;
	}
	
	@Override
	protected void onResume() {
		Log.i("UserInfo", "onResume");
		super.onResume();
		if ( _intent==null ) _intent = new Intent(this, UserInfoPull.class);
		bindService(_intent, this, BIND_AUTO_CREATE);
		updateUserInfo();
	}
	
	@Override
	protected void onStop() {
		Log.i("UserInfo", "onStop");
		super.onStop();
		if(_activityMessenger!=null) unbindService(this);
	}
	
	public void onServiceConnected(ComponentName name, IBinder service) {
		Log.i("UserInfo", "onServiceConnected");
		_activityMessenger = new Messenger(service);
		updateUserInfo();
	}
	
	public void onServiceDisconnected(ComponentName name) {
		Log.i("UserInfo", "onServiceDisconnected");
		_activityMessenger = null;
		
	}
	
	private void updateUserInfo(){
		Log.i("UserInfo", "updateUserInfo"); 
		send(UserInfoPull.UPDATE);
	}
	
	private void send(int op) {
		if (_activityMessenger==null) return;
		try {
			Message m = Message.obtain(null, op);
			m.replyTo = callback;
			_activityMessenger.send(m);
		} catch (RemoteException e) {
			Log.e("UserInfo", "Remote exception="+e);
		}
	}
	
	/**
	 * Messenger que recebe os replies
	 */
	private Messenger callback = new Messenger( new Handler() {
		public void handleMessage(Message msg) {
			Log.i("UserInfo", "handleMessage");
			Bundle b = msg.getData();
			String urlImage = b.getString(USERIMAGE);
			try {
				_userImage.setImageDrawable(Utils.fetch(urlImage));
			} 
			catch (MalformedURLException e) {e.printStackTrace();} 
			catch (IOException e) {e.printStackTrace();}
			String username = b.getString(USERNAME);
			int msgNr = b.getInt(MSGNR);
			int subscriptionNr = b.getInt(SUBSCRPNR);
			int subscriberNr = b.getInt(SUBSCRBNR);
			_userName.setText("User Name: "+username);
			_msgNr.setText("Number of Status Messages: "+msgNr);
			_subscriptionNr.setText("Number of Subscriptions: "+subscriptionNr);
			_subscriberNr.setText("Number of Subscribers: "+subscriberNr);
		}
	});
}