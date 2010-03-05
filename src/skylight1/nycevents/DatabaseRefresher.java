package skylight1.nycevents;

import static skylight1.nycevents.Constants.LAST_UPDATED_PREF;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import skylight1.util.LoggingExceptionHandler;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

public class DatabaseRefresher extends Service {

	//note: workaround for rare case when background thread and foreground compete (short of using a lock)
	public static boolean updating;

	@Override
	public void onCreate() {
		super.onCreate();
		LoggingExceptionHandler.setURL(Tabs.ANDROIDLOGS_URL);
		Thread.setDefaultUncaughtExceptionHandler(new LoggingExceptionHandler(this));
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		updating = true;
		Log.i(DatabaseRefresher.class.getName(),String.format("service id=%d started", startId));

		new Thread(new Runnable() {
			@Override
			public void run() {
				new DataFetchRunnable(DatabaseRefresher.this, Constants.MUSIC).run();
				new DataFetchRunnable(DatabaseRefresher.this, Constants.ART).run();
				new DataFetchRunnable(DatabaseRefresher.this, Constants.PARKS).run();
			}
		}).start();
	}

	private static SQLiteDatabase getDatabase(Context aContext) {
		SQLiteDatabase database = new SQLiteOpenHelper(aContext, "events", null, 1) {
			@Override
			public void onCreate(SQLiteDatabase database) {
				Log.i(DatabaseRefresher.class.getName(), "new database; creating tables");
				database
						.execSQL("create table events (_id integer primary key, title text, category text, description text, location text, startTime long, endTime long, website text, telephone text, location2 text)");
			}

			@Override
			public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
				db.execSQL("DROP TABLE IF EXISTS events");
				onCreate(db);
			}
		}.getWritableDatabase();
		return database;
	}

	public static class DataFetchRunnable implements Runnable {
		ProgressDialog progressDialog = null;

		Context context;
		SharedPreferences settings;
		SQLiteDatabase database;

		final private String eventType;

		public DataFetchRunnable(Context aContext, String anEventType) {
			context = aContext;
			eventType = anEventType;
			settings = PreferenceManager.getDefaultSharedPreferences(context);
		}

		public DataFetchRunnable(ProgressDialog pd, Context aContext, String anEventType) {
			progressDialog = pd;
			context = aContext;
			eventType = anEventType;
			settings = PreferenceManager.getDefaultSharedPreferences(context);
		}

		@Override
		public void run() {
			LoggingExceptionHandler.setURL(Tabs.ANDROIDLOGS_URL);
			Thread.setDefaultUncaughtExceptionHandler(new LoggingExceptionHandler(context));

			if(updating && progressDialog==null || !updating && progressDialog!=null) {
				Log.i(DatabaseRefresher.class.getName(), "about to open database");

				try {
					database = getDatabase(context);

					final List<EventData> eventList;

					if (eventType.equals(Constants.ART)) {
						eventList = ArtMusicEventLoader.getEvents(Constants.ART);
					} else if (eventType.equals(Constants.PARKS)) {
						eventList = new ParkEventLoader().getEvents();
					} else if (eventType.equals(Constants.MUSIC)) {
						eventList = ArtMusicEventLoader.getEvents(Constants.MUSIC);
					} else {
						throw new RuntimeException("knew we should have used an enum!");
					}

					if(eventList.size()>0) {
						database.beginTransaction();

						// remove all of the existing events
						database.execSQL("delete from events WHERE events.category=\"" + eventType + "\"");

						final SQLiteStatement compiledStatement = database
								.compileStatement("insert into events (_id, title, category, description, location, startTime, endTime, website, telephone, location2) values (null, ?,?,?,?,?,?,?,?,?)");

						insertEvents(compiledStatement, eventList);

						database.setTransactionSuccessful();

						database.endTransaction();
					}

				} catch (IOException e) {
					// no network connection, no problem
				} finally {
					close(database);
				}
			}
			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.cancel();
			}
			if (eventType.equals(Constants.PARKS)) {
				updating = false;
				final Editor edit = settings.edit();
				edit.putLong(LAST_UPDATED_PREF, System.currentTimeMillis());
				edit.commit();
			}
		}

	}

	public static void close(SQLiteDatabase database) {
		if (database != null) {
			database.close();
			SQLiteDatabase.releaseMemory();
			database=null;
		}

		Log.i(DatabaseRefresher.class.getName(), "database closed");

	}

	public static long count(Context aContext, String anEventType) {
		int count = 0;
		SQLiteDatabase database;
		database = getDatabase(aContext);
		database.beginTransaction();

		Cursor cursor = database.rawQuery("SELECT COUNT(*) FROM events WHERE events.category=\"" + anEventType + "\"", null);
		if (null == cursor) {
			return 0;
		}
		cursor.moveToFirst();
		count = cursor.getInt(0);
		cursor.close();

		database.endTransaction();
		database.close();
		SQLiteDatabase.releaseMemory();
		database=null;
		return count;
	}

	public Cursor getEventDataListCursor(Context aContext, String category) {

		SQLiteDatabase database = getDatabase(aContext);

		Cursor cursor = database.rawQuery("SELECT * FROM events WHERE events.category=\"" + category + "\"", null);

		if (null == cursor) {
			return null;
		}

		// closing the database kills the cursor!!! i'm hoping the adapter is dealing with this for us   :o(
//		database.close();

		return cursor;
	}

	private static void insertEvents(final SQLiteStatement compiledStatement, final List<EventData> eventList) {
		for (EventData eventData : eventList) {
			setString(compiledStatement, 1, eventData.getTitle());
			setString(compiledStatement, 2, eventData.getCategory());
			setString(compiledStatement, 3, eventData.getDescription());
			setString(compiledStatement, 4, eventData.getLocation());
			setDate(compiledStatement, 5, eventData.getStartTime());
			setDate(compiledStatement, 6, eventData.getEndTime());
			setString(compiledStatement, 7, eventData.getWebsite());
			setString(compiledStatement, 8, eventData.getTelephone());
			setString(compiledStatement, 9, eventData.getLocation2());

			compiledStatement.execute();
		}
		Log.i(DatabaseRefresher.class.getName(), String.format("inserted %d rows", eventList.size()));
	}

	private static void setString(SQLiteStatement statement, int i, String s) {
		if (s == null) {
			statement.bindNull(i);
		} else {
			statement.bindString(i, s);
		}
	}

	private static void setDate(SQLiteStatement statement, int i, Date d) {
		if (d == null) {
			statement.bindNull(i);
		} else {
			statement.bindLong(i, d.getTime());
		}
	}
}
