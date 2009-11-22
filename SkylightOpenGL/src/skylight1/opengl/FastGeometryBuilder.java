package skylight1.opengl;

/**
 * Encapsulates the construction of OpenGLGeometry objects.
 */
public interface FastGeometryBuilder<T, R> extends GeometryBuilder<T, R> {
	/**
	 * Adds a 3D triangle to the current geometry. Uses mode GL_TRIANGLES.
	 * 
	 * @return A Triangle3D, which permits adding textures, colour and normals as per the configuration of the
	 *         OpenGLGeometryBuilder
	 */
	T add3DTriangle();

	/**
	 * Adds a 2D (z = 0) upright rectangle to the current geometry. Uses mode GL_TRIANGLES.
	 * 
	 * @return A Rectangle2D, which permits adding textures and colour as per the configuration of the
	 *         OpenGLGeometryBuilder
	 */
	R add2DRectangle();

	/**
	 * Resets the builder to its initial state.
	 */
	void reset();
}
