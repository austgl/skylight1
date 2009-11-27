package skylight1.opengl;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.microedition.khronos.opengles.GL10;

/**
 * Encapsulates the construction of OpenGLGeometry objects.
 */
class OpenGLGeometryBuilderImpl<T, R> extends GeometryBuilderImpl<T, R> implements OpenGLGeometryBuilder<T, R> {
	// TODO decide what to do about clients not bothering to provide textures (for example) for some of the components
	// of a geometry. this will not work as is! Simple solution would be to add zeros in the add methods, and use set in
	// the inner classes

	/**
	 * Inner class that permits the addition of texture, normals, and colours to a 3D triangle.
	 */
	public class Triangle3D<X> implements TexturableTriangle3D<X>, ColourableTriangle3D<X>, NormalizableTriangle3D<X> {
		public X setTextureCoordinates(float aU1, float aV1, float aU2, float aV2, float aU3, float aV3) {
			textureCoordinates.add(aU1);
			textureCoordinates.add(aV1);

			textureCoordinates.add(aU2);
			textureCoordinates.add(aV2);

			textureCoordinates.add(aU3);
			textureCoordinates.add(aV3);

			@SuppressWarnings("unchecked")
			X typeSafeThis = (X) this;

			return typeSafeThis;
		}

		public X setNormal(float aNormalX1, float aNormalY1, float aNormalZ1, float aNormalX2, float aNormalY2,
				float aNormalZ2, float aNormalX3, float aNormalY3, float aNormalZ3) {
			normals.add(aNormalX1);
			normals.add(aNormalY1);
			normals.add(aNormalZ1);

			normals.add(aNormalX2);
			normals.add(aNormalY2);
			normals.add(aNormalZ2);

			normals.add(aNormalX3);
			normals.add(aNormalY3);
			normals.add(aNormalZ3);

			@SuppressWarnings("unchecked")
			X typeSafeThis = (X) this;

			return typeSafeThis;
		}

		public X setColour(float aRed1, float aGreen1, float aBlue1, float aRed2, float aGreen2, float aBlue2,
				float aRed3, float aGreen3, float aBlue3) {
			colours.add(aRed1);
			colours.add(aGreen1);
			colours.add(aBlue1);

			colours.add(aRed2);
			colours.add(aGreen2);
			colours.add(aBlue2);

			colours.add(aRed3);
			colours.add(aGreen3);
			colours.add(aBlue3);

			@SuppressWarnings("unchecked")
			X typeSafeThis = (X) this;

			return typeSafeThis;
		}
	}

	/**
	 * Inner class that permits the addition of texture and colours to a 2D rectangle.
	 */
	public class Rectangle2D<X> implements TexturableRectangle2D<X>, ColourableRectangle2D<X> {
		public X setTextureCoordinates(float aU, float aV, float aUWidth, float aVHeight) {
			// first triangle of rectangle
			textureCoordinates.add(aU);
			textureCoordinates.add(aV);

			textureCoordinates.add(aU + aUWidth);
			textureCoordinates.add(aV);

			textureCoordinates.add(aU + aUWidth);
			textureCoordinates.add(aV + aVHeight);

			// second triangle of rectangle
			textureCoordinates.add(aU + aUWidth);
			textureCoordinates.add(aV + aVHeight);

			textureCoordinates.add(aU);
			textureCoordinates.add(aV + aVHeight);

			textureCoordinates.add(aU);
			textureCoordinates.add(aV);

			@SuppressWarnings("unchecked")
			X typeSafeThis = (X) this;

			return typeSafeThis;
		}

		public X setColour(float aRed1, float aGreen1, float aBlue1, float aRed2, float aGreen2, float aBlue2,
				float aRed3, float aGreen3, float aBlue3, float aRed4, float aGreen4, float aBlue4) {
			// first triangle of rectangle
			colours.add(aRed1);
			colours.add(aGreen1);
			colours.add(aBlue1);

			colours.add(aRed2);
			colours.add(aGreen2);
			colours.add(aBlue2);

			colours.add(aRed3);
			colours.add(aGreen3);
			colours.add(aBlue3);

			// second triangle of rectangle
			colours.add(aRed3);
			colours.add(aGreen3);
			colours.add(aBlue3);

			colours.add(aRed2);
			colours.add(aGreen2);
			colours.add(aBlue2);

			colours.add(aRed4);
			colours.add(aGreen4);
			colours.add(aBlue4);

			@SuppressWarnings("unchecked")
			X typeSafeThis = (X) this;

			return typeSafeThis;
		}
	}

