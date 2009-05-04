package net.nycjava.skylight.service;

import junit.framework.TestCase;
import net.nycjava.skylight.dependencyinjection.DependencyInjectingObjectFactory;
import net.nycjava.skylight.service.CountdownObserver;
import net.nycjava.skylight.service.CountdownPublicationService;
import net.nycjava.skylight.service.counter_status;

import java.util.Set;
import java.util.HashSet;
public class CountdownServicePublicationServiceImplTest extends TestCase 
{
	private int currentCount = -1;
	private Set notifications= new HashSet<Integer>();

	public void testAddObserverAndStart()
	{
		DependencyInjectingObjectFactory factory = new DependencyInjectingObjectFactory();
		factory.registerImplementationClass(CountdownPublicationService.class,
				CountdownPublicationServiceImpl.class);

		CountdownPublicationService service = factory.getObject(CountdownPublicationService.class);
		service.setDuration(10);
		
		service.addObserver(new CountdownObserver() {
			public void countdownNotification(int aRemainingTime) {
				currentCount = aRemainingTime;
				System.out.println("notified: "+ currentCount);
				notifications.add(new Integer(currentCount));
			}
		});
		service.startCountdown();
		try{
			service.joinThread();
		}
		catch(InterruptedException e)
		{
			System.err.println("interruptions " + e);
					
		}
		System.out.println("notifications " + notifications.size());
		assertEquals("all notifications: "+notifications.toString(), 10, notifications.size());
	}
	
		
	public void testStop()
	{
		DependencyInjectingObjectFactory factory = new DependencyInjectingObjectFactory();
		factory.registerImplementationClass(CountdownPublicationService.class,
				CountdownPublicationServiceImpl.class);

		try{
			CountdownPublicationService service = factory.getObject(CountdownPublicationService.class);
			service.setDuration(50);
			service.startCountdown();
			Thread.sleep(5000);
			service.stopCountdown();
			service.joinThread();
			counter_status current_status=service.getStatus();
			assertEquals(counter_status.stopped, current_status);
			
		}
		catch(InterruptedException e)
		{
			System.err.println("interrupted: " +e);
		}
		
	}
	
	public void testAddObserverWhenNotRunning()
	{
		DependencyInjectingObjectFactory factory = new DependencyInjectingObjectFactory();		
		factory.registerImplementationClass(CountdownPublicationService.class,
				CountdownPublicationServiceImpl.class);

		CountdownPublicationService service = factory.getObject(CountdownPublicationService.class);
		service.addObserver(new CountdownObserver() {
			public void countdownNotification(int aRemainingTime) {
				currentCount = aRemainingTime;
			}
		});
		counter_status current_status=service.getStatus();
		assertEquals(counter_status.uninitialized, current_status);
				
	}

	public void testTimeUp()
	{
		DependencyInjectingObjectFactory factory = new DependencyInjectingObjectFactory();
		factory.registerImplementationClass(CountdownPublicationService.class,
				CountdownPublicationServiceImpl.class);

		try{
		CountdownPublicationService service = factory.getObject(CountdownPublicationService.class);
		service.setDuration(10);
		service.startCountdown();
		service.addObserver(new CountdownObserver() {
			public void countdownNotification(int aRemainingTime) {
				currentCount = aRemainingTime;
			}
		});
		service.joinThread();
		counter_status current_status=service.getStatus();
		assertEquals(counter_status.finished, current_status);
		}
		catch(InterruptedException e)
		{
			System.err.println("Exception occured:" +e);
		}
		
	}
}
