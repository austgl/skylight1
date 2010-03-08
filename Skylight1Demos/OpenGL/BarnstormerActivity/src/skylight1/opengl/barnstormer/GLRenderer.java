package skylight1.opengl.barnstormer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import skylight1.opengl.barnstormer.R;

import skylight1.util.FPSLogger;
import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;

public class GLRenderer implements Renderer {
	private static final int NUMBER_OF_TEXTURES = 2;

	private static final int MS_BETWEEN_LOGGING_FPS = 1000;

	private final Context context;

	private ByteBuffer verticesAsByteBuffer;

	private ByteBuffer textureCoordinatesAsByteBuffer;

	private ByteBuffer normalsAsByteBuffer;

	private int planeTexture;

	private FPSLogger fPSLogger = new FPSLogger(GLRenderer.class.getName(), MS_BETWEEN_LOGGING_FPS);

	public GLRenderer(Context aContext) {
		context = aContext;
	}

	public void onSurfaceCreated(final GL10 gl, final EGLConfig config) {
		// create texture
		final int[] textures = new int[NUMBER_OF_TEXTURES];
		gl.glGenTextures(textures.length, textures, 0);

		planeTexture = textures[0];

		loadBitmap(gl, planeTexture, R.drawable.airplaneredtex);

		gl.glEnable(GL10.GL_DEPTH_TEST);
	}

	private void loadBitmap(final GL10 gl, int aTextureId, int aResource) {
		gl.glBindTexture(GL10.GL_TEXTURE_2D, aTextureId);

		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);
		// Log.i(GLRenderer.class.getName(), String.format("10=%b, 10Ext=%b, 11=%b, 11Ext=%b, 11ExtFP=%b",
		// gl instanceof GL10, gl instanceof GL10Ext, gl instanceof GL11, gl instanceof GL11Ext,
		// gl instanceof GL11ExtensionPack));
		// if (gl instanceof GL11) {
		// Log.i(GLRenderer.class.getName(), "running under OpenGL 1.1 ES");
		// gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL11.GL_GENERATE_MIPMAP, GL10.GL_FALSE);
		// }

		final InputStream inputStream = context.getResources().openRawResource(aResource);
		try {
			Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
			final int error = gl.glGetError();
			if (error != 0) {
				Log.i(GLRenderer.class.getName(), String.format("error is %d = %s", error, gl.glGetString(error)));
			}

			bitmap.recycle();
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				// Ignore.
			}
		}
	}

	public void onSurfaceChanged(GL10 gl, int w, int h) {
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glViewport(0, 0, w, h);
		GLU.gluPerspective(gl, 45, (float) w / (float) h, 1, 300f);

		loadPlane();

		gl.glClearColor(0.5f, 0.5f, 1, 1.0f);
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, verticesAsByteBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureCoordinatesAsByteBuffer);
		gl.glNormalPointer(GL10.GL_FLOAT, 0, normalsAsByteBuffer);
		gl.glEnable(GL10.GL_CULL_FACE);

		gl.glBindTexture(GL10.GL_TEXTURE_2D, planeTexture);
	}

	/** May want to use w and h in the future */
	private void loadPlane() {
		try {
			ObjectLoader objectLoader = new ObjectLoader(context.getResources().openRawResource(
					R.raw.airplane_red_mesh_obj));

			verticesAsByteBuffer = objectLoader.getVerticesBuffer();
			textureCoordinatesAsByteBuffer = objectLoader.getTextureBuffer();
			normalsAsByteBuffer = objectLoader.getNormalsBuffer();
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void onDrawFrame(GL10 gl) {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glColor4f(1, 1, 1, 1);

		float distance = -250f + (float) (System.currentTimeMillis() % 15000) / 15000f * 300f;

		gl.glMatrixMode(GL10.GL_MODELVIEW);

		gl.glLoadIdentity();
		GLU.gluLookAt(gl, 0, 0, 0, 0, 2, distance, 0, 1, 0);

		drawPlane(gl, 0, 2, distance);
		for (int p = 0; p < 8; p++) {
			final int rank = p / 2 + 1;
			final int side = (int) Math.signum(0.5f - (float) (p % 2));
			drawPlane(gl, side * 8 * rank, 2 + 2 * rank, distance);
		}

		fPSLogger.frameRendered();
	}

	private void drawPlane(GL10 gl, float anX, float aY, float aZ) {
		gl.glPushMatrix();
		gl.glTranslatef(anX, aY, aZ);
		gl.glDrawArrays(GL10.GL_TRIANGLES, 0, verticesAsByteBuffer.capacity() / Float.SIZE * Byte.SIZE / 3);
		gl.glPopMatrix();
	}
}
