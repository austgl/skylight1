package skylight1.marketapp;

import java.util.List;

import roboguice.application.GuiceApplication;

import com.google.inject.Module;

public class MarketAppApplication extends GuiceApplication {
	@Override
	protected void addApplicationModules(List<Module> aModules) {
		aModules.add(new MarketAppModule());
	}
}
