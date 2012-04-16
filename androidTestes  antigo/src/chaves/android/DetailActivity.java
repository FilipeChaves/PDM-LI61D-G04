package chaves.android;

import android.os.Bundle;
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
		_authorImage = (ImageView)findViewById(R.id.imgDetail);
		_author = (TextView) findViewById(R.id.authorTextView);
		_date = (TextView) findViewById(R.id.dateTextView);
		_message = (TextView) findViewById(R.id.messageTextView);
	}

}
