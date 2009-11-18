package skylight1.opengl;

import javax.microedition.khronos.opengles.GL10;

/**
 * Used by onDrawFrame
 * 
 */
public class OpenGLGeometry {
	private final int mode;

	private final int first;

	private final int numberOfVerticies;

	public OpenGLGeometry(final int aMode, final int aFirst, final int aNumberOfVerticies) {
		mode = aMode;
		first = aFirst;
		numberOfVerticies = aNumberOfVerticies;
	}
	
	public void draw(GL10 aGL10) {
		aGL10.glDrawArrays(mode, first, numberOfVerticies);
	}
}
