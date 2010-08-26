package skylight1.opengl.files;

import static skylight1.opengl.files.QuickParseUtil.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import skylight1.opengl.GeometryBuilder;
import skylight1.opengl.OpenGLGeometry;
import skylight1.opengl.OpenGLGeometryBuilder;
import android.content.Context;

/**
 * Loads 3D model from a WaveFront OBJ file.
 * 
 */
public class ObjFileLoader {
	private static final String FLOAT = "(-?\\d+\\.\\d+)";

	private static final String INTEGER = "(\\d+)";

	// some inner classes to model the data in the obj file
	private static class ModelCoordinates {
		Float x, y, z;

		public ModelCoordinates(Float anX, Float aY, Float aZ) {
			x = anX;
			y = aY;
			z = aZ;
		}
	}

	private static class TextureCoordinates {
		Float u, v;

		public TextureCoordinates(Float aU, Float aV) {
			u = aU;
			v = aV;
		}
	}

	private static class Normal {
		Float x, y, z;

		public Normal(Float anX, Float aY, Float aZ) {
			x = anX;
			y = aY;
			z = aZ;
		}
	}

	private static class Vertex {
		ModelCoordinates modelCoordinates;

		TextureCoordinates textureCoordinates;

		Normal normal;

		public Vertex(ModelCoordinates aModelCoordinates, TextureCoordinates aTextureCoordinates, Normal aNormal) {
			modelCoordinates = aModelCoordinates;
			textureCoordinates = aTextureCoordinates;
			normal = aNormal;
		}
	}

	private static class Face {
		Vertex v1, v2, v3;

		public Face(Vertex aV1, Vertex aV2, Vertex aV3) {
			v1 = aV1;
			v2 = aV2;
			v3 = aV3;
		}
	}

	private List<Face> faces = new ArrayList<Face>();

	public ObjFileLoader(final Context aContext, final int aRawResourceId) throws IOException {
		final InputStream inputStream = aContext.getResources().openRawResource(aRawResourceId);
		try {
			loadModelFromInputStream(inputStream);
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				// No harm, no foul
			}
		}
	}

	public ObjFileLoader(final InputStream anInputStream) throws IOException {
		loadModelFromInputStream(anInputStream);
	}

	private void loadModelFromInputStream(final InputStream anInputStream) throws IOException {
		final List<ModelCoordinates> modelCoordinates = new ArrayList<ModelCoordinates>();

		final List<TextureCoordinates> texturesCoordinates = new ArrayList<TextureCoordinates>();

		final List<Normal> normals = new ArrayList<Normal>();

		BufferedReader br = new BufferedReader(new InputStreamReader(anInputStream));
		String line;
		Pattern pattern = Pattern.compile(String.format(
				"^(?:v %s %s %s|vt %s %s|vn %s %s %s|f %s/%s/%s %s/%s/%s %s/%s/%s)$", FLOAT, FLOAT, FLOAT, FLOAT,
				FLOAT, FLOAT, FLOAT, FLOAT, INTEGER, INTEGER, INTEGER, INTEGER, INTEGER, INTEGER, INTEGER, INTEGER,
				INTEGER));
		Matcher matcher = pattern.matcher("");
		while ((line = br.readLine()) != null) {
			matcher.reset(line);

			if (!matcher.find()) {
				continue;
			}

			if (matcher.group(1) != null) {
				modelCoordinates.add(new ModelCoordinates(parseFloat(matcher.group(1)), parseFloat(matcher
						.group(2)), parseFloat(matcher.group(3))));
			} else if (matcher.group(4) != null) {
				texturesCoordinates.add(new TextureCoordinates(parseFloat(matcher.group(4)),
						1f - parseFloat(matcher.group(5))));
			} else if (matcher.group(6) != null) {
				normals.add(new Normal(parseFloat(matcher.group(6)), parseFloat(matcher.group(7)),
						parseFloat(matcher.group(8))));
			} else if (matcher.group(9) != null) {
				final Vertex v1 = new Vertex(modelCoordinates.get(parseInteger(matcher.group(9)) - 1),
						texturesCoordinates.get(parseInteger(matcher.group(10)) - 1), normals
								.get(parseInteger(matcher.group(11)) - 1));
				final Vertex v2 = new Vertex(modelCoordinates.get(parseInteger(matcher.group(12)) - 1),
						texturesCoordinates.get(parseInteger(matcher.group(13)) - 1), normals
								.get(parseInteger(matcher.group(14)) - 1));
				final Vertex v3 = new Vertex(modelCoordinates.get(parseInteger(matcher.group(15)) - 1),
						texturesCoordinates.get(parseInteger(matcher.group(16)) - 1), normals
								.get(parseInteger(matcher.group(17)) - 1));
				faces.add(new Face(v1, v2, v3));
			}
		}
	}

	public void createGeometry(
			final GeometryBuilder<GeometryBuilder.TexturableTriangle3D<GeometryBuilder.NormalizableTriangle3D<Object>>, GeometryBuilder.TexturableRectangle2D<Object>> anOpenGLGeometryBuilder) {

		for (int f = 0; f < faces.size(); f++) {
			Face face = faces.get(f);

			final Vertex v1 = face.v1;
			final Vertex v2 = face.v2;
			final Vertex v3 = face.v3;

			final ModelCoordinates v1ModelCoordinates = v1.modelCoordinates;
			final TextureCoordinates v1TextureCoordinates = v1.textureCoordinates;
			final Normal v1Normal = v1.normal;
			final ModelCoordinates v2ModelCoordinates = v2.modelCoordinates;
			final TextureCoordinates v2TextureCoordinates = v2.textureCoordinates;
			final Normal v2Normal = v2.normal;
			final ModelCoordinates v3ModelCoordinates = v3.modelCoordinates;
			final TextureCoordinates v3TextureCoordinates = v3.textureCoordinates;
			final Normal v3Normal = v3.normal;
			anOpenGLGeometryBuilder.add3DTriangle(v1ModelCoordinates.x, v1ModelCoordinates.y, v1ModelCoordinates.z,
					v2ModelCoordinates.x, v2ModelCoordinates.y, v2ModelCoordinates.z, v3ModelCoordinates.x,
					v3ModelCoordinates.y, v3ModelCoordinates.z).setTextureCoordinates(v1TextureCoordinates.u,
					v1TextureCoordinates.v, v2TextureCoordinates.u, v2TextureCoordinates.v, v3TextureCoordinates.u,
					v3TextureCoordinates.v).setNormal(v1Normal.x, v1Normal.y, v1Normal.z, v2Normal.x, v2Normal.y,
					v2Normal.z, v3Normal.x, v3Normal.y, v3Normal.z);
		}
	}
}
