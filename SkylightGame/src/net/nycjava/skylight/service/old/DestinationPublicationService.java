package net.nycjava.skylight.service.old;

import net.nycjava.skylight.service.Observable;

public interface DestinationPublicationService extends Observable<DestinationObserver> {
	void setDestinationPosition(Position aDestinationPosition);
}
