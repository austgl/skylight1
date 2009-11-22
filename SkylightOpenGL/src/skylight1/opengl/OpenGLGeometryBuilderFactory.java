package skylight1.opengl;

import skylight1.opengl.GeometryBuilder.ColourableRectangle2D;
import skylight1.opengl.GeometryBuilder.ColourableTriangle3D;
import skylight1.opengl.GeometryBuilder.NormalizableTriangle3D;
import skylight1.opengl.GeometryBuilder.TexturableRectangle2D;
import skylight1.opengl.GeometryBuilder.TexturableTriangle3D;

public class OpenGLGeometryBuilderFactory {
	public static interface Void {
	}

	public static OpenGLGeometryBuilder<TexturableTriangle3D<ColourableTriangle3D<NormalizableTriangle3D<Void>>>, TexturableRectangle2D<ColourableRectangle2D<Void>>> createTexturableColourableNormalizable() {
		return new OpenGLGeometryBuilderImpl(true, true, true);
	}

	public static OpenGLGeometryBuilder<TexturableTriangle3D<NormalizableTriangle3D<Void>>, TexturableRectangle2D<Void>> createTexturableNormalizable() {
		return new OpenGLGeometryBuilderImpl(true, true, false);
	}

	public static OpenGLGeometryBuilder<TexturableTriangle3D<ColourableTriangle3D<Void>>, TexturableRectangle2D<ColourableRectangle2D<Void>>> createTexturableColourable() {
		return new OpenGLGeometryBuilderImpl(true, false, true);
	}

	public static OpenGLGeometryBuilder<TexturableTriangle3D<ColourableTriangle3D<Void>>, TexturableRectangle2D<Void>> createTexturable() {
		return new OpenGLGeometryBuilderImpl(true, false, false);
	}

	public static OpenGLGeometryBuilder<ColourableTriangle3D<NormalizableTriangle3D<Void>>, ColourableRectangle2D<Void>> createColourableNormalizable() {
		return new OpenGLGeometryBuilderImpl(false, true, true);
	}

	public static OpenGLGeometryBuilder<NormalizableTriangle3D<Void>, Void> createNormalizable() {
		return new OpenGLGeometryBuilderImpl(false, true, false);
	}

	public static OpenGLGeometryBuilder<ColourableTriangle3D<Void>, ColourableRectangle2D<Void>> createColourable() {
		return new OpenGLGeometryBuilderImpl(false, false, true);
	}

	public static OpenGLGeometryBuilder<Void, Void> create() {
		return new OpenGLGeometryBuilderImpl(false, false, false);
	}

	public static FastGeometryBuilder<TexturableTriangle3D<ColourableTriangle3D<Void>>, TexturableRectangle2D<Void>> createFastTexture(
			OpenGLGeometry aGeometry) {
		return new FastGeometryBuilderImpl<TexturableTriangle3D<ColourableTriangle3D<Void>>, TexturableRectangle2D<Void>>(
				true, false, false, aGeometry.numberOfVerticies);
	}
}
