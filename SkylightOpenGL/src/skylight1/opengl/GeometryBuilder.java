package skylight1.opengl;

/**
 * Encapsulates the construction of OpenGLGeometry objects.
 */
public interface GeometryBuilder<T, R> {
	/**
	 * Interfaces that permits the addition of texture, normals, and colours to a 3D triangle.
	 */
	interface TexturableTriangle3D<X> {
		X setTextureCoordinates(float aU1, float aV1, float aU2, float aV2, float aU3, float aV3);
	}

	interface NormalizableTriangle3D<X> {
		X setNormal(float aNormalX1, float aNormalY1, float aNormalZ1, float aNormalX2, float aNormalY2,
				float aNormalZ2, float aNormalX3, float aNormalY3, float aNormalZ3);
	}

	interface ColourableTriangle3D<X> {
		X setColour(float aRed1, float aGreen1, float aBlue1, float aRed2, float aGreen2, float aBlue2, float aRed3,
				float aGreen3, float aBlue3);
	}

	/**
	 * Interface that permits the addition of texture and colours to a 2D rectangle.
	 */
	interface TexturableRectangle2D<X> {
		X setTextureCoordinates(float aU, float aV, float aUWidth, float aVHeight);
	}

	interface ColourableRectangle2D<X> {
		X setColour(float aRed1, float aGreen1, float aBlue1, float aRed2, float aGreen2, float aBlue2, float aRed3,
				float aGreen3, float aBlue3, float aRed4, float aGreen4, float aBlue4);
	}

	/**
	 * Adds a 3D triangle to the current geometry. Uses mode GL_TRIANGLES.
	 * 
	 * @return A Triangle3D, which permits adding textures, colour and normals as per the configuration of the
	 *         OpenGLGeometryBuilder
	 */
	T add3DTriangle(float anX1, float aY1, float aZ1, float anX2, float aY2, float aZ2, float anX3, float aY3, float aZ3);

	/**
	 * Adds a 2D (z = 0) upright rectangle to the current geometry. Uses mode GL_TRIANGLES.
	 * 
	 * @return A Rectangle2D, which permits adding textures and colour as per the configuration of the
	 *         OpenGLGeometryBuilder
	 */
	R add2DRectangle(float anX, float aY, float aWidth, float aHeight);
}
