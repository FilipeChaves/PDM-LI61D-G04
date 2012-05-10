package chaves.android;

import java.io.IOException;
import java.net.MalformedURLException;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailActivity extends SMActivity{
	


	public ImageView _authorImage;
	public TextView _author;
	public TextView _date;
	public TextView _message;
	/**
	 * message.getIntent();
	 * Cada elemento da lista (Status)
	 * Como enviar um objecto de Timeline para Detail? criar uma representação externa...  (marshall)
	 * ...para recriação na detail (unmarshall)
	 * Entao -> usar Parcelable (criar uma classe que estende esta) que esta no Bundle...
	 * ..., e por cada objectos fazer .parcelable or wat ever
	 * 
	 * CREATOR.createObjectFromParcelable();
	 */
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);
        init();
	}

	private void init() {
		Log.i("YAMBA", "DETAILSACTIVITY");
		Bundle b = getIntent().getExtras();
		DetailsModel p = b.getParcelable("chaves.android.DetailActivity");
		
	
		_authorImage = (ImageView)findViewById(R.id.imgDetail);
		try {
			_authorImage.setImageDrawable(Utils.fetch(p.get(getString(R.string.imgKey))));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		_author = (TextView) findViewById(R.id.authorTextView);
		_author.setText(p.get(getString(R.string.titleKey)));
		_author = (TextView) findViewById(R.id.authorID);
		_author.setText(p.get("id"));
		_date = (TextView) findViewById(R.id.dateTextView);
		_date.setText(p.get(getString(R.string.publishTimeKey)));
		_message = (TextView) findViewById(R.id.messageTextView);
		_message.setText(p.get(getString(R.string.descrKey)));
	}

}