package skylight1.opengl.tanked;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class TankedActivity extends Activity {
	private GLSurfaceView gLSurfaceView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		gLSurfaceView = new InteractiveGLSurfaceView(this);
		setContentView(gLSurfaceView);
	}

	@Override
	protected void onResume() {
		super.onResume();

		gLSurfaceView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();

		gLSurfaceView.onPause();
	}
}