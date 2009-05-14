package net.nycjava.skylight.mocks.file;

import static java.lang.String.format;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import net.nycjava.skylight.mocks.camera.CameraEvent;
import net.nycjava.skylight.mocks.camera.CameraParametersEvent;
import net.nycjava.skylight.mocks.camera.CameraPreviewEvent;
import android.hardware.Camera;

// TODO change to use java.nio
public class CameraEventStreamReader {

	private DataInputStream dataInputStream;

	public CameraEventStreamReader(InputStream anInputStream) {
		dataInputStream = new DataInputStream(anInputStream);
	}

	public CameraEvent readSensorEvent() {
		try {
			if (dataInputStream.available() == 0) {
				return null;
			}

			final long time = dataInputStream.readLong();
			final byte type = dataInputStream.readByte();
			switch (type) {
			case CameraEventStreamWriter.PARAMETERS_EVENT:
				Camera.Parameters parameters = new Camera.Parameters();
				parameters.unflatten(dataInputStream.readUTF());
				return new CameraParametersEvent(time, parameters);
			case CameraEventStreamWriter.PREVIEW_FRAME_EVENT:
				final byte[] data = new byte[dataInputStream.readInt()];
				dataInputStream.read(data);
				return new CameraPreviewEvent(time, data);
			default:
				throw new RuntimeException("not meant to get here");
			}
		} catch (IOException e) {
			throw new IllegalArgumentException(
					format("Unable to write to input stream %s.", dataInputStream.toString()), e);
		}
	}
}
