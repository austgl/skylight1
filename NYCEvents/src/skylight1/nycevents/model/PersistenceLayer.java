package skylight1.nycevents.model;

import java.util.Date;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PersistenceLayer {

	public static final String TAG = "PersistanceLayer";

	public static final String KEY_NAME  = "name";
	public static final String KEY_VAL   = "value";
	public static final String KEY_ROWID = "id";

	private DatabaseHelper dbHelper ;
	private SQLiteDatabase db;
	private Context mCtx = null;

	private static final String DATABASE_NAME = "nycevents_db";

	private static final String DATABASE_TABLE = "EventData";

	private static final int DATABASE_VERSION = 1;

	private static final String DATABASE_CREATE =
	"create table "+DATABASE_TABLE+" ( "
	+ "id integer primary key autoincrement, "
	+ "title text not null,"
	+ "category text not null,"
	+ "location text not null,"
	+ "startTime date not null,"
	+ "endTime date,"
	+ "website text,"
	+ "telephone varchar(20),"
	+ "location2 text"
	+");";

	public PersistenceLayer(Context ctx) {
		mCtx = ctx;
	}
	public PersistenceLayer open () throws SQLException {
		dbHelper = new DatabaseHelper(mCtx);
		db = dbHelper.getWritableDatabase();
		return this;
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context ctx) {
			super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
		}
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE);
		}
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP IF TABLE EXISTS "+DATABASE_TABLE);
			onCreate(db);
		}
	}
	public void close() {
		dbHelper.close();
	}
}
