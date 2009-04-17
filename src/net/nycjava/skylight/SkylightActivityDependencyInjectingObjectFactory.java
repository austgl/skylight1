package net.nycjava.skylight;

import net.nycjava.skylight.dependencyinjection.DependencyInjectingObjectFactory;
import net.nycjava.skylight.service.AndroidPositionPublicationService;
import net.nycjava.skylight.service.PositionPublicationService;
import android.widget.LinearLayout;

public class SkylightActivityDependencyInjectingObjectFactory extends DependencyInjectingObjectFactory {
	public SkylightActivityDependencyInjectingObjectFactory(SkylightActivity aSkylightActivity) {
		registerImplementationClass(PositionPublicationService.class, AndroidPositionPublicationService.class);
		registerImplementationObject(LinearLayout.class, (LinearLayout) aSkylightActivity.getLayoutInflater().inflate(
				R.layout.welcome, null));
	}
}
