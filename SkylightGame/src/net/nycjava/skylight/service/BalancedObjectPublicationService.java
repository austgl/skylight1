package net.nycjava.skylight.service;

public interface BalancedObjectPublicationService extends Observable<BalancedObjectObserver> {
	void applyForce(float anXForce, float aYForce, long aDuration);
}
