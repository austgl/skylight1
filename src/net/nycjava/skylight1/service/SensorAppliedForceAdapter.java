package net.nycjava.skylight1.service;

/**
 * Depends on a BalancedObjectPublicationService and a SensorManager. Adapts SensorManager events to apply forces to a
 * BalancedObjectPublicationService.
 */
public interface SensorAppliedForceAdapter {
	void start();
	
	void stop();
}
