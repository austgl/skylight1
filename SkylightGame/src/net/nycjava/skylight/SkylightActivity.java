package net.nycjava.skylight;

import net.nycjava.skylight.dependencyinjection.DependencyInjectingObjectFactory;
import net.nycjava.skylight.dependencyinjection.DependencyInjector;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;

public abstract class SkylightActivity extends Activity {
	protected DependencyInjectingObjectFactory dependencyInjectingObjectFactory;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		dependencyInjectingObjectFactory = new DependencyInjectingObjectFactory();
		addDependencies(dependencyInjectingObjectFactory);

		// since activities are instantiated by the framework, use the dependency injector directly to inject any
		// dependencies this activity may have
		new DependencyInjector(dependencyInjectingObjectFactory).injectDependenciesForClassHierarchy(this);
	}
	  public boolean onCreateOptionsMenu(Menu menu) 
	  {

	        boolean supRetVal = super.onCreateOptionsMenu(menu);

	        menu.add(0, 0, Menu.NONE, "exit");

	        menu.add(0, 1, Menu.NONE, "continue");


	        return supRetVal;

	    }
	  
	    public boolean onOptionsItemSelected(MenuItem item) {

	        switch (item.getItemId()) {

	            case 0:

	                // Go back to the list page

	                finish();

	                return true;

	          
/*
 *   case 1:
	                // Go to the employer detail page

	                Intent iEmp = new Intent(MicroJobsDetail.this, MicroJobsEmpDetail.class);

	                Bundle bEmp = new Bundle();

	                bEmp.putInt("_id", job_id.intValue());

	                iEmp.putExtras(bEmp);



	                startActivity(iEmp);

	                return true;
*/
	            case 1:

	            	// Delete this job

	                // Setup Delete Alert Dialog

	            	final int DELETE_JOB = 0;

	            	final int CANCEL_DELETE = 1;

	            	

	                Handler mHandler = new Handler() {    	

	                    public void handleMessage(Message msg) {

	                        switch (msg.what) {

	                            case DELETE_JOB:

	    //                        db.deleteJob(job_id);

	 //                           startActivity(new Intent(MicroJobsDetail.this, MicroJobsList.class));

	                            break;

	                    

	                            case CANCEL_DELETE:

	                            // Do nothing

	                            break;

	                        }

	                    }

	                };

	                // "Answer" callback.

	                final Message acceptMsg = Message.obtain();

	                acceptMsg.setTarget(mHandler);

	                acceptMsg.what = DELETE_JOB;

	                    

	                // "Cancel" callback.

	                final Message rejectMsg = Message.obtain();

	                rejectMsg.setTarget(mHandler);

	                rejectMsg.what = CANCEL_DELETE;



	                new AlertDialog.Builder(this)

	                  .setMessage("Are you sure you want to delete this job?")

	                  .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

	                	  public void onClick(DialogInterface dialog, int value) {

	                		  rejectMsg.sendToTarget();

	                	  }})

	                  .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

	                	  public void onClick(DialogInterface dialog, int value) {

	                    		  acceptMsg.sendToTarget();

	                	  }})

	                  .setOnCancelListener(new DialogInterface.OnCancelListener() {

	                        public void onCancel(DialogInterface dialog) {

	                          rejectMsg.sendToTarget();

	                      }})

	                      .show();    

	            	return true;


	            	

	            default:

	                return false;

	        }

	    }

	abstract protected void addDependencies(DependencyInjectingObjectFactory aDependencyInjectingObjectFactory);
}