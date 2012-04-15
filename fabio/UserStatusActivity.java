package chaves.android;

import winterwell.jtwitter.Twitter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class UserStatusActivity extends SMActivity implements TextWatcher  {
	
	private String BUTTON_TEXT = "buttonText";
	private String BUTTON_BOOL = "buttonEnable";
	private String TAG;
	public int maxChars = 140, aux;
    public TextView _numCharsText;
    public Button _button;
    public EditText _edidText;
    public InputFilter[] filters = {new InputFilter.LengthFilter(maxChars)};
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.main);
        int orientation = getResources().getConfiguration().orientation;
        if( orientation == Configuration.ORIENTATION_PORTRAIT){
        	setContentView(R.layout.main);
        }
        else setContentView(R.layout.statuslandscape);
        init();
        if(savedInstanceState != null){
        	_button.setEnabled(savedInstanceState.getBoolean(BUTTON_BOOL));
        	_button.setText(savedInstanceState.getString(BUTTON_TEXT));
        }
        Log.i(TAG, "UserStatusActivity.onCreate()");
    }
    public void init(){
		
		TAG = getResources().getString(R.string.hello);
		
		_edidText = (EditText)findViewById(R.id.textEdit);
        _edidText.setFilters(filters);
        
        _numCharsText = (TextView)findViewById(R.id.buttonNChars);
        _numCharsText.setBackgroundColor(Color.BLACK);
        _numCharsText.setTextColor(Color.GREEN);
        _numCharsText.refreshDrawableState();
        
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
    
    
	public void afterTextChanged(Editable s) {
		 aux = maxChars - s.length();
		 String st = "" + aux;
		 _numCharsText.setText(st.toCharArray(), 0, st.length());

         if(aux > 0)
        	 _numCharsText.setTextColor(Color.GREEN);
         else
        	 _numCharsText.setTextColor(Color.RED);
         _numCharsText.refreshDrawableState();
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
	public boolean onCreateOptionsMenu(Menu menu) {  
	   super.onCreateOptionsMenu(menu);
	   menu.findItem(R.id.menu_icon_status).setEnabled(false);
	   return true;
	}
}