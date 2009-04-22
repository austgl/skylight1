package net.nycjava.skylight;

import static java.lang.String.format;
import net.nycjava.skylight.dependencyinjection.Dependency;
import net.nycjava.skylight.service.PositionPublicationService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

public class SkillTestActivity extends SkylightActivity {
	@Dependency
	private PositionPublicationService positionPublicationService;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.skilltest);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		Log.i(this.getClass().getName(), format("my PositionPublicationService was %s", positionPublicationService));
		final Intent intent = new Intent(SkillTestActivity.this, WelcomeActivity.class);
		startActivity(intent);
		return true;
	}
}
