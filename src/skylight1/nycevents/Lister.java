package skylight1.nycevents;

import static skylight1.nycevents.Constants.CURRENT_SITE_ID_PREF;

import java.util.Date;
import java.util.List;

import skylight1.util.LoggingExceptionHandler;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * List sites and allow selecting them to see details.
 *
 */
public abstract class Lister extends ListActivity {
	private static List<EventData> eventList;

	private static EventData currentEvent;

	private String[] titles;
	
	public static EventData getCurrentEvent() {
		return currentEvent;
	}

	public static void setCurrentEvent(EventData currentEvent) {
		Lister.currentEvent = currentEvent;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LoggingExceptionHandler.setURL(Tabs.ANDROIDLOGS_URL);
		Thread.setDefaultUncaughtExceptionHandler(new LoggingExceptionHandler(this));

		setContentView(R.layout.lister);

		fillData();
	}

	abstract String getMyType();

	private void fillData() {
		final DatabaseRefresher dbRefresher = new DatabaseRefresher();
		final String tabList = getMyType();
		if (tabList.equals(Constants.PARKS) || tabList.equals(Constants.ART) || tabList.equals(Constants.MUSIC)) {
			if (DatabaseRefresher.count(this, tabList) == 0) {
				final ProgressDialog progressDialog = new ProgressDialog(Lister.this);
				progressDialog.setCancelable(true);
				progressDialog.setIndeterminate(true);
				progressDialog.setTitle(tabList + " events");
				progressDialog.setMessage("Initial Download Please Wait...");
				progressDialog.show();
				progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface arg0) {
						setContentToCursor(dbRefresher, tabList);
					}
				});

				// TODO if the screen times out before this is finished, when the user opens the window this activity gets launched again, and now two threads are d/ling!!
				new Thread(new DatabaseRefresher.DataFetchRunnable(progressDialog, this, tabList)).start();

				// tried to address issue above but this didn't work
//				getListView().postDelayed(new DatabaseRefresher.DataFetchRunnable(progressDialog, this),1000);

			} else {
				setContentToCursor(dbRefresher, tabList);
			}

		} else {
			return;
		}
	}

	private void setContentToCursor(final DatabaseRefresher dbRefresher, final String eventType) {
		Cursor cursor = dbRefresher.getEventDataListCursor(Lister.this, eventType);
		startManagingCursor(cursor);

		String[] names = new String[] { "title", "startTime", "location", };
		int[] ids = new int[] { R.id.lister_row_site_title, R.id.lister_row_site_date, R.id.lister_row_site_location, };

		EventViewAdapter eventViewAdapter = new EventViewAdapter(Lister.this, R.layout.lister_row, cursor, names, ids);
		setListAdapter(eventViewAdapter);

//		SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(Lister.this, R.layout.lister_row, cursor, names, ids);
//
//		setListAdapter(mAdapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		SQLiteCursor cursor = (SQLiteCursor) ((SimpleCursorAdapter)l.getAdapter()).getCursor();
		cursor.moveToPosition(position);

		String title = cursor.getString(cursor.getColumnIndex("title"));
		String category = cursor.getString(cursor.getColumnIndex("category"));
		String description =  cursor.getString(cursor.getColumnIndex("description"));
		String location =  cursor.getString(cursor.getColumnIndex("location"));
		Date startTime = new Date(cursor.getLong(cursor.getColumnIndex("startTime")));
		Date endTime =  new Date(cursor.getLong(cursor.getColumnIndex("endTime")));
		String website =  cursor.getString(cursor.getColumnIndex("website"));
		String telephone = cursor.getString(cursor.getColumnIndex("telephone"));

//		currentEvent = eventList.get(position);
		currentEvent = new EventData(title, category, description, location, startTime, endTime, website, telephone);

		if (Constants.debug)
			Log.d("Lister", currentEvent.getDescription());
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		Editor edit = settings.edit();
		edit.putLong(CURRENT_SITE_ID_PREF, id);
		edit.commit();
		Intent i = new Intent(this, Details.class);
		startActivity(i);
		sendBroadcast(i);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setContentView(R.layout.lister);
	}

	/**
	 * Checks for active network connections
     *   note: disabled for now as persistence should eliminate
     *   the need for an extra android permission for the most part
	 *
	 * @param 	context
	 * @return	true, if active network connections are available.
	 */
/*
	private static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(
				   Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
		    if (info != null) {
		    	for (int i = 0; i < info.length; i++) {
		    		if (info[i].getState() == NetworkInfo.State.CONNECTED) {
		    			return true;
		            }
		         }
		      }
		   }
		   return false;
		}
*/

	private EventData getModel(int position) {
		if (position > eventList.size() - 1) {
			return eventList.get(eventList.size() - 1);
		}
		return eventList.get(position);
		// return(((EventDataAdapter)getListAdapter()).getItem(position));
	}

	class EventDataAdapter extends ArrayAdapter<String> { // TODO: generalize for reuse...
		EventDataAdapter() {
			super(Lister.this, R.layout.lister_row, titles);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			ViewWrapper wrapper = null;

			if (row == null) {
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.lister_row, parent, false);
				wrapper = new ViewWrapper(row);
				row.setTag(wrapper);
			} else {
				wrapper = (ViewWrapper) row.getTag();
			}

			wrapper.getTitle().setText(getModel(position).getTitle());
			wrapper.getDate().setText(EventUtilities.displayDateRange(getModel(position)));
			wrapper.getLocation().setText(getModel(position).getLocation());

			return (row);
		}
	}

	class ViewWrapper { // TODO: like above, move to skylight util lib (wrapper prevents inflating all the time from
		// xml)
		View base;

		TextView title = null;

		TextView date = null;

		TextView location = null;

		ViewWrapper(View base) {
			this.base = base;
		}

		TextView getTitle() {
			if (title == null) {
				title = (TextView) base.findViewById(R.id.lister_row_site_title);
			}
			return (title);
		}

		TextView getDate() {
			if (date == null) {
				date = (TextView) base.findViewById(R.id.lister_row_site_date);
			}
			return (date);
		}

		TextView getLocation() {
			if (location == null) {
				location = (TextView) base.findViewById(R.id.lister_row_site_location);
			}
			return (location);
		}
	}

	/**
	 *  Adapter for binding data to list
	 */
	class EventViewAdapter extends SimpleCursorAdapter {

		public EventViewAdapter(Context context, int layout, Cursor c,
				String[] from, int[] to) {
			super(context, layout, c, from, to);
			setViewBinder(new EventListViewBinder());
		}

	}

	/**
	 *  Only picks out hardcoded column 5 (startDate) to be reformatted date text
	 *  If view returns false, SimpleCursorAdapter binds field string to TextView
	 *  which is what we want.
	 */
	class EventListViewBinder implements SimpleCursorAdapter.ViewBinder {

		@Override
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {

			int dateColIndex = 5;
			if(dateColIndex == columnIndex) {
				TextView showDateView = (TextView)view;
				Date startTime = new Date(cursor.getLong(dateColIndex));
				Date endTime = new Date(cursor.getLong(dateColIndex + 1));
				showDateView.setText(EventUtilities.displayDateRange(startTime, endTime));
				return true;
			}
			return false;
		}

	}
}
