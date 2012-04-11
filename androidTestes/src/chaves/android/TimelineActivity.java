package chaves.android;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.Twitter.Status;
import android.app.ListActivity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class TimelineActivity extends ListActivity{
	//o melhor é colocar aqui o arrayAdapter que assim é logo modificado.
	ArrayList<Map<String,String>> showedList = new ArrayList<Map<String, String>>();
	List<Status> timelineList;
	private final int LIST_MAX_SIZE = 10;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timeline);
        Twitter t = ((MyApplication) getApplication()).getTwitter();

        timelineList = t.getHomeTimeline();
        init();
	}

	private void init() {
		HashMap<String, String> map;
		int i = 0;
		String[] from = {"name", "descr", "url"};
		while(i < timelineList.size() && i < LIST_MAX_SIZE){
			map = new HashMap<String, String>();
			map.put(from[0], timelineList.get(i).user.name);
			map.put(from[1], timelineList.get(i).getText());
			map.put(from[2], timelineList.get(i).user.profileImageUrl.toString());
			showedList.add(map);
			++i;
		}
		
		this.setListAdapter(new myAdapter(this, showedList,
				R.layout.timelinelist, from, new int[]{ R.id.img, R.id.title, R.id.description }));
        //this.setListAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, showedList));
        //Guardar o adapter para nao estarmos sempre a criar um novo.
	}
	
    private class myAdapter extends SimpleAdapter {

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

            ((TextView) convertView.findViewById(R.id.title))
                    .setText(data.get("name"));
            ((TextView) convertView.findViewById(R.id.description))
            .setText(data.get("descr"));
            try {
				((ImageView) convertView.findViewById(R.id.img))
				        .setImageDrawable(this.fetch(data.get("url")));
			} catch (Exception e) {
				new RuntimeException(e);
			}

            return convertView;
        }
        
        public Drawable fetch(String address) throws MalformedURLException,IOException {
    		URL url = new URL(address);
    		InputStream content = (InputStream) url.getContent();
    		
    		return Drawable.createFromStream(content, address);
    	}
    }
}
