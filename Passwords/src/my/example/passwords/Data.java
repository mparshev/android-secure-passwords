package my.example.passwords;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;

public class Data extends ContentProvider {
	
	public final static String AUTHORITY = "my.example.passwords";
	
	public final static Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

	private static final String TABLE = "secrets";
		
	public final static Uri URI = Uri.withAppendedPath(CONTENT_URI, TABLE); 
	
	public final static String _ID = BaseColumns._ID;
	public final static String TITLE = "title";
	public final static String PATH = "path";
	public final static String LOGIN = "login";
	public final static String SECRET = "secret";
	
	private final static String CREATE_SQL = "CREATE TABLE " + TABLE + " ( " 
			+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ TITLE + " TEXT, "
			+ PATH + " TEXT, "
			+ LOGIN + " TEXT, "
			+ SECRET + " TEXT) ";
	
	private final static String DROP_SQL = "DROP TABLE IF EXISTS " + TABLE;
	
	private static final UriMatcher sUriMatcher;
	
	private static final int ALL_QUERY = 1;
	private static final int ROW_QUERY = 2;
	
	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(AUTHORITY, TABLE, ALL_QUERY);
		sUriMatcher.addURI(AUTHORITY, TABLE + "/#", ROW_QUERY);
	}
	
	private static final String DATABASE_NAME = "passwords";
	private static final int DATABASE_VERSION = 2;
	
	private static class DataHelper extends SQLiteOpenHelper {

		public DataHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_SQL);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL(DROP_SQL);
			onCreate(db);
		}
		
	}
	
	private DataHelper mDataHelper;

	@Override
	public boolean onCreate() {
		mDataHelper = new DataHelper(getContext());
		return true;
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		switch(sUriMatcher.match(uri)) {
		case ALL_QUERY:
			Cursor cursor = mDataHelper.getReadableDatabase().query(TABLE, 
					new String[] { _ID, TITLE }, null, null, null, null, TITLE);
			cursor.setNotificationUri(getContext().getContentResolver(), uri);
			return cursor;
		case ROW_QUERY:
			return mDataHelper.getReadableDatabase().query(TABLE, null, 
					_ID + " = " + uri.getLastPathSegment(), null, null, null, null);
		}
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		switch(sUriMatcher.match(uri)) {
		case ALL_QUERY:
			Uri result = Uri.withAppendedPath(uri, 
					""+mDataHelper.getWritableDatabase().insert(TABLE, null, values));
			getContext().getContentResolver().notifyChange(URI, null);
			return result;
		}
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		switch(sUriMatcher.match(uri)) {
		case ROW_QUERY:
			int result = mDataHelper.getWritableDatabase()
					.update(TABLE, values, _ID + " = " + uri.getLastPathSegment(), null);
			getContext().getContentResolver().notifyChange(URI, null);
			return result;
		}
		return 0;
	}

	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		switch(sUriMatcher.match(uri)) {
		case ROW_QUERY:
			int result = mDataHelper.getWritableDatabase()
					.delete(TABLE, _ID + " = " + uri.getLastPathSegment(), null);
			getContext().getContentResolver().notifyChange(URI, null);
			return result;
		}
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		switch(sUriMatcher.match(uri)) {
		case ALL_QUERY: 
			return "vnd.android.cursor.dir/vnd." + AUTHORITY + "." + TABLE;
		case ROW_QUERY: 
			return "vnd.android.cursor.item/vnd." + AUTHORITY + "." + TABLE;
		}
		return null;
	}

	public static String getString(Cursor cursor, String columnName) {
		int columnIndex = cursor.getColumnIndex(columnName);
		if(!cursor.isNull(columnIndex)) 
			return cursor.getString(columnIndex);
		return "";
		
	}
}
