package skylight1.opengl;

/**
 * Encapsulates the construction of FastGeometry objects.
 */
public interface FastGeometryBuilder<T, R> extends GeometryBuilder<T, R> {
	/**
	 * Adds a 3D triangle to the current geometry..
	 * 
	 * @return A Triangle3D, which permits adding textures, colour and normals as per the configuration of the
	 *         GeometryBuilder
	 */
	T add3DTriangle();

	/**
	 * Adds a 2D (z = 0) upright rectangle to the current geometry..
	 * 
	 * @return A Rectangle2D, which permits adding textures and colour as per the configuration of the GeometryBuilder
	 */
	R add2DRectangle();

	/**
	 * Resets the builder to its initial state. Not necessary for the first use of the object, but required for repeated
	 * use.
	 */
	void reset();
}
