package net.nycjava.skylight.service;

import static java.lang.String.format;
import static net.nycjava.skylight.service.CameraObscurementObserver.CameraObscurementState.obscured;
import static net.nycjava.skylight.service.CameraObscurementObserver.CameraObscurementState.unobscured;

import java.util.HashSet;
import java.util.Set;

import net.nycjava.skylight.dependencyinjection.Dependency;
import net.nycjava.skylight.service.CameraObscurementObserver.CameraObscurementState;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.util.Log;

// TODO javadoc
public class CameraObscurementPublicationServiceAndroidImpl implements CameraObscurementPublicationService {
	@Dependency
	private Camera camera;

	private Set<CameraObscurementObserver> cameraObscurementObservers = new HashSet<CameraObscurementObserver>();

	private Camera.PreviewCallback previewCallback;

	public void addObserver(CameraObscurementObserver anObserver) {
		cameraObscurementObservers.add(anObserver);
		if (cameraObscurementObservers.size() == 1) {
			Parameters parameters = camera.getParameters();
			final int bytesOfGreyScale = parameters.getPreviewSize().width * parameters.getPreviewSize().height;
			previewCallback = new Camera.PreviewCallback() {
				public void onPreviewFrame(byte[] data, Camera camera) {
					Log.i(CameraObscurementPublicationServiceAndroidImpl.class.getName(), format("data length = %d",
							data.length));
					int totalLuminosity = 0;
					for (int i = bytesOfGreyScale; i >= 0; i--) {
						if (data[i] < 0) {
							totalLuminosity += 256 + data[i];
						} else {
							totalLuminosity += data[i];
						}
					}
					CameraObscurementState obscurementState = totalLuminosity > (bytesOfGreyScale >> 2) ? unobscured
							: obscured;
					notifyObservers(obscurementState);
				}
			};
			Log.i(CameraObscurementPublicationServiceAndroidImpl.class.getName(), parameters.flatten());
			parameters.setPreviewFormat(PixelFormat.YCbCr_422_SP);
			camera.setParameters(parameters);
			camera.setPreviewCallback(previewCallback);
			camera.startPreview();
		}
	}

	public boolean removeObserver(CameraObscurementObserver anObserver) {
		final boolean existed = cameraObscurementObservers.remove(anObserver);
		if (cameraObscurementObservers.isEmpty()) {
			camera.stopPreview();
			camera.setPreviewCallback(null);
		}
		return existed;
	}

	private void notifyObservers(CameraObscurementState anObscurementState) {
		for (CameraObscurementObserver cameraObscurementObserver : cameraObscurementObservers) {
			cameraObscurementObserver.cameraObscurementNotification(anObscurementState);
		}
	}
}