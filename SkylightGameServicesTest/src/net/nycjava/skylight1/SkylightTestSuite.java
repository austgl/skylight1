package net.nycjava.skylight1;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.nycjava.skylight1.service.BalancedObjectPublicationServiceImplTest;
import net.nycjava.skylight1.service.CountdownServicePublicationServiceImplTest;
import net.nycjava.skylight1.service.RandomForceServiceImplTest;
import net.nycjava.skylight1.service.SensorAppliedForceAdaptorServiceAndroidImplTest;

public class SkylightTestSuite extends TestSuite {
	public static Test suite() {

		TestSuite suite = new TestSuite();

		suite.addTestSuite(RandomForceServiceImplTest.class);
		suite.addTestSuite(BalancedObjectPublicationServiceImplTest.class);
		suite.addTestSuite(CountdownServicePublicationServiceImplTest.class);
		suite.addTestSuite(SensorAppliedForceAdaptorServiceAndroidImplTest.class);
		return suite;
	}

}
