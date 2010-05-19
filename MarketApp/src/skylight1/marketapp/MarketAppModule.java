package skylight1.marketapp;

import roboguice.config.AbstractAndroidModule;
import skylight1.marketapp.feed.DummyEquityPricingInformationFeed;
import skylight1.marketapp.feed.EquityPricingInformationFeed;

public class MarketAppModule extends AbstractAndroidModule {
	@Override
	protected void configure() {
		bind(EquityPricingInformationFeed.class).to(DummyEquityPricingInformationFeed.class);
	}
}
