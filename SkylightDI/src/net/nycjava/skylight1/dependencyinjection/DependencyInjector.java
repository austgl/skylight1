package net.nycjava.skylight1.dependencyinjection;

import static java.lang.String.format;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;

/**
 * Given a DependencyInjectingObjectFactory, injects dependencies into a class.
 */
public class DependencyInjector {
	private DependencyInjectingObjectFactory dependencyInjectingObjectFactory;

	public DependencyInjector(DependencyInjectingObjectFactory aDependencyInjectingObjectFactory) {
		dependencyInjectingObjectFactory = aDependencyInjectingObjectFactory;
	}

	@SuppressWarnings("unchecked")
	public <T> void injectDependenciesForClassHierarchy(T anObject) {
		// the reflection API requires that each class in the hierarchy be considered
		// start with the lowest class in the hierarchy
		Class<?> interfaceOfObject = anObject.getClass();

		do {
			// inject the dependencies for this class
			injectDependenciesForSingleClass(anObject, (Class<T>) interfaceOfObject);

			// move on up the class hierarchy...
			interfaceOfObject = interfaceOfObject.getSuperclass();

			// until the top is reached
		} while (interfaceOfObject != null);
	}

	private <T, S extends T> void injectDependenciesForSingleClass(S anObject, Class<T> aClass) {
		// for each
		for (Field field : aClass.getDeclaredFields()) {
			if (field.isAnnotationPresent(Dependency.class)) {
				field.setAccessible(true);
				try {
					// if the field has not already been set (possibly through injection)...
					if (field.get(anObject) == null) {
						Class<?> classOfDependency = field.getType();
						final Object injectedValue;
						final int modifiers = classOfDependency.getModifiers();
						if (Modifier.isInterface(modifiers) || Modifier.isAbstract(modifiers)) {
							injectedValue = createProxy(classOfDependency);
						} else {
							injectedValue = dependencyInjectingObjectFactory.getObject(classOfDependency);
						}
						field.set(anObject, injectedValue);
					}
				} catch (IllegalArgumentException e) {
					throw new RuntimeException(e);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(format("Unable to access field %s.", field.getName()), e);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private <T> T createProxy(final Class<T> aClass) {
		// TODO optimize this to replace the proxy once the object is lazily created
		return (T) Proxy.newProxyInstance(DependencyInjectingObjectFactory.class.getClassLoader(),
				new Class[] { aClass }, new InvocationHandler() {
					private T object;

					public Object invoke(Object aProxy, Method aMethod, Object[] anArrayOfArguments) throws Throwable {
						if (object == null) {
							final ObjectSource<T> objectSource = dependencyInjectingObjectFactory
									.getObjectSource(aClass);
							if (objectSource == null) {
								throw new RuntimeException(format(
										"No source was registered for the dependecy of type %s.", aClass.getName()));
							}
							object = (T) objectSource.getObject();
						}
						return aMethod.invoke(object, anArrayOfArguments);
					}
				});
	}

}
