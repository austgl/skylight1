package skylight1.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.List;

class ByteBufferFactory {
	/**
	 * Creates a direct IntBuffer (in native byte order) and populates it with fixed point integers from a given list of
	 * Floats.
	 */
	public static IntBuffer createBuffer(final List<Float> aListOfFloats) {
		// create a direct byte buffer in native byte order
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(aListOfFloats.size() * Float.SIZE / Byte.SIZE);
		byteBuffer.order(ByteOrder.nativeOrder());
		final IntBuffer byteBufferAsIntBuffer = byteBuffer.asIntBuffer();

		for (float singleFloat : aListOfFloats) {
			// convert into fixed point and store
			byteBufferAsIntBuffer.put((int) (singleFloat * (1 << 16)));
		}

		// point the buffer back to the beginning
		byteBufferAsIntBuffer.position(0);

		return byteBufferAsIntBuffer;
	}
}
