package net.nycjava.skylight.service.impl;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import net.nycjava.skylight.service.BalancedObjectObserver;
import net.nycjava.skylight.service.BalancedObjectPublicationService;

public class BalancedObjectPublicationServiceImpl implements BalancedObjectPublicationService {
	private static final int NUMBER_OF_MILLISECONDS_IN_A_SECOND = 1000;

	private float positionX;

	private float positionY;

	private float velocityX;

	private float velocityY;

	private int difficultyLevel;

	private boolean isServiceRunning;

	final private ArrayList<BalancedObjectObserver> balancedObjectObservers = new ArrayList<BalancedObjectObserver>();

	private Timer timer;

	public BalancedObjectPublicationServiceImpl() {
		super();
	}

	private static final float FRICTION_COEFF = 0.3f;

	private static final long PERIOD_IN_MILLISECONDS = 16; // seems to be a bug by a factor of 10??? only in emulator???

	public void applyForce(float anXForce, float aYForce, long duration) {

		// as long as X Y forces imply less than 45 deg angle
		if (difficultyLevel < 5 && (anXForce < 6.8) && (aYForce < 6.8)) {
			// scale FRICTION_COEFF based on difficulty level
			anXForce = anXForce * (FRICTION_COEFF + (difficultyLevel * 0.1f));
			aYForce = aYForce * (FRICTION_COEFF + (difficultyLevel * 0.1f));
		}
		velocityX += anXForce * duration / NUMBER_OF_MILLISECONDS_IN_A_SECOND;
		velocityY += aYForce * duration / NUMBER_OF_MILLISECONDS_IN_A_SECOND;
		// Log.d(TAG,Float.toString(velocityX) + " " + Float.toString(velocityY));
	}

	public void addObserver(BalancedObjectObserver anObserver) {
		if (!balancedObjectObservers.contains(anObserver)) {
			balancedObjectObservers.add(anObserver);
		}
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

			if (timer == null) {
				timer = new Timer();
			}
			timer.scheduleAtFixedRate(timerTask, 0, PERIOD_IN_MILLISECONDS);
		}
	}

	public boolean removeObserver(BalancedObjectObserver anObserver) {
		final boolean existed = balancedObjectObservers.remove(anObserver);
		if (balancedObjectObservers.isEmpty()) {
			timer.cancel();
			timer = null;
		}
		return existed;
	}

	private void notifyObservers() {
		if(isServiceRunning) {
			final int balancedObjectObserversSize = balancedObjectObservers.size();
			for (int i = 0; i < balancedObjectObserversSize; i++) {
				final BalancedObjectObserver observer = balancedObjectObservers.get(i);
				observer.balancedObjectNotification(positionX, positionY);
			}
			if ( positionX < -1f || positionX > 1f || positionY < -1f || positionY > 1f) {
				for (int i = 0; i < balancedObjectObserversSize; i++) {
					final BalancedObjectObserver observer = balancedObjectObservers.get(i);
					observer.fallenOverNotification();
				}
			}
		}
	}

	public void setDifficultyLevel(int aDifficulty) {
		difficultyLevel = aDifficulty;
	}

	public void startService() {
		isServiceRunning = true;
	}

	public void stopService() {
		isServiceRunning = false;
	}
}
