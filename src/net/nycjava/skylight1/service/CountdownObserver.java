package net.nycjava.skylight1.service;

public interface CountdownObserver {
	/**
	 * Called in one second intervals to notify the observer of the remaining time in the countdown. Time is measured in
	 * seconds.
	 */
	void countdownNotification(int aRemainingTime);
}
