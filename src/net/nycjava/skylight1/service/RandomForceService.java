package net.nycjava.skylight1.service;

/**
 * Applies random forces at random times to a BalancedObjectPublicationService.
 */
public interface RandomForceService {
	void start();
	
	void stop();

	public void setDifficultyLevel(int aLevel);
}
