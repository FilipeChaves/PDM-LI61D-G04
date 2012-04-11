package chaves.android;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import android.app.Activity;

public abstract class MyAsyncTask<Params, Progress, Result> {
	
	private Activity _activity;
	private Executor _worker;
//	private Status _status;
	
	@Deprecated
	public MyAsyncTask(Activity activity) {
		_activity = activity;
		_worker = Executors.newSingleThreadExecutor();
//		_status = MyAsyncTask.Status.PENDING;
	}
	
	public MyAsyncTask() {
		
	}
	
	
	protected abstract Progress onPreExecute();
	
	protected abstract Result doInBackground();
	
	public final void execute(final Params... params){
		
	}
	
	private final void publishResult(final Result result) {

	}
	
	protected abstract Progress onPostExecute();
	
	protected abstract Progress onCancelled();
	
	protected abstract Progress onProgressUpdate();
}
