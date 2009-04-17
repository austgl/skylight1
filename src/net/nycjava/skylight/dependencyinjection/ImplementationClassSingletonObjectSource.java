package net.nycjava.skylight.dependencyinjection;

//TODO javadoc
class ImplementationClassSingletonObjectSource<T, S extends T> extends ObjectSource<T> {
	private final DependencyInjectingObjectFactory dependencyInjectingObjectFactory;

	private Class<S> clazz;

	private S singleObject;

	ImplementationClassSingletonObjectSource(DependencyInjectingObjectFactory dependencyInjectingObjectFactory,
			DependencyInjectingObjectFactory aDependencyInjectingObjectFactory, Class<S> aClass) {
		super(aDependencyInjectingObjectFactory);
		this.dependencyInjectingObjectFactory = dependencyInjectingObjectFactory;
		clazz = aClass;
	}

	@Override
	public T getObject() {
		if (singleObject == null) {
			try {
				singleObject = clazz.newInstance();
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (InstantiationException e) {
				throw new RuntimeException(e);
			}
			new DependencyInjector(dependencyInjectingObjectFactory).injectDependenciesForClassHierarchy(singleObject);
		}
		return singleObject;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
}