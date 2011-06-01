package skylight1.opengl;

import skylight1.opengl.GeometryBuilder.ColourableRectangle2D;
import skylight1.opengl.GeometryBuilder.ColourableTriangle3D;
import skylight1.opengl.GeometryBuilder.NormalizableTriangle3D;
import skylight1.opengl.GeometryBuilder.TexturableRectangle2D;
import skylight1.opengl.GeometryBuilder.TexturableTriangle3D;

/**
 * Factory methods for creating type-safe OpenGLGeometryBuilders. Each method is of the form
 * create[Texturable][Colourable][Normalizable], where the presence of each of Texturable, Colourable, and Normalizable
 * indicates the GeometryBuilder's abilities.
 */
public class FastGeometryBuilderFactory {
	public static FastGeometryBuilder<TexturableTriangle3D<ColourableTriangle3D<NormalizableTriangle3D<Object>>>, TexturableRectangle2D<ColourableRectangle2D<Object>>> createTexturableColourableNormalizable(
			OpenGLGeometry aGeometry) {
		return new FastGeometryBuilderImpl<TexturableTriangle3D<ColourableTriangle3D<NormalizableTriangle3D<Object>>>, TexturableRectangle2D<ColourableRectangle2D<Object>>>(
				true, true, true, aGeometry != null ? aGeometry.getNumberOfVerticies() : 0);
	}

	public static FastGeometryBuilder<TexturableTriangle3D<NormalizableTriangle3D<Object>>, TexturableRectangle2D<Object>> createTexturableNormalizable(
			OpenGLGeometry aGeometry) {
		return new FastGeometryBuilderImpl<TexturableTriangle3D<NormalizableTriangle3D<Object>>, TexturableRectangle2D<Object>>(
				true, true, false, aGeometry != null ? aGeometry.getNumberOfVerticies() : 0);
	}

	public static FastGeometryBuilder<TexturableTriangle3D<ColourableTriangle3D<Object>>, TexturableRectangle2D<ColourableRectangle2D<Object>>> createTexturableColourable(
			OpenGLGeometry aGeometry) {
		return new FastGeometryBuilderImpl<TexturableTriangle3D<ColourableTriangle3D<Object>>, TexturableRectangle2D<ColourableRectangle2D<Object>>>(
				true, false, true, aGeometry != null ? aGeometry.getNumberOfVerticies() : 0);
	}

	public static FastGeometryBuilder<TexturableTriangle3D<Object>, TexturableRectangle2D<Object>> createTexturable(
			OpenGLGeometry aGeometry) {
		return new FastGeometryBuilderImpl<TexturableTriangle3D<Object>, TexturableRectangle2D<Object>>(true, false,
				false, aGeometry != null ? aGeometry.getNumberOfVerticies() : 0);
	}

	public static FastGeometryBuilder<ColourableTriangle3D<NormalizableTriangle3D<Object>>, ColourableRectangle2D<Object>> createColourableNormalizable(
			OpenGLGeometry aGeometry) {
		return new FastGeometryBuilderImpl<ColourableTriangle3D<NormalizableTriangle3D<Object>>, ColourableRectangle2D<Object>>(
				false, true, true, aGeometry != null ? aGeometry.getNumberOfVerticies() : 0);
	}

	public static FastGeometryBuilder<NormalizableTriangle3D<Object>, Object> createNormalizable(
			OpenGLGeometry aGeometry) {
		return new FastGeometryBuilderImpl<NormalizableTriangle3D<Object>, Object>(false, true, false,
				aGeometry != null ? aGeometry.getNumberOfVerticies() : 0);
	}

	public static FastGeometryBuilder<ColourableTriangle3D<Object>, ColourableRectangle2D<Object>> createColourable(
			OpenGLGeometry aGeometry) {
		return new FastGeometryBuilderImpl<ColourableTriangle3D<Object>, ColourableRectangle2D<Object>>(false, false,
				true, aGeometry != null ? aGeometry.getNumberOfVerticies() : 0);
	}

	public static FastGeometryBuilder<Object, Object> create(OpenGLGeometry aGeometry) {
		return new FastGeometryBuilderImpl<Object, Object>(false, false, false,
				aGeometry != null ? aGeometry.getNumberOfVerticies() : 0);
	}
}
