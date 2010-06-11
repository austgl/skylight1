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
import com.sun.jndi.dns.DnsName;

import java.util.*;

public class MarketDatabase extends ContentProvider {
    public static final Uri CONTENT_URI = Uri.parse("content:/skylight1.marketapp/EquityPricingInformation");
    private final static String TAG = " dbMarket ";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "market.db";
    private static final String MARKET_TABLE = "MarkeTable";
    // Column Names
    public static final String KEY_ID = "_id";
    public static final String KEY_DATE = "date";
    public static final String KEY_SYMBOL = "symbol";
    public static final String KEY_B2 = "ask real-time";
    public static final String KEY_B3 = "bid real-time";
    // Column indexes
    public static final int DATE_COLUMN = 1;
    public static final int SYMBOL_COLUMN = 2;
    public static final int REAL_TIME_COLUMN = 3;
    public static final int BID_TIME_COLUMN = 4;
    private Set<String> watchList = new HashSet<String>();

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        int count;
        try {
            count = marketDB.delete(MARKET_TABLE, where, whereArgs);
            String segment = uri.getPathSegments().get(1);
            count = marketDB.delete(MARKET_TABLE, KEY_ID + "="
                    + segment
                    + (!TextUtils.isEmpty(where) ? " AND ("
                    + where + ')' : ""), whereArgs);
        } catch (Exception e) {
            throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        return count;
    }

    @Override
    public String getType(Uri uri) {
        return "vnd.android.cursor.dir/vnd.skylight1.market.provider.MarketDB.dbMarket.";

    }

    @Override
    public Uri insert(Uri _uri, ContentValues values) {
        //	 Insert the new row, will return the row number if successful.

        long rowID = marketDB.insert(MARKET_TABLE, "ticker", values);
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
        marketDatabaseHelper dbHelper;

        dbHelper = new marketDatabaseHelper(context, DATABASE_NAME,
                null, DATABASE_VERSION);
        marketDB = dbHelper.getWritableDatabase();
        return (marketDB == null) ? false : true;

    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(MARKET_TABLE);
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
        try {
            String segment = uri.getPathSegments().get(1);
            count = marketDB.update(MARKET_TABLE, values, KEY_ID
                    + "=" + segment
                    + (!TextUtils.isEmpty(where) ? " AND ("
                    + where + ')' : ""), whereArgs);
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        return count;
    }

    private SQLiteDatabase marketDB;

    private static class marketDatabaseHelper extends SQLiteOpenHelper {
        private static final String DATABASE_CREATE =
                "create table " + MARKET_TABLE + "(" + KEY_ID + " integer primary key autoincrement, "
                        + KEY_DATE + " LONG, "
                        + KEY_SYMBOL + " TEXT );";


        public marketDatabaseHelper(Context context, String name,
                                    CursorFactory factory, int version) {
            super(context, name, factory, version);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO Auto-generated method stub
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + MARKET_TABLE);
            onCreate(db);
        }

    }//end of marketDatabaseHelper extends SQLiteOpenHelper

    public void addWatchListTicker(String aTicker) {
        watchList.add(aTicker);
    }

    public Set<String> getWatchListTickers() {
        return Collections.unmodifiableSet(watchList);
    }
}//end of public class dbMarket   extends ContentProvider
