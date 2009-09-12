package net.nycjava.skylight.service;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import net.nycjava.skylight.dependencyinjection.DependencyInjectingObjectFactory;
import net.nycjava.skylight.service.impl.BalancedObjectPublicationServiceImpl;
import net.nycjava.skylight.service.impl.RandomForceServiceImpl;

public class RandomForceServiceImplTest extends TestCase {

	public void test() throws InterruptedException {
		List<Float> forces = new ArrayList<Float>();

		DependencyInjectingObjectFactory factory = new DependencyInjectingObjectFactory();
		factory.registerImplementationObject(BalancedObjectPublicationService.class,
				new BalancedObjectPublicationServiceImpl() {
					@SuppressWarnings("unused")
					public void applyForce(float anXForce, float aYForce) {
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

					@Override
					public void applyForce(float anXForce, float aYForce,
							long aDuration) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void setDifficultyLevel(int aDifficulty) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void startService() {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void stopService() {
						// TODO Auto-generated method stub
						
					}
				});
		factory.registerImplementationClass(RandomForceService.class, RandomForceServiceImpl.class);

		RandomForceService service = factory.getObject(RandomForceService.class);

		Thread.sleep(1500);

		assertEquals("no forces should be applied until after start", 0, forces.size());

		service.start();

		Thread.sleep(10000);

		service.stop();

		final int finalCount = forces.size();

		for (float force : forces) {
			assertTrue("" + force, force >= -1f);
			assertTrue("" + force, force <= 1f);
		}
		
		assertTrue("expect at least one force per second, but got " + forces.size(), forces.size() >= 5);

		Thread.sleep(10000);

		assertEquals("no additional forces should be applied after stop", finalCount, forces.size());
	}
}
