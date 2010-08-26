package skylight1.opengl;

import android.opengl.Matrix;

/**
 * Encapsulates the construction of OpenGLGeometry objects.
 */
public class TransformingGeometryBuilder<T, R> implements GeometryBuilder<T, R> {
	/**
	 * Inner class that permits the addition of texture, normalComponents, and colours to a 3D triangle.
	 */
	public class Triangle3D<X> implements TexturableTriangle3D<X>, ColourableTriangle3D<X>, NormalizableTriangle3D<X> {
		private Object delegate;

		public void setDelegate(Object aDelegate) {
			delegate = aDelegate;
		}

		public X setTextureCoordinates(float aU1, float aV1, float aU2, float aV2, float aU3, float aV3) {
			if (null != coordinateTransform) {
				// copy coordinates into input vectors
				inputMatrix[0] = aU1;
				inputMatrix[1] = aV1;
				inputMatrix[4] = aU2;
				inputMatrix[5] = aV2;
				inputMatrix[8] = aU3;
				inputMatrix[9] = aV3;

				// multiply by transformation matrix
				Matrix.multiplyMV(outputMatrix, 0, textureTransform, 0, inputMatrix, 0);
				Matrix.multiplyMV(outputMatrix, 4, textureTransform, 0, inputMatrix, 4);
				Matrix.multiplyMV(outputMatrix, 8, textureTransform, 0, inputMatrix, 8);

				// copy the transformed vectors into named variables
				final float u1 = outputMatrix[0];
				final float v1 = outputMatrix[1];
				final float u2 = outputMatrix[4];
				final float v2 = outputMatrix[5];
				final float u3 = outputMatrix[8];
				final float v3 = outputMatrix[9];

				// call wrapped builder
				delegate = ((TexturableTriangle3D<X>) delegate).setTextureCoordinates(u1, v1, u2, v2, u3, v3);
			} else {
				// call wrapped builder
				delegate = ((TexturableTriangle3D<X>) delegate).setTextureCoordinates(aU1, aV1, aU2, aV2, aU3, aV3);
			}

			@SuppressWarnings("unchecked")
			X typeSafeThis = (X) this;

			return typeSafeThis;
		}

		public X setNormal(float aNormalX1, float aNormalY1, float aNormalZ1, float aNormalX2, float aNormalY2, float aNormalZ2, float aNormalX3,
				float aNormalY3, float aNormalZ3) {
			if (null != coordinateTransform) {
				// copy coordinates into input vectors
				inputMatrix[0] = aNormalX1;
				inputMatrix[1] = aNormalY1;
				inputMatrix[2] = aNormalZ1;
				inputMatrix[4] = aNormalX2;
				inputMatrix[5] = aNormalY2;
				inputMatrix[6] = aNormalZ2;
				inputMatrix[8] = aNormalX3;
				inputMatrix[9] = aNormalY3;
				inputMatrix[10] = aNormalZ3;

				// multiply by transformation matrix
				Matrix.multiplyMV(outputMatrix, 0, coordinateTransform, 0, inputMatrix, 0);
				Matrix.multiplyMV(outputMatrix, 4, coordinateTransform, 0, inputMatrix, 4);
				Matrix.multiplyMV(outputMatrix, 8, coordinateTransform, 0, inputMatrix, 8);

				// copy the transformed vectors into named variables
				final float resultX1 = outputMatrix[0];
				final float resultY1 = outputMatrix[1];
				final float resultZ1 = outputMatrix[2];
				final float resultX2 = outputMatrix[4];
				final float resultY2 = outputMatrix[5];
				final float resultZ2 = outputMatrix[6];
				final float resultX3 = outputMatrix[8];
				final float resultY3 = outputMatrix[9];
				final float resultZ3 = outputMatrix[10];

				// call wrapped builder
				delegate =
						((NormalizableTriangle3D<X>) delegate)
								.setNormal(resultX1, resultY1, resultZ1, resultX2, resultY2, resultZ2, resultX3, resultY3, resultZ3);
			} else {
				// call wrapped builder
				delegate =
						((NormalizableTriangle3D<X>) delegate)
								.setNormal(aNormalX1, aNormalY1, aNormalZ1, aNormalX2, aNormalY2, aNormalZ2, aNormalX3, aNormalY3, aNormalZ3);
			}

			@SuppressWarnings("unchecked")
			X typeSafeThis = (X) this;

			return typeSafeThis;
		}

