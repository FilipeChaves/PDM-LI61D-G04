package chaves.android.activities;

import java.io.IOException;
import java.net.MalformedURLException;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import chaves.android.DetailsModel;
import chaves.android.R;
import chaves.android.Utils;

public class Detail extends SMActivity{
	
	public ImageView _authorImage;
	public TextView _author;
	public TextView _date;
	public TextView _message;
	public Button _button;
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
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		_author = (TextView) findViewById(R.id.authorTextView);
		_author.setText(p.get(getString(R.string.titleKey)));
		final String user = p.get(getString(R.string.titleKey));
		_author = (TextView) findViewById(R.id.authorID);
		_author.setText(p.get("id"));
		_date = (TextView) findViewById(R.id.dateTextView);
		_date.setText(p.get(getString(R.string.publishTimeKey)));
		final String message = p.get(getString(R.string.descrKey));
		_message = (TextView) findViewById(R.id.messageTextView);
		_message.setText(message);
		
		_button = (Button) findViewById(R.id.sendEmail);
		_button.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				final Intent emailIntent = new Intent( android.content.Intent.ACTION_SEND);

				emailIntent.setType("plain/text");

				emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, "aefgg");

				
				emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.tweetOwner) + " " + user + ": " + message);

				startActivity(Intent.createChooser(emailIntent, "Send mail..."));
			}
		});
	}

}