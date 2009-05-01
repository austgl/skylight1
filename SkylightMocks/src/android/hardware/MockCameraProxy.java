package android.hardware;

public class MockCameraProxy {
	private Camera camera = new Camera();

	public MockCameraProxy() {
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
