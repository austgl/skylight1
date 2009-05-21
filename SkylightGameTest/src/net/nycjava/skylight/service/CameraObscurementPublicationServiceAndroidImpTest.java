package net.nycjava.skylight.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;

import junit.framework.TestCase;
import net.nycjava.skylight.dependencyinjection.DependencyInjectingObjectFactory;
import net.nycjava.skylight.service.CameraObscurementObserver.CameraObscurementState;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.MockCameraProxy;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;

public class CameraObscurementPublicationServiceAndroidImpTest extends TestCase {
	private CameraObscurementState observedCameraObscuredState;

	private final byte[] previewData = new byte[455 * 320 * 2];

	public void testObscured() {
		DependencyInjectingObjectFactory factory = new DependencyInjectingObjectFactory();
		factory.registerImplementationClass(CameraObscurementPublicationService.class,
				CameraObscurementPublicationServiceAndroidImpl.class);
		final MockCameraProxy mockCameraProxy = new MockCameraProxy();
		factory.registerImplementationObject(Camera.class, mockCameraProxy.getCamera());

		setCameraParameters(mockCameraProxy);

		CameraObscurementPublicationService service = factory.getObject(CameraObscurementPublicationService.class);
		service.addObserver(new CameraObscurementObserver() {
			public void cameraObscurementNotification(CameraObscurementState aCameraObscuredState) {
				// Log.i(CameraObscurementPublicationServiceAndroidImpTest.class.getName(), format("wow, my obscurement
				// state was %s!",aCameraObscuredState));
				observedCameraObscuredState = aCameraObscuredState;
			}
		});

		mockCameraProxy.sendPreviewFrame(previewData);

		assertEquals(CameraObscurementState.obscured, observedCameraObscuredState);
	}

	public void testUnobscured() {
		DependencyInjectingObjectFactory factory = new DependencyInjectingObjectFactory();
		factory.registerImplementationClass(CameraObscurementPublicationService.class,
				CameraObscurementPublicationServiceAndroidImpl.class);
		final MockCameraProxy mockCameraProxy = new MockCameraProxy();
		factory.registerImplementationObject(Camera.class, mockCameraProxy.getCamera());

		setCameraParameters(mockCameraProxy);

		CameraObscurementPublicationService service = factory.getObject(CameraObscurementPublicationService.class);
		service.addObserver(new CameraObscurementObserver() {
			public void cameraObscurementNotification(CameraObscurementState aCameraObscuredState) {
				// Log.i(CameraObscurementPublicationServiceAndroidImpTest.class.getName(), format("wow, my obscurement
				// state was %s!",aCameraObscuredState));
				observedCameraObscuredState = aCameraObscuredState;
			}
		});

		Arrays.fill(previewData, (byte) 0xff);

		mockCameraProxy.sendPreviewFrame(previewData);

		assertEquals(CameraObscurementState.unobscured, observedCameraObscuredState);
	}

	public void testTDCFile() throws FileNotFoundException {
		DependencyInjectingObjectFactory factory = new DependencyInjectingObjectFactory();
		factory.registerImplementationClass(CameraObscurementPublicationService.class,
				CameraObscurementPublicationServiceAndroidImpl.class);
		final MockCameraProxy mockCameraProxy = new MockCameraProxy(new FileInputStream(
				"testData_camera_20090514_210943.tdc"));
		final Camera camera = mockCameraProxy.getCamera();
		factory.registerImplementationObject(Camera.class, camera);

		camera.setPreviewCallback(new PreviewCallback() {
			@Override
			public void onPreviewFrame(byte[] data, Camera camera) {
				final Parameters parameters = camera.getParameters();
				final Size previewSize = parameters.getPreviewSize();
				final int bytesPerPixel = data.length / previewSize.height / previewSize.width;
				System.out
						.println(String
								.format(
										"in here with %d bytes of data; preview is of format %d (bytes per pixel are %d), rate is %d, and size is %d x %d",
										data.length, parameters.getPreviewFormat(), bytesPerPixel, parameters
												.getPreviewFrameRate(), previewSize.width, previewSize.height));
			}
		});

		camera.startPreview();

		// camera.stopPreview();

		// setCameraParameters(mockCameraProxy);
		//
		// CameraObscurementPublicationService service = factory.getObject(CameraObscurementPublicationService.class);
		// service.addObserver(new CameraObscurementObserver() {
		// public void cameraObscurementNotification(CameraObscurementState aCameraObscuredState) {
		// // Log.i(CameraObscurementPublicationServiceAndroidImpTest.class.getName(), format("wow, my obscurement
		// // state was %s!",aCameraObscuredState));
		// observedCameraObscuredState = aCameraObscuredState;
		// }
		// });
		//
		// Arrays.fill(previewData, (byte) 0xff);
		//
		// mockCameraProxy.sendPreviewFrame(previewData);
		//
		// assertEquals(CameraObscurementState.unobscured, observedCameraObscuredState);
	}

	private void setCameraParameters(final MockCameraProxy mockCameraProxy) {
		mockCameraProxy.getCamera().getParameters().getPreviewSize().height = 455;
		mockCameraProxy.getCamera().getParameters().getPreviewSize().width = 320;
		mockCameraProxy.getCamera().getParameters().setPreviewFormat(PixelFormat.YCbCr_422_SP);
	}
}
