package chaves.android.activities;

import chaves.android.R;
import chaves.android.YambaApplication;
import chaves.android.R.id;
import chaves.android.R.menu;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class SMActivity extends Activity{
	private String TAG = "SharedMenu";
	protected YambaApplication app;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (YambaApplication)getApplication();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	   MenuInflater inflater = getMenuInflater();
	   inflater.inflate(R.menu.menu, menu);
	   Log.i("SMActivity", "SMActivity.onCreateOptionsMenu()");
	   return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){ 
	   super.onOptionsItemSelected(item);
	   if(item.getItemId() == R.id.menu_icon_prefs)
		   startActivity(new Intent(this, UserPreferences.class));
	   if(item.getItemId() == R.id.menu_icon_timeline)
		   startActivity(new Intent(this, Timeline.class));
	   if(item.getItemId() == R.id.menu_icon_status)
		   startActivity(new Intent(this, UserStatus.class));
	   if(item.getItemId() == R.id.menu_icon_userinfo)
		   startActivity(new Intent(this, UserInfo.class));
	   Log.i(TAG, "SharedMenu.onOptionsItemSelected()");
	   return true;
   }
}
