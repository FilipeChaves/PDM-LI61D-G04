package chaves.android;

import winterwell.jtwitter.Twitter;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class UserStatusActivity extends SMActivity implements TextWatcher  {
	
	private String BUTTON_TEXT = "buttonText";
	private String BUTTON_BOOL = "buttonEnable";
	private String TAG;
	
	public void init(){
		
		TAG = getResources().getString(R.string.hello);
		
		_edidText = (EditText)findViewById(R.id.textEdit);
        _edidText.setFilters(filters);
        
        _textButton = (Button)findViewById(R.id.buttonNChars);
        _textButton.setBackgroundColor(Color.BLACK);
        _textButton.setTextColor(Color.GREEN);
        _textButton.refreshDrawableState();
        
        _button = (Button) findViewById(R.id.button1);
        _button.setOnClickListener(new View.OnClickListener(){
        	public void onClick(View v) {
        		(new AsyncTask<String, Void, Twitter.Status>(){

        			@Override
        			protected void onPreExecute() {
        				_button.setEnabled(false);
        				_button.setText(R.string.load);
        			};
        			
					@Override
					protected Twitter.Status doInBackground(String... params) {
						try{Thread.sleep(5000);}
						catch(InterruptedException e){}
						return ((MyApplication)getApplication()).getTwitter().updateStatus(params[0]);
					}
        			
					@Override
					protected void onPostExecute(Twitter.Status result) {
						_button.setEnabled(true);
						_button.setText(R.string.done);
					}
        		}).execute(_edidText.getText().toString());
        	}
        });
        _edidText.addTextChangedListener(this);
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        init();
        if(savedInstanceState != null){
        	_button.setEnabled(savedInstanceState.getBoolean(BUTTON_BOOL));
        	_button.setText(savedInstanceState.getString(BUTTON_TEXT));
        }
        Log.i(TAG, "UserStatusActivity.onCreate()");
    }
    
    public int maxChars = 140, aux;
    public Button _textButton;
    public Button _button;
    public EditText _edidText;
    public InputFilter[] filters = {new InputFilter.LengthFilter(maxChars)};
    
	public void afterTextChanged(Editable s) {
		 aux = maxChars - s.length();
		 String st = "" + aux;
         _textButton.setText(st.toCharArray(), 0, st.length());

         if(aux > 0)
        	 _textButton.setTextColor(Color.GREEN);
         else
        	 _textButton.setTextColor(Color.RED);
         _textButton.refreshDrawableState();
     }
     public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
     public void onTextChanged(CharSequence s, int start, int before, int count) { }

   @Override
   protected void onSaveInstanceState(Bundle outState) {
	   super.onSaveInstanceState(outState);
	   outState.putString(BUTTON_TEXT, _button.getText().toString());
	   outState.putBoolean(BUTTON_BOOL, _button.isEnabled());
	   Log.i(TAG, "UserStatusActivity.onSaveInstanceState()");
   }
   
   @Override
   protected void onRestart() {
	   super.onRestart();
	   Log.i(TAG, "UserStatusActivity.onRestart()");
   }
   
   @Override
   protected void onResume() {
	   super.onResume();
	   Log.i(TAG, "UserStatusActivity.onResume()");
   }
   
   @Override
   public boolean onOptionsItemSelected(MenuItem item){ //criacao da vista depois de carregado no bota
	   if(item.getItemId() == R.id.menu_icon_prefs)
		   startActivity(new Intent(this, UserPreferencesActivity.class));
	   if(item.getItemId() == R.id.menu_icon_timeline)
		   startActivity(new Intent(this, TimelineActivity.class));
	   Log.i(TAG, "SharedMenu.onOptionsItemSelected()");
	   return true;
   }
}