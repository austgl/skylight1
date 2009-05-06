package android.hardware;

import android.view.SurfaceHolder;

public class Camera {

	public static interface AutoFocusCallback {
		void onAutoFocus(boolean success, Camera camera);
	}

	public static interface ErrorCallback {
		void onError(int error, Camera camera);
	}

	public static class Parameters {
		int pictureFormat;

		Size pictureSize;

		int previewFormat;

		int previewFrameRate;

		Size previewSize = new Size(0, 0);

		public String flatten() {
			return String.format("pictureFormat=%d, pictureSize=%s, previewFormat=%d, previewFrameRate=%d",
					pictureFormat, pictureSize, previewFormat, previewFrameRate);
		};

		public String get(String key) {
			throw new RuntimeException("Not implemented yet.");
		};

		public int getInt(String key) {
			throw new RuntimeException("Not implemented yet.");
		};

		public int getPictureFormat() {
			throw new RuntimeException("Not implemented yet.");
		};

		public Size getPictureSize() {
			throw new RuntimeException("Not implemented yet.");
		};

		public int getPreviewFormat() {
			throw new RuntimeException("Not implemented yet.");
		};

		public int getPreviewFrameRate() {
			throw new RuntimeException("Not implemented yet.");
		};

		public Size getPreviewSize() {
			return previewSize;
		};

		public void remove(String key) {
			throw new RuntimeException("Not implemented yet.");
		};

		public void set(String key, int value) {
			throw new RuntimeException("Not implemented yet.");
		};

		public void set(String key, String value) {
			throw new RuntimeException("Not implemented yet.");
		};

		public void setPictureFormat(int pixel_format) {
			throw new RuntimeException("Not implemented yet.");
		};

		public void setPictureSize(int width, int height) {
			throw new RuntimeException("Not implemented yet.");
		};

		public void setPreviewFormat(int pixel_format) {
			previewFormat = pixel_format;
		};

		public void setPreviewFrameRate(int fps) {
			previewFrameRate = fps;
		};

		public void setPreviewSize(int width, int height) {
			previewSize = new Size(width, height);
		};

		public void unflatten(String flattened) {
			throw new RuntimeException("Not implemented yet.");
		};
	}

	public static interface PictureCallback {
		abstract void onPictureTaken(byte[] data, Camera camera);
	}

	public static interface PreviewCallback {
		abstract void onPreviewFrame(byte[] data, Camera camera);
	}

	public static interface ShutterCallback {
		abstract void onShutter();
	}

	public static class Size {
		public int height;

		public int width;

		Size(int w, int h) {
			width = w;
			height = h;
		}
	}

	final static int CAMERA_ERROR_SERVER_DIED = 100;

	final static int CAMERA_ERROR_UNKNOWN = 1;

	ErrorCallback errorCallback;

	PictureCallback pictureCallback;

	PreviewCallback previewCallback;

	AutoFocusCallback autoFocusCallback;

	Parameters parameters = new Parameters();

	private byte[] rawPictureData;

	private byte[] jPEGPictureData;

	Camera() {
		// Log.w(Camera.class.getName(), "THIS IS THE MOCK OBJECT - DO NOT ALLOW IN APK");
	}

	void setRawPictureData(byte[] aRawPictureData) {
		rawPictureData = aRawPictureData;
	}

	void setJPEGPictureData(byte[] aJPEGPictureData) {
		jPEGPictureData = aJPEGPictureData;
	}

	public final void autoFocus(AutoFocusCallback cb) {
		autoFocusCallback = cb;
	}

	public static Camera open() {
		throw new RuntimeException("Use MockCameraFactory to obtain a Camera object.");
	}

	public final void release() {
	}

	public final void setErrorCallback(ErrorCallback cb) {
		errorCallback = cb;
	}

	public Parameters getParameters() {
		return parameters;
	}

	public void setParameters(Parameters aParameters) {
		parameters = aParameters;
	}

	public final void setPreviewCallback(PreviewCallback cb) {
		previewCallback = cb;
	}

	public final void setPreviewDisplay(SurfaceHolder holder) {
	}

	public final void startPreview() {
	}

	public final void stopPreview() {
	}

	public final void takePicture(ShutterCallback shutter, PictureCallback raw, PictureCallback jpeg) {
		if (shutter != null) {
			shutter.onShutter();
		}
		if (raw != null) {
			raw.onPictureTaken(rawPictureData, this);
		}
		if (jpeg != null) {
			jpeg.onPictureTaken(jPEGPictureData, this);
		}
	}
}
