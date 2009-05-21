package android.hardware;

import java.io.InputStream;

public class MockCameraProxy {
	final private Camera camera;

	public MockCameraProxy() {
		camera = new Camera();
	}

	public MockCameraProxy(InputStream aTDCInputStream) {
		camera = new Camera(aTDCInputStream);
	}

	public void setPicture(byte[] aRawPictureData, byte[] aJPEGPictureData) {
		camera.setRawPictureData(aRawPictureData);
		camera.setJPEGPictureData(aJPEGPictureData);
	}

	public void sendError(int anError) {
		camera.errorCallback.onError(anError, camera);
	}

	public void sendAutoFocus(boolean aSuccess) {
		camera.autoFocusCallback.onAutoFocus(aSuccess, camera);
	}

	public void sendPictureTaken(byte[] aData) {
		camera.pictureCallback.onPictureTaken(aData, camera);
	}

	public void sendPreviewFrame(byte[] aData) {
		camera.previewCallback.onPreviewFrame(aData, camera);
	}

	public Camera getCamera() {
		return camera;
	}
}
