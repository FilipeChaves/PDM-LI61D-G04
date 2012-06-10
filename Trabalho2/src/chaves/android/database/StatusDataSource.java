package chaves.android.database;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import winterwell.jtwitter.Twitter.Status;
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
			StatusSQLiteHelper.COLUMN_STATUS_MESSAGE, StatusSQLiteHelper.COLUMN_STATUS_CREATED_AT, StatusSQLiteHelper.COLUMN_USER_IMAGE};

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
		ContentValues values = new ContentValues();
		values.put(StatusSQLiteHelper.COLUMN_STATUS_ID, status.getId());
		values.put(StatusSQLiteHelper.COLUMN_USER_NAME, status.user.name);
		values.put(StatusSQLiteHelper.COLUMN_STATUS_MESSAGE, status.text);
		values.put(StatusSQLiteHelper.COLUMN_STATUS_CREATED_AT, status.createdAt.getTime());
		values.put(StatusSQLiteHelper.COLUMN_USER_IMAGE, status.user.profileImageUrl.toString());
		database.insert(StatusSQLiteHelper.STATUS_TABLE, null, values);
		Cursor cursor = database.query(StatusSQLiteHelper.STATUS_TABLE,
				allColumns, StatusSQLiteHelper.COLUMN_STATUS_ID + " = " + status.getId(), null,
				null, null, null);
		cursor.moveToFirst();
		StatusDTO newStatus = cursorToStatus(cursor);
		Log.i("Inserted","ID = " + status.getId());
		cursor.close();
		return newStatus;
	}

	public void deleteStatus(StatusDTO status) {
		int id = status.get_id();
		database.delete(StatusSQLiteHelper.STATUS_TABLE, StatusSQLiteHelper.COLUMN_ID
				+ " = " + id, null);
	}

	public LinkedList<StatusDTO> getAllStatus() {
		LinkedList<StatusDTO> listOfStatus = new LinkedList<StatusDTO>();

		Cursor cursor = database.query(StatusSQLiteHelper.STATUS_TABLE,
				allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			StatusDTO status = cursorToStatus(cursor);
			listOfStatus.addFirst(status);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return listOfStatus;
	}
	
	public boolean isStatusOnDataBase(Status status){
		String[] selectionArgs = {String.valueOf(status.id)};
		Cursor cursor = database.query(StatusSQLiteHelper.STATUS_TABLE, allColumns, "_id = ?", selectionArgs, null, null, null);
	   return cursor.moveToFirst();
	}

	private StatusDTO cursorToStatus(Cursor cursor) {
		StatusDTO status = new StatusDTO();
		status.set_id(cursor.getInt(0));
		status.set_user(cursor.getString(1));
		status.setMessage(cursor.getString(2));
		status.set_date(cursor.getLong(3));
		status.setImage(cursor.getString(4));
		return status;
	}
}