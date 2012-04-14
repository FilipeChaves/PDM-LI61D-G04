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

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.Twitter.Status;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class TimelineActivity extends SMActivity{
	//o melhor é colocar aqui o arrayAdapter que assim é logo modificado.
	ArrayList<Map<String,String>> showedList = new ArrayList<Map<String, String>>();
	List<Status> timelineList;
	private int list_max_size;
	private String[] timeAgo;
	public String[] from;
	private ListView lv;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timeline);
        
        Twitter t = app.getTwitter();
        timelineList = t.getHomeTimeline();
        
        init();
	}

	private void init() {
		
		lv = (ListView) findViewById(android.R.id.list);
        from = new String[]{ getString(R.string.imgKey), getString(R.string.titleKey), 
    			getString(R.string.descrKey), getString(R.string.publishTimeKey) };
        timeAgo = new String[]{getString(R.string.hours), getString(R.string.minutes)};
        
        final TimelineActivity t = this;
		
		(new AsyncTask<String, Void, Void>(){    //AsyncTask To Send Tweet to server

			@Override
			protected Void doInBackground(String... params) {
				list_max_size = app.getListMaxSize();
				HashMap<String, String> map;
				int i = 0;
				while(i < timelineList.size() && i < list_max_size){
					map = new HashMap<String, String>();
					map.put(from[0], timelineList.get(i).user.profileImageUrl.toString());
					map.put(from[1], timelineList.get(i).user.name);
					map.put(from[2], timelineList.get(i).getText());
					map.put(from[3], getDate(timelineList.get(i).createdAt));
					showedList.add(map);
					++i;
				}
				return null;
			}
			@Override
			protected void onPostExecute(Void result) {
				lv.setAdapter(new myAdapter(t, showedList,
						R.layout.timelinelist, from, new int[]{ R.id.img, R.id.title, R.id.description, R.id.publishingTime }));
			}
		}).execute();
		
		
		 //this.setListAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, showedList));
        //Guardar o adapter para nao estarmos sempre a criar um novo.
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
        	
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.timelinelist,
                        null);
            }

            HashMap<String, String> data = (HashMap<String, String>) getItem(position);
            
            try {
            	((ImageView) convertView.findViewById(R.id.img))
            	.setImageDrawable(fetch(data.get(from[0])));
            } catch (Exception e) {
            	new RuntimeException(e);
            }
            
            ((TextView) convertView.findViewById(R.id.title))
                    .setText(data.get(from[1]));
            ((TextView) convertView.findViewById(R.id.description))
            .setText(data.get(from[2]));
            ((TextView) convertView.findViewById(R.id.publishingTime))
            .setText(data.get(from[3]));

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
}
