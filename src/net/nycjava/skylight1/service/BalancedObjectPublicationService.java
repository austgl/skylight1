package net.nycjava.skylight1.service;

public interface BalancedObjectPublicationService extends Observable<BalancedObjectObserver> {
	void applyForce(float anXForce, float aYForce, long aDuration);
	void setDifficultyLevel(int aDifficulty);
	void startService();
	void stopService();
}
