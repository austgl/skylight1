package skylight1.opengl.files;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests QuickParseUtil with some common kinds of float values.
 * 
 */
public class QuickParseUtilTest {

	private static final float[] VALUES = new float[] {
		0f, 1f, 213123f, 0.1f, 0.2352533f, -0f, -1f, -213123f, -0.1f, -0.2352533f };

	private static final String[] STRINGS = new String[VALUES.length];

	static {
		for (int i = 0; i < VALUES.length; i++) {
			STRINGS[i] = Float.toString(VALUES[i]);
		}
	}

	@Test
	public void parseFloat() {
		for (int i = 0; i < VALUES.length; i++) {
			float result = QuickParseUtil.parseFloat(STRINGS[i]);
			assertEquals(STRINGS[i], Float.toString(result));
		}

	}

}
