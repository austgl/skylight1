package net.nycjava.skylight.service;

/**
 * Depends on a BalancedObjectPublicationService and a SensorManager. Adapts Touch events to apply forces to a
 * BalancedObjectPublicationService.
 */
public interface TouchAppliedForceAdapter {
	void start();
	
	void stop();
}
