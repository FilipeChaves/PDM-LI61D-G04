package chaves.android.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.Twitter.Status;
import winterwell.jtwitter.TwitterException;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import chaves.android.Utils;
import chaves.android.YambaApplication;
import chaves.android.database.StatusDataSource;
import chaves.android.model.StatusDTO;

public class TimelinePull extends Service {
	
	private static final String TAG = "TimeLinePull";
	
	//Tempo de intervalo da obtenção dos tweets
	private int INITIAL_DELAY = 1 * 1000;
	private boolean _runFlag = false;
	private Updater _updater;
	private YambaApplication _application;
	private LinkedList<StatusDTO> listOfStatus;
	private StatusDataSource _dataSource;
	/**
	 * 	Não Implementado , só para boundedServices
	 */
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	/**
	 * Primeiro método chamado do serviço
	 */
	@Override
	public void onCreate(){
		super.onCreate();
		_application = (YambaApplication)getApplication();
		_updater = new Updater();
		_dataSource = _application.getDataSource();
		Log.i(TAG,"onCreate");
	}
	
	/**
	 * Depois de passar os vários estados do serviço este é o principal que vai correr o código pretendido.
	 */
	@Override
	public int onStartCommand(Intent intent,int flags, int startId)
	{
		super.onStartCommand(intent, flags, startId);
		
		_runFlag = true;
		_updater.start();
		_application.setTimeLineServiceRunning(true);
		
		Log.i(TAG,"onStartCommand");
		return START_STICKY;
	}
	/**
	 * Quando o serviço é destruido afecta as variaveis partilhadas na aplicação
	 */
	@Override
	public void onDestroy(){
		super.onDestroy();
		
		_runFlag = false;
		_updater.stop();
		_updater = null;
		_application.setTimeLineServiceRunning(false);
		_application.setServiceThread(null);
		Log.i(TAG,"onDestroy");
	}
	
	/**
	 * Implementação da thread que vai correr o serviço
	 * @author LeoBurn
	 *
	 */
	public class Updater extends Thread{
		//Lista actual no formato Twitter.Status
		private List<Status> _timeline;
		//Lista actual no formato Map<String,String> que contem o valor e o id para a descrição
		private ArrayList<Map<String,String>> _showedList;
		
		public Updater(){
			super("Service-TimeLinePull");
			_showedList = new ArrayList<Map<String, String>>();
			_application.setServiceThread(this);
			listOfStatus = new LinkedList<StatusDTO>();
		}
		
		public void run(){
			TimelinePull timeLineService = TimelinePull.this;
			List<Status> actualTimeLine = null;
			while(true)
			{
				Log.i(TAG, "TimeLine running");
				Twitter t = null;
				do{
					try {
						timeLineService._runFlag = false;
						Thread.sleep(INITIAL_DELAY);
					} catch (InterruptedException e) {}
					finally{ t = _application.getTwitter();
							 timeLineService._runFlag = true;}
				}while(t == null);
				try{
					actualTimeLine = t.getHomeTimeline();
				}
				catch(TwitterException e){
					Log.e(TAG,e.getMessage());
					Log.e(TAG,"Failed to connect to twitter");
				}
				if(actualTimeLine == null){
					_runFlag = false;
					return;
				}
				//So notifica se houver mudanças
				_timeline = actualTimeLine;
				for(Status status : actualTimeLine){
					if(!_dataSource.isStatusOnDataBase(status)){
						_dataSource.createStatus(status);
					}
					else break;
				}
				
				listOfStatus = _dataSource.getAllStatus();
				
				putDataInMapper();
				_application.setTimeLinedata(_showedList);
				
				Log.i(TAG, "TimeLine ran");
				if(!_application.getAutoRefresh()){
					_runFlag = false;
					return;
				}
				try{
					Thread.sleep(_application.getDelay());
				}
				catch(InterruptedException e){}
			}
		}
		
		public boolean isRunning(){
			return TimelinePull.this._runFlag;
		}
		
		/**
		 * Metodo que transforma List<Twitter.Status> no tipo ArrayList<Map<String,String>>
		 */
		private void putDataInMapper(){
			HashMap<String, String> map;
			int maxsize = _application.getListMaxSize();
			_showedList.clear();
			int i = 0;
			while(i < listOfStatus.size() && i < maxsize){
				map = Utils.getMap(listOfStatus.get(i));
				_showedList.add(map);
				++i;
			}
		}		
		
	}

}
