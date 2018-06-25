package inc.can_a.journalapp.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import inc.can_a.journalapp.Model.Entry;

public class DatabaseHandler extends SQLiteOpenHelper {

	// All Static variables
	// Logcat tag
	private static final String LOG = "DatabaseHelper";

	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "Journal";

	// Entry table name
	private static final String TABLE= "Notes";

	// Entry Table Columns names
	private static final String KEY_ID = "id";
	private static final String KEY_TITLE = "title";
	private static final String KEY_NOTE = "note";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_TABLE = "CREATE TABLE " + TABLE+ "("
				+ KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_TITLE + " TEXT,"
                + KEY_NOTE + " TEXT"
                + ")";

		db.execSQL(CREATE_TABLE);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE);
		// Create tables again
		onCreate(db);
	}

	/**
	 * All CRUD(Create, Read, Update, Delete) Operations
	 */

	// Adding new Entry
	public void addEntry(Entry entry) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_TITLE, entry.getTitle()); //Jornal title
		values.put(KEY_NOTE, entry.getNote()); //Journal entry
				// Inserting Row
		db.insert(TABLE, null, values);
		db.close(); // Closing database connection
	}

	// Getting single entry
	Entry getEntry(int id) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE, new String[] { KEY_ID,
				KEY_TITLE, KEY_NOTE }, KEY_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();

		Entry entry = new Entry(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2));

		return entry;
	}
	
	// Getting All Entries
	public List<Entry> getAllJournalEntries() {
		List<Entry> entryList = new ArrayList<>();
		// Select All Query
		String selectQuery = "SELECT * FROM " + TABLE;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
            //Entry entry;
            do {
                Entry note = new Entry();
                note.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                note.setTitle(cursor.getString(cursor.getColumnIndex(KEY_TITLE)));
                note.setNote(cursor.getString(cursor.getColumnIndex(KEY_NOTE)));

				// Adding Entry to list
				entryList.add(note);
			} while (cursor.moveToNext());
		}

		return entryList;
	}

	// Updating single entry
	public int updateEntry(Entry entry) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_TITLE, entry.getTitle());
		values.put(KEY_NOTE, entry.getNote());

		// updating row
		return db.update(TABLE, values, KEY_ID + " = ?",
				new String[] { String.valueOf(entry.getId()) });
	}

	// Deleting single Entry
	public void deleteEntry(Entry entry) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE, KEY_ID + " = ?",
				new String[]{String.valueOf(entry.getId())});
		db.close();
	}
}
