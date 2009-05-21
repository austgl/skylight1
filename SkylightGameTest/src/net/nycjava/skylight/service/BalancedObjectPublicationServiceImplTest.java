package net.nycjava.skylight.service;

import junit.framework.TestCase;
import net.nycjava.skylight.dependencyinjection.DependencyInjectingObjectFactory;
import net.nycjava.skylight.service.BalancedObjectObserver;
import net.nycjava.skylight.service.BalancedObjectPublicationService;
public class BalancedObjectPublicationServiceImplTest extends TestCase {

	private BalancedObjectPublicationService service = null;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		DependencyInjectingObjectFactory factory = new DependencyInjectingObjectFactory();
		factory.registerSingletonImplementationClass(BalancedObjectPublicationService.class,
				BalancedObjectPublicationServiceImpl.class);

		service = factory.getObject(BalancedObjectPublicationService.class);
		service.addObserver(new BalancedObjectObserver() {		
			public void balancedObjectNotification(
					float directionOfFallingInRadians,
					float anAngleOfLeanInRadians) {

				//System.out.println("getting directionOfFallingInRadiance:" + directionOfFallingInRadians);
				//System.out.println("getting anAngleOfLeanInRadians:" + anAngleOfLeanInRadians);
			}

			public void fallenOverNotification() 
			{
				//don't know 
			}

		});

		
	}

	public void testAddObserverAndInitialCondition()
	{					
		assertEquals("initial angle should be pi over 2 ", (float)Math.PI/2, service.getCurrentAngle() );
	}
	public void testResetCondition()
	{
		service.resetCurrentCondition((float)Math.PI, (float)10);
		assertEquals("now we should have pi raidiance ", (float)Math.PI, service.getCurrentAngle() ); 
		assertEquals("now we should have 10 newtons ", (float)10, service.getCurrentMagnitude()); 		
	}

	public void testAddingForce1()
	{
		service.resetCurrentCondition((float) 0, (float)8);
		service.applyForce((float)Math.toRadians(30), 6);
		assertEquals("now we should have 12.8 degree ", Math.round(12.8),  Math.round(Math.toDegrees(service.getCurrentAngle()))); 
		assertEquals("now we should have 13.5 newtons ", Math.round(13.5),  Math.round(service.getCurrentMagnitude())); 		

	}
	
	public void testAddingForce2()
	{
		service.resetCurrentCondition((float) 0, (float)8);
		service.applyForce((float)Math.toRadians(60), 6);
		assertEquals("now we should have 25.3 degree ", Math.round(25.3),  Math.round(Math.toDegrees(service.getCurrentAngle()))); 
		assertEquals("now we should have 12.2 newtons ", Math.round(12.2),  Math.round(service.getCurrentMagnitude())); 		

	}

	public void testAddingForce3()
	{
		service.resetCurrentCondition((float) 0, (float)8);
		service.applyForce((float)Math.toRadians(90), 6);
		assertEquals("now we should have 36.9 degree ", Math.round(36.9),  Math.round(Math.toDegrees(service.getCurrentAngle()))); 
		assertEquals("now we should have 10.0 newtons ", Math.round(10.0),  Math.round(service.getCurrentMagnitude())); 		

	}

}