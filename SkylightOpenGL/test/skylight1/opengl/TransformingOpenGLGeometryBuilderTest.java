package skylight1.opengl;

import junit.framework.Assert;

import org.junit.Test;

import skylight1.opengl.GeometryBuilder.NormalizableTriangle3D;
import skylight1.opengl.GeometryBuilder.TexturableRectangle2D;
import skylight1.opengl.GeometryBuilder.TexturableTriangle3D;
import android.opengl.Matrix;

public class TransformingOpenGLGeometryBuilderTest {

	private final class Mock implements GeometryBuilder<TexturableTriangle3D<NormalizableTriangle3D<Object>>, TexturableRectangle2D<Object>> {
		float x1;

		float y1;

		float z1;

		float x2;

		float y2;

		float z2;

		float x3;

		float y3;

		float z3;

		@Override
		public TexturableRectangle2D<Object> add2DRectangle(float aAnX, float aY, float aWidth, float aHeight) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public TexturableTriangle3D<NormalizableTriangle3D<Object>> add3DTriangle(float anX1, float aY1, float aZ1, float anX2, float aY2, float aZ2,
				float anX3, float aY3, float aZ3) {

			x1 = anX1;
			y1 = aY1;
			z1 = aZ1;

			x2 = anX2;
			y2 = aY2;
			z2 = aZ2;

			x3 = anX3;
			y3 = aY3;
			z3 = aZ3;

			return null;
		}
	}

	@Test
	public void testAdd3DTriangleWithIdentityTransformation() {
		final Mock mock = new Mock();

		float[] modelTransform = new float[16];

		Matrix.setIdentityM(modelTransform, 0);

		final TransformingGeometryBuilder<TexturableTriangle3D<NormalizableTriangle3D<Object>>, TexturableRectangle2D<Object>> openGLGeometryBuilderImpl =
				new TransformingGeometryBuilder<TexturableTriangle3D<NormalizableTriangle3D<Object>>, TexturableRectangle2D<Object>>(mock, modelTransform, null);

		openGLGeometryBuilderImpl.add3DTriangle(0, 1, 2, 3, 4, 5, 6, 7, 8);

		Assert.assertEquals(mock.x1, 0f);
		Assert.assertEquals(mock.y1, 1f);
		Assert.assertEquals(mock.z1, 2f);
		Assert.assertEquals(mock.x2, 3f);
		Assert.assertEquals(mock.y2, 4f);
		Assert.assertEquals(mock.z2, 5f);
		Assert.assertEquals(mock.x3, 6f);
		Assert.assertEquals(mock.y3, 7f);
		Assert.assertEquals(mock.z3, 8f);
	}
}
