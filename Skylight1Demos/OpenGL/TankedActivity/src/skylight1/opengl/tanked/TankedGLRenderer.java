package skylight1.opengl.tanked;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import skylight1.opengl.tanked.R;

import skylight1.opengl.FastGeometryBuilder;
import skylight1.opengl.FastGeometryBuilderFactory;
import skylight1.opengl.OpenGLGeometry;
import skylight1.opengl.OpenGLGeometryBuilder;
import skylight1.opengl.OpenGLGeometryBuilderFactory;
import skylight1.opengl.Texture;
import skylight1.opengl.GeometryBuilder.TexturableRectangle2D;
import skylight1.opengl.GeometryBuilder.TexturableTriangle3D;
import skylight1.util.FPSLogger;
import android.content.Context;
import android.opengl.GLU;
import android.opengl.GLSurfaceView.Renderer;

public class TankedGLRenderer implements Renderer {
	private static final int FRAMES_BETWEEN_LOGGING_FPS = 60;

	private static final float SCALE = 500f;

	private final Context context;

	private Texture tankTexture;

	private int tankBodyDirection;

	private int tankTurretDirection;

	private int enemyTankBodyDirection;

	private int enemyTankTurretDirection;

	private OpenGLGeometry enemyTankBodyTextureGeometry;

	private OpenGLGeometry enemyTankTurretTextureGeometry;

	private Texture groundTexture;

	private OpenGLGeometryBuilder<TexturableTriangle3D<Object>, TexturableRectangle2D<Object>> openGLGeometryBuilder;

	private FPSLogger fPSLogger;

	private OpenGLGeometry tankGeometry;

	private OpenGLGeometry tankBodyTextureGeometry;

	private OpenGLGeometry tankTurretTextureGeometry;

	private OpenGLGeometry groundGeometry;

	private OpenGLGeometry enemyTankGeometry;

	FastGeometryBuilder<TexturableTriangle3D<Object>, TexturableRectangle2D<Object>> textureChangingGeometryBuilder;

	private OpenGLGeometry bothTanksGeometry;

	public TankedGLRenderer(Context aContext) {
		context = aContext;
	}

	public void onSurfaceCreated(final GL10 gl, final EGLConfig config) {
		openGLGeometryBuilder = OpenGLGeometryBuilderFactory.createTexturable();
		createGeometry();
		openGLGeometryBuilder.enable(gl);

		textureChangingGeometryBuilder = FastGeometryBuilderFactory.createTexturable(bothTanksGeometry);

		// enable blending of the foreground over the background
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gl.glClearColor(1, 0, 0, 1.0f);
	}

	public void onSurfaceChanged(GL10 gl, int w, int h) {
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glViewport(0, 0, w, h);
		float ratio = (float) h / (float) w;
		GLU.gluOrtho2D(gl, 0, 1, 0, +ratio);

		groundTexture = new Texture(gl, context, R.drawable.grass);
		// TODO get this into the texture
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);

		tankTexture = new Texture(gl, context, R.drawable.tank);
		// TODO get this into the texture
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);

		textureChangingGeometryBuilder.reset();
		updateTankRotation();
		updateEnemyTankRotation();
		bothTanksGeometry.updateTexture(textureChangingGeometryBuilder);

		fPSLogger = new FPSLogger(TankedActivity.class.getName(), FRAMES_BETWEEN_LOGGING_FPS);
	}

	private void createGeometry() {
		openGLGeometryBuilder.startGeometry();
		openGLGeometryBuilder.add2DRectangle(0, 0, 1, 1).setTextureCoordinates(0, 0, 5, 5);
		groundGeometry = openGLGeometryBuilder.endGeometry();

		openGLGeometryBuilder.startGeometry();

		openGLGeometryBuilder.startGeometry();
		createTankImageLayer(0.5f, 0.5f, 110 / SCALE, 91 / SCALE);
		createTankImageLayer(0.5f, 0.5f, 160 / SCALE, 112 / SCALE);
		tankGeometry = openGLGeometryBuilder.endGeometry();

		openGLGeometryBuilder.startGeometry();
		createTankImageLayer(0.25f, 0.75f, 110 / SCALE, 91 / SCALE);
		createTankImageLayer(0.25f, 0.75f, 160 / SCALE, 112 / SCALE);
		enemyTankGeometry = openGLGeometryBuilder.endGeometry();

		bothTanksGeometry = openGLGeometryBuilder.endGeometry();
	}

	private void createTankImageLayer(final float aCenterX, final float aCenterY, final float aWidth,
			final float aHeight) {
		final float tankX = aCenterX - aWidth / 2f;
		final float tankY = aCenterY - aHeight / 2f;

		openGLGeometryBuilder.add2DRectangle(tankX, tankY, aWidth, aHeight).setTextureCoordinates(0, 0, 0, 0);
	}

	private void updateTankRotation() {
		// TODO allow the user to control facing
		tankBodyDirection = (int) (System.currentTimeMillis() / 200L % 32L);
		tankTurretDirection = (int) (System.currentTimeMillis() / 100L % 32L);

		updateTankBodyLayerRotation(tankBodyDirection, tankBodyTextureGeometry);
		updateTankTurretLayerRotation(tankTurretDirection, tankTurretTextureGeometry);
	}

	private void updateEnemyTankRotation() {
		// TODO allow the AI or remote user to control facing
		enemyTankBodyDirection = 31 - (int) (System.currentTimeMillis() / 400L % 32L);
		enemyTankTurretDirection = 31 - (int) (System.currentTimeMillis() / 300L % 32L);

		updateTankBodyLayerRotation(enemyTankBodyDirection, enemyTankBodyTextureGeometry);
		updateTankTurretLayerRotation(enemyTankTurretDirection, enemyTankTurretTextureGeometry);
	}

	private void updateTankBodyLayerRotation(int aLayerRotation, OpenGLGeometry anOpenGLGeometry) {
		final float x = (aLayerRotation % 9) * 110f / 1024f;
		final float y = (aLayerRotation / 9 + 1) * 91f / 1024f;
		final float width = 110f / 1024f;
		final float height = -91f / 1024f;

		textureChangingGeometryBuilder.add2DRectangle().setTextureCoordinates(x, y, width, height);
	}

	private void updateTankTurretLayerRotation(int aLayerRotation, OpenGLGeometry anOpenGLGeometry) {
		final float x = 1f - ((aLayerRotation % 6) + 1) * 160f / 1024f;
		final float y = 1f - ((aLayerRotation / 6) + 1) * 112f / 1024f;
		final float width = 160f / 1024f;
		final float height = 112f / 1024f;

		textureChangingGeometryBuilder.add2DRectangle().setTextureCoordinates(x, y, width, height);
	}

	public void onDrawFrame(GL10 gl) {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		gl.glColor4f(1, 1, 1, 1);

		groundTexture.activateTexture();
		groundGeometry.draw(gl);

		textureChangingGeometryBuilder.reset();
		updateTankRotation();
		updateEnemyTankRotation();
		bothTanksGeometry.updateTexture(textureChangingGeometryBuilder);

		tankTexture.activateTexture();

		tankGeometry.draw(gl);
		gl.glColor4f(0.5f, 0.5f, 1f, 1f);
		enemyTankGeometry.draw(gl);

		fPSLogger.frameRendered();
	}
}
