package chaves.android.activities;

import java.util.HashMap;
import java.util.List;

import chaves.android.R;
import chaves.android.R.id;
import chaves.android.R.layout;
import chaves.android.R.string;
import chaves.android.services.PublishService;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.os.Parcel;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class UserStatus extends SMActivity implements TextWatcher  {

	private String BUTTON_TEXT = "buttonText";
	private String BUTTON_BOOL = "buttonEnable";

	public int _maxChars, _NumberOfCharsLeft;
	public TextView _numCharsText;
	public Button _sendButton;
	public EditText _tweetTextArea;
	public InputFilter[] _filters;
	public UserStatus us;
	public Intent service;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		us = this;
		if( getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        	setContentView(R.layout.main);
        else setContentView(R.layout.statuslandscape);
		init();
		if(savedInstanceState != null){
			_sendButton.setEnabled(savedInstanceState.getBoolean(BUTTON_BOOL));
			_sendButton.setText(savedInstanceState.getString(BUTTON_TEXT));
		}
		
		app.setUserStatusActivity(this);
	}
	/**Se o utilizador rodar o ecrã enquanto o botão está disable
	 * a application aquando da criação da activity UserStatus
	 * Chama este método para colocar o botão enable.
	 *  */
	public void setButtonEnable() {
		_sendButton.setEnabled(true);
	}

	/**
	 * Metodo que inicia os componentes da Activity.
	 */
	public void init(){

		//Numero máximo de caracteres por tweet
		_NumberOfCharsLeft = (_maxChars = app.getTweetMaxSize());
		_filters = new InputFilter[] {new InputFilter.LengthFilter(_maxChars)};

		//Caixa de texto para inserir o tweet
		_tweetTextArea = (EditText)findViewById(R.id.textEdit); 
		_tweetTextArea.setFilters(_filters);

		//TextView que conta o número de caracteres
		_numCharsText = (TextView)findViewById(R.id.buttonNChars);
		_numCharsText.setText("" + _maxChars);
		_numCharsText.setBackgroundColor(Color.BLACK);
		_numCharsText.setTextColor(Color.GREEN);


		//Botão para enviar o tweet
		_sendButton = (Button) findViewById(R.id.button1);
		_sendButton.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v) {
				if(_NumberOfCharsLeft < 0){
					Toast.makeText(us, getString(R.string.errorMessage), Toast.LENGTH_LONG).show();
					return;
				}
				_sendButton.setEnabled(false);
				app.setButtonEnable(false);
				_sendButton.setText(R.string.load);
				
				putMessage(_tweetTextArea.getText().toString());
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					new RuntimeException(e);
				}
				_sendButton.setEnabled(true);
				app.setButtonEnable(true);
				_sendButton.setText(R.string.done);
				_tweetTextArea.setText("");   //Clear the text
				startActivity(new Intent(UserStatus.this,Timeline.class));
			}
			
			/** Coloca a mensagem num Intent e inicia o PublishService 
			 * */
			private void putMessage(String message) {
				
				HashMap<String,String> m = new HashMap<String,String>();
				m.put("Twitter", message);
				Intent i = new Intent(UserStatus.this, PublishService.class);
				i.putExtra("TwitterMessage", message);
				
				startService(i);
			}

		});

		_tweetTextArea.addTextChangedListener(this); //Adiciona o Lister que estamos a implementar
	}


	//Metodo chamado depois do texto do tweet ser alterado
	public void afterTextChanged(Editable s) {
		_NumberOfCharsLeft = _maxChars - s.length();
		String st = "" + _NumberOfCharsLeft;
		_numCharsText.setText(st.toCharArray(), 0, st.length());
		
		if(_NumberOfCharsLeft > 0)
			_numCharsText.setTextColor(Color.GREEN);
		else
			_numCharsText.setTextColor(Color.RED);
		_numCharsText.refreshDrawableState();
	}
	//Não  implementado por causa do filtro aplicado ao tweetTextArea
	public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
	public void onTextChanged(CharSequence s, int start, int before, int count) { }

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(BUTTON_TEXT, _sendButton.getText().toString());
		outState.putBoolean(BUTTON_BOOL, _sendButton.isEnabled());
	}

	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		app.setUserStatusActivity(null);
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(_maxChars == app.getTweetMaxSize()) return;
		int aux = _maxChars - _NumberOfCharsLeft;

		_maxChars = app.getTweetMaxSize();
		_NumberOfCharsLeft = _maxChars - aux;
		_filters = new InputFilter[] {new InputFilter.LengthFilter(_maxChars)};
		
		_numCharsText.setText("" + _NumberOfCharsLeft);
		if(_NumberOfCharsLeft <= 0)
			_numCharsText.setTextColor(Color.RED);
		else
			_numCharsText.setTextColor(Color.GREEN);
		_tweetTextArea.setFilters(_filters);
	}

	//##############################################################################################################
	//#                                      IMPLEMENTAÇÂO DO MENU                                                 #
	//##############################################################################################################

	@Override
	public boolean onCreateOptionsMenu(Menu menu){	//criacao de menu
		super.onCreateOptionsMenu(menu);
		menu.findItem(R.id.menu_icon_status).setEnabled(false);
		return true;
	}

}