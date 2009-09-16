package net.nycjava.skylight1.dependencyinjection;

import static java.lang.String.format;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory that creates objects with their dependencies injected. Object dependencies are required to be coded as fields
 * (static or instance) annotated with the Dependency annotation. Supports object graph cycles through a proxy-based
 * lazy-dependency-injection of interface types; class types require eager-dependency-injection.
 * 
 * Example usage: <code>
 // register implementations
 DependencyInjectingObjectFactory dependencyInjectingObjectFactory = new DependencyInjectingObjectFactory();
 dependencyInjectingObjectFactory.registerImplementationClass(Y.class, YImpl.class);
 dependencyInjectingObjectFactory.registerImplementationClass(X.class, XImpl.class);

 // obtain an instance of Y with its dependency on X pre-populated
 Y y1 = dependencyInjectingObjectFactory.getObject(Y.class);
 * </code>
 */
public class DependencyInjectingObjectFactory {
	private Map<Class<?>, ObjectSource<?>> implementationSources = new HashMap<Class<?>, ObjectSource<?>>();

	/**
	 * Registers a class as the implementor of the type. A <b>new</b> instance of the class will be injected into every
	 * matching dependency.
	 */
	public <T, S extends T> void registerImplementationClass(Class<T> anInterface, Class<S> anImplementationClass) {
		checkClassNotAlreadyRegistered(anInterface);
		implementationSources.put(anInterface, new ImplementationClassObjectSource<T, S>(this, anImplementationClass));
	}

	/**
	 * Registers a class as the implementor of the type. A <b>single</b> instance of the class will be injected into
	 * every matching dependency.
	 */
	public <T, S extends T> void registerSingletonImplementationClass(Class<T> anInterface,
			Class<S> anImplementationClass) {
		checkClassNotAlreadyRegistered(anInterface);
		implementationSources.put(anInterface, new ImplementationClassSingletonObjectSource<T, S>(this, this,
				anImplementationClass));
	}

	/**
	 * Registers a single object as the implementor of the type. The object will be injected into every matching
	 * dependency.
	 */
	public <T, S extends T> void registerImplementationObject(Class<T> anInterface, S anImplementationObject) {
		checkClassNotAlreadyRegistered(anInterface);
		implementationSources.put(anInterface, new SingletonObjectSource<T>(this, this, anImplementationObject));
	}

	/**
	 * If the passed class was not previously registered, throws an IllegalArgumentException. Otherwise, based on the
	 * prior registrations, returns an object that is assignable to the passed type, and which has had all of its
	 * dependencies injected.
	 */
	@SuppressWarnings("unchecked")
	public <T> T getObject(Class<T> aClass) {
		if (implementationSources.containsKey(aClass)) {
			return (T) implementationSources.get(aClass).getObject();
		}

		throw new RuntimeException(format("No object source was registered for class %s.", aClass.getCanonicalName()));
	}

	@SuppressWarnings("unchecked")
	<T, S extends T> ObjectSource<S> getObjectSource(Class<T> aClass) {
		return (ObjectSource<S>) implementationSources.get(aClass);
	}

	private void checkClassNotAlreadyRegistered(Class<?> aClass) {
		if (implementationSources.containsKey(aClass)) {
			throw new IllegalArgumentException(format(
					"Unable to register for the class %s as it is already registered: %s.", aClass.getCanonicalName(),
					implementationSources.get(aClass).toString()));
		}
	}
}
