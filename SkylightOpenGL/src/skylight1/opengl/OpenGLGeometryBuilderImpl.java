package skylight1.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
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
	 * Inner class that permits the addition of texture, normalComponents, and colours to a 3D triangle.
	 */
	public class Triangle3D<X> implements TexturableTriangle3D<X>, ColourableTriangle3D<X>, NormalizableTriangle3D<X> {
		public X setTextureCoordinates(float aU1, float aV1, float aU2, float aV2, float aU3, float aV3) {
			final int offset = vertexOffsetOfCurrentGeometry * TEXTURE_COORDINATES_PER_VERTEX;

			textureCoordinates[offset] = (int) (aU1 * (1 << 16));
			textureCoordinates[offset + 1] = (int) (aV1 * (1 << 16));

			textureCoordinates[offset + 2] = (int) (aU2 * (1 << 16));
			textureCoordinates[offset + 3] = (int) (aV2 * (1 << 16));

			textureCoordinates[offset + 4] = (int) (aU3 * (1 << 16));
			textureCoordinates[offset + 5] = (int) (aV3 * (1 << 16));

			@SuppressWarnings("unchecked")
			X typeSafeThis = (X) this;

			return typeSafeThis;
		}

		public X setNormal(float aNormalX1, float aNormalY1, float aNormalZ1, float aNormalX2, float aNormalY2,
				float aNormalZ2, float aNormalX3, float aNormalY3, float aNormalZ3) {
			final int offset = vertexOffsetOfCurrentGeometry * NORMAL_COMPONENTS_PER_VERTEX;

			normalComponents[offset] = (int) (aNormalX1 * (1 << 16));
			normalComponents[offset + 1] = (int) (aNormalY1 * (1 << 16));
			normalComponents[offset + 2] = (int) (aNormalZ1 * (1 << 16));

			normalComponents[offset + 3] = (int) (aNormalX2 * (1 << 16));
			normalComponents[offset + 4] = (int) (aNormalY2 * (1 << 16));
			normalComponents[offset + 5] = (int) (aNormalZ2 * (1 << 16));

			normalComponents[offset + 6] = (int) (aNormalX3 * (1 << 16));
			normalComponents[offset + 7] = (int) (aNormalY3 * (1 << 16));
			normalComponents[offset + 8] = (int) (aNormalZ3 * (1 << 16));

			@SuppressWarnings("unchecked")
			X typeSafeThis = (X) this;

			return typeSafeThis;
		}

		public X setColour(float aRed1, float aGreen1, float aBlue1, float aRed2, float aGreen2, float aBlue2,
				float aRed3, float aGreen3, float aBlue3) {
			final int offset = vertexOffsetOfCurrentGeometry * COLOUR_PARTS_PER_VERTEX;

			colours[offset] = (int) (aRed1 * (1 << 16));
			colours[offset + 1] = (int) (aGreen1 * (1 << 16));
			colours[offset + 2] = (int) (aBlue1 * (1 << 16));

			colours[offset + 3] = (int) (aRed2 * (1 << 16));
			colours[offset + 4] = (int) (aGreen2 * (1 << 16));
			colours[offset + 5] = (int) (aBlue2 * (1 << 16));

			colours[offset + 6] = (int) (aRed3 * (1 << 16));
			colours[offset + 7] = (int) (aGreen3 * (1 << 16));
			colours[offset + 8] = (int) (aBlue3 * (1 << 16));

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
			final int offset = vertexOffsetOfCurrentGeometry * TEXTURE_COORDINATES_PER_VERTEX;

			final int u1 = (int) (aU * (1 << 16));
			final int v1 = (int) (aV * (1 << 16));
			final int u2 = (int) ((aU + aUWidth) * (1 << 16));
			final int v2 = (int) ((aV + aVHeight) * (1 << 16));

			// first triangle of rectangle
			textureCoordinates[offset] = u1;
			textureCoordinates[offset + 1] = v1;

			textureCoordinates[offset + 2] = u2;
			textureCoordinates[offset + 3] = v1;

			textureCoordinates[offset + 4] = u2;
			textureCoordinates[offset + 5] = v2;

			// second triangle of rectangle
			textureCoordinates[offset + 6] = u2;
			textureCoordinates[offset + 7] = v2;

			textureCoordinates[offset + 8] = u1;
			textureCoordinates[offset + 9] = v2;

			textureCoordinates[offset + 10] = u1;
			textureCoordinates[offset + 11] = v1;

			@SuppressWarnings("unchecked")
			X typeSafeThis = (X) this;

			return typeSafeThis;
		}

		public X setColour(float aRed1, float aGreen1, float aBlue1, float aRed2, float aGreen2, float aBlue2,
				float aRed3, float aGreen3, float aBlue3, float aRed4, float aGreen4, float aBlue4) {
			final int offset = vertexOffsetOfCurrentGeometry * COLOUR_PARTS_PER_VERTEX;

			// first triangle of rectangle
			colours[offset] = (int) (aRed1 * (1 << 16));
			colours[offset + 1] = (int) (aGreen1 * (1 << 16));
			colours[offset + 2] = (int) (aBlue1 * (1 << 16));

			colours[offset + 3] = (int) (aRed2 * (1 << 16));
			colours[offset + 4] = (int) (aGreen2 * (1 << 16));
			colours[offset + 5] = (int) (aBlue2 * (1 << 16));

			colours[offset + 6] = (int) (aRed3 * (1 << 16));
			colours[offset + 7] = (int) (aGreen3 * (1 << 16));
			colours[offset + 8] = (int) (aBlue3 * (1 << 16));

			// second triangle of rectangle
			colours[offset + 9] = (int) (aRed3 * (1 << 16));
			colours[offset + 10] = (int) (aGreen3 * (1 << 16));
			colours[offset + 11] = (int) (aBlue3 * (1 << 16));

			colours[offset + 12] = (int) (aRed2 * (1 << 16));
			colours[offset + 13] = (int) (aGreen2 * (1 << 16));
			colours[offset + 14] = (int) (aBlue2 * (1 << 16));

			colours[offset + 15] = (int) (aRed4 * (1 << 16));
			colours[offset + 16] = (int) (aGreen4 * (1 << 16));
			colours[offset + 17] = (int) (aBlue4 * (1 << 16));

			@SuppressWarnings("unchecked")
			X typeSafeThis = (X) this;

			return typeSafeThis;
		}
	}

	static final int MODEL_COORDINATES_PER_VERTEX = 3;

	static final int TEXTURE_COORDINATES_PER_VERTEX = 2;

	static final int NORMAL_COMPONENTS_PER_VERTEX = 3;

	static final int COLOUR_PARTS_PER_VERTEX = 3;

	private static final int VERTICES_PER_TRIANGLE = 3;

	private static final int TRIANGLES_PER_RECTANGLE = 2;

	private static final int NO_MODE = -1;

	private static int currentMode = NO_MODE;

	private final Stack<Integer> geometryStartVertexStack = new Stack<Integer>();

	@SuppressWarnings("unchecked")
	private final T triangle3D = (T) new Triangle3D<Object>();

	@SuppressWarnings("unchecked")
	private final R rectangle2D = (R) new Rectangle2D<Object>();

	int[] modelCoordinates;

	int[] textureCoordinates;

	int[] normalComponents;

	int[] colours;

	int vertexOffsetOfCurrentGeometry;

	int vertexOffsetOfNextGeometry;

	IntBuffer modelCoordinatesAsBuffer;

	IntBuffer textureCoordinatesAsBuffer;

	IntBuffer normalsAsBuffer;

	IntBuffer coloursAsBuffer;

	boolean complete;

	/**
	 * @param aUsesTexturesCoordinates
	 *            indicates if this geometries will include textures.
	 * @param aUsesNormals
	 *            indicates if this geometries will include normalComponents.
	 * @param aUsesColours
	 *            indicates if this geometries will include colours.
	 */
	public OpenGLGeometryBuilderImpl(final boolean aUsesTexturesCoordinates, final boolean aUsesNormals,
			final boolean aUsesColours, int aNumberOfVertices) {
		super(aUsesTexturesCoordinates, aUsesNormals, aUsesColours);

		modelCoordinates = new int[MODEL_COORDINATES_PER_VERTEX * aNumberOfVertices];

		if (aUsesTexturesCoordinates) {
			textureCoordinates = new int[TEXTURE_COORDINATES_PER_VERTEX * aNumberOfVertices];
		}

		if (aUsesNormals) {
			normalComponents = new int[NORMAL_COMPONENTS_PER_VERTEX * aNumberOfVertices];
		}

		if (aUsesColours) {
			colours = new int[COLOUR_PARTS_PER_VERTEX * aNumberOfVertices];
		}
	}

	/**
	 * Adds a 3D triangle to the current geometry. Uses mode GL_TRIANGLES.
	 * 
	 * @return A Triangle3D, which permits adding textures, colour and normalComponents as per the configuration of the
	 *         OpenGLGeometryBuilder
	 */
	@Override
	public T add3DTriangle(float anX1, float aY1, float aZ1, float anX2, float aY2, float aZ2, float anX3, float aY3,
			float aZ3) {
		vertexOffsetOfCurrentGeometry = vertexOffsetOfNextGeometry;
		vertexOffsetOfNextGeometry += VERTICES_PER_TRIANGLE;

		checkCurrentGeometryAndCompleteAndCheckAndSetMode(GL10.GL_TRIANGLES);

		updateArraySizesIfNecessary();

		final int offset = vertexOffsetOfCurrentGeometry * MODEL_COORDINATES_PER_VERTEX;

		modelCoordinates[offset] = (int) (anX1 * (1 << 16));
		modelCoordinates[offset + 1] = (int) (aY1 * (1 << 16));
		modelCoordinates[offset + 2] = (int) (aZ1 * (1 << 16));

		modelCoordinates[offset + 3] = (int) (anX2 * (1 << 16));
		modelCoordinates[offset + 4] = (int) (aY2 * (1 << 16));
		modelCoordinates[offset + 5] = (int) (aZ2 * (1 << 16));

		modelCoordinates[offset + 6] = (int) (anX3 * (1 << 16));
		modelCoordinates[offset + 7] = (int) (aY3 * (1 << 16));
		modelCoordinates[offset + 8] = (int) (aZ3 * (1 << 16));

		return triangle3D;
	}

	/**
	 * Adds a 2D (z = 0) upright rectangle to the current geometry. Uses mode GL_TRIANGLES.
	 * 
	 * @return A Rectangle2D, which permits adding textures and colour as per the configuration of the
	 *         OpenGLGeometryBuilder
	 */
	@Override
	public R add2DRectangle(float anX, float aY, float aWidth, float aHeight) {
		vertexOffsetOfCurrentGeometry = vertexOffsetOfNextGeometry;
		vertexOffsetOfNextGeometry += VERTICES_PER_TRIANGLE * TRIANGLES_PER_RECTANGLE;

		checkCurrentGeometryAndCompleteAndCheckAndSetMode(GL10.GL_TRIANGLES);

		updateArraySizesIfNecessary();

		final int offset = vertexOffsetOfCurrentGeometry * MODEL_COORDINATES_PER_VERTEX;

		// triangle 1
		modelCoordinates[offset] = (int) (anX * (1 << 16));
		modelCoordinates[offset + 1] = (int) (aY * (1 << 16));
		modelCoordinates[offset + 2] = 0;

		modelCoordinates[offset + 3] = (int) ((anX + aWidth) * (1 << 16));
		modelCoordinates[offset + 4] = (int) (aY * (1 << 16));
		modelCoordinates[offset + 5] = 0;

		modelCoordinates[offset + 6] = (int) ((anX + aWidth) * (1 << 16));
		modelCoordinates[offset + 7] = (int) ((aY + aHeight) * (1 << 16));
		modelCoordinates[offset + 8] = 0;

		// triangle 2
		modelCoordinates[offset + 9] = (int) ((anX + aWidth) * (1 << 16));
		modelCoordinates[offset + 10] = (int) ((aY + aHeight) * (1 << 16));
		modelCoordinates[offset + 11] = 0;

		modelCoordinates[offset + 12] = (int) (anX * (1 << 16));
		modelCoordinates[offset + 13] = (int) ((aY + aHeight) * (1 << 16));
		modelCoordinates[offset + 14] = 0;

		modelCoordinates[offset + 15] = (int) (anX * (1 << 16));
		modelCoordinates[offset + 16] = (int) (aY * (1 << 16));
		modelCoordinates[offset + 17] = 0;

		return rectangle2D;
	}

	private void updateArraySizesIfNecessary() {
		if (modelCoordinates.length < vertexOffsetOfNextGeometry * MODEL_COORDINATES_PER_VERTEX) {
			modelCoordinates = extend(modelCoordinates);
			if (usesTextureCoordinates) {
				textureCoordinates = extend(textureCoordinates);
			}
			if (usesColours) {
				colours = extend(colours);
			}
			if (usesNormals) {
				normalComponents = extend(normalComponents);
			}
		}
	}

	private int[] extend(int[] anArray) {
		final int[] newArray = new int[anArray.length * 4];
		System.arraycopy(anArray, 0, newArray, 0, anArray.length);
		return newArray;
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

		int firstVertexOffset = vertexOffsetOfNextGeometry;
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
		final int numberOfVertices = vertexOffsetOfNextGeometry - firstVertexOffset;
		final OpenGLGeometry openGLGeometry = new OpenGLGeometry(currentMode, firstVertexOffset, numberOfVertices, this);

		// if the stack is empty, then clear the current mode: the next geometry can use a different mode
		if (geometryStartVertexStack.isEmpty()) {
			currentMode = NO_MODE;
		}

		return openGLGeometry;
	}

	/**
	 * Completes the use of this builder for building. Must be called before using any of the OpenGLGeometry created by
	 * this object.
	 */
	private void complete() {
		// TODO allow the client to choose interleaved arrays or not. interleaved arrays are better for updating the
		// model coordinates, normals, colour and textures coordinates all at once, but non-interleaved are better for
		// changing only one aspect of a geometry at a time, such as during texture animations on fixed models
		// coordinates
		if (!geometryStartVertexStack.isEmpty()) {
			throw new IllegalStateException("Cannot complete until all started geometries are ended");
		}

		modelCoordinatesAsBuffer = createBuffer(modelCoordinates);
		modelCoordinates = null;

		if (usesTextureCoordinates) {
			textureCoordinatesAsBuffer = createBuffer(textureCoordinates);
		}
		textureCoordinates = null;

		if (usesNormals) {
			normalsAsBuffer = createBuffer(normalComponents);
		}
		normalComponents = null;

		if (usesColours) {
			coloursAsBuffer = createBuffer(colours);
		}
		colours = null;

		complete = true;
	}

	/**
	 * Creates a direct IntBuffer (in native byte order) and populates it with fixed point integers from a given list of
	 * Floats.
	 */
	private IntBuffer createBuffer(final int[] anInts) {
		// create a direct byte buffer in native byte order
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(anInts.length * Float.SIZE / Byte.SIZE);
		byteBuffer.order(ByteOrder.nativeOrder());
		final IntBuffer byteBufferAsIntBuffer = byteBuffer.asIntBuffer();

		byteBufferAsIntBuffer.put(anInts);

		// point the buffer back to the beginning
		byteBufferAsIntBuffer.position(0);

		return byteBufferAsIntBuffer;
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
