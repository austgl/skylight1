package skylight1.opengl.barnstormer;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class BarnstormerActivity extends Activity {
	private GLSurfaceView gLSurfaceView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		gLSurfaceView = new InteractiveGLSurfaceView(this);
		setContentView(gLSurfaceView);

	}
}