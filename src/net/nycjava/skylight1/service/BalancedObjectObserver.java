package net.nycjava.skylight1.service;

public interface BalancedObjectObserver {
	/**
	 * ???
	 */
	void balancedObjectNotification(float anX, float aY);

	/**
	 * ???
	 */
	void fallenOverNotification();
}
