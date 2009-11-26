package skylight1.opengl.files;

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

public class ObjFileLoader {
	private static final String FLOAT = "(-?\\d+\\.\\d+)";

	private static final String INTEGER = "(\\d+)";

	// some inner classes to model the data in the obj file
	private static class ModelCoordinates {
		Float x, y, z;

		public ModelCoordinates(Float x, Float y, Float z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}

	private static class TextureCoordinates {
		Float u, v;

		public TextureCoordinates(Float u, Float v) {
			this.u = u;
			this.v = v;
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

	private static class Vertex {
		ModelCoordinates modelCoordinates;

		TextureCoordinates textureCoordinates;

		Normal normal;

		public Vertex(ModelCoordinates modelCoordinates, TextureCoordinates textureCoordinates, Normal normal) {
			this.modelCoordinates = modelCoordinates;
			this.textureCoordinates = textureCoordinates;
			this.normal = normal;
		}
	}

	private static class Face {
		Vertex v1, v2, v3;

		public Face(Vertex v1, Vertex v2, Vertex v3) {
			this.v1 = v1;
			this.v2 = v2;
			this.v3 = v3;
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
				modelCoordinates.add(new ModelCoordinates(quickParseFloat(matcher.group(1)), quickParseFloat(matcher
						.group(2)), quickParseFloat(matcher.group(3))));
			} else if (matcher.group(4) != null) {
				texturesCoordinates.add(new TextureCoordinates(quickParseFloat(matcher.group(4)),
						1f - quickParseFloat(matcher.group(5))));
			} else if (matcher.group(6) != null) {
				normals.add(new Normal(quickParseFloat(matcher.group(6)), quickParseFloat(matcher.group(7)),
						quickParseFloat(matcher.group(8))));
			} else if (matcher.group(9) != null) {
				final Vertex v1 = new Vertex(modelCoordinates.get(quickParseInteger(matcher.group(9)) - 1),
						texturesCoordinates.get(quickParseInteger(matcher.group(10)) - 1), normals
								.get(quickParseInteger(matcher.group(11)) - 1));
				final Vertex v2 = new Vertex(modelCoordinates.get(quickParseInteger(matcher.group(12)) - 1),
						texturesCoordinates.get(quickParseInteger(matcher.group(13)) - 1), normals
								.get(quickParseInteger(matcher.group(14)) - 1));
				final Vertex v3 = new Vertex(modelCoordinates.get(quickParseInteger(matcher.group(15)) - 1),
						texturesCoordinates.get(quickParseInteger(matcher.group(16)) - 1), normals
								.get(quickParseInteger(matcher.group(17)) - 1));
				faces.add(new Face(v1, v2, v3));
			}
		}
	}

	private final static float[][] FLOAT_DECIMAL_VALUES = new float[7][10];

	static {
		for (int decimalPlace = 0; decimalPlace < FLOAT_DECIMAL_VALUES.length; decimalPlace++) {
			for (int decimalValue = 0; decimalValue < 10; decimalValue++) {
				FLOAT_DECIMAL_VALUES[decimalPlace][decimalValue] = (float) (Math.pow(10d, -decimalPlace) * decimalValue);
			}
		}
	}

	private float quickParseFloat(final String aStringRepresentationOfAFloat) {
		final int startOfDigits;
		final float sign;
		if (aStringRepresentationOfAFloat.charAt(0) == '-') {
			startOfDigits = 1;
			sign = -1f;
		} else {
			startOfDigits = 0;
			sign = 1f;
		}
		float result = FLOAT_DECIMAL_VALUES[0][aStringRepresentationOfAFloat.charAt(startOfDigits) - '0'];
		int decimalPlace = 0;
		final int stringLength = aStringRepresentationOfAFloat.length();
		for (int i = startOfDigits + 2; i < stringLength; i++) {
			decimalPlace++;
			result += FLOAT_DECIMAL_VALUES[decimalPlace][aStringRepresentationOfAFloat.charAt(i) - '0'];
		}
		return sign * result;
	}

	private final static int[][] INTEGER_DECIMAL_VALUES = new int[4][10];

	static {
		for (int decimalPlace = 0; decimalPlace < INTEGER_DECIMAL_VALUES.length; decimalPlace++) {
			for (int decimalValue = 0; decimalValue < 10; decimalValue++) {
				INTEGER_DECIMAL_VALUES[decimalPlace][decimalValue] = (int) (Math.pow(10d, decimalPlace) * decimalValue);
			}
		}
	}

	private int quickParseInteger(final String aStringRepresentationOfAnInteger) {
		final int startOfDigits;
		final int sign;
		if (aStringRepresentationOfAnInteger.charAt(0) == '-') {
			startOfDigits = 1;
			sign = -1;
		} else {
			startOfDigits = 0;
			sign = 1;
		}
		int result = 0;
		int decimalPlace = -1;
		final int stringLength = aStringRepresentationOfAnInteger.length();
		for (int i = stringLength - 1; i >= startOfDigits; i--) {
			decimalPlace++;
			result += INTEGER_DECIMAL_VALUES[decimalPlace][aStringRepresentationOfAnInteger.charAt(i) - '0'];
		}
		return sign * result;
	}

	public OpenGLGeometry createGeometry(
			final OpenGLGeometryBuilder<GeometryBuilder.TexturableTriangle3D<GeometryBuilder.NormalizableTriangle3D<Object>>, GeometryBuilder.TexturableRectangle2D<Object>> anOpenGLGeometryBuilder) {
		anOpenGLGeometryBuilder.startGeometry();

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

		return anOpenGLGeometryBuilder.endGeometry();
	}
}
