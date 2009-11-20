package skylight1.opengl;

import skylight1.opengl.OpenGLGeometryBuilder.ColourableRectangle2D;
import skylight1.opengl.OpenGLGeometryBuilder.ColourableTriangle3D;
import skylight1.opengl.OpenGLGeometryBuilder.NormalizableTriangle3D;
import skylight1.opengl.OpenGLGeometryBuilder.TexturableRectangle2D;
import skylight1.opengl.OpenGLGeometryBuilder.TexturableTriangle3D;

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

	public static void test() {
		createTexturableColourableNormalizable().add3DTriangle(0, 0, 0, 0, 0, 0, 0, 0, 0).setTextureCoordinates(0, 0,
				0, 0, 0, 0).setColour(0, 0, 0, 0, 0, 0, 0, 0, 0).setNormal(0, 0, 0, 0, 0, 0, 0, 0, 0);
	}
}
