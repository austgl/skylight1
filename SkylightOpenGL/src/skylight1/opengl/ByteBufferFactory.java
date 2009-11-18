package skylight1.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.List;

public class ByteBufferFactory {
	/**
	 * Creates a direct ByteBuffer in native byte order from a given list of Floats.
	 */
	public static ByteBuffer createBuffer(final List<Float> aListOfFloats) {
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(aListOfFloats.size() * Float.SIZE / Byte.SIZE);
		byteBuffer.order(ByteOrder.nativeOrder());
		final FloatBuffer byteBufferAsFloatBuffer = byteBuffer.asFloatBuffer();

		for (float singleFloat : aListOfFloats) {
			byteBufferAsFloatBuffer.put(singleFloat);
		}

		return byteBuffer;
	}
}