	private static final int MODEL_COORDINATES_PER_VERTEX = 3;

	private static final int NO_MODE = -1;

	private int currentMode = NO_MODE;

	private final Stack<Integer> geometryStartVertexStack = new Stack<Integer>();

	@SuppressWarnings("unchecked")
	private final T triangle3D = (T) new Triangle3D<Object>();

	@SuppressWarnings("unchecked")
	private final R rectangle2D = (R) new Rectangle2D<Object>();

	private List<Float> modelCoordinates = new ArrayList<Float>();

	private List<Float> textureCoordinates = new ArrayList<Float>();

	private List<Float> normals = new ArrayList<Float>();

	private List<Float> colours = new ArrayList<Float>();

	IntBuffer modelCoordinatesAsBuffer;

	IntBuffer textureCoordinatesAsBuffer;

	IntBuffer normalsAsBuffer;

	IntBuffer coloursAsBuffer;

	boolean complete;

	/**
	 * @param aUsesTexturesCoordinates
	 *            indicates if this geometries will include textures.
	 * @param aUsesNormals
	 *            indicates if this geometries will include normals.
	 * @param aUsesColours
	 *            indicates if this geometries will include colours.
	 */
	public OpenGLGeometryBuilderImpl(final boolean aUsesTexturesCoordinates, final boolean aUsesNormals,
			final boolean aUsesColours) {
		super(aUsesTexturesCoordinates, aUsesNormals, aUsesColours);
		// TODO allow the client to choose interleaved arrays or not. interleaved arrays are better for updating the
		// model coordinates, normals, colour and textures coordinates all at once, but non-interleaved are better for
		// changing only one aspect of a geometry at a time, such as during texture animations on fixed models
		// coordinates
	}

	/**
	 * Used to mark the start of a new geometry. The end of the geometry is marked by a matching call to endGeometry. A
	 * given geometry must contain all triangles, triangle strips, triangle fans, lines, line strips, or points, not a
	 * mix there-of. All triangles, triangle strips, triangle fans, lines, line strips, or points added between the
	 * matching pair of startGeometry/endGeometry will belong to the single OpenGLGeometry object returned by
	 * endGeometry. Geometries may be nested, so two calls to startGeometry may be followed by two calls to endGeometry.
	 */
	public void startGeometry() {
		if (complete) {
			throw new IllegalStateException("Cannot start geometry after complete");
		}

		int firstVertexOffset = modelCoordinates.size() / MODEL_COORDINATES_PER_VERTEX;
		geometryStartVertexStack.push(firstVertexOffset);
	}

	/**
	 * @see OpenGLGeometryBuilderImpl#startGeometry()
	 * @return The returned OpenGLGeometry must not be used until <i>after</i> this OpenGLGeometryBuilder has been
	 *         enabled.
	 */
	public OpenGLGeometry endGeometry() {
		if (geometryStartVertexStack.isEmpty()) {
			throw new IllegalStateException("calls to endGeometry must match calls to startGeometry");
		}

		int firstVertexOffset = geometryStartVertexStack.pop();
		final int numberOfVertices = modelCoordinates.size() / MODEL_COORDINATES_PER_VERTEX - firstVertexOffset;
		final OpenGLGeometry openGLGeometry = new OpenGLGeometry(currentMode, firstVertexOffset, numberOfVertices, this);

		// if the stack is empty, then clear the current mode: the next geometry can use a different mode
		if (geometryStartVertexStack.isEmpty()) {
			currentMode = NO_MODE;
		}

		return openGLGeometry;
	}

	/**
	 * Adds a 3D triangle to the current geometry. Uses mode GL_TRIANGLES.
	 * 
	 * @return A Triangle3D, which permits adding textures, colour and normals as per the configuration of the
	 *         OpenGLGeometryBuilder
	 */
	public T add3DTriangle(float anX1, float aY1, float aZ1, float anX2, float aY2, float aZ2, float anX3, float aY3,
			float aZ3) {
		checkCurrentGeometryAndCompleteAndCheckAndSetMode(GL10.GL_TRIANGLES);

		modelCoordinates.add(anX1);
		modelCoordinates.add(aY1);
		modelCoordinates.add(aZ1);

		modelCoordinates.add(anX2);
		modelCoordinates.add(aY2);
		modelCoordinates.add(aZ2);

		modelCoordinates.add(anX3);
		modelCoordinates.add(aY3);
		modelCoordinates.add(aZ3);

		return triangle3D;
	}

