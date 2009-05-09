package net.nycjava.skylight;

import android.content.Intent;
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

/**
 * reporting success; report acknowledged; go to get ready
 */
public class SuccessActivity extends SkylightActivity {

	@Dependency
	private LinearLayout contentView;
	
	@Override
	protected void addDependencies(DependencyInjectingObjectFactory dependencyInjectingObjectFactory) {
		dependencyInjectingObjectFactory.registerImplementationObject(LinearLayout.class,
				(LinearLayout)getLayoutInflater().inflate(R.layout.successmsg, null));
	}

	
	 /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Hide the window title.
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN ,
        		             WindowManager.LayoutParams.FLAG_FULLSCREEN );
		
		setContentView(contentView);
		
		Button goButton = (Button)findViewById(R.id.go);
		goButton.setOnClickListener(new OnClickListener() {
	        public void onClick(View v) {
			    Intent intent = new Intent();
			    intent.setClass(SuccessActivity.this, GetReadyActivity.class);
			    startActivity(intent);
			    finish();
	        }
		});
		
	   	MediaPlayer.create(getBaseContext(), R.raw.tada).start();
	}
}