		public X setColour(float aRed1, float aGreen1, float aBlue1, float aRed2, float aGreen2, float aBlue2, float aRed3, float aGreen3, float aBlue3) {
			delegate = ((ColourableTriangle3D<X>) delegate).setColour(aRed1, aGreen1, aBlue1, aRed2, aGreen2, aBlue2, aRed3, aGreen3, aBlue3);

			@SuppressWarnings("unchecked")
			X typeSafeThis = (X) this;

			return typeSafeThis;
		}
	}

	/**
	 * Inner class that permits the addition of texture and colours to a 2D rectangle.
	 */
	public class Rectangle2D<X> implements TexturableRectangle2D<X>, ColourableRectangle2D<X> {
		private Object delegate;

		public void setDelegate(Object aDelegate) {
			delegate = aDelegate;
		}

		public X setTextureCoordinates(float aU, float aV, float aUWidth, float aVHeight) {
			if (null != coordinateTransform) {
				// copy coordinates into input vectors
				inputMatrix[0] = aU;
				inputMatrix[1] = aV;
				inputMatrix[4] = aU + aUWidth;
				inputMatrix[5] = aV + aVHeight;

				// multiply by transformation matrix
				Matrix.multiplyMV(outputMatrix, 0, textureTransform, 0, inputMatrix, 0);
				Matrix.multiplyMV(outputMatrix, 4, textureTransform, 0, inputMatrix, 4);

				// copy the transformed vectors into named variables
				final float u = outputMatrix[0];
				final float v = outputMatrix[1];
				final float width = outputMatrix[4] - u;
				final float height = outputMatrix[5] - v;

				// call wrapped builder
				delegate = ((TexturableRectangle2D<X>) delegate).setTextureCoordinates(u, v, width, height);
			} else {
				// call wrapped builder
				delegate = ((TexturableRectangle2D<X>) delegate).setTextureCoordinates(aU, aV, aUWidth, aVHeight);
			}

			@SuppressWarnings("unchecked")
			X typeSafeThis = (X) this;

			return typeSafeThis;
		}

		public X setColour(float aRed1, float aGreen1, float aBlue1, float aRed2, float aGreen2, float aBlue2, float aRed3, float aGreen3, float aBlue3,
				float aRed4, float aGreen4, float aBlue4) {
			delegate =
					((ColourableRectangle2D<X>) delegate)
							.setColour(aRed1, aGreen1, aBlue1, aRed2, aGreen2, aBlue2, aRed3, aGreen3, aBlue3, aRed4, aGreen4, aBlue4);

			@SuppressWarnings("unchecked")
			X typeSafeThis = (X) this;

			return typeSafeThis;
		}
	}

	@SuppressWarnings("unchecked")
	private final Triangle3D<Object> triangle3D = new Triangle3D<Object>();

	@SuppressWarnings("unchecked")
	private final Rectangle2D<Object> rectangle2D = new Rectangle2D<Object>();

	private GeometryBuilder<T, R> openGLGeometryBuilder;

	private float[] coordinateTransform;

	private float[] textureTransform;

	private float[] inputMatrix = new float[12];

	private float[] outputMatrix = new float[12];

	/**
	 * @param aUsesTexturesCoordinates
	 *            indicates if this geometries will include textures.
	 * @param aUsesNormals
	 *            indicates if this geometries will include normalComponents.
	 * @param aUsesColours
	 *            indicates if this geometries will include colours.
	 */
	public TransformingGeometryBuilder(GeometryBuilder<T, R> anGeometryBuilder, float[] aCoordinateTransform, float[] aTextureTransform) {
		openGLGeometryBuilder = anGeometryBuilder;
		coordinateTransform = aCoordinateTransform;
		textureTransform = aTextureTransform;
	}
	
