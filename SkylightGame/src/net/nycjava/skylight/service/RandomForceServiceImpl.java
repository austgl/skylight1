package net.nycjava.skylight.service;

import java.util.Timer;
import java.util.TimerTask;

import net.nycjava.skylight.dependencyinjection.Dependency;

public class RandomForceServiceImpl implements RandomForceService {
	private static final double MAXIMUM_MILLISECONDS_BETWEEN_FORCES = 1000;

	protected static final double MAXIMUM_FORCE = 1.0d;

	@Dependency
	private BalancedObjectPublicationService balancedObjectPublicationService;

	private Timer timer = new Timer();

	@Override
	public void start() {
		applyForceAtRandomTime();
	}

	private void applyForceAtRandomTime() {
		// TODO stop potential race condition between this method and stop()
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				balancedObjectPublicationService.applyForce((float) (Math.random() * Math.PI * 2), (float) (Math
						.random() * MAXIMUM_FORCE));
				applyForceAtRandomTime();
			}
		}, (long) (Math.random() * MAXIMUM_MILLISECONDS_BETWEEN_FORCES));
	}

	@Override
	public void stop() {
		timer.cancel();
	}

}