	/**
	 * Adds a 2D (z = 0) upright rectangle to the current geometry. Uses mode GL_TRIANGLES.
	 * 
	 * @return A Rectangle2D, which permits adding textures and colour as per the configuration of the
	 *         OpenGLGeometryBuilder
	 */
	public R add2DRectangle(float anX, float aY, float aWidth, float aHeight) {
		checkCurrentGeometryAndCompleteAndCheckAndSetMode(GL10.GL_TRIANGLES);

		if (modelCoordinates == null) {
			throw new IllegalStateException("Cannot change geometry after the buffers have been retrieved");
		}

		// first triangle of rectangle
		modelCoordinates.add(anX);
		modelCoordinates.add(aY);
		modelCoordinates.add(0f);

		modelCoordinates.add(anX + aWidth);
		modelCoordinates.add(aY);
		modelCoordinates.add(0f);

		modelCoordinates.add(anX + aWidth);
		modelCoordinates.add(aY + aHeight);
		modelCoordinates.add(0f);

		// second triangle of rectangle
		modelCoordinates.add(anX + aWidth);
		modelCoordinates.add(aY + aHeight);
		modelCoordinates.add(0f);

		modelCoordinates.add(anX);
		modelCoordinates.add(aY + aHeight);
		modelCoordinates.add(0f);

		modelCoordinates.add(anX);
		modelCoordinates.add(aY);
		modelCoordinates.add(0f);

		return rectangle2D;
	}

	/**
	 * Completes the use of this builder for building. Must be called before using any of the OpenGLGeometry created by
	 * this object.
	 */
	private void complete() {
		if (!geometryStartVertexStack.isEmpty()) {
			throw new IllegalStateException("Cannot complete until all started geometries are ended");
		}

		modelCoordinatesAsBuffer = ByteBufferFactory.createBuffer(modelCoordinates);
		modelCoordinates = null;

		if (usesTextureCoordinates) {
			textureCoordinatesAsBuffer = ByteBufferFactory.createBuffer(textureCoordinates);
		}
		textureCoordinates = null;

		if (usesNormals) {
			normalsAsBuffer = ByteBufferFactory.createBuffer(normals);
		}
		normals = null;

		if (usesColours) {
			coloursAsBuffer = ByteBufferFactory.createBuffer(colours);
		}
		colours = null;

		complete = true;
	}

	/**
	 * Enables all of the features necessary to render any of the geometries created by this builder.
	 */
	public void enable(GL10 aGL10) {
		if (!complete) {
			complete();
		}
		aGL10.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		aGL10.glVertexPointer(3, GL10.GL_FIXED, 0, modelCoordinatesAsBuffer);
		if (usesTextureCoordinates) {
			aGL10.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			aGL10.glTexCoordPointer(2, GL10.GL_FIXED, 0, textureCoordinatesAsBuffer);
		} else {
			aGL10.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		}
		if (usesNormals) {
			aGL10.glEnableClientState(GL10.GL_NORMAL_ARRAY);
			aGL10.glNormalPointer(GL10.GL_FIXED, 0, normalsAsBuffer);
		} else {
			aGL10.glDisableClientState(GL10.GL_NORMAL_ARRAY);
		}
		if (usesColours) {
			aGL10.glEnableClientState(GL10.GL_COLOR_ARRAY);
			aGL10.glNormalPointer(GL10.GL_FIXED, 0, coloursAsBuffer);
		} else {
			aGL10.glDisableClientState(GL10.GL_COLOR_ARRAY);
		}
	}

	private void checkCurrentGeometryAndCompleteAndCheckAndSetMode(int aMode) {
		if (complete) {
			throw new IllegalStateException("Cannot add geometry to a completed builder");
		}

		if (geometryStartVertexStack.isEmpty()) {
			throw new IllegalStateException(
					"Adding points, lines, and shapes may only be made between matched calls to startGeometry and endGeometry");
		}

		// if no mode has been set yet (i.e., this is the first triangle, line, etc. for this geometry), then use the
		// provided mode
		if (currentMode == NO_MODE) {
			currentMode = aMode;
		} else if (currentMode != aMode) {
			// TODO show textual values for modes
			throw new IllegalStateException(
					String
							.format(
									"Cannot change the mode (point, line, triangle, triangle strip, or triangle fan) within a geometry; current is %d, new is %d",
									currentMode, aMode));
		}
	}
}
