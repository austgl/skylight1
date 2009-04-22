package net.nycjava.skylight.service;

/**
 * A service to allow observers to subscribe to position events. The first position notification for each observer
 * reports position 0, 0, 0; subsequent position notifications are relative to this origin.
 * 
 * TODO Consider controlling the frequency and accuracy of the service. There is no requirement for that yet, so hold
 * off on making a decision for now.
 */
public interface PositionPublicationService extends Observable<PositionObserver> {

}
