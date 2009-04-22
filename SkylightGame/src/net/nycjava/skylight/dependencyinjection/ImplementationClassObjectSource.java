package net.nycjava.skylight.dependencyinjection;

// TODO javadoc
class ImplementationClassObjectSource<T, S extends T> extends ObjectSource<T> {
	private Class<S> clazz;

	ImplementationClassObjectSource(DependencyInjectingObjectFactory aDependencyInjectingObjectFactory, Class<S> aClass) {
		super(aDependencyInjectingObjectFactory);
		clazz = aClass;
	}

	@Override
	public T getObject() {
		final T object;
		try {
			object = clazz.newInstance();
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		}
		new DependencyInjector(dependencyInjectingObjectFactory).injectDependenciesForClassHierarchy(object);
		return object;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
}