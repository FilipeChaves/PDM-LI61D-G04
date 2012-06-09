package chaves.android.database;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.SortedMap;

import winterwell.jtwitter.Status;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import chaves.android.model.StatusDTO;

public class StatusDataSource {

	// Database fields
	private SQLiteDatabase database;
	private StatusSQLiteHelper dbHelper;
	private String[] allColumns = { StatusSQLiteHelper.COLUMN_STATUS_ID, StatusSQLiteHelper.COLUMN_USER_NAME,
			StatusSQLiteHelper.COLUMN_STATUS_MESSAGE, StatusSQLiteHelper.COLUMN_STATUS_CREATED_AT};

	public StatusDataSource(Context context) {
		dbHelper = new StatusSQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public StatusDTO createStatus(Status status) {
		HashMap<String,String> oi = new LinkedHashMap<String,String>();
		ContentValues values = new ContentValues();
		values.put(StatusSQLiteHelper.COLUMN_STATUS_ID, status.getId().toString());
		values.put(StatusSQLiteHelper.COLUMN_USER_NAME, status.user.name);
		values.put(StatusSQLiteHelper.COLUMN_STATUS_MESSAGE, status.text);
		values.put(StatusSQLiteHelper.COLUMN_STATUS_CREATED_AT, status.createdAt.toString());
		//values.putAll(oi);
		database.insert(StatusSQLiteHelper.STATUS_TABLE, null, values);
		Cursor cursor = database.query(StatusSQLiteHelper.STATUS_TABLE,
				allColumns, StatusSQLiteHelper.COLUMN_STATUS_ID + " = " + status.getId(), null,
				null, null, null);
		cursor.moveToFirst();
		StatusDTO newComment = cursorToStatus(cursor);
		Log.i("Inserted","ID = " + status.getId());
		cursor.close();
		return newComment;
	}

	public void deleteStatus(StatusDTO status) {
		int id = status.get_id();
		database.delete(StatusSQLiteHelper.STATUS_TABLE, StatusSQLiteHelper.COLUMN_ID
				+ " = " + id, null);
	}

	public List<StatusDTO> getAllStatus() {
		List<StatusDTO> listOfStatus = new ArrayList<StatusDTO>();

		Cursor cursor = database.query(StatusSQLiteHelper.STATUS_TABLE,
				allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			StatusDTO status = cursorToStatus(cursor);
			listOfStatus.add(status);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return listOfStatus;
	}

	private StatusDTO cursorToStatus(Cursor cursor) {
		StatusDTO status = new StatusDTO();
		status.set_id(cursor.getInt(0));
		status.set_user(cursor.getString(1));
		status.setMessage(cursor.getString(2));
		status.set_date(cursor.getString(3));
		return status;
	}
}