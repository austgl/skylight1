package net.nycjava.skylight1.dependencyinjection;

//TODO javadoc
class SingletonObjectSource<T> extends ObjectSource<T> {

	private final DependencyInjectingObjectFactory dependencyInjectingObjectFactory;

	private T singleObject;

	private boolean dependenciesInjected;

	SingletonObjectSource(DependencyInjectingObjectFactory dependencyInjectingObjectFactory,
			DependencyInjectingObjectFactory aDependencyInjectingObjectFactory, T aSingleObject) {
		super(aDependencyInjectingObjectFactory);
		this.dependencyInjectingObjectFactory = dependencyInjectingObjectFactory;
		singleObject = aSingleObject;
	}

	@Override
	public T getObject() {
		if (!dependenciesInjected) {
			new DependencyInjector(dependencyInjectingObjectFactory).injectDependenciesForClassHierarchy(singleObject);
			dependenciesInjected = true;
		}
		return singleObject;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
}