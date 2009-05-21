package android.hardware;

import static java.lang.String.format;

import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.nycjava.skylight.mocks.camera.CameraEvent;
import net.nycjava.skylight.mocks.camera.CameraParametersEvent;
import net.nycjava.skylight.mocks.camera.CameraPreviewEvent;
import net.nycjava.skylight.mocks.file.CameraEventStreamReader;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.SurfaceHolder;

public class Camera {

	public static interface AutoFocusCallback {
		void onAutoFocus(boolean success, Camera camera);
	}

	public static interface ErrorCallback {
		void onError(int error, Camera camera);
	}

	public static class Parameters {
		private static final Pattern previewSizePattern = Pattern.compile("preview-size=(\\d+)x(\\d+);");

		private static final Pattern previewFrameRatePattern = Pattern.compile("preview-frame-rate=(\\d+);");

		private static final Pattern previewFormatPattern = Pattern.compile("preview-format=([^;]+);");

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
			return previewFormat;
		};

		public int getPreviewFrameRate() {
			return previewFrameRate;
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
			Matcher previewSizeMatcher = previewSizePattern.matcher(flattened);
			if (!previewSizeMatcher.find()) {
				throw new RuntimeException(format("could not find preview size in %s", flattened));
			}
			previewSize.width = Integer.parseInt(previewSizeMatcher.group(1));
			previewSize.height = Integer.parseInt(previewSizeMatcher.group(2));

			Matcher previewFrameRateMatcher = previewFrameRatePattern.matcher(flattened);
			if (!previewFrameRateMatcher.find()) {
				throw new RuntimeException(format("could not find preview frame rate in %s", flattened));
			}
			previewFrameRate = Integer.parseInt(previewFrameRateMatcher.group(1));

			Matcher previewFormatMatcher = previewFormatPattern.matcher(flattened);
			if (!previewFormatMatcher.find()) {
				throw new RuntimeException(format("could not find preview format in %s", flattened));
			}
			String previewFormatString = previewFormatMatcher.group(1);
			if (previewFormatString.contains("420")) {
				previewFormat = PixelFormat.YCbCr_420_SP;
			} else if (previewFormatString.toUpperCase().contains("RGB")) {
				previewFormat = PixelFormat.RGB_565;
			} else if (previewFormatString.toUpperCase().contains("JPEG")) {
				previewFormat = PixelFormat.JPEG;
			}
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

	private CameraEventStreamReader cameraEventStreamReader;

	Camera() {
		Log.w(Camera.class.getName(), "THIS IS THE MOCK OBJECT - DO NOT ALLOW IN APK");
	}

	Camera(InputStream aTDCInputStream) {
		Log.w(Camera.class.getName(), "THIS IS THE MOCK OBJECT - DO NOT ALLOW IN APK");
		cameraEventStreamReader = new CameraEventStreamReader(aTDCInputStream);
		parameters = ((CameraParametersEvent) cameraEventStreamReader.readCameraEvent()).getParameters();
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
		if (cameraEventStreamReader != null) {
			new Thread(new Runnable() {
				public void run() {
					long startTime = System.currentTimeMillis();
					CameraEvent cameraEvent;
					while ((cameraEvent = cameraEventStreamReader.readCameraEvent()) != null) {
						long delay = cameraEvent.getTime() + startTime - System.currentTimeMillis();
						if (delay > 0L) {
							try {
								Thread.sleep(delay);
							} catch (InterruptedException e) {
								Log.e("mocks", null, e);
							}
						}

						if (cameraEvent instanceof CameraPreviewEvent) {
							CameraPreviewEvent cameraPreviewEvent = (CameraPreviewEvent) cameraEvent;
							previewCallback.onPreviewFrame(cameraPreviewEvent.getData(), Camera.this);
						} else {
							throw new RuntimeException("should not be any other type of event");
						}
					}
				}
			}).start();
		}
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
