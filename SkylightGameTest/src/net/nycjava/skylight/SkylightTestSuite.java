package net.nycjava.skylight;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.nycjava.skylight.service.CameraObscurementPublicationServiceAndroidImpTest;
import net.nycjava.skylight.service.RandomForceServiceImplTest;

public class SkylightTestSuite extends TestSuite {
	public static Test suite() {

		TestSuite suite = new TestSuite();

		suite.addTestSuite(CameraObscurementPublicationServiceAndroidImpTest.class);

		suite.addTestSuite(RandomForceServiceImplTest.class);
//		 suite.addTestSuite(CountdownServicePublicationServiceImplTest.class);
		return suite;
	}

}
