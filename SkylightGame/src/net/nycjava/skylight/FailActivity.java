package net.nycjava.skylight;

import android.content.Intent;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import net.nycjava.skylight.dependencyinjection.Dependency;
import net.nycjava.skylight.dependencyinjection.DependencyInjectingObjectFactory;
import net.nycjava.skylight.view.Preview;

/**
 * reporting unsteady hand; report acknowledged; reporting slow hand; report acknowledged; go to welcome
 */
public class FailActivity extends SkylightActivity {

	@Dependency
	private LinearLayout view;
	
	@Override
	protected void addDependencies(DependencyInjectingObjectFactory dependencyInjectingObjectFactory) 
	{

		dependencyInjectingObjectFactory.registerImplementationObject(LinearLayout.class,
				(LinearLayout) getLayoutInflater().inflate(R.layout.failmsg, null));
	}

	
	 /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Hide the window title.
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN ,
        		             WindowManager.LayoutParams.FLAG_FULLSCREEN );
		setContentView(view);

		Button goButton = (Button)findViewById(R.id.go);
		goButton.setOnClickListener(new OnClickListener() {
	        public void onClick(View v) {
			    Intent intent = new Intent();
			    intent.setClass(FailActivity.this, GetReadyActivity.class);
			    startActivity(intent);
			    finish();
	        }
		});
		
	   	MediaPlayer.create(getBaseContext(), R.raw.uhoh).start();
	}
		
	
}
