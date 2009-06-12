package net.nycjava.skylight;

import static java.lang.System.out;
import net.nycjava.skylight.dependencyinjection.Dependency;
import net.nycjava.skylight.dependencyinjection.DependencyInjectingObjectFactory;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WelcomeActivity extends SkylightActivity {
	@Dependency
	private LinearLayout contentView;

	protected void addDependencies(DependencyInjectingObjectFactory aDependencyInjectingObjectFactory) {
		aDependencyInjectingObjectFactory.registerImplementationObject(LinearLayout.class,
				(LinearLayout) getLayoutInflater().inflate(R.layout.welcome, null));
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ImageView imageView = new ImageView(this);
		imageView.setImageResource(R.drawable.icon);
		contentView.addView(imageView);
		setContentView(contentView);
		
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		final Intent intent = new Intent(WelcomeActivity.this, GetReadyActivity.class);
		startActivity(intent);
		return true;
	}

	@Override
    public boolean onTouchEvent(MotionEvent event) {
//        float x = event.getX() - CENTER_X;
//        float y = event.getY() - CENTER_Y;
//        boolean inCenter = java.lang.Math.sqrt(x*x + y*y) <= CENTER_RADIUS;
        
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            	break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
        		final Intent intent = new Intent(WelcomeActivity.this, GetReadyActivity.class);
        		startActivity(intent);
        		finish();
                break;
        }
        return true;
    }
}
