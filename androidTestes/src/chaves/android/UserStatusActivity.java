package chaves.android;

import java.util.HashMap;
import java.util.List;

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

public class UserStatusActivity extends SMActivity implements TextWatcher  {

	private String BUTTON_TEXT = "buttonText";
	private String BUTTON_BOOL = "buttonEnable";

	public int _maxChars, _NumberOfCharsLeft;
	public TextView _numCharsText;
	public Button _sendButton;
	public EditText _tweetTextArea;
	public InputFilter[] _filters;
	public UserStatusActivity us;
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
				_sendButton.setText(R.string.load);

				putMessage(_tweetTextArea.getText().toString());

				_sendButton.setEnabled(true);
				_sendButton.setText(R.string.done);
				_tweetTextArea.setText("");   //Clear the text
				startActivity(new Intent(UserStatusActivity.this,TimelineActivity.class));
			}
			
			private void putMessage(String message) {
				
				HashMap<String,String> m = new HashMap<String,String>();
				m.put("Twitter", message);
				Intent i = new Intent(UserStatusActivity.this, PublishService.class);
				i.putExtra("TwitterMessage", message);
				
				startService(i);
			}
			
			
//			private void putMessage(String message) {
////				MyHandler h = new MyHandler("PublishHandlerThread"/*, i*/);
//				
//				HashMap<String,String> m = new HashMap<String,String>();
//				m.put("Twitter", message);
//				Intent i = new Intent(UserStatusActivity.this, PublishService.class);
//				
//				DetailsModel parcel = new DetailsModel();
////				parcel.putMap(m);
////				Bundle b = new Bundle();
////				b.putParcelable("chaves.android.UserStatusActivity", parcel);
//				Message msg = new Message();
//				//Parcel p = Parcel.obtain();
//				p.createStringArray();
//				p.writeStringArray(new String[]{message});
//				msg.obj = p;
//				//m.put("Twitter", message);
//				//parcel.putMap(m);
//				i.putExtra("chaves.android.DetailsModel", msg);
////				Intent i = new Intent(UserStatusActivity.this, PublishService.class);
//				startService(i);
//			}

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