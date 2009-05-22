package net.nycjava.skylight.service;

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class BalancedObjectPublicationServiceImpl implements BalancedObjectPublicationService {
	private float positionX;

	private float positionY;

	private float velocityX;

	private float velocityY;

	private Set<BalancedObjectObserver> balancedObjectObservers = new HashSet<BalancedObjectObserver>();

	private final Timer timer = new Timer();

	private static final long PERIOD_IN_MILLISECONDS = 50;

	@Override
	public void applyForce(float anXForce, float aYForce) {
		velocityX += anXForce;
		velocityY += aYForce;
	}

	@Override
	public void addObserver(BalancedObjectObserver anObserver) {
		balancedObjectObservers.add(anObserver);
		if (balancedObjectObservers.size() == 1) {
			TimerTask timerTask = new TimerTask() {
				@Override
				public void run() {
					// update the position by the velocity
					positionX += velocityX / (1000 / PERIOD_IN_MILLISECONDS);
					positionY += velocityY / (1000 / PERIOD_IN_MILLISECONDS);

					notifyObservers();
				}
			};

			timer.scheduleAtFixedRate(timerTask, 0, PERIOD_IN_MILLISECONDS);

		}
	}

	@Override
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
	}
}
