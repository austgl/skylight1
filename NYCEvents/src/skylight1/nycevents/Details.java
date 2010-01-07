package skylight1.nycevents;

import static skylight1.nycevents.Constants.CURRENT_SITE_ID_PREF;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import skylight1.util.LoggingExceptionHandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Show details of a site.
 *
 */
public class Details extends Activity {

	private static final String LOG_TAG = "TSL Details";
	private static final String BASE_MAP = "geo:0,0?q=";

    private WebView mDetailsWeb;

    private Long mSiteId;

    private EventData newEventData;

//    private DbAdapter mDb;

    //We assume the site count stays the same and sites are numbered from 1 to count.
    private long mSiteCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		LoggingExceptionHandler.setURL(Tabs.ANDROIDLOGS_URL);
		Thread.setDefaultUncaughtExceptionHandler(new LoggingExceptionHandler(this));

//        mDb = new DbAdapter(this);
//        mDb.open();
//        mSiteCount = mDb.count();

        setContentView(R.layout.details);

        mDetailsWeb = (WebView) findViewById(R.id.details_details);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        AlertDialog.Builder  builder = new AlertDialog.Builder(this);
        final View layout = inflater.inflate(R.layout.dialog, null);
        builder.setView(layout);

        final EditText[] userNamePasswd = new EditText[3];

        final AlertDialog googlelogindialog = builder
        		.setTitle(R.string.dialog_google_login)
        		.setView(layout)
        		.setPositiveButton(R.string.dialog_google_login_button,
        		new DialogInterface.OnClickListener()  {

					public void onClick(DialogInterface googledialog, int whichButton){

        				AlertDialog indialog =(AlertDialog)googledialog;
        				userNamePasswd[0]=(EditText) indialog.findViewById(R.id.UN);
        				userNamePasswd[1]=(EditText) indialog.findViewById(R.id.PWD);

        				if(userNamePasswd[0] == null ||
        						userNamePasswd[1] == null ||
        						userNamePasswd[0].getText().toString().trim().equals("")||
        						userNamePasswd[1].getText().toString().trim().equals("")
        						){
        					Log.i("login fail","login fail");

        					Toast.makeText(indialog.getContext(),
        				    		R.string.dialog_google_login_authentication_failure,
        				    		Toast.LENGTH_LONG).show();
        				}
        				else{
        					GoogleCredentials gCredentials = new GoogleCredentials(
							userNamePasswd[0].getText().toString().trim(),
							userNamePasswd[1].getText().toString().trim());

        					GoogleCalendarUtilities.ResultCode rc = GoogleCalendarUtilities.addCalendarEvent(gCredentials,newEventData);
        					Log.i("user login"+userNamePasswd[0].getText(),"user login"+userNamePasswd[0].getText());
        					int resultCode = 0;
        					switch(rc) {
        					case AuthenticationFailed:
        						resultCode = R.string.dialog_google_login_authentication_failure;
        						break;
        					case ProcessSucceeded:
        						resultCode = R.string.dialog_google_login_success;
        						break;
        					default:
        						resultCode = R.string.dialog_google_login_connection_failure;
        					}
        				    Toast.makeText(indialog.getContext(),
        				    		resultCode,
        				    		Toast.LENGTH_LONG).show();
        				}

        				googledialog.dismiss();

        			}
        		})
        		.create();


