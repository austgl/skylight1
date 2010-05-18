package skylight1.marketapp.feed;

import java.util.Set;

import skylight1.marketapp.model.EquityPricingInformation;

public interface EquityFeedObserver {
	void equityPricingInformationUpdate(Set<EquityPricingInformation> aSetOfEquityPricingInformation);
}
