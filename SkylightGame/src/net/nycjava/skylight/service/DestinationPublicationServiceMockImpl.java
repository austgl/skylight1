package net.nycjava.skylight.service;

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class DestinationPublicationServiceMockImpl implements DestinationPublicationService {
	private Position currentPosition;

	private Set<DestinationObserver> destinationObservers = new HashSet<DestinationObserver>();

	private Position destinationPosition;

	private TimerTask timerTask;

	public void setDestinationPosition(Position aDestinationPosition) {
		destinationPosition = aDestinationPosition;
	}

	public void addObserver(DestinationObserver anObserver) {
		destinationObservers.add(anObserver);
		if (destinationObservers.size() == 1) {
			currentPosition = new Position(0, 0, 0);
			timerTask = new TimerTask() {
				@Override
				public void run() {
					currentPosition = new Position(currentPosition.x, currentPosition.y + 0.01f, currentPosition.z);
					if (currentPosition.y >= destinationPosition.y) {
						currentPosition = destinationPosition;
					}
					notifyObservers(currentPosition);
				}
			};
			Timer timer = new Timer();
			timer.scheduleAtFixedRate(timerTask, 0, 100);
		}
	}

	public boolean removeObserver(DestinationObserver anObserver) {
		final boolean existed = destinationObservers.remove(anObserver);
		if (destinationObservers.isEmpty()) {
			timerTask.cancel();
		}
		return existed;
	}

	private void notifyObservers(Position aPosition) {
		final float angle = 120;
		final float distance = (float) Math.sqrt(Math.pow((double) aPosition.getX()
				- (double) destinationPosition.getX(), 2)
				+ Math.pow((double) aPosition.getY() - (double) destinationPosition.getY(), 2)
				+ Math.pow((double) aPosition.getZ() - (double) destinationPosition.getZ(), 2));

		for (DestinationObserver destinationObserver : destinationObservers) {
			destinationObserver.destinationNotification(angle, distance);
		}
	}
}
