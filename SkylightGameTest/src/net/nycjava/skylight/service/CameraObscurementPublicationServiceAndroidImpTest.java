package net.nycjava.skylight.service;

import java.util.Arrays;

import junit.framework.TestCase;
import net.nycjava.skylight.dependencyinjection.DependencyInjectingObjectFactory;
import net.nycjava.skylight.service.CameraObscurementObserver.CameraObscurementState;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.MockCameraProxy;

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

	public void testTDCFile() {
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

	private void setCameraParameters(final MockCameraProxy mockCameraProxy) {
		mockCameraProxy.getCamera().getParameters().getPreviewSize().height = 455;
		mockCameraProxy.getCamera().getParameters().getPreviewSize().width = 320;
		mockCameraProxy.getCamera().getParameters().setPreviewFormat(PixelFormat.YCbCr_422_SP);
	}
}
