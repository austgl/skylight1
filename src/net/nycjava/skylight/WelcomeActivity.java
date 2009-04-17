package net.nycjava.skylight;

import static java.lang.String.format;
import net.nycjava.skylight.dependencyinjection.Dependency;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.LinearLayout;

public class WelcomeActivity extends SkylightActivity {
	@Dependency
	private LinearLayout contentView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(contentView);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		Log.i(this.getClass().getName(), format("my contentView was %s", contentView));
		final Intent intent = new Intent(WelcomeActivity.this, SkillTestActivity.class);
		startActivity(intent);
		return true;
	}
}
