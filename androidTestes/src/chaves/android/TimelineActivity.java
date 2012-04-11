package chaves.android;

import java.util.ArrayList;
import java.util.List;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.Twitter.Status;
import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class TimelineActivity extends ListActivity{
	//o melhor é colocar aqui o arrayAdapter que assim é logo modificado.
	ArrayList<String> showedList;
	List<Status> timelineList;
	private final int LIST_MAX_SIZE = 10;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timeline);
        Twitter t = ((MyApplication) getApplication()).getTwitter();

        timelineList = t.getUserTimeline();
        init();
	}

	private void init() {
		showedList = new ArrayList<String>();
		int i = 0;
		while(i < timelineList.size()){
			showedList.add(timelineList.get(i).user.name);
			showedList.add(timelineList.get(i).getText());
			++i;
		}
        this.setListAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, showedList));
        //Guardar o adapter para nao estarmos sempre a criar um novo.
	}
	
	

}