	/**
	 * Adds a 3D triangle to the current geometry. Uses mode GL_TRIANGLES.
	 * 
	 * @return A Triangle3D, which permits adding textures, colour and normalComponents as per the configuration of the
	 *         OpenGLGeometryBuilder
	 */
	@Override
	public T add3DTriangle(final float anX1, final float aY1, final float aZ1, final float anX2, final float aY2, final float aZ2, final float anX3,
			final float aY3, final float aZ3) {

		if (null != coordinateTransform) {
			// copy coordinates into input vectors
			inputMatrix[0] = anX1;
			inputMatrix[1] = aY1;
			inputMatrix[2] = aZ1;
			inputMatrix[4] = anX2;
			inputMatrix[5] = aY2;
			inputMatrix[6] = aZ2;
			inputMatrix[8] = anX3;
			inputMatrix[9] = aY3;
			inputMatrix[10] = aZ3;

			// multiply by transformation matrix
			Matrix.multiplyMV(outputMatrix, 0, coordinateTransform, 0, inputMatrix, 0);
			Matrix.multiplyMV(outputMatrix, 4, coordinateTransform, 0, inputMatrix, 4);
			Matrix.multiplyMV(outputMatrix, 8, coordinateTransform, 0, inputMatrix, 8);

			// copy the transformed vectors into named variables
			final float resultX1 = outputMatrix[0];
			final float resultY1 = outputMatrix[1];
			final float resultZ1 = outputMatrix[2];
			final float resultX2 = outputMatrix[4];
			final float resultY2 = outputMatrix[5];
			final float resultZ2 = outputMatrix[6];
			final float resultX3 = outputMatrix[8];
			final float resultY3 = outputMatrix[9];
			final float resultZ3 = outputMatrix[10];

			// call wrapped builder
			triangle3D.setDelegate(openGLGeometryBuilder
					.add3DTriangle(resultX1, resultY1, resultZ1, resultX2, resultY2, resultZ2, resultX3, resultY3, resultZ3));

			return (T) triangle3D;
		}

		// call wrapped builder
		return openGLGeometryBuilder.add3DTriangle(anX1, aY1, aZ1, anX2, aY2, aZ2, anX3, aY3, aZ3);
	}

	/**
	 * Adds a 2D (z = 0) upright rectangle to the current geometry. Uses mode GL_TRIANGLES.
	 * 
	 * @return A Rectangle2D, which permits adding textures and colour as per the configuration of the
	 *         OpenGLGeometryBuilder
	 */
	@Override
	public R add2DRectangle(float anX, float aY, float aWidth, float aHeight) {

		if (null != coordinateTransform) {
			// copy coordinates into input matrix
			inputMatrix[0] = anX;
			inputMatrix[1] = aY;
			inputMatrix[2] = 0;
			inputMatrix[4] = anX + aWidth;
			inputMatrix[5] = aY + aHeight;
			inputMatrix[6] = 0;

			// multiply by transformation matrix
			Matrix.multiplyMV(outputMatrix, 0, coordinateTransform, 0, inputMatrix, 0);
			Matrix.multiplyMV(outputMatrix, 4, coordinateTransform, 0, inputMatrix, 4);

			// copy the transformed vectors into named variables
			final float resultX1 = outputMatrix[0];
			final float resultY1 = outputMatrix[1];
			final float width = outputMatrix[4] - resultX1;
			final float height = outputMatrix[5] - resultY1;

			// call wrapped builder
			rectangle2D.setDelegate(openGLGeometryBuilder.add2DRectangle(resultX1, resultY1, width, height));

			return (R) rectangle2D;
		}

		// call wrapped builder
		return openGLGeometryBuilder.add2DRectangle(anX, aY, aWidth, aHeight);
	}
}
