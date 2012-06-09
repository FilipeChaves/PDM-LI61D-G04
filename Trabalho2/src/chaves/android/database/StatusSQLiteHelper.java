package chaves.android.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class StatusSQLiteHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 3;
	
	public static final String STATUS_TABLE = "status";
	public static final String COLUMN_STATUS_ID = "_id";
	public static final String COLUMN_STATUS_MESSAGE = "message";
	public static final String COLUMN_STATUS_CREATED_AT = "createdAt";
	
	public static final String COLUMN_USER_NAME = "name";

	public static final String USER_TABLE = "user";
	public static final String COLUMN_USER_IMAGE = "image";
	
	public static final String OFFLINE_TABLE = "offline";
	public static final String COLUMN_ID = "_id";	
	public static final String COLUMN_MESSAGE = "message";	
	
	private static final String DATABASE_NAME = "yambaApplication.db";

	//String que cria a tabela com o nome "user"
	private static final String DATABASE_USER_CREATE = "create table " + USER_TABLE + "( " +
			COLUMN_USER_NAME + " text primary key," +
			COLUMN_USER_IMAGE + " text not null" +
			");";
	
	//String que cria a tabela com o nome "status"
	private static final String DATABASE_STATUS_CREATE = "create table "+ STATUS_TABLE + "( " + 
			COLUMN_STATUS_ID + " integer primary key, " + 
			COLUMN_USER_NAME + " text not null," +
			COLUMN_STATUS_MESSAGE + " text not null," +
			COLUMN_STATUS_CREATED_AT + "text not null" +
			");";
	
	//String que cria a tabela com o nome "status"
	private static final String DATABASE_OFFLINE_CREATE = "create table "+ OFFLINE_TABLE + "( " + 
			COLUMN_ID + " integer primary key autoincrement, " + 
			COLUMN_MESSAGE + "text not null" +
			");";

	public StatusSQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		Log.i("SQLiteHelper","onCreate");
		database.execSQL(DATABASE_USER_CREATE);
		database.execSQL(DATABASE_STATUS_CREATE);
		database.execSQL(DATABASE_OFFLINE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i(StatusSQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + STATUS_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + OFFLINE_TABLE);
		onCreate(db);
	}
}
