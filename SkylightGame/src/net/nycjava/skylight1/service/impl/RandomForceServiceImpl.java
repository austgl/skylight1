package net.nycjava.skylight1.service.impl;

import java.util.Timer;
import java.util.TimerTask;

import net.nycjava.skylight1.dependencyinjection.Dependency;
import net.nycjava.skylight1.service.BalancedObjectPublicationService;
import net.nycjava.skylight1.service.RandomForceService;

public class RandomForceServiceImpl implements RandomForceService {
	
	private static final int NUMBER_OF_MILLISECONDS_FASTER_PER_DIFFICULTY_LEVEL = 25;

	private static final double MAXIMUM_MILLISECONDS_BETWEEN_FORCES = 2000;

	private static final double MINIMUM_MILLISECONDS_BETWEEN_FORCES = 1000;

	protected static final double MAXIMUM_FORCE = 1.0d;

	protected static final float FORCE_FACTOR = 0.3f;
	
	static final float forceAdj[] = { 
		1.0f,
		1.0f,
		1.2f,
		1.4f,
		1.6f
	};
	
	static final float difficultyLevels[] = {
		1.0f, // easy (sober) 0
		1.1f,
		1.2f,
		1.3f,
		1.4f,
		1.5f, // medium (buzzed) 5
		1.6f,
		1.7f,
		1.8f,
		1.9f,
		2.0f, // hard (smashed) 10
		2.2f,
		2.4f,
		2.6f,
		2.8f,
		3.0f  // insane (blackout) 15+
	};

	@Dependency
	private BalancedObjectPublicationService balancedObjectPublicationService;

	private Timer timer;
	
	public int difficultyLevel;
	
	@Override
	public void start() {
		timer = new Timer();
		applyForceAtRandomTime();
	}

	private void applyForceAtRandomTime() {
		// TODO stop potential race condition between this method and stop()
		final double minimumMillisecondsBetweenForcesGivenDifficulty = MINIMUM_MILLISECONDS_BETWEEN_FORCES - Math.min(MINIMUM_MILLISECONDS_BETWEEN_FORCES, difficultyLevel * NUMBER_OF_MILLISECONDS_FASTER_PER_DIFFICULTY_LEVEL);
		final double maximumMillisecondsBetweenForcesGivenDifficulty = MAXIMUM_MILLISECONDS_BETWEEN_FORCES - Math.min(MAXIMUM_MILLISECONDS_BETWEEN_FORCES, difficultyLevel * NUMBER_OF_MILLISECONDS_FASTER_PER_DIFFICULTY_LEVEL);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				balancedObjectPublicationService.applyForce(adjRandom(),adjRandom(),50);
				applyForceAtRandomTime();
			}
		}, (long) (minimumMillisecondsBetweenForcesGivenDifficulty + Math.random()
				* (maximumMillisecondsBetweenForcesGivenDifficulty - minimumMillisecondsBetweenForcesGivenDifficulty)));
	}

	@Override
	public void stop() {
		timer.cancel();
		timer = null;
	}
	
	/**
	 * Try to distribute the forces differently so that weak forces are more common
	 * than strong forces. Use difficulty level as a multiplier for the force used.
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
		
		if(difficultyLevel >= difficultyLevels.length) {
			answer = answer * difficultyLevels[difficultyLevels.length - 1];
		} else {
			answer = answer * difficultyLevels[difficultyLevel];
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
