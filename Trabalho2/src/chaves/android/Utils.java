package chaves.android;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.drawable.Drawable;

public class Utils {
	
	public static Drawable fetch(String address) throws MalformedURLException,IOException {
		URL url = new URL(address);
		InputStream content = (InputStream) url.getContent();
		
		return Drawable.createFromStream(content, address);
	}

}
