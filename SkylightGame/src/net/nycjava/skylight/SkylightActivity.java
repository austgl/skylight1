package net.nycjava.skylight;

import net.nycjava.skylight.dependencyinjection.DependencyInjectingObjectFactory;
import net.nycjava.skylight.dependencyinjection.DependencyInjector;
import android.app.Activity;
import android.os.Bundle;

public abstract class SkylightActivity extends Activity {
	protected DependencyInjectingObjectFactory dependencyInjectingObjectFactory;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		dependencyInjectingObjectFactory = new DependencyInjectingObjectFactory();
		addDependencies(dependencyInjectingObjectFactory);

		// since activities are instantiated by the framework, use the dependency injector directly to inject any
		// dependencies this activity may have
		new DependencyInjector(dependencyInjectingObjectFactory).injectDependenciesForClassHierarchy(this);
	}

	abstract protected void addDependencies(DependencyInjectingObjectFactory aDependencyInjectingObjectFactory);
}