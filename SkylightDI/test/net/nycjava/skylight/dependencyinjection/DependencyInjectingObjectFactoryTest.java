package net.nycjava.skylight.dependencyinjection;

import junit.framework.TestCase;

public class DependencyInjectingObjectFactoryTest extends TestCase {
	static interface Y {
		void setValue(int aValue);

		int getValue();

		X getX1();

		X getX2();
	}

	static class YImpl implements Y {
		private int value;

		@Dependency
		private X x1;

		private X x2;

		public void setValue(int aValue) {
			value = aValue;
		}

		public int getValue() {
			return value;
		}

		public X getX1() {
			return x1;
		}

		public X getX2() {
			return x2;
		}
	}

	static interface X {

		Y getY1();

		Y getY2();
	}

	static class XImpl implements X {
		@Dependency
		private Y y1;

		private Y y2;

		public Y getY1() {
			return y1;
		}

		public Y getY2() {
			return y2;
		}
	}

	static interface Z {
		Y getY1();

		Y getY2();
	}

	static class ZImpl implements Z {
		@Dependency
		private YImpl y1;

		private YImpl y2;

		public Y getY1() {
			return y1;
		}

		public Y getY2() {
			return y2;
		}
	}

	public void testRegisterImplementationClass() {
		// register implementations
		DependencyInjectingObjectFactory dependencyInjectingObjectFactory = new DependencyInjectingObjectFactory();
		dependencyInjectingObjectFactory.registerImplementationClass(Y.class, YImpl.class);
		dependencyInjectingObjectFactory.registerImplementationClass(X.class, XImpl.class);

		// obtain two instances
		Y y1 = dependencyInjectingObjectFactory.getObject(Y.class);
		y1.setValue(100);
		Y y2 = dependencyInjectingObjectFactory.getObject(Y.class);
		y2.setValue(200);

		// make sure they are different objects
		assertEquals(100, y1.getValue());
		assertEquals(200, y2.getValue());

		// make sure their dependencies were set correctly
		assertNotNull(y1.getX1());
		assertNull(y1.getX2());
		assertNotNull(y2.getX1());
		assertNull(y2.getX2());
	}

	public void testRegisterSingletonImplementationClass() {
		// register implementations
		DependencyInjectingObjectFactory dependencyInjectingObjectFactory = new DependencyInjectingObjectFactory();
		dependencyInjectingObjectFactory.registerSingletonImplementationClass(Y.class, YImpl.class);
		dependencyInjectingObjectFactory.registerSingletonImplementationClass(X.class, XImpl.class);

		// obtain two instances
		Y y1 = dependencyInjectingObjectFactory.getObject(Y.class);
		y1.setValue(100);
		Y y2 = dependencyInjectingObjectFactory.getObject(Y.class);
		y2.setValue(200);

		// make sure they are the same object
		assertEquals(200, y1.getValue());
		assertEquals(200, y2.getValue());

		// make sure its dependencies were set correctly
		assertNotNull(y1.getX1());
		assertNull(y1.getX2());
	}

	public void testRegisterImplementationObjectForInterface() {
		// register implementations
		DependencyInjectingObjectFactory dependencyInjectingObjectFactory = new DependencyInjectingObjectFactory();
		dependencyInjectingObjectFactory.registerImplementationObject(Y.class, new YImpl());
		dependencyInjectingObjectFactory.registerImplementationObject(X.class, new XImpl());

		// obtain two instances
		Y y1 = dependencyInjectingObjectFactory.getObject(Y.class);
		y1.setValue(100);
		Y y2 = dependencyInjectingObjectFactory.getObject(Y.class);
		y2.setValue(200);

		// make sure they are the same object
		assertEquals(200, y1.getValue());
		assertEquals(200, y2.getValue());

		// make sure its dependencies were set correctly
		assertNotNull(y1.getX1());
		assertNull(y1.getX2());
	}

	public void testRegisterImplementationObjectForClass() {
		// register implementation
		DependencyInjectingObjectFactory dependencyInjectingObjectFactory = new DependencyInjectingObjectFactory();
		dependencyInjectingObjectFactory.registerImplementationObject(Z.class, new ZImpl());
		dependencyInjectingObjectFactory.registerImplementationObject(YImpl.class, new YImpl());

		// obtain an instance
		Z z = dependencyInjectingObjectFactory.getObject(Z.class);

		// make sure its dependencies were set correctly
		assertNotNull(z.getY1());
		assertNull(z.getY2());
	}

	public void testCyclesAreSafe() {
		// register implementation
		DependencyInjectingObjectFactory dependencyInjectingObjectFactory = new DependencyInjectingObjectFactory();
		YImpl y = new YImpl();
		dependencyInjectingObjectFactory.registerImplementationObject(Y.class, y);
		XImpl x = new XImpl();
		dependencyInjectingObjectFactory.registerImplementationObject(X.class, x);

		// obtain an instance
		Y y1 = dependencyInjectingObjectFactory.getObject(Y.class);

		// check that cycles are ok
		assertNotNull(y1.getX1().getY1().getX1().getY1());
	}

	public void testMissingRegistration() {
		// don't register implementation
		DependencyInjectingObjectFactory dependencyInjectingObjectFactory = new DependencyInjectingObjectFactory();

		try {
			// obtain an instance
			dependencyInjectingObjectFactory.getObject(Y.class);
			fail();
		} catch (RuntimeException e) {
			// expected
		}
	}

	public void testMissingInterfaceDependencyRegistration() {
		// don't register all implementations
		DependencyInjectingObjectFactory dependencyInjectingObjectFactory = new DependencyInjectingObjectFactory();
		dependencyInjectingObjectFactory.registerImplementationClass(Y.class, YImpl.class);

		// obtain an instance
		Y y1 = dependencyInjectingObjectFactory.getObject(Y.class);
		X x1 = y1.getX1();
		try {
			x1.getY1();
			fail();
		} catch (RuntimeException e) {
			// expected
		}
	}

	public void testMissingConcreteDependencyRegistration() {
		// don't register all implementations
		DependencyInjectingObjectFactory dependencyInjectingObjectFactory = new DependencyInjectingObjectFactory();
		dependencyInjectingObjectFactory.registerImplementationClass(Z.class, ZImpl.class);

		try {
			// obtain an instance
			dependencyInjectingObjectFactory.getObject(Z.class);
			fail();
		} catch (RuntimeException e) {
			// expected
		}
	}

	public void testImplementationClassMustBeConcrete() {
		// register interface as implementation
		DependencyInjectingObjectFactory dependencyInjectingObjectFactory = new DependencyInjectingObjectFactory();

		try {
			dependencyInjectingObjectFactory.registerImplementationClass(Y.class, Y.class);
			fail();
		} catch (IllegalArgumentException e) {
			// expected
		}
	}

	public void testDuplicateRegistration() {
		DependencyInjectingObjectFactory dependencyInjectingObjectFactory = new DependencyInjectingObjectFactory();

		// register implementation twice
		dependencyInjectingObjectFactory.registerImplementationClass(Y.class, YImpl.class);
		try {
			dependencyInjectingObjectFactory.registerImplementationObject(Y.class, new YImpl());
			fail();
		} catch (IllegalArgumentException e) {
			// expected
		}
	}
}
