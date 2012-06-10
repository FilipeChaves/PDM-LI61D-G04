package chaves.android;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import chaves.android.model.StatusDTO;

public class Utils {
	
	private static String[] _from;
	private static String[] _timeAgo;
	
	public String[] getFrom(){
		return _from;
	}
	
	public String[] getTimeAgo(){
		return _timeAgo;
	}
	
	public static void init(String[] from, String[] timeAgo){
		_from = from;
		_timeAgo = timeAgo;
	}
	
	public static Drawable fetch(String address) throws MalformedURLException,IOException {
		URL url = new URL(address);
		InputStream content = (InputStream) url.getContent();
		
		return Drawable.createFromStream(content, address);
	}

	public static boolean haveInternet(Context ctx) {

	    NetworkInfo info = ((ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE))
	    		//.getNetworkInfo(ConnectivityManager.TYPE_WIFI); //NAO FUNCIONA COM EMULADOR!
	    		.getActiveNetworkInfo();
	    if (info == null)
	    	return false;
	    if(info.isConnected())
	    	return true;
	    return false;
	}

	public static HashMap<String, String> getMap(StatusDTO status) {
		HashMap<String,String> map = new HashMap<String, String>();
		map.put(_from[0], status.getImage());
		map.put(_from[1], status.get_user());
		map.put(_from[2], status.getMessage());
		map.put(_from[3], getDate(new Date(status.get_date())));
		map.put(_from[4], "" + status.get_id());
		return map;
	}
	
	/**
	 * Transforma a data corrente no tipo de data do twitter
	 * ex: 3 hours ago
	 * @param createdAt
	 * @return
	 */
	private static String getDate(Date createdAt) {
		Date d = new Date();
		if(d.getHours() - createdAt.getHours() > 0)
			return (((d.getDay() - createdAt.getDay()) * 24) + d.getHours() - createdAt.getHours()) + " " + _timeAgo[0];
		if(d.getMinutes() - createdAt.getMinutes() > 0)
			return (d.getMinutes() - createdAt.getMinutes()) + " " + _timeAgo[1];
		return _timeAgo[2];
	}
}
