package net.nycjava.skylight.service;

/**
 * A service to allow observers to subscribe to position events. The first position notification occurs <i>during</i>
 * the call to addObserver. Subsequent position notifications do not occur until after the call to addObserver returns.
 * 
 * TODO Consider controlling the frequency and accuracy of the service. There is no requirement for that yet, so hold
 * off on making a decision for now.
 */
public interface PositionPublicationService extends Observable<PositionObserver> {

}
