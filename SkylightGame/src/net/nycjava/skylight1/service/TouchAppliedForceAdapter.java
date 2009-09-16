package net.nycjava.skylight1.service;

/**
 * Depends on a BalancedObjectPublicationService and a SensorManager. Adapts Touch events to apply forces to a
 * BalancedObjectPublicationService.
 */
public interface TouchAppliedForceAdapter {
	void start();
	
	void stop();
}
