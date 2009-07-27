package net.nycjava.skylight.service.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import android.util.Log;

import net.nycjava.skylight.service.BalancedObjectObserver;
import net.nycjava.skylight.service.BalancedObjectPublicationService;

public class BalancedObjectPublicationServiceImpl implements BalancedObjectPublicationService {
	private static final int NUMBER_OF_MILLISECONDS_IN_A_SECOND = 1000;
	private final String TAG = "BalancedObjectPublicationService";

	private float positionX;

	private float positionY;

	private float velocityX;

	private float velocityY;

	private Set<BalancedObjectObserver> balancedObjectObservers = new HashSet<BalancedObjectObserver>();

	private final Timer timer = new Timer();

	public BalancedObjectPublicationServiceImpl() {
		super();
	}

	private static final float FRICTION_COEFF = 0.05f;
	private static final long PERIOD_IN_MILLISECONDS = 50;
	private static final float SO_SLOW_IT_SHOULD_STOP = 0.005f;
	
	public void applyForce(float anXForce, float aYForce,long duration) {
		
		// not used but here for reference
		//float frictionX = (Math.abs(velocityX)<SO_SLOW_IT_SHOULD_STOP)?-velocityX:-velocityX*FRICTION_COEFF;
		//float frictionY = (Math.abs(velocityY)<SO_SLOW_IT_SHOULD_STOP)?-velocityY:-velocityY*FRICTION_COEFF;
		//velocityX += frictionX;
		//velocityY += frictionY;
		velocityX += anXForce*duration/NUMBER_OF_MILLISECONDS_IN_A_SECOND;
		velocityY += aYForce *duration/NUMBER_OF_MILLISECONDS_IN_A_SECOND;
		//Log.d(TAG,Float.toString(velocityX) + " " + Float.toString(velocityY));
	}

	public void addObserver(BalancedObjectObserver anObserver) {
		balancedObjectObservers.add(anObserver);
		if (balancedObjectObservers.size() == 1) {
			TimerTask timerTask = new TimerTask() {
				@Override
				public void run() {
					// update the position by the velocity
					positionX += velocityX / (NUMBER_OF_MILLISECONDS_IN_A_SECOND / PERIOD_IN_MILLISECONDS);
					positionY += velocityY / (NUMBER_OF_MILLISECONDS_IN_A_SECOND / PERIOD_IN_MILLISECONDS);

					notifyObservers();
				}
			};

			timer.scheduleAtFixedRate(timerTask, 0, PERIOD_IN_MILLISECONDS);
		}
	}

	public boolean removeObserver(BalancedObjectObserver anObserver) {
		final boolean existed = balancedObjectObservers.remove(anObserver);
		if (balancedObjectObservers.isEmpty()) {
			timer.cancel();
		}
		return existed;
	}

	private void notifyObservers() {
		for (BalancedObjectObserver observer : balancedObjectObservers) {
			observer.balancedObjectNotification(positionX, positionY);
		}
		if (Math.abs(positionX) > 1f || Math.abs(positionY) > 1f) {
			for (BalancedObjectObserver observer : balancedObjectObservers) {
				observer.fallenOverNotification();
			}
		}
	}
}
