package net.nycjava.skylight1.service;

/**
 * Type-safe generic interface that can be extended or implemented by an observable.
 */
public interface Observable<T> {
	/**
	 * Adds an observer to the observable.
	 */
	void addObserver(T anObserver);

	/**
	 * Removes an observer from the observable. If the observer was observing, returns true, else false.
	 */
	boolean removeObserver(T anObserver);
}
