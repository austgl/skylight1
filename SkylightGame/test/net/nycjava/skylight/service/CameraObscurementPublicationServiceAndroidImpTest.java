package net.nycjava.skylight.service;

import junit.framework.TestCase;
import net.nycjava.skylight.dependencyinjection.DependencyInjectingObjectFactory;
import android.hardware.Camera;
import android.hardware.MockCameraProxy;
import android.util.Log;

public class CameraObscurementPublicationServiceAndroidImpTest extends TestCase {
	public void test() {
		DependencyInjectingObjectFactory factory = new DependencyInjectingObjectFactory();
		factory.registerImplementationClass(CameraObscurementPublicationService.class,
				CameraObscurementPublicationServiceAndroidImpl.class);
		final MockCameraProxy mockCameraProxy = new MockCameraProxy();
		factory.registerImplementationObject(Camera.class, mockCameraProxy.getCamera());

		CameraObscurementPublicationService service = factory.getObject(CameraObscurementPublicationService.class);
		service.addObserver(new CameraObscurementObserver() {
			public void cameraObscurementNotification(CameraObscurementState cameraObscuredState) {
				Log.i(CameraObscurementPublicationServiceAndroidImpTest.class.getName(), "wow!");
			}
		});

		byte[] previewData = new byte[100];

		mockCameraProxy.sendReviewFrame(previewData);
	}
}
