package net.nycjava.skylight1.dependencyinjection;

//TODO javadoc
abstract class ObjectSource<T> {
	DependencyInjectingObjectFactory dependencyInjectingObjectFactory;

	ObjectSource(DependencyInjectingObjectFactory aDependencyInjectingObjectFactory) {
		dependencyInjectingObjectFactory = aDependencyInjectingObjectFactory;
	}

	abstract T getObject();
}