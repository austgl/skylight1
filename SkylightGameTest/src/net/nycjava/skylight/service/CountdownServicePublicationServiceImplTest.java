package net.nycjava.skylight.service;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;
import net.nycjava.skylight.dependencyinjection.DependencyInjectingObjectFactory;
import net.nycjava.skylight.service.impl.CountdownPublicationServiceImpl;
import static net.nycjava.skylight.service.CountdownPublicationService.CounterStatus;

public class CountdownServicePublicationServiceImplTest extends TestCase {
	private int currentCount = -1;

	private Set<Integer> notifications = new HashSet<Integer>();

	public void testAddObserverAndStart() {
		DependencyInjectingObjectFactory factory = new DependencyInjectingObjectFactory();
		factory.registerImplementationClass(CountdownPublicationService.class, CountdownPublicationServiceImpl.class);

		CountdownPublicationService service = factory.getObject(CountdownPublicationService.class);
		service.setDuration(10);

		service.addObserver(new CountdownObserver() {
			public void countdownNotification(int aRemainingTime) {
				currentCount = aRemainingTime;
				System.out.println("notified: " + currentCount);
				notifications.add(new Integer(currentCount));
			}
		});
		service.startCountdown();
		while (service.getStatus() != CounterStatus.finished) {
			try {
				Thread.sleep(2);
			} catch (InterruptedException e) {
				System.err.println("test thread is interrupted");
			}
		}

		System.out.println("notifications " + notifications.size());
		assertEquals("all notifications: " + notifications.toString(), 10, notifications.size());
	}

	public void testStop() {
		DependencyInjectingObjectFactory factory = new DependencyInjectingObjectFactory();
		factory.registerImplementationClass(CountdownPublicationService.class, CountdownPublicationServiceImpl.class);

		try {
			CountdownPublicationService service = factory.getObject(CountdownPublicationService.class);
			service.setDuration(50);
			service.startCountdown();
			Thread.sleep(5000);
			service.stopCountdown();

			CounterStatus current_status = service.getStatus();
			assertEquals(CounterStatus.stopped, current_status);

		} catch (InterruptedException e) {
			System.err.println("interrupted: " + e);
		}

	}

	public void testAddObserverWhenNotRunning() {
		DependencyInjectingObjectFactory factory = new DependencyInjectingObjectFactory();
		factory.registerImplementationClass(CountdownPublicationService.class, CountdownPublicationServiceImpl.class);

		CountdownPublicationService service = factory.getObject(CountdownPublicationService.class);
		service.addObserver(new CountdownObserver() {
			public void countdownNotification(int aRemainingTime) {
				currentCount = aRemainingTime;
			}
		});
		CounterStatus current_status = service.getStatus();
		assertEquals(CounterStatus.uninitialized, current_status);

	}

	public void testTimeUp() {
		DependencyInjectingObjectFactory factory = new DependencyInjectingObjectFactory();
		factory.registerImplementationClass(CountdownPublicationService.class, CountdownPublicationServiceImpl.class);

		CountdownPublicationService service = factory.getObject(CountdownPublicationService.class);
		service.setDuration(10);
		service.startCountdown();
		service.addObserver(new CountdownObserver() {
			public void countdownNotification(int aRemainingTime) {
				currentCount = aRemainingTime;
			}
		});
		while (service.getStatus() != CounterStatus.finished) {
			try {
				Thread.sleep(2);
			} catch (InterruptedException e) {
				System.err.println("test thread is interrupted");
			}
		}
		assertEquals(CounterStatus.finished, service.getStatus());

	}
}
