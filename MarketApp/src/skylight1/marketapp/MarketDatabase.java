package skylight1.marketapp;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.*;

public class MarketDatabase extends ContentProvider {
    private final static String TAG = " dbMarket ";

    public static final Uri CONTENT_URI = Uri.parse("content://skylight1.marketapp/EquityPricingInformation");
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "market.db";
    private static final String WATCHLIST_TABLE = "watchlist"; // Don't put word table in the name
    // Column Names
    public static final String KEY_ID = "_id";
    public static final String KEY_DATE = "date";
    public static final String KEY_SYMBOL = "symbol";
//    public static final String KEY_B2 = "ask_real_time";
//    public static final String KEY_B3 = "bid_real_time";
    // Column indexes
    //    public static final int DATE_COLUMN = 1;
//    public static final int SYMBOL_COLUMN = 2;
//    public static final int REAL_TIME_COLUMN = 3;
//    public static final int BID_TIME_COLUMN = 4;
    private Set<String> watchList = new HashSet<String>();
    private SQLiteDatabase marketDB;
    //    private SQLiteOpenHelper dbOpenHelper;
    MarketDatabaseHelper dbHelper;

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        int count;
        count = marketDB.delete(WATCHLIST_TABLE, where, whereArgs);
        getContext().getContentResolver().notifyChange(uri, null);

        return count;
    }

    @Override
    public String getType(Uri uri) {
        return "vnd.android.cursor.dir/vnd.skylight1.market.provider.EquityPricingInformation";

    }

    @Override
    public Uri insert(Uri _uri, ContentValues values) {
        //	 Insert the new row, will return the row number if successful.

        Log.i(TAG, "insert");
        long rowID = marketDB.insert(WATCHLIST_TABLE, "ticker", values);
        // Return a URI to the newly inserted row on success.
        if (rowID > 0) {
            Uri uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(uri, null);
            return uri;
        } else {
            throw new SQLException("Failed to insert row into " + _uri);
        }
    }


    @Override
    public boolean onCreate() {
        Context context = getContext();

        Log.i(TAG, "== Setting up MarketDatabase ==");

        dbHelper = new MarketDatabaseHelper(context, DATABASE_NAME, null, DATABASE_VERSION);

        marketDB = dbHelper.getWritableDatabase();
        establishDb();
        return (marketDB != null);

    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(WATCHLIST_TABLE);
        //   this is a row query,limit the result set to the passed in a row.
//        qb.appendWhere(KEY_ID + "=" + uri.getPath());

        // If no sort order is specified sort by date / time
        String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = KEY_DATE;
        } else {
            orderBy = sortOrder;
        }
        // Apply the query to the underlying database.
        Cursor c = qb.query(marketDB,
                projection,
                selection, selectionArgs,
                null, null, orderBy);
        // Register the contexts ContentResolver to be notified if
        // the cursor result set changes.
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where,
                      String[] whereArgs) {
        int count;

        Log.i(TAG, "update: " + where);

        count = marketDB.update(WATCHLIST_TABLE, values, where, whereArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }


    private static class MarketDatabaseHelper extends SQLiteOpenHelper {
        private static final String DATABASE_CREATE =
                "create table " + WATCHLIST_TABLE + "(" + KEY_ID + " integer primary key autoincrement, "
                        + KEY_DATE + " LONG, "
                        + KEY_SYMBOL + " TEXT );";


        public MarketDatabaseHelper(Context context, String name,
                                    CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        /*
         *
         */
        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i(TAG, "Create database: " + DATABASE_CREATE);
            db.execSQL(DATABASE_CREATE);
        }

        /*
        *
        */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + WATCHLIST_TABLE);
            onCreate(db);
        }

    }//end of marketDatabaseHelper extends SQLiteOpenHelper


    private void establishDb() {
        if (dbHelper != null) {
            marketDB = dbHelper.getWritableDatabase();
        }
    }

    //cleanup method is used by callers who can invoke it when they pause
    //in order to close connections.

    public void cleanup() {
        if (marketDB != null) {
            dbHelper.close();
            marketDB = null;
        }
    }

    public void addWatchListTicker(String aTicker) {
        watchList.add(aTicker);
    }

    public Set<String> getWatchListTickers() {
        return Collections.unmodifiableSet(watchList);
    }
}//end of public class dbMarket   extends ContentProvider
