package chaves.android.activities;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import winterwell.jtwitter.Status;
import winterwell.jtwitter.Twitter;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import chaves.android.DetailsModel;
import chaves.android.R;
import chaves.android.Utils;
import chaves.android.YambaApplication;
import chaves.android.services.TimelinePull;

public class Timeline extends SMActivity implements OnItemClickListener{
	
	private final String TAG = "TimelineActivity";
	ArrayList<Map<String,String>> showedList = new ArrayList<Map<String, String>>();
	List<Status> timelineList;
	private int list_max_size;
	private int max_chars_per_tweet = 25;
	private String[] timeAgo;
	public String[] from;
	private ListView lv;
	private Twitter t;
	private final Timeline timelineActivity = this;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timeline);
//		AccountManager am = AccountManager.get(this);
//		Account[] a = am.getAccounts();
//		for(int i = 0; i < a.length; ++i){
//			Toast.makeText(this, "cenas : " + a[i].name, Toast.LENGTH_LONG).show();
//		}
		lv = (ListView) findViewById(android.R.id.list);
		lv.setOnItemClickListener(this);
		from = app.getFrom();
		timeAgo = app.getTimeAgo();
		((YambaApplication)getApplication()).setTimeLineActivity(this);
		Log.i(TAG,"onCreate");
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		app.setTimeLineActivity(null);
	}

	public void refreshTimeline(List<Map<String,String>> list) {
		if(list == null) return;
		lv.setAdapter(new myAdapter(timelineActivity, list,
				R.layout.timelinelist, from, new int[]{ R.id.img, R.id.title, R.id.description, R.id.publishingTime }));
		Log.i(TAG,"refreshTimeLine");
	}

	public class myAdapter extends SimpleAdapter {
		/*Mapa onde são guardados os Drawable para que seja mais rápido o seu carregamento*/
		Map<String, Drawable> drawableMap = new HashMap<String, Drawable>();

		public myAdapter(Context context, List<? extends Map<String, String>> data,
				int resource, String[] from, int[] to) {
			super(context, data, resource, from, to);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Holder h;
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(R.layout.timelinelist,
						null);
				h=new Holder();
				h.image = (ImageView) convertView.findViewById(R.id.img);
				h.author = (TextView) convertView.findViewById(R.id.title);
				h.message = (TextView) convertView.findViewById(R.id.description);
				h.date = (TextView) convertView.findViewById(R.id.publishingTime);
				convertView.setTag(h);
			}
			else h = (Holder)convertView.getTag();

			HashMap<String, String> data = (HashMap<String, String>) getItem(position);
			try {
				Drawable d;
				String url;
				if(drawableMap.containsKey(url = data.get(from[0])))
					d = drawableMap.get(url);
				else{
					d = Utils.fetch(url);
					drawableMap.put(url, d);
				}
				h.image.setImageDrawable(d);
			} catch (Exception e) {
				new RuntimeException(e);
			}

			h.author.setText(data.get(from[1]));
			String st = data.get(from[2]);
			h.message.setText((st.length() < max_chars_per_tweet )
					? st : (st.substring(0, max_chars_per_tweet) + "..." ));
			h.date.setText(data.get(from[3]));
			return convertView;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){	//criacao de menu
		super.onCreateOptionsMenu(menu);
		menu.findItem(R.id.menu_icon_timeline).setEnabled(false);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){ 
		super.onOptionsItemSelected(item);
		if(item.getItemId() == R.id.timelineRefresh){
			app.refreshTimeline();
		}
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		app.setTimeLineActivity(this);
		if(!app.isTimeLineServiceRunning())
			startService(new Intent(this, TimelinePull.class));
		refreshTimeline(app.getTimeLinedata());
		Log.i(TAG,"onResume");
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		app.setTimeLineActivity(null);
	}

	private static class Holder{
		ImageView image;
		TextView author, date, message;
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		Log.i("YAMBA", "TIMELINEACTIVITY"
				);
		Object map = lv.getItemAtPosition(position);
		DetailsModel parcel = new DetailsModel();
		parcel.putMap((HashMap<String, String>)map);
		Bundle b = new Bundle();
		b.putParcelable("chaves.android.DetailActivity", parcel);

		Intent i = new Intent(this, Detail.class);
		i.putExtras(b);

		startActivity(i);
	}

}