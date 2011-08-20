package skylight1.opengl;

import skylight1.opengl.GeometryBuilder.ColourableRectangle2D;
import skylight1.opengl.GeometryBuilder.ColourableTriangle3D;
import skylight1.opengl.GeometryBuilder.NormalizableTriangle3D;
import skylight1.opengl.GeometryBuilder.TexturableRectangle2D;
import skylight1.opengl.GeometryBuilder.TexturableTriangle3D;

/**
 * Factory methods for creating type-safe FastGeometryBuilders. Each method is of the form
 * create[Texturable][Colourable][Normalizable], where the presence of each of Texturable, Colourable, and Normalizable
 * indicates the GeometryBuilder's abilities.
 */
public class OpenGLGeometryBuilderFactory {
	public static OpenGLGeometryBuilder<TexturableTriangle3D<ColourableTriangle3D<NormalizableTriangle3D<Object>>>, TexturableRectangle2D<ColourableRectangle2D<Object>>> createTexturableColourableNormalizable() {
		return createTexturableColourableNormalizable(4000);
	}

	public static OpenGLGeometryBuilder<TexturableTriangle3D<NormalizableTriangle3D<Object>>, TexturableRectangle2D<Object>> createTexturableNormalizable() {
		return createTexturableNormalizable(4000);
	}

	public static OpenGLGeometryBuilder<TexturableTriangle3D<ColourableTriangle3D<Object>>, TexturableRectangle2D<ColourableRectangle2D<Object>>> createTexturableColourable() {
		return createTexturableColourable(4000);
	}

	public static OpenGLGeometryBuilder<TexturableTriangle3D<Object>, TexturableRectangle2D<Object>> createTexturable() {
		return createTexturable(4000);
	}

	public static OpenGLGeometryBuilder<ColourableTriangle3D<NormalizableTriangle3D<Object>>, ColourableRectangle2D<Object>> createColourableNormalizable() {
		return createColourableNormalizable(4000);
	}

	public static OpenGLGeometryBuilder<NormalizableTriangle3D<Object>, Object> createNormalizable() {
		return createNormalizable(4000);
	}

	public static OpenGLGeometryBuilder<ColourableTriangle3D<Object>, ColourableRectangle2D<Object>> createColourable() {
		return createColourable(4000);
	}

	public static OpenGLGeometryBuilder<Object, Object> create() {
		return create(4000);
	}

	public static OpenGLGeometryBuilder<TexturableTriangle3D<ColourableTriangle3D<NormalizableTriangle3D<Object>>>, TexturableRectangle2D<ColourableRectangle2D<Object>>> createTexturableColourableNormalizable(
			final int anInitialNumberOfVertices) {
		return new OpenGLGeometryBuilderImpl<TexturableTriangle3D<ColourableTriangle3D<NormalizableTriangle3D<Object>>>, TexturableRectangle2D<ColourableRectangle2D<Object>>>(true, true, true, anInitialNumberOfVertices);
	}

	public static OpenGLGeometryBuilder<TexturableTriangle3D<NormalizableTriangle3D<Object>>, TexturableRectangle2D<Object>> createTexturableNormalizable(
			final int anInitialNumberOfVertices) {
		return new OpenGLGeometryBuilderImpl<TexturableTriangle3D<NormalizableTriangle3D<Object>>, TexturableRectangle2D<Object>>(true, true, false, anInitialNumberOfVertices);
	}

	public static OpenGLGeometryBuilder<TexturableTriangle3D<ColourableTriangle3D<Object>>, TexturableRectangle2D<ColourableRectangle2D<Object>>> createTexturableColourable(
			final int anInitialNumberOfVertices) {
		return new OpenGLGeometryBuilderImpl<TexturableTriangle3D<ColourableTriangle3D<Object>>, TexturableRectangle2D<ColourableRectangle2D<Object>>>(true, false, true, anInitialNumberOfVertices);
	}

	public static OpenGLGeometryBuilder<TexturableTriangle3D<Object>, TexturableRectangle2D<Object>> createTexturable(final int anInitialNumberOfVertices) {
		return new OpenGLGeometryBuilderImpl<TexturableTriangle3D<Object>, TexturableRectangle2D<Object>>(true, false, false, anInitialNumberOfVertices);
	}

	public static OpenGLGeometryBuilder<ColourableTriangle3D<NormalizableTriangle3D<Object>>, ColourableRectangle2D<Object>> createColourableNormalizable(
			final int anInitialNumberOfVertices) {
		return new OpenGLGeometryBuilderImpl<ColourableTriangle3D<NormalizableTriangle3D<Object>>, ColourableRectangle2D<Object>>(false, true, true, anInitialNumberOfVertices);
	}

	public static OpenGLGeometryBuilder<NormalizableTriangle3D<Object>, Object> createNormalizable(final int anInitialNumberOfVertices) {
		return new OpenGLGeometryBuilderImpl<NormalizableTriangle3D<Object>, Object>(false, true, false, anInitialNumberOfVertices);
	}

	public static OpenGLGeometryBuilder<ColourableTriangle3D<Object>, ColourableRectangle2D<Object>> createColourable(final int anInitialNumberOfVertices) {
		return new OpenGLGeometryBuilderImpl<ColourableTriangle3D<Object>, ColourableRectangle2D<Object>>(false, false, true, anInitialNumberOfVertices);
	}

	public static OpenGLGeometryBuilder<Object, Object> create(final int anInitialNumberOfVertices) {
		return new OpenGLGeometryBuilderImpl<Object, Object>(false, false, false, anInitialNumberOfVertices);
	}
}
