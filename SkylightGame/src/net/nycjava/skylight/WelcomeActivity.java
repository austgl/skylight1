package net.nycjava.skylight;

import net.nycjava.skylight.dependencyinjection.Dependency;
import net.nycjava.skylight.dependencyinjection.DependencyInjectingObjectFactory;
import net.nycjava.skylight.view.TypeFaceTextView;
import android.content.Intent;
import android.content.SharedPreferences;
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
//			finish();
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

		((Button) contentView.findViewById(R.id.easy)).setOnClickListener(new DifficultyClickListener(
				SOBER_DIFFICULTY_LEVEL));
		((Button) contentView.findViewById(R.id.normal)).setOnClickListener(new DifficultyClickListener(
				BUZZED_DIFFICULTY_LEVEL));
		((Button) contentView.findViewById(R.id.hard)).setOnClickListener(new DifficultyClickListener(
				SMASHED_DIFFICULTY_LEVEL));

		// show the high score
		SharedPreferences sharedPreferences = getSharedPreferences(PASS_THE_DRINK_PREFS_FILE, MODE_PRIVATE);
		int highScore = sharedPreferences.getInt(HIGH_SCORE_PREFERENCE_NAME, 0);
		TypeFaceTextView highScoreTextView = (TypeFaceTextView) contentView.findViewById(R.id.highScore);
		highScoreTextView.setText(String.format("%s: %d", getResources().getString(R.string.high_score), highScore));

		setContentView(contentView);
	}
}
