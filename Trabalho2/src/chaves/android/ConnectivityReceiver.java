package chaves.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ConnectivityReceiver extends BroadcastReceiver{

	private boolean _wifi;
	private boolean _firstTime = true;
	private YambaApplication app;
	
	@Override
	public void onReceive(Context ctx, Intent intent) {
		Log.i("adefgaeg", "afgaeg");
		if(_firstTime){
			app = (YambaApplication)ctx;
			app.setWiFiState(Utils.haveInternet(ctx));
		}
		app.setWiFiState(_wifi = ! _wifi);
	}

}
