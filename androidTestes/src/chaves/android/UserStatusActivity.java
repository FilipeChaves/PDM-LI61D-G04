package chaves.android;

import winterwell.jtwitter.Twitter;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class UserStatusActivity extends SMActivity implements TextWatcher  {

	private String BUTTON_TEXT = "buttonText";
	private String BUTTON_BOOL = "buttonEnable";

	public int _maxChars = 140, _actualNumberOfChars;
	public TextView _numCharsText;
	public Button _sendButton;
	public EditText _tweetTextArea;
	public InputFilter[] _filters = {new InputFilter.LengthFilter(_maxChars)};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		init();
		if(savedInstanceState != null){
			_sendButton.setEnabled(savedInstanceState.getBoolean(BUTTON_BOOL));
			_sendButton.setText(savedInstanceState.getString(BUTTON_TEXT));
		}
	}

	/**
	 * Metodo que inicia os componentes da Activity.
	 */
	public void init(){

		//Caixa de texto para inserir o tweet
		_tweetTextArea = (EditText)findViewById(R.id.textEdit); 
		_tweetTextArea.setFilters(_filters);	

		//TextView que conta o n�mero de caracteres
		_numCharsText = (TextView)findViewById(R.id.buttonNChars);
		_numCharsText.setBackgroundColor(Color.BLACK);
		_numCharsText.setTextColor(Color.GREEN);


		//Bot�o para enviar o tweet
		_sendButton = (Button) findViewById(R.id.button1);
		_sendButton.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v) {
				(new AsyncTask<String, Void, Twitter.Status>(){    //AsyncTask To Send Tweet to server

					@Override
					protected void onPreExecute() {
						_sendButton.setEnabled(false);
						_sendButton.setText(R.string.load);
					};

					@Override
					protected Twitter.Status doInBackground(String... params) {
						try{Thread.sleep(3000);}
						catch(InterruptedException e){}
						return ((MyApplication)getApplication()).getTwitter().updateStatus(params[0]);
					}

					@Override
					protected void onPostExecute(Twitter.Status result) {
						_sendButton.setEnabled(true);
						_sendButton.setText(R.string.done);
						_tweetTextArea.setText("");   //Clear the text
						startActivity(new Intent(UserStatusActivity.this,TimelineActivity.class));
					}
				}).execute(_tweetTextArea.getText().toString());
			}
		});

		_tweetTextArea.addTextChangedListener(this); //Adiciona o Lister que estamos a implementar

	}

	//Metodo chamado depois do texto do tweet ser alterado
	public void afterTextChanged(Editable s) {
		_actualNumberOfChars = _maxChars - s.length();
		String st = "" + _actualNumberOfChars;
		_numCharsText.setText(st.toCharArray(), 0, st.length());

		if(_actualNumberOfChars > 0)
			_numCharsText.setTextColor(Color.GREEN);
		else
			_numCharsText.setTextColor(Color.RED);
		_numCharsText.refreshDrawableState();
	}
	//N�o  implementado por causa do filtro aplicado ao tweetTextArea
	public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
	public void onTextChanged(CharSequence s, int start, int before, int count) { }

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(BUTTON_TEXT, _sendButton.getText().toString());
		outState.putBoolean(BUTTON_BOOL, _sendButton.isEnabled());
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	//##############################################################################################################
	//#                                      IMPLEMENTA��O DO MENU                                                 #
	//##############################################################################################################
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){	//criacao de menu
		super.onCreateOptionsMenu(menu);
		menu.findItem(R.id.menu_icon_status).setEnabled(false);
		return true;
	}
	

}