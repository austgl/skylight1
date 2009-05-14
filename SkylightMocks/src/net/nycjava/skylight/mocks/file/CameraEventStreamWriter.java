package net.nycjava.skylight.mocks.file;

import static java.lang.String.format;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import net.nycjava.skylight.mocks.camera.CameraEvent;
import net.nycjava.skylight.mocks.camera.CameraParametersEvent;
import net.nycjava.skylight.mocks.camera.CameraPreviewEvent;

// TODO change to use java.nio
// TODO make more OO - use polymorphism
public class CameraEventStreamWriter {
	static final byte PARAMETERS_EVENT = 1;

	static final byte PREVIEW_FRAME_EVENT = 0;

	private final DataOutputStream dataOutputStream;

	public CameraEventStreamWriter(OutputStream anOutputStream) {
		dataOutputStream = new DataOutputStream(anOutputStream);
	}

	public void writeCameraEvent(CameraEvent aCameraEvent) {
		try {
			dataOutputStream.writeLong(aCameraEvent.getTime());
			if (aCameraEvent instanceof CameraParametersEvent) {
				dataOutputStream.writeByte(PARAMETERS_EVENT);
				dataOutputStream.writeUTF(((CameraParametersEvent) aCameraEvent).getParameters().flatten());
			} else {
				dataOutputStream.writeByte(PREVIEW_FRAME_EVENT);
				final byte[] data = ((CameraPreviewEvent) aCameraEvent).getData();
				dataOutputStream.writeInt(data.length);
				dataOutputStream.write(data);
			}
		} catch (IOException e) {
			throw new RuntimeException(format("Unable to write event %s to %s.", aCameraEvent, dataOutputStream), e);
		}
	}
}
