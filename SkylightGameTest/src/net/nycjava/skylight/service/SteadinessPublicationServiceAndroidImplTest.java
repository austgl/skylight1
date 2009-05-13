package net.nycjava.skylight.service;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import junit.framework.TestCase;
import net.nycjava.skylight.dependencyinjection.DependencyInjectingObjectFactory;
import net.nycjava.skylight.service.SteadinessObserver;
import android.hardware.SensorManager;
import android.hardware.SensorManagerProxy;

public class SteadinessPublicationServiceAndroidImplTest extends TestCase {
	
	private float force;
	private boolean unsteady;
	
	public void testSteady() {
		
		// need to populate input with data for test
		InputStream data = null;
		
		DependencyInjectingObjectFactory factory = new DependencyInjectingObjectFactory();
		factory.registerImplementationClass(SteadinessPublicationService.class,
				SteadinessPublicationServiceAndroidImpl.class);
		SensorManager sensorManager;
		try {
			sensorManager = new SensorManagerProxy().getSensorManager(new FileInputStream("testData_20090507_221542.tdc"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		factory.registerImplementationObject(SensorManager.class, sensorManager);

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
