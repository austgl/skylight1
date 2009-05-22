package net.nycjava.skylight.service;

import junit.framework.TestCase;
import net.nycjava.skylight.dependencyinjection.DependencyInjectingObjectFactory;

public class RandomForceServiceImplTest extends TestCase {
	volatile private int count;

	public void test() throws InterruptedException {
		DependencyInjectingObjectFactory factory = new DependencyInjectingObjectFactory();
		factory.registerImplementationObject(BalancedObjectPublicationService.class,
				new BalancedObjectPublicationService() {
					@Override
					public void applyForce(float anXForce, float aYForce) {
						assertTrue(anXForce >= -1f);
						assertTrue(anXForce <= 1f);
						assertTrue(aYForce >= -1f);
						assertTrue(aYForce <= 1f);

						count++;
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

		assertEquals("no forces should be applied until after start", 0, count);

		service.start();

		Thread.sleep(10000);

		service.stop();

		final int finalCount = count;

		assertTrue("expect at least one force per second, but got " + count, count >= 5);

		Thread.sleep(10000);

		assertEquals("no additional forces should be applied after stop", finalCount, count);
	}
}
