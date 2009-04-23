package net.nycjava.skylight;

import static java.lang.String.format;
import net.nycjava.skylight.dependencyinjection.Dependency;
import net.nycjava.skylight.dependencyinjection.DependencyInjectingObjectFactory;
import net.nycjava.skylight.service.AndroidPositionPublicationService;
import net.nycjava.skylight.service.PositionPublicationService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.LinearLayout;

public class SkillTestActivity extends SkylightActivity {
	@Dependency
	private PositionPublicationService positionPublicationService;

	@Dependency
	private LinearLayout contentView;

	@Override
	protected void addDependencies(DependencyInjectingObjectFactory aDependencyInjectingObjectFactory) {
		aDependencyInjectingObjectFactory.registerImplementationClass(PositionPublicationService.class,
				AndroidPositionPublicationService.class);
		aDependencyInjectingObjectFactory.registerImplementationObject(LinearLayout.class,
				(LinearLayout) getLayoutInflater().inflate(R.layout.skilltest, null));
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(contentView);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		Log.i(this.getClass().getName(), format("my PositionPublicationService was %s", positionPublicationService));
		final Intent intent = new Intent(SkillTestActivity.this, WelcomeActivity.class);
		startActivity(intent);
		return true;
	}
}
