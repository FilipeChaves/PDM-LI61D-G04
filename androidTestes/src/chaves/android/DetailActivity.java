package chaves.android;

import android.app.Activity;
import android.os.Bundle;

public class DetailActivity extends Activity{
	

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
		
	}

}
