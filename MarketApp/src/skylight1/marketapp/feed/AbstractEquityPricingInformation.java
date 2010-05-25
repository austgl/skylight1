package skylight1.marketapp.feed;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import android.util.Log;
import skylight1.marketapp.model.EquityPricingInformation;

/*
 * This class has all the tickers for the app.
 * 
 */
public class AbstractEquityPricingInformation implements EquityPricingInformationFeed {
    private static String TAG = "AbstractEquityPricingInformation";

    private final Map<EquityFeedObserver, Set<String>> observers = new HashMap<EquityFeedObserver, Set<String>>();

    protected Set<String> getTickers() {
        Set<String> allObservedTickers = new HashSet<String>();

        // Need everything for Alerts
        for (Set<String> observedTickers : observers.values()) {
            allObservedTickers.addAll(observedTickers);
        }

        return allObservedTickers;
    }

    /*
     *
     */
    @Override
    public void addEquityFeedObserver(EquityFeedObserver anEquityFeedObserver, Set<String> aSetOfTickers) {
        // get (or create) the set of tickers for this observer
        final Set<String> tickers;
        if (observers.containsKey(anEquityFeedObserver)) {
            // get the existing set for an existing observer
            tickers = observers.get(anEquityFeedObserver);
        } else {
            // create a new set for a new observer
            tickers = new HashSet<String>();
            observers.put(anEquityFeedObserver, tickers);
        }

        // add the new tickers to the set, thanks to set semantics, duplicates are removed
        tickers.addAll(aSetOfTickers);
    }

    @Override
    public void removeEquityForObserver(EquityFeedObserver anEquityFeedObserver, Set<String> aSetOfTickers) {
        if (!observers.containsKey(anEquityFeedObserver)) {
            throw new IllegalArgumentException(String.format("Observer %s is not an observer of this feed", anEquityFeedObserver));
        }

        // get the set of tickers for this observer
        final Set<String> setOfTickers = observers.get(anEquityFeedObserver);

        // remove the requested tickers
        setOfTickers.removeAll(aSetOfTickers);

        // if there are no tickers remaining for this observer, then remove the observer
        if (setOfTickers.isEmpty()) {
            observers.remove(anEquityFeedObserver);
        }
    }

    @Override
    public void removeEquityFeedObserver(EquityFeedObserver anEquityFeedObserver) {
        observers.remove(anEquityFeedObserver);
    }

    protected void notifyObservers(Set<EquityPricingInformation> aSetOfEquityPricingInformation) {
        for (Entry<EquityFeedObserver, Set<String>> equityFeedObserverTicker : observers.entrySet()) {
            Log.i(TAG, "Telling observers");
            final Set<String> tickersForObserver = equityFeedObserverTicker.getValue();
            final Set<EquityPricingInformation> subsetOfEquityPricingInformation = new HashSet<EquityPricingInformation>();
            for (EquityPricingInformation equityPricingInformation : aSetOfEquityPricingInformation) {
                Log.i(TAG, "Should I tell " + tickersForObserver + ", " + equityPricingInformation);
                if (tickersForObserver.contains(equityPricingInformation.getTicker())) {
                    Log.i(TAG, "Telling...");
                    subsetOfEquityPricingInformation.add(equityPricingInformation);
                }
            }

            if (!subsetOfEquityPricingInformation.isEmpty()) {
                equityFeedObserverTicker.getKey().equityPricingInformationUpdate(subsetOfEquityPricingInformation);
            }
        }
    }
}
