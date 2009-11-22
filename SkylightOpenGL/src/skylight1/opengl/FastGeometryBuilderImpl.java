package skylight1.opengl;

/**
 * Encapsulates the construction of OpenGLGeometry objects.
 */
class FastGeometryBuilderImpl<T, R> extends GeometryBuilderImpl<T, R> implements FastGeometryBuilder<T, R> {
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

			return (X) this;
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

			return (X) this;
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

			return (X) this;
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
			textureCoordinates[offset] = u2;
			textureCoordinates[offset + 1] = v2;

			textureCoordinates[offset + 2] = u1;
			textureCoordinates[offset + 3] = v2;

			textureCoordinates[offset + 4] = u1;
			textureCoordinates[offset + 5] = v1;

			return (X) this;
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
			colours[offset] = (int) (aRed3 * (1 << 16));
			colours[offset + 1] = (int) (aGreen3 * (1 << 16));
			colours[offset + 2] = (int) (aBlue3 * (1 << 16));

			colours[offset + 3] = (int) (aRed2 * (1 << 16));
			colours[offset + 4] = (int) (aGreen2 * (1 << 16));
			colours[offset + 5] = (int) (aBlue2 * (1 << 16));

			colours[offset + 6] = (int) (aRed4 * (1 << 16));
			colours[offset + 7] = (int) (aGreen4 * (1 << 16));
			colours[offset + 8] = (int) (aBlue4 * (1 << 16));

			return (X) this;
		}
	}

	static final int MODEL_COORDINATES_PER_VERTEX = 3;

	static final int TEXTURE_COORDINATES_PER_VERTEX = 2;

	static final int NORMAL_COMPONENTS_PER_VERTEX = 3;

	static final int COLOUR_PARTS_PER_VERTEX = 3;

	private static final int VERTICES_PER_TRIANGLE = 3;

	private static final int TRIANGLES_PER_RECTANGLE = 2;

	private final T triangle3D = (T) new Triangle3D();

	private final R rectangle2D = (R) new Rectangle2D();

	int[] modelCoordinates;

	int[] textureCoordinates;

	int[] normalComponents;

	int[] colours;

	int vertexOffsetOfCurrentGeometry;

	int vertexOffsetOfNextGeometry;

	/**
	 * @param aUsesTexturesCoordinates
	 *            indicates if this geometries will include textures.
	 * @param aUsesNormals
	 *            indicates if this geometries will include normalComponents.
	 * @param aUsesColours
	 *            indicates if this geometries will include colours.
	 */
	public FastGeometryBuilderImpl(final boolean aUsesTexturesCoordinates, final boolean aUsesNormals,
			final boolean aUsesColours, int aNumberOfVertices) {
		super(aUsesTexturesCoordinates, aUsesNormals, aUsesColours);

		modelCoordinates = new int[MODEL_COORDINATES_PER_VERTEX * aNumberOfVertices];

		if (aUsesTexturesCoordinates) {
			textureCoordinates = new int[TEXTURE_COORDINATES_PER_VERTEX * aNumberOfVertices];
		}

		if (aUsesNormals) {
			textureCoordinates = new int[NORMAL_COMPONENTS_PER_VERTEX * aNumberOfVertices];
		}

		if (aUsesColours) {
			textureCoordinates = new int[COLOUR_PARTS_PER_VERTEX * aNumberOfVertices];
		}
	}

	/**
	 * Adds a 3D triangle to the current geometry. Uses mode GL_TRIANGLES.
	 * 
	 * @return A Triangle3D, which permits adding textures, colour and normalComponents as per the configuration of the
	 *         OpenGLGeometryBuilder
	 */
	public T add3DTriangle(float anX1, float aY1, float aZ1, float anX2, float aY2, float aZ2, float anX3, float aY3,
			float aZ3) {
		vertexOffsetOfCurrentGeometry = vertexOffsetOfNextGeometry;
		vertexOffsetOfNextGeometry += VERTICES_PER_TRIANGLE;

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
	public R add2DRectangle(float anX, float aY, float aWidth, float aHeight) {
		vertexOffsetOfCurrentGeometry = vertexOffsetOfNextGeometry;
		vertexOffsetOfNextGeometry += VERTICES_PER_TRIANGLE * TRIANGLES_PER_RECTANGLE;

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

	@Override
	public R add2DRectangle() {
		vertexOffsetOfCurrentGeometry = vertexOffsetOfNextGeometry;
		vertexOffsetOfNextGeometry += VERTICES_PER_TRIANGLE * TRIANGLES_PER_RECTANGLE;

		return rectangle2D;
	}

	@Override
	public T add3DTriangle() {
		vertexOffsetOfCurrentGeometry = vertexOffsetOfNextGeometry;
		vertexOffsetOfNextGeometry += VERTICES_PER_TRIANGLE;

		return triangle3D;
	}

	@Override
	public void reset() {
		vertexOffsetOfCurrentGeometry = 0;
		vertexOffsetOfNextGeometry = 0;
	}
}
