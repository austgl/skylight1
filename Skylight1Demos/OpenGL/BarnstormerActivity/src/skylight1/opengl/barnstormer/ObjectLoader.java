package skylight1.opengl.barnstormer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ObjectLoader {
	private static final String FLOAT = "(-?\\d+\\.\\d+)";

	private static final String INTEGER = "(\\d+)";

	private static class Vertex {
		Float x, y, z;

		public Vertex(Float x, Float y, Float z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}

	private static class TexturePoint {
		Float x, y;

		public TexturePoint(Float x, Float y) {
			this.x = x;
			this.y = y;
		}
	}

	private static class Normal {
		Float x, y, z;

		public Normal(Float x, Float y, Float z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}

	private static class Face {
		VertexIndexes v1, v2, v3;

		public Face(VertexIndexes v1, VertexIndexes v2, VertexIndexes v3) {
			this.v1 = v1;
			this.v2 = v2;
			this.v3 = v3;
		}
	}

	private static class VertexIndexes {
		int coordinatesIndex, texturesCoordinatesIndex, normalsIndex;

		public VertexIndexes(int coordinatesIndex, int texturesCoordinatesIndex, int normalsIndex) {
			this.coordinatesIndex = coordinatesIndex;
			this.texturesCoordinatesIndex = texturesCoordinatesIndex;
			this.normalsIndex = normalsIndex;
		}
	}

	private List<Vertex> vertices = new ArrayList<Vertex>();

	private List<TexturePoint> textures = new ArrayList<TexturePoint>();

	private List<Normal> normals = new ArrayList<Normal>();

	private List<Face> faces = new ArrayList<Face>();

	private List<Float> verticesBuffer = new ArrayList<Float>();

	private List<Float> textureBuffer = new ArrayList<Float>();

	private List<Float> normalsBuffer = new ArrayList<Float>();

	private boolean buffersMade;

	public ObjectLoader(final InputStream anInputStream) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(anInputStream));
		String line;
		Pattern pattern = Pattern.compile(String.format(
				"^(?:v %s %s %s|vt %s %s|vn %s %s %s|f %s/%s/%s %s/%s/%s %s/%s/%s)$", FLOAT, FLOAT, FLOAT, FLOAT,
				FLOAT, FLOAT, FLOAT, FLOAT, INTEGER, INTEGER, INTEGER, INTEGER, INTEGER, INTEGER, INTEGER, INTEGER,
				INTEGER));
		while ((line = br.readLine()) != null) {
			Matcher matcher = pattern.matcher(line);
			// Log.i(ObjectLoader.class.getName(), String.format("parsing %s, found %b", line, matcher.matches()));

			if (!matcher.find()) {
				continue;
			}

			if (matcher.group(1) != null) {
				vertices.add(new Vertex(Float.parseFloat(matcher.group(1)), Float.parseFloat(matcher.group(2)), Float
						.parseFloat(matcher.group(3))));
			} else if (matcher.group(4) != null) {
				textures.add(new TexturePoint(Float.parseFloat(matcher.group(4)), Float.parseFloat(matcher.group(5))));
			} else if (matcher.group(6) != null) {
				normals.add(new Normal(Float.parseFloat(matcher.group(6)), Float.parseFloat(matcher.group(7)), Float
						.parseFloat(matcher.group(8))));
			} else if (matcher.group(9) != null) {
				final VertexIndexes v1 = new VertexIndexes(Integer.parseInt(matcher.group(9)), Integer.parseInt(matcher
						.group(10)), Integer.parseInt(matcher.group(11)));
				final VertexIndexes v2 = new VertexIndexes(Integer.parseInt(matcher.group(12)), Integer
						.parseInt(matcher.group(13)), Integer.parseInt(matcher.group(14)));
				final VertexIndexes v3 = new VertexIndexes(Integer.parseInt(matcher.group(15)), Integer
						.parseInt(matcher.group(16)), Integer.parseInt(matcher.group(17)));
				faces.add(new Face(v1, v2, v3));
			}
		}
	}

	public ByteBuffer getVerticesBuffer() {
		makeBuffers();
		return ByteBufferFactory.createBuffer(verticesBuffer);
	}

	public ByteBuffer getTextureBuffer() {
		makeBuffers();
		return ByteBufferFactory.createBuffer(textureBuffer);
	}

	public ByteBuffer getNormalsBuffer() {
		makeBuffers();
		return ByteBufferFactory.createBuffer(normalsBuffer);
	}

	private void makeBuffers() {
		if (buffersMade)
			return;

		for (int f = 0; f < faces.size(); f++) {
			Face face = faces.get(f);
			addVertex(face.v1);
			addVertex(face.v2);
			addVertex(face.v3);
		}

		buffersMade = true;
	}

	private void addVertex(VertexIndexes vertexIndexes) {
		final int coordinatesIndex = vertexIndexes.coordinatesIndex - 1;
		verticesBuffer.add(vertices.get(coordinatesIndex).x);
		verticesBuffer.add(vertices.get(coordinatesIndex).y);
		verticesBuffer.add(vertices.get(coordinatesIndex).z);

		final int normalsIndex = vertexIndexes.normalsIndex - 1;
		normalsBuffer.add(normals.get(normalsIndex).x);
		normalsBuffer.add(normals.get(normalsIndex).y);
		normalsBuffer.add(normals.get(normalsIndex).z);

		final int textureCoordinatesIndex = vertexIndexes.texturesCoordinatesIndex - 1;
		textureBuffer.add(textures.get(textureCoordinatesIndex).x);
		textureBuffer.add(1f - textures.get(textureCoordinatesIndex).y);
	}
}
