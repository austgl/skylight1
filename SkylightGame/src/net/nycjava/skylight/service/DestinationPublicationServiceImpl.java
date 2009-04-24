package net.nycjava.skylight.service;

import java.util.HashSet;
import java.util.Set;

import net.nycjava.skylight.dependencyinjection.Dependency;

public class DestinationPublicationServiceImpl implements DestinationPublicationService {
	@Dependency
	private PositionPublicationService positionPublicationService;

	private Set<DestinationObserver> destinationObservers = new HashSet<DestinationObserver>();

	private PositionObserver positionObserver;

	private Position destinationPosition;

	public void setDestinationPosition(Position aDestinationPosition) {
		destinationPosition = aDestinationPosition;
	}

	@Override
	public void addObserver(DestinationObserver anObserver) {
		destinationObservers.add(anObserver);
		if (destinationObservers.size() == 1) {
			positionObserver = new PositionObserver() {

				@Override
				public void positionNotification(Position aPosition) {
					notifyObservers(aPosition);
				}
			};
			positionPublicationService.addObserver(positionObserver);
		}
	}

	@Override
	public boolean removeObserver(DestinationObserver anObserver) {
		final boolean existed = destinationObservers.remove(anObserver);
		if (destinationObservers.isEmpty()) {
			positionPublicationService.removeObserver(positionObserver);
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