		final Button addToGoogleCalendar = (Button) findViewById(R.id.details_cbtn);
        addToGoogleCalendar.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View v) {
				googlelogindialog.show();
			}

        });

        final Button previous = (Button) findViewById(R.id.details_mbtn);
        if(newEventData != null) {
        	if((newEventData.getLocation() == null) && (newEventData.getLocation2() == null)) {
        		previous.setEnabled(false);
        	}
        }
        previous.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mSiteId--;
				if ( 0 == mSiteId ) {
					mSiteId = mSiteCount;
				}
				populateFields();

				String data;
				if(newEventData != null) {
					if(newEventData.getLocation2()==null) {
						data = BASE_MAP + newEventData.getLocation();
					} else {
						data = BASE_MAP + newEventData.getLocation2() + ",NY,NY";
					}
					Log.d("map request:",data);
					Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(data));
					startActivity(intent);
				}
			}
		});

        final Button send = (Button) findViewById(R.id.details_button_send);
        send.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(newEventData != null) {
					String title = newEventData.getTitle();
					String text = "\n" + title;

					Date startTime = newEventData.getStartTime();
					if (startTime != null) {
						text += "\n\nDate:         "
							+ new SimpleDateFormat("EEE, MMM d, yyyy").format(startTime);
						text += "\n\nStart time:   "
							+ DateFormat.getTimeInstance(DateFormat.SHORT).format(startTime);
					}
					Date endTime = newEventData.getEndTime();
					if (endTime != null) {
						text += "\nEnd time:    "
							+ DateFormat.getTimeInstance(DateFormat.SHORT).format(endTime);
					}
					String website = newEventData.getWebsite();
					if (website != null) {
						text += "\n\nURL: " + website;
					}
					String location = newEventData.getLocation();
					if (location != null) {
						text += "\n\nLocation:\n" + location;
						String location2 = newEventData.getLocation2();
						if (location2 != null && !location.equals(location2)) {
							text += "\n" + location2;
						}
					}

					Intent sendEmail = new Intent(Intent.ACTION_SEND);
					sendEmail.setType("text/plain");
					sendEmail.putExtra(Intent.EXTRA_SUBJECT, "Event: " + title);
					sendEmail.putExtra(Intent.EXTRA_TEXT, text);

					startActivity(Intent.createChooser(sendEmail, "Send event"));
				}
			}
		});
    }

    @Override
	protected void onResume() {
		super.onResume();

		if (Constants.debug) {
			Log.d(LOG_TAG, "onResume");
		}

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        mSiteId = settings.getLong(CURRENT_SITE_ID_PREF, 1);
		populateFields();
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (Constants.debug) {
			Log.d(LOG_TAG, "onPause");
		}

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    	Editor edit = settings.edit();
    	edit.putLong(CURRENT_SITE_ID_PREF, mSiteId);
    	edit.commit();
	}

	private void populateFields() {
		newEventData = Lister.getCurrentEvent();
		if(Lister.getCurrentEvent() != null) {
			mDetailsWeb.loadData(EventUtilities.generateHTML(Lister.getCurrentEvent()),"text/html","utf-8");
		}
       if ( null != mSiteId ) {
//            Cursor site = mDb.fetchSite(mSiteId);
//            mNameText.setText(getString(Keys.NAME, site));


            StringBuilder details = new StringBuilder();
//            String parkName = getString(Keys.PARK_NAME, site);
//            if ( null != parkName ) {
            	details
//            		.append(parkName)
            		.append('\n');
            }
//            details
//            	.append(getString(Keys.LOCATION, site)).append('\n')
//            	.append(getString(Keys.PHONE, site)).append('\n');


//            details
//            	.append(getText(R.string.details_accessible_field_label))
//            	.append(' ');
//          	.append(getString(Keys.ACCESSIBLE, site));
//            String accessibleNotes = getString(Keys.ACCESSIBLE_NOTES, site);
//            if ( null != accessibleNotes ) {
//            	details
//            		.append(" (")
//            		.append(accessibleNotes)
//            		.append(')');
//            }
//            details.append("\n\n");


//            String hhtWebsite = getString(Keys.HHT_WEBPAGE, site);
//            details
//            	.append(hhtWebsite)
//            	.append('\n');

//            String houseSite = getString(Keys.HOUSE_SITE, site);
//            if ( null != houseSite ) {
//                details
//                	.append("\n")
//               	.append(houseSite)
//                	.append('\n');
//           }

            //mDetailsText.setText(details);
            //Linkify.addLinks(mDetailsText, Linkify.WEB_URLS);

//            site.close();
        }
//    }

//	private String getString(String column, Cursor c) {
//		return c.getString(c.getColumnIndexOrThrow(column));
//	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean supRetVal = super.onCreateOptionsMenu(menu);
		final int groupId = 0;
		final int aboutId = 0;
		menu.add(groupId, aboutId, Menu.NONE, getString(R.string.about))
				.setIcon(android.R.drawable.ic_menu_info_details);
		return supRetVal;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			showDialog(Constants.DIALOG_ABOUT_ID);
			return true;
		default:
			return false;
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		LayoutInflater inflater = null;
		View layout = null;
		switch (id) {
		case Constants.DIALOG_ABOUT_ID:
			inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
			layout = inflater.inflate(R.layout.about_dialog,
					(ViewGroup) findViewById(R.id.about_root));
			dialog = new AlertDialog.Builder(this).setIcon(R.drawable.icon)
					.setTitle(R.string.about_title).setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									removeDialog(Constants.DIALOG_ABOUT_ID);
								}
							}).setView(layout).create();
			break;
		default:
			dialog = null;
		}
		return dialog;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
//		mDb.close();
	}

}

