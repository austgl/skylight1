package net.nycjava.skylight.service;

import java.util.Timer;
import java.util.TimerTask;

import net.nycjava.skylight.dependencyinjection.Dependency;

public class RandomForceServiceImpl implements RandomForceService {
	private static final double MAXIMUM_MILLISECONDS_BETWEEN_FORCES = 2000;

	private static final double MINIMUM_MILLISECONDS_BETWEEN_FORCES = 1000;

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
				balancedObjectPublicationService.applyForce(2f - 2f * (float) Math.random(), 2f - 2f * (float) Math
						.random());
				applyForceAtRandomTime();
			}
		}, (long) (MINIMUM_MILLISECONDS_BETWEEN_FORCES + Math.random()
				* (MAXIMUM_MILLISECONDS_BETWEEN_FORCES - MINIMUM_MILLISECONDS_BETWEEN_FORCES)));
	}

	@Override
	public void stop() {
		timer.cancel();
	}

}
