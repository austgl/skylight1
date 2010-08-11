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
    static final String WATCHLIST_TABLE = "watchlist"; // Don't put word table in the name
    static final String PORTFOLIO_TABLE = "portfolio";
    // Column Names
    public static final String KEY_ID = "_id";
    public static final String KEY_DATE = "date";
    public static final String KEY_SYMBOL = "symbol";
    public static final String KEY_QUANTITY = "quantity";
    public static final String KEY_AVG_PRICE = "avg_price";
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
    private Context mContext;

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     *
     * @param ctx the Context within which to work
     * 
     * This establishes marketDB as an instance of  SQLiteDatabase through
     * MarketDatabaseHelper.
     * dbHelper=new MarketDatabaseHelper(ctx, null, DATABASE_NAME, DATABASE_VERSION)
     */
    public MarketDatabase(Context ctx) {
        this.mContext = ctx;
        dbHelper=new MarketDatabaseHelper(ctx, null, DATABASE_NAME, DATABASE_VERSION);
        }


    /*
     * Need to provide a default constructor because we are a Provider in AndroidManifest.xml
     *  TODO: This won't work because we need context 
     */

    public MarketDatabase() {

    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        int count;
        count = marketDB.delete(WATCHLIST_TABLE, where, whereArgs);
        getContext().getContentResolver().notifyChange(uri, null);

        return count;
    }
    
    
    public int delete(Uri uri,
    					String where, 
    					String[] whereArgs, 
    					String tablename,
    					Context context) {
        int count =0;
        StringBuffer wherebuf;
        try{
        	if(whereArgs.length >0){       		
        		wherebuf = new StringBuffer();
        		wherebuf.append(where).append("=\'").append(whereArgs[0]).append("\'");
        		for(int i = 1; i < whereArgs.length; i++){        			
        			wherebuf.append(" or ")
        				.append(where)
        				.append("=\'")
        				.append(whereArgs[i])
        				.append("\'");
        		}    
        		count = marketDB.delete(tablename, wherebuf.toString() , null); 
        		context.getContentResolver().notifyChange(uri, null);
        	}
	     } catch(Exception e) {
         	e.printStackTrace();
         }
	     return count;
     }

    @Override
    public String getType(Uri uri) {
        return "vnd.android.cursor.dir/vnd.skylight1.market.provider.EquityPricingInformation";

    }

    /*
     *
     */

    @Override
    public Uri insert(Uri _uri, ContentValues values) {
        //	 Insert the new row, will return the row number if successful.

        Log.i(TAG, "insert ticker");
        if (marketDB == null) {
            Log.e(TAG, "marketDB is null.  Opening...");
            open();
        }
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


    /*
    *
    */

    public long insertWatchlistTicker(String ticker) {
        //	 Insert the new row, will return the row number if successful.
        long status;
        Log.i(TAG, "insert Ticker");
        ContentValues newWatchlistTicker = new ContentValues();
        newWatchlistTicker.put(KEY_SYMBOL, ticker);

        if (marketDB == null) {
            Log.e(TAG, "marketDB is null");
            status = 0;
        } else {
            status = marketDB.insert(WATCHLIST_TABLE, null, newWatchlistTicker);
        }
        return status;
    }
    /*
    *
    */

    public long insertPortfolioItem(String ticker, int quantity, float avgPrice) {
        //	 Insert the new row, will return the row number if successful.
        long status;
        Log.i(TAG, "insertPortfolio");
        ContentValues newPortfolioItem = new ContentValues();
        newPortfolioItem.put(KEY_SYMBOL, ticker);
        newPortfolioItem.put(KEY_QUANTITY, quantity);
        newPortfolioItem.put(KEY_AVG_PRICE, avgPrice);

        if (marketDB == null) {
            Log.e(TAG, "marketDB is null");
            status = 0;
        } else {
            status = marketDB.insert(PORTFOLIO_TABLE, null, newPortfolioItem);
        }
        return status;
    }

    /*
     *
     */
     
    @Override
    public boolean onCreate() {
        mContext = getContext();   // TODO: where should we set the context?

        Log.i(TAG, "== Setting up MarketDatabase ==");

        open();
        return (marketDB != null);

    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        // A little error checking
        if (marketDB == null) {
            Log.e(TAG, "marketDB is null");
            return null;
        }

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

    public Cursor getAllWatchlistTickers() {
        return marketDB.query(WATCHLIST_TABLE, new String[]{KEY_ID, KEY_SYMBOL}, null, null, null, null, null);

    }

    public Cursor getAllPositions() {
        return marketDB.query(PORTFOLIO_TABLE, new String[]{KEY_ID, KEY_SYMBOL, KEY_QUANTITY, KEY_AVG_PRICE}, null, null, null, null, null);

    }

    private static class MarketDatabaseHelper extends SQLiteOpenHelper {
        private static final String WATCHLIST_CREATE =
                "create table " + WATCHLIST_TABLE + "(" + KEY_ID + " integer primary key autoincrement, "
                        + KEY_DATE + " LONG, "
                        + KEY_SYMBOL + " varchar(20) );";


        private static final String PORTFOLIO_CREATE =
                "create table " + PORTFOLIO_TABLE + "(" + KEY_ID + " integer primary key autoincrement, "
                        + KEY_DATE + " LONG, "
                        + KEY_SYMBOL + " varchar(20),"
                        + "quantity  LONG, "
                        + "avg_price  float "
                        + ");";

        public MarketDatabaseHelper(Context context,String str,String str1,Integer i){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        /*
         *
         */

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i(TAG, "Create database: " + WATCHLIST_CREATE);
            db.execSQL(WATCHLIST_CREATE);
            db.execSQL(PORTFOLIO_CREATE);
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
            // TODO: Handle other tables
        }

    }//end of marketDatabaseHelper extends SQLiteOpenHelper


    /*
    *
    */

    public void open() {
        if (mContext != null) {
            dbHelper = new MarketDatabaseHelper(mContext, DATABASE_NAME, null, DATABASE_VERSION);

            marketDB = dbHelper.getWritableDatabase();
            if (dbHelper != null) {
                marketDB = dbHelper.getWritableDatabase();
            } else {
                Log.e(TAG, "dbHelper is null");
            }
        } else {
            Log.e(TAG, "Context is not defined.");
        }
    }

    //cleanup method is used by callers who can invoke it when they pause
    //in order to close connections.

    public void cleanup() {
        if (marketDB != null) {
            dbHelper.close();
            marketDB = null;
            Log.i(TAG, "Database Cleanup");
        }
    }

    /*
     * Dummy function that just adds to the set
     */

    public void addWatchListTicker(String aTicker) {
        watchList.add(aTicker);
    }

    /*
     *
     */

    public Set<String> getWatchListTickers() {
        return Collections.unmodifiableSet(watchList);
    }
}//end of public class dbMarket   extends ContentProvider
