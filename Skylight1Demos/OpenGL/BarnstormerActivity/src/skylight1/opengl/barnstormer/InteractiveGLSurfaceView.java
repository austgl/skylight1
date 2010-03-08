package skylight1.opengl.barnstormer;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class InteractiveGLSurfaceView extends GLSurfaceView {
	GLRenderer renderer;

	public InteractiveGLSurfaceView(Context context) {
		super(context);

		setDebugFlags(DEBUG_CHECK_GL_ERROR);

		renderer = new GLRenderer(context);
		setRenderer(renderer);
	}

	public boolean onTouchEvent(final MotionEvent event) {
		queueEvent(new Runnable() {
			public void run() {
				// renderer.setColor(event.getX() / getWidth(), event.getY() / getHeight(), 1.0f);
			}
		});
		return true;
	}
}
