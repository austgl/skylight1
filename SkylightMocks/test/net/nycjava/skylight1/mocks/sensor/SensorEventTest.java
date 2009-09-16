package net.nycjava.skylight1.mocks.sensor;

import net.nycjava.skylight1.mocks.sensor.SensorAccuracyEvent;
import net.nycjava.skylight1.mocks.sensor.SensorEvent;
import net.nycjava.skylight1.mocks.sensor.SensorValuesEvent;
import junit.framework.TestCase;

public class SensorEventTest extends TestCase {

	public void testSensorValuesEventEquals() {
		SensorEvent sensorValuesEvent1 = new SensorValuesEvent(1, 2, new float[] { 1f, 2f, 3f });
		SensorEvent sensorValuesEvent2 = new SensorValuesEvent(1, 2, new float[] { 1f, 2f, 3f });
		SensorEvent sensorValuesEvent3 = new SensorValuesEvent(1, 2, new float[] { 1f, 2f, 4f });

		assertTrue(sensorValuesEvent1.equals(sensorValuesEvent1));
		assertTrue(sensorValuesEvent1.equals(sensorValuesEvent2));
		assertFalse(sensorValuesEvent1.equals(null));
		assertFalse(sensorValuesEvent1.equals(""));
		assertFalse(sensorValuesEvent1.equals(sensorValuesEvent3));
	}

	public void testSensorAccuracyEventEquals() {
		SensorEvent sensorAccuracyEvent1 = new SensorAccuracyEvent(1, 2, 3);
		SensorEvent sensorAccuracyEvent2 = new SensorAccuracyEvent(1, 2, 3);
		SensorEvent sensorAccuracyEvent3 = new SensorAccuracyEvent(1, 2, 4);

		assertTrue(sensorAccuracyEvent1.equals(sensorAccuracyEvent1));
		assertTrue(sensorAccuracyEvent1.equals(sensorAccuracyEvent2));
		assertFalse(sensorAccuracyEvent1.equals(null));
		assertFalse(sensorAccuracyEvent1.equals(""));
		assertFalse(sensorAccuracyEvent1.equals(sensorAccuracyEvent3));
	}

	public void testNullNotValidValues() {
		try {
			new SensorValuesEvent(1, 2, null);
			fail();
		} catch (IllegalArgumentException e) {
			// expected
		}
	}
}
