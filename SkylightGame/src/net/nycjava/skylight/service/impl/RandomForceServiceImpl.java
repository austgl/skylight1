package net.nycjava.skylight.service.impl;

import java.util.Timer;
import java.util.TimerTask;

import net.nycjava.skylight.dependencyinjection.Dependency;
import net.nycjava.skylight.service.BalancedObjectPublicationService;
import net.nycjava.skylight.service.RandomForceService;

public class RandomForceServiceImpl implements RandomForceService {
	private static final double MAXIMUM_MILLISECONDS_BETWEEN_FORCES = 2000;

	private static final double MINIMUM_MILLISECONDS_BETWEEN_FORCES = 1000;

	protected static final double MAXIMUM_FORCE = 1.0d;

	protected static final float FORCE_FACTOR = 0.3f;
	
	static final float forceAdj[] = { 
		1.5f,
		1.8f,
		2.0f,
		2.5f,
		3.0f
	};

	@Dependency
	private BalancedObjectPublicationService balancedObjectPublicationService;

	private Timer timer = new Timer();
	
	private int difficultyLevel;

	@Override
	public void start() {
		applyForceAtRandomTime();
	}

	private void applyForceAtRandomTime() {
		// TODO stop potential race condition between this method and stop()
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				balancedObjectPublicationService.applyForce(adjRandom(),adjRandom(),50);
//				balancedObjectPublicationService.applyForce((1f - 2f * (float) Math.random()) * FORCE_FACTOR,
//						(1f - 2f * (float) Math.random()) * FORCE_FACTOR);
				applyForceAtRandomTime();
			}
		}, (long) (MINIMUM_MILLISECONDS_BETWEEN_FORCES + Math.random()
				* (MAXIMUM_MILLISECONDS_BETWEEN_FORCES - MINIMUM_MILLISECONDS_BETWEEN_FORCES)));
	}

	@Override
	public void stop() {
		timer.cancel();
	}
	
	/**
	 * Try to distribute the forces differently so that weak forces are more common
	 * than strong forces.
	 * @return random force
	 */
	private float adjRandom() {
		float answer;
		float value;
		double sign;
		sign = Math.random();
		
		value = (float)Math.random();
		if(value < 0.2) {
			answer = forceAdj[0];
		} else if (value < 0.4) {
			answer = forceAdj[1];
		} else if (value < 0.6) {
			answer = forceAdj[2];	
		} else if (value < 0.8) {
			answer = forceAdj[3];
		} else {
			answer = forceAdj[4];
		}
		
		if(sign < 0.5) {
			answer = -answer;
		}
		return answer;
	}
	
	public void setDifficultyLevel(int aLevel) {
		difficultyLevel = aLevel;
	}
}
