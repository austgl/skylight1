package skylight1.marketapp.feed;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import skylight1.marketapp.model.EquityPricingInformation;
import android.util.Log;

public class DummyEquityPricingInformationFeed extends AbstractEquityPricingInformation {
	protected Set<EquityPricingInformation> dummySetOfEquityPricingInformation = new HashSet<EquityPricingInformation>();

	public DummyEquityPricingInformationFeed() {
		dummySetOfEquityPricingInformation
				.add(new EquityPricingInformation("INTC", "Intel", new BigDecimal(50), new BigDecimal(100000), new Date(), new BigDecimal(40), new BigDecimal(120000), new Date()));

		new Timer().scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				Log.i(DummyEquityPricingInformationFeed.class.getName(), "here we are");
				notifyObservers(dummySetOfEquityPricingInformation);
			}
		}, 2000, 2000);
	}
}
