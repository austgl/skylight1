package net.nycjava.skylight1.service;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import net.nycjava.skylight1.dependencyinjection.DependencyInjectingObjectFactory;
import net.nycjava.skylight1.service.impl.BalancedObjectPublicationServiceImpl;

public class BalancedObjectPublicationServiceImplTest extends TestCase {

	private BalancedObjectPublicationService service = null;

	private BalancedObjectObserver balancedObjectObserver;

	private List<Float> listOfX = new ArrayList<Float>();

	private List<Float> listOfY = new ArrayList<Float>();

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		DependencyInjectingObjectFactory factory = new DependencyInjectingObjectFactory();
		factory.registerSingletonImplementationClass(BalancedObjectPublicationService.class,
				BalancedObjectPublicationServiceImpl.class);

		service = factory.getObject(BalancedObjectPublicationService.class);

		balancedObjectObserver = new BalancedObjectObserver() {
			public void balancedObjectNotification(float anXPosition, float aYPosition) {
				listOfX.add(anXPosition);
				listOfY.add(aYPosition);
			}

			public void fallenOverNotification() {
				// don't know
			}
		};
	}

	public void testAddingAndRemovingObserver() {
		service.addObserver(balancedObjectObserver);

		assertTrue(service.removeObserver(balancedObjectObserver));
	}

	public void testNoForces() throws InterruptedException {
		service.addObserver(balancedObjectObserver);

		Thread.sleep(1000);

		assertTrue(listOfX.size() > 0);
		for (float f : listOfX) {
			assertEquals(0.0f, f);
		}
		for (float f : listOfY) {
			assertEquals(0.0f, f);
		}

		assertTrue(service.removeObserver(balancedObjectObserver));
	}

	public void testZeroForce() throws InterruptedException {
		service.addObserver(balancedObjectObserver);

		service.applyForce(0f, 0f, 0);

		Thread.sleep(1000);

		assertTrue(listOfX.size() > 0);
		for (float f : listOfX) {
			assertEquals(0.0f, f);
		}
		for (float f : listOfY) {
			assertEquals(0.0f, f);
		}

		assertTrue(service.removeObserver(balancedObjectObserver));
	}

	public void testOneForce() throws InterruptedException {
		service.addObserver(balancedObjectObserver);

		service.applyForce(-0.1f, 0f, 0);

		Thread.sleep(1000);

		assertTrue(listOfX.size() > 0);
		float lastX = 0f;
		for (float x : listOfX) {
			assertTrue(x < lastX);
			lastX = x;
		}
		for (float y : listOfY) {
			assertEquals(0.0f, y);
		}

		assertTrue(service.removeObserver(balancedObjectObserver));
	}

	public void testTwoForces() throws InterruptedException {
		service.addObserver(balancedObjectObserver);

		service.applyForce(-0.1f, 0.1f, 0);

		Thread.sleep(1000);

		assertTrue(listOfX.size() > 0);
		float lastX = 0f;
		for (float x : listOfX) {
			assertTrue(x < lastX);
			lastX = x;
		}
		float lastY = 0f;
		for (float y : listOfY) {
			assertTrue(y > lastY);
			lastY = y;
		}

		assertTrue(service.removeObserver(balancedObjectObserver));
	}

	public void testTwoSeparateForces() throws InterruptedException {
		service.addObserver(balancedObjectObserver);

		service.applyForce(-0.1f, 0.1f, 0);

		Thread.sleep(500);

		service.applyForce(0.1f, -0.1f, 0);

		Thread.sleep(500);

		assertTrue(listOfX.size() > 0);
		final int indexOfLast = listOfX.size() - 1;
		assertEquals(listOfX.get(indexOfLast), listOfX.get(indexOfLast - 1));
		assertEquals(listOfY.get(indexOfLast), listOfY.get(indexOfLast - 1));
		assertTrue(service.removeObserver(balancedObjectObserver));
	}
}