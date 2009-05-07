package net.nycjava.skylight.service;


import java.io.InputStream;

import junit.framework.TestCase;
import net.nycjava.skylight.dependencyinjection.DependencyInjectingObjectFactory;
import net.nycjava.skylight.service.SteadinessObserver;
import android.hardware.SensorManager;
import android.hardware.MockSensorManager;

public class SteadinessPublicationServiceAndroidImplTest extends TestCase {
	
	private float force;
	private boolean unsteady;
	
	public void testSteady() {
		
		// need to populate input with data for test
		InputStream data = null;
		
		DependencyInjectingObjectFactory factory = new DependencyInjectingObjectFactory();
		factory.registerImplementationClass(SteadinessPublicationService.class,
				SteadinessPublicationServiceAndroidImpl.class);
		MockSensorManager  sensorManager = new MockSensorManager(data);
		factory.registerImplementationObject(SensorManager.class, sensorManager);

		SteadinessPublicationService SteadyService = factory.getObject(SteadinessPublicationService.class);
		SteadyService.addObserver(new SteadinessObserver() {
			public void steadinessNotification(float aForce) {
				force = aForce;	
			}
			public void unsteadinessNotification() {
				unsteady = true;	
			}
		});
		
		assertEquals(1.0, force);
		assertEquals(true, unsteady);
	
	}

}
