package net.nycjava.skylight;

import net.nycjava.skylight.dependencyinjection.Dependency;
import net.nycjava.skylight.dependencyinjection.DependencyInjectingObjectFactory;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class WelcomeActivity extends SkylightActivity {
	private final class DifficultyClickListener implements OnClickListener {
		final private int difficulty;

		public DifficultyClickListener(int aDifficulty) {
			super();
			difficulty = aDifficulty;
		}

		@Override
		public void onClick(View arg0) {
			final Intent intent = new Intent(WelcomeActivity.this, GetReadyActivity.class);
			intent.putExtra(SkylightActivity.DIFFICULTY_LEVEL, difficulty);
			startActivity(intent);
			finish();
		}
	}

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

		((Button) contentView.findViewById(R.id.easy)).setOnClickListener(new DifficultyClickListener(0));
		((Button) contentView.findViewById(R.id.normal)).setOnClickListener(new DifficultyClickListener(5));
		((Button) contentView.findViewById(R.id.hard)).setOnClickListener(new DifficultyClickListener(10));

		setContentView(contentView);
	}
}
