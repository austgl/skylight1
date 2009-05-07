package net.nycjava.skylight.service;


import java.io.InputStream;

import junit.framework.TestCase;
import net.nycjava.skylight.dependencyinjection.DependencyInjectingObjectFactory;
import net.nycjava.skylight.service.PositionObserver;
import android.hardware.SensorManager;
import android.hardware.MockSensorManager;

public class PositionPublicationServiceAndroidImplTest extends TestCase {
	
	private Position pos = new Position(1.0f,1.0f,1.0f);;
	private Position mpos;

	public void testPosition() {
		
		// need to populate input with data for test
		InputStream data = null;
		
		DependencyInjectingObjectFactory factory = new DependencyInjectingObjectFactory();
		factory.registerImplementationClass(PositionPublicationService.class,
				PositionPublicationServiceAndroidImpl.class);
		MockSensorManager  sensorManager = new MockSensorManager(data);
		factory.registerImplementationObject(SensorManager.class, sensorManager);

		PositionPublicationService positionService = factory.getObject(PositionPublicationService.class);
		positionService.addObserver(new PositionObserver() {
			public void positionNotification(Position position) {
				mpos = position;	
			}
		});
		
		assertEquals(mpos, pos);
		
	}

}
