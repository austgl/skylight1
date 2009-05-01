package net.nycjava.skylight.service;

import java.util.Arrays;

import junit.framework.TestCase;
import net.nycjava.skylight.dependencyinjection.DependencyInjectingObjectFactory;
import net.nycjava.skylight.service.CameraObscurementObserver.CameraObscurementState;
import android.hardware.Camera;
import android.hardware.MockCameraProxy;

public class CameraObscurementPublicationServiceAndroidImpTest extends TestCase {
	private CameraObscurementState observedCameraObscuredState;

	public void testObscured() {
		DependencyInjectingObjectFactory factory = new DependencyInjectingObjectFactory();
		factory.registerImplementationClass(CameraObscurementPublicationService.class,
				CameraObscurementPublicationServiceAndroidImpl.class);
		final MockCameraProxy mockCameraProxy = new MockCameraProxy();
		factory.registerImplementationObject(Camera.class, mockCameraProxy.getCamera());

		CameraObscurementPublicationService service = factory.getObject(CameraObscurementPublicationService.class);
		service.addObserver(new CameraObscurementObserver() {
			public void cameraObscurementNotification(CameraObscurementState aCameraObscuredState) {
				// Log.i(CameraObscurementPublicationServiceAndroidImpTest.class.getName(), format("wow, my obscurement
				// state was %s!",aCameraObscuredState));
				observedCameraObscuredState = aCameraObscuredState;
			}
		});

		byte[] previewData = new byte[100];

		mockCameraProxy.sendPreviewFrame(previewData);

		assertEquals(CameraObscurementState.obscured, observedCameraObscuredState);
	}

	public void testUnobscured() {
		DependencyInjectingObjectFactory factory = new DependencyInjectingObjectFactory();
		factory.registerImplementationClass(CameraObscurementPublicationService.class,
				CameraObscurementPublicationServiceAndroidImpl.class);
		final MockCameraProxy mockCameraProxy = new MockCameraProxy();
		factory.registerImplementationObject(Camera.class, mockCameraProxy.getCamera());

		CameraObscurementPublicationService service = factory.getObject(CameraObscurementPublicationService.class);
		service.addObserver(new CameraObscurementObserver() {
			public void cameraObscurementNotification(CameraObscurementState aCameraObscuredState) {
				// Log.i(CameraObscurementPublicationServiceAndroidImpTest.class.getName(), format("wow, my obscurement
				// state was %s!",aCameraObscuredState));
				observedCameraObscuredState = aCameraObscuredState;
			}
		});

		byte[] previewData = new byte[100];
		Arrays.fill(previewData, (byte) 0xff);

		mockCameraProxy.sendPreviewFrame(previewData);

		assertEquals(CameraObscurementState.unobscured, observedCameraObscuredState);
	}
}
