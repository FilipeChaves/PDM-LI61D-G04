package chaves.android;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import winterwell.jtwitter.Status;
import winterwell.jtwitter.Twitter;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
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

public class TimelineActivity extends SMActivity implements OnItemClickListener{
	//o melhor é colocar aqui o arrayAdapter que assim é logo modificado.
	ArrayList<Map<String,String>> showedList = new ArrayList<Map<String, String>>();
	List<Status> timelineList;
	private int list_max_size;
	private int max_chars_per_tweet = 25;
	private String[] timeAgo;
	public String[] from;
	private ListView lv;
	private Twitter t;
	private final TimelineActivity timelineActivity = this;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timeline);
		//app.setTwitter();
		lv = (ListView) findViewById(android.R.id.list);
		lv.setOnItemClickListener(this);
		from = new String[]{ getString(R.string.imgKey), getString(R.string.titleKey), 
				getString(R.string.descrKey), getString(R.string.publishTimeKey) , "id"};
		timeAgo = new String[]{getString(R.string.hours), getString(R.string.minutes)};
	}

	protected void refreshTimeline() {
		timelineList = t.getHomeTimeline();
		if(timelineList.size() == 0) return;
		(new AsyncTask<String, Void, Void>(){    //AsyncTask para a conversão de lista para hashMap
			@Override
			protected Void doInBackground(String... params) {
				HashMap<String, String> map;
				list_max_size = app.getListMaxSize();
				showedList.clear();
				int i = 0;
				while(i < timelineList.size() && i < list_max_size){
					map = new HashMap<String, String>();
					winterwell.jtwitter.Status status = timelineList.get(i);
					map.put(from[0], status.user.profileImageUrl.toString());
					map.put(from[1], status.user.name);
					map.put(from[2], status.getText());
					map.put(from[3], getDate(status.createdAt));
					map.put(from[4], "" + status.user.id);
					showedList.add(map);
					++i;
				}
				return null;
			}
			@Override
			protected void onPostExecute(Void result) {
				lv.setAdapter(new myAdapter(timelineActivity, showedList,
						R.layout.timelinelist, from, new int[]{ R.id.img, R.id.title, R.id.description, R.id.publishingTime }));
			}
		}).execute();
	}

	private String getDate(Date createdAt) {
		Date d = new Date();

		if(d.getHours() - createdAt.getHours() != 0)
			return (d.getHours() - createdAt.getHours()) + " " + timeAgo[0];
		return (d.getMinutes() - createdAt.getMinutes()) + " " + timeAgo[1];
	}

	public class myAdapter extends SimpleAdapter {

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
				h.image.setImageDrawable(fetch(data.get(from[0])));
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

		public Drawable fetch(String address) throws MalformedURLException,IOException {
			URL url = new URL(address);
			InputStream content = (InputStream) url.getContent();

			return Drawable.createFromStream(content, address);
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
			timelineList = t.getHomeTimeline();
			refreshTimeline();
		}
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		t = app.getTwitter();
		if(t == null) return;
		refreshTimeline();
	}

	private static class Holder{
		ImageView image;
		TextView author, date, message;
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		Log.i("YAMBA", "TIMELINEACTIVITY");
		Object map = lv.getItemAtPosition(position);
		DetailsModel parcel = new DetailsModel();
		parcel.putMap((HashMap<String, String>)map);
		Bundle b = new Bundle();
		b.putParcelable("chaves.android.DetailActivity", parcel);

		Intent i = new Intent(this, DetailActivity.class);
		i.putExtras(b);

		startActivity(i);
	}
}
