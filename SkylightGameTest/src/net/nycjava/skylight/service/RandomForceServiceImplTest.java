package net.nycjava.skylight.service;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import net.nycjava.skylight.dependencyinjection.DependencyInjectingObjectFactory;

public class RandomForceServiceImplTest extends TestCase {
	public void test() throws InterruptedException {
		final List<Float> angles = new ArrayList<Float>();
		final List<Float> forces = new ArrayList<Float>();

		DependencyInjectingObjectFactory factory = new DependencyInjectingObjectFactory();
		factory.registerImplementationObject(BalancedObjectPublicationService.class,
				new BalancedObjectPublicationService() {
					@Override
					public void applyForce(float anAngleInRadians, float aForceInNewtons) {
						assertTrue(anAngleInRadians >= 0);
						assertTrue(anAngleInRadians <= Math.PI * 2f);
						assertTrue(aForceInNewtons >= 0);

						angles.add(anAngleInRadians);
						forces.add(aForceInNewtons);
					}

					@Override
					public void addObserver(BalancedObjectObserver anObserver) {
						fail("should not come here");
					}

					@Override
					public boolean removeObserver(BalancedObjectObserver anObserver) {
						fail("should not come here");
						return false;
					}
				});
		factory.registerImplementationClass(RandomForceService.class, RandomForceServiceImpl.class);

		RandomForceService service = factory.getObject(RandomForceService.class);

		Thread.sleep(1500);

		assertEquals("no forces should be applied until after start", 0, angles.size());
		assertEquals("no forces should be applied until after start", 0, forces.size());

		service.start();

		Thread.sleep(10000);

		service.stop();

		final int anglesSize = angles.size();
		final int forcesSize = forces.size();
		assertTrue("expect at least one force per second", anglesSize > 10);
		assertTrue("expect at least one force per second", forcesSize > 10);

		Thread.sleep(10000);

		assertEquals("no additional forces should be applied after stop", anglesSize, angles.size());
		assertEquals("no additional forces should be applied after stop", forcesSize, forces.size());
	}
}
