package skylight1.opengl;

/**
 * Encapsulates the construction of OpenGLGeometry objects.
 */
abstract class GeometryBuilderImpl<T, R> implements GeometryBuilder<T, R> {
	final boolean usesNormals;

	final boolean usesTextureCoordinates;

	final boolean usesColours;

	/**
	 * @param aUsesTexturesCoordinates
	 *            indicates if this geometries will include textures.
	 * @param aUsesNormals
	 *            indicates if this geometries will include normals.
	 * @param aUsesColours
	 *            indicates if this geometries will include colours.
	 */
	public GeometryBuilderImpl(final boolean aUsesTexturesCoordinates, final boolean aUsesNormals,
			final boolean aUsesColours) {
		usesTextureCoordinates = aUsesTexturesCoordinates;
		usesNormals = aUsesNormals;
		usesColours = aUsesColours;
	}

	/**
	 * Adds a 3D triangle to the current geometry. Uses mode GL_TRIANGLES.
	 * 
	 * @return A Triangle3D, which permits adding textures, colour and normals as per the configuration of the
	 *         OpenGLGeometryBuilder
	 */
	abstract public T add3DTriangle(float anX1, float aY1, float aZ1, float anX2, float aY2, float aZ2, float anX3,
			float aY3, float aZ3);

	/**
	 * Adds a 2D (z = 0) upright rectangle to the current geometry. Uses mode GL_TRIANGLES.
	 * 
	 * @return A Rectangle2D, which permits adding textures and colour as per the configuration of the
	 *         OpenGLGeometryBuilder
	 */
	abstract public R add2DRectangle(float anX, float aY, float aWidth, float aHeight);
}
