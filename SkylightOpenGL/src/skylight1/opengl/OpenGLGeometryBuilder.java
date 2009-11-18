package skylight1.opengl;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.microedition.khronos.opengles.GL10;

public class OpenGLGeometryBuilder {
	private static class Geometry {
		int firstVertexOffset;

		int mode;
	}
	
	private static final int MODEL_COORDINATES_PER_VERTEX = 3;

	// private static final int TEXTURE_COORDINATES_PER_VERTEX = 2;

	private List<Float> modelCoordinates = new ArrayList<Float>();

	private List<Float> textureCoordinates = new ArrayList<Float>();

	private List<Float> normals = new ArrayList<Float>();

	private ByteBuffer modelCoordinatesAsByteBuffer;

	private ByteBuffer textureCoordinatesAsByteBuffer;

	private ByteBuffer normalsAsByteBuffer;

	private final Stack<Geometry> geometryStack = new Stack<Geometry>();

	private boolean usesNormals;

	private boolean usesTextureCoordinates;

	private boolean usesColors;

	public OpenGLGeometryBuilder(final boolean aUsesTexturesCoordinates, final boolean aUsesNormals,
			final boolean aUsesColors) {
		usesTextureCoordinates = aUsesTexturesCoordinates;
		usesNormals = aUsesNormals;
		usesColors = aUsesColors;
	}

	public void startGeometry() {
		geometryStack.push(new Geometry());
	}

	public OpenGLGeometry endGeometry() {
		if (geometryStack.isEmpty()) {
			throw new IllegalStateException("calls to endGeometry must match calls to startGeometry");
		}
		Geometry geometry = geometryStack.pop();
		final int numberOfVertices = getVerticesOffset() - geometry.firstVertexOffset;
		return new OpenGLGeometry(geometry.mode, geometry.firstVertexOffset, numberOfVertices);
	}

	private void checkCurrentGeometryAndCheckAndSetMode(int aMode) {
		if (geometryStack.isEmpty()) {
			throw new IllegalStateException(
					"adding points, lines, and shapes must be made between calls to startGeometry and endGeometry");
		}

		final Geometry currentGeometry = geometryStack.peek();
		final int currentMode = currentGeometry.mode;
		if (currentMode == 0) {
			currentGeometry.mode = aMode;
		} else if (currentMode != aMode) {
			// TODO show textual values for modes
			throw new IllegalStateException(
					String
							.format(
									"cannot change the mode (point, line, triangle, triangle strip, or triangle fan) within a geometry; current is %d, new is %d",
									currentMode, aMode));
		}
	}

	public void add3DTriangleMTN(float anX1, float aY1, float aZ1, float aU1, float aV1, float aNormalX1,
			float aNormalY1, float aNormalZ1, float anX2, float aY2, float aZ2, float aU2, float aV2, float aNormalX2,
			float aNormalY2, float aNormalZ2, float anX3, float aY3, float aZ3, float aU3, float aV3, float aNormalX3,
			float aNormalY3, float aNormalZ3) {
		checkCurrentGeometryAndCheckAndSetMode(GL10.GL_TRIANGLES);

		addVertex(anX1, aY1, aZ1, aU1, aV1, aNormalX1, aNormalY1, aNormalZ1);
		addVertex(anX2, aY2, aZ2, aU2, aV2, aNormalX2, aNormalY2, aNormalZ2);
		addVertex(anX3, aY3, aZ3, aU3, aV3, aNormalX3, aNormalY3, aNormalZ3);
	}

	public void add2DRectangle(float anX, float aY, float aWidth, float aHeight, float aTextureX, float aTextureY,
			float aTextureWidth, float aTextureHeight) {

		checkCurrentGeometryAndCheckAndSetMode(GL10.GL_TRIANGLES);

		if (modelCoordinates == null) {
			throw new IllegalStateException("Cannot change geometry after the buffers have been retrieved");
		}

		// first triangle of square
		modelCoordinates.add(anX);
		modelCoordinates.add(aY);
		modelCoordinates.add(0f);

		textureCoordinates.add(aTextureX);
		textureCoordinates.add(aTextureY);

		modelCoordinates.add(anX + aWidth);
		modelCoordinates.add(aY);
		modelCoordinates.add(0f);

		textureCoordinates.add(aTextureX + aTextureWidth);
		textureCoordinates.add(aTextureY);

		modelCoordinates.add(anX + aWidth);
		modelCoordinates.add(aY + aHeight);
		modelCoordinates.add(0f);

		textureCoordinates.add(aTextureX + aTextureWidth);
		textureCoordinates.add(aTextureY + aTextureHeight);

		// second triangle of square
		modelCoordinates.add(anX + aWidth);
		modelCoordinates.add(aY + aHeight);
		modelCoordinates.add(0f);

		textureCoordinates.add(aTextureX + aTextureWidth);
		textureCoordinates.add(aTextureY + aTextureHeight);

		modelCoordinates.add(anX);
		modelCoordinates.add(aY + aHeight);
		modelCoordinates.add(0f);

		textureCoordinates.add(aTextureX);
		textureCoordinates.add(aTextureY + aTextureHeight);

		modelCoordinates.add(anX);
		modelCoordinates.add(aY);
		modelCoordinates.add(0f);

		textureCoordinates.add(aTextureX);
		textureCoordinates.add(aTextureY);
	}

	public void complete() {
		modelCoordinatesAsByteBuffer = ByteBufferFactory.createBuffer(modelCoordinates);
		textureCoordinatesAsByteBuffer = ByteBufferFactory.createBuffer(textureCoordinates);
		normalsAsByteBuffer = ByteBufferFactory.createBuffer(normals);
	}

	public void enable(GL10 aGL10) {
		aGL10.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		aGL10.glVertexPointer(3, GL10.GL_FLOAT, 0, modelCoordinatesAsByteBuffer);
		if (usesTextureCoordinates) {
			aGL10.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			aGL10.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureCoordinatesAsByteBuffer);
		} else {
			aGL10.glDisable(GL10.GL_TEXTURE_COORD_ARRAY);
		}
		if (usesNormals) {
			aGL10.glEnableClientState(GL10.GL_NORMAL_ARRAY);
			aGL10.glNormalPointer(GL10.GL_FLOAT, 0, normalsAsByteBuffer);
		} else {
			aGL10.glDisable(GL10.GL_NORMAL_ARRAY);
		}
		// TODO colors
	}

	private void addVertex(float anX, float aY, float aZ, float aU, float aV, float aNormalX1, float aNormalY1,
			float aNormalZ1) {
		modelCoordinates.add(anX);
		modelCoordinates.add(aY);
		modelCoordinates.add(aZ);

		textureCoordinates.add(aU);
		textureCoordinates.add(aV);

		normals.add(aNormalX1);
		normals.add(aNormalY1);
		normals.add(aNormalZ1);
	}

	private int getVerticesOffset() {
		return (modelCoordinates != null ? modelCoordinates.size() : modelCoordinatesAsByteBuffer.capacity()
				/ Float.SIZE * Byte.SIZE)
				/ MODEL_COORDINATES_PER_VERTEX;
	}
}
