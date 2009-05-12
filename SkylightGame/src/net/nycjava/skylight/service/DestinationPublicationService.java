package net.nycjava.skylight.service;

public interface DestinationPublicationService extends Observable<DestinationObserver> {
	void setDestinationPosition(Position aDestinationPosition);
}
