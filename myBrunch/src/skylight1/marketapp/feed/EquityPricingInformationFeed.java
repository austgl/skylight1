package skylight1.marketapp.feed;

import java.util.Set;

/**
 * A feed of equity pricing information. Interested observers may add and remove themselves.
 */
public interface EquityPricingInformationFeed {
	/**
	 * Register an observer to receive updates for a given list of tickers. The observer may already be registered for
	 * for other tickers.
	 */
	void addEquityFeedObserver(EquityFeedObserver anEquityFeedObserver, Set<String> aListOfTickers);

	/**
	 * Remove the registration of an observer for a given list of tickers.
	 */
	void removeEquityForObserver(EquityFeedObserver anEquityFeedObserver, Set<String> aListOfTickers);

	/**
	 * Unregisters an observer for all of its tickers.
	 */
	void removeEquityFeedObserver(EquityFeedObserver anEquityFeedObserver);
}
