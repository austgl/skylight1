package net.nycjava.skylight.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;

import junit.framework.TestCase;
import net.nycjava.skylight.dependencyinjection.DependencyInjectingObjectFactory;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.hardware.SensorManagerProxy;

public class PositionPublicationServiceAndroidImplTest2 extends TestCase {
	static class Dummy implements SensorListener {
		long startTime = System.currentTimeMillis();

		@Override
		public void onAccuracyChanged(int arg0, int arg1) {
			System.out.printf("%4.3f: accuracy changed ", ((float) (System.currentTimeMillis() - startTime)) / 1000f);
			System.out.printf("%d, %d\n", arg0, arg1);
		}

		@Override
		public void onSensorChanged(int arg0, float[] arg1) {
			System.out.printf("%4.3f: sensor changed ", ((float) (System.currentTimeMillis() - startTime)) / 1000f);
			System.out.printf("%d, %s\n", arg0, Arrays.toString(arg1));
		}
	}

	public void xtest1() throws FileNotFoundException, InterruptedException {
		System.out.println("Starting");

		SensorManager mockSensorManager = new SensorManagerProxy().getSensorManager(new FileInputStream(
				"testData_20090507_221542.tdc"));

		mockSensorManager.registerListener(new Dummy(), SensorManager.SENSOR_ALL);

		Thread.sleep(60000);

		System.out.println("Finished");
	}

	public void xtest2() throws FileNotFoundException, InterruptedException {
		System.out.println("Starting");

		SensorManager mockSensorManager = new SensorManagerProxy().getSensorManager(new FileInputStream(
				"testData_20090507_221442.tdc"));

		mockSensorManager.registerListener(new Dummy(), SensorManager.SENSOR_ALL);

		Thread.sleep(60000);

		System.out.println("Finished");
	}

	public void xtest3() throws FileNotFoundException, InterruptedException {
		System.out.println("Starting");

		SensorManager mockSensorManager = new SensorManagerProxy().getSensorManager(new FileInputStream(
				"testData_20090511_214404.tdc"));

		mockSensorManager.registerListener(new Dummy(), SensorManager.SENSOR_ALL);

		Thread.sleep(10000);

		System.out.println("Finished");
	}

	public void xtest4() throws FileNotFoundException, InterruptedException {
		System.out.println("Starting");

		SensorManager mockSensorManager = new SensorManagerProxy().getSensorManager(new FileInputStream(
				"testData_20090511_214433.tdc"));

		mockSensorManager.registerListener(new Dummy(), SensorManager.SENSOR_ALL);

		Thread.sleep(60000);

		System.out.println("Finished");
	}

	public void xtest5() throws FileNotFoundException, InterruptedException {
		System.out.println("Starting");

		DependencyInjectingObjectFactory factory = new DependencyInjectingObjectFactory();
		factory.registerImplementationClass(PositionPublicationService.class,
				PositionPublicationServiceAndroidImpl.class);
		SensorManager mockSensorManager = new SensorManagerProxy().getSensorManager(new FileInputStream(
				"testData_20090511_214433.tdc"));
		factory.registerImplementationObject(SensorManager.class, mockSensorManager);

		PositionPublicationService service = factory.getObject(PositionPublicationService.class);

		service.addObserver(new PositionObserver() {
			@Override
			public void positionNotification(Position position) {
				System.out.println(position);
			}
		});

		Thread.sleep(60000);

		System.out.println("Finished");
	}

	public void xtest6() throws FileNotFoundException, InterruptedException {
		System.out.println("Starting");

		DependencyInjectingObjectFactory factory = new DependencyInjectingObjectFactory();
		factory.registerImplementationClass(DestinationPublicationService.class,
				DestinationPublicationServiceImpl.class);
		factory.registerImplementationClass(PositionPublicationService.class,
				PositionPublicationServiceAndroidImpl.class);
		SensorManager mockSensorManager = new SensorManagerProxy().getSensorManager(new FileInputStream(
				"testData_20090511_214433.tdc"));
		factory.registerImplementationObject(SensorManager.class, mockSensorManager);

		DestinationPublicationService service = factory.getObject(DestinationPublicationService.class);

		service.setDestinationPosition(new Position(0, -1000, 0));

		service.addObserver(new DestinationObserver() {
			@Override
			public void destinationNotification(float anAngle, float distance) {
				System.out.println(distance);
			}
		});

		Thread.sleep(60000);

		System.out.println("Finished");
	}

	public void test7() throws FileNotFoundException, InterruptedException {
		System.out.println("Starting");

		DependencyInjectingObjectFactory factory = new DependencyInjectingObjectFactory();
		factory.registerImplementationClass(DestinationPublicationService.class,
				DestinationPublicationServiceImpl.class);
		factory.registerImplementationClass(PositionPublicationService.class,
				PositionPublicationServiceAndroidImpl.class);
		SensorManager mockSensorManager = new SensorManagerProxy().getSensorManager(new FileInputStream(
				"testData_20090511_220830.tdc"));
		factory.registerImplementationObject(SensorManager.class, mockSensorManager);

		DestinationPublicationService service = factory.getObject(DestinationPublicationService.class);

		service.setDestinationPosition(new Position(0, -1000, 0));

		service.addObserver(new DestinationObserver() {
			@Override
			public void destinationNotification(float anAngle, float distance) {
				System.out.println(distance);
			}
		});

		Thread.sleep(60000);

		System.out.println("Finished");
	}
}
