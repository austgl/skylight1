package net.nycjava.skylight.mocks.file;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import junit.framework.TestCase;
import net.nycjava.skylight.mocks.sensor.SensorAccuracyEvent;
import net.nycjava.skylight.mocks.sensor.SensorEvent;
import net.nycjava.skylight.mocks.sensor.SensorValuesEvent;

public class SensorEventStreamTest extends TestCase {
	public void testStreamWritingAndReading() throws IOException {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final SensorEventStreamWriter sensorEventStreamWriter = new SensorEventStreamWriter(baos);

		final SensorEvent sensorValuesEvent1 = new SensorValuesEvent(1, 2, new float[] { 1f, 2f, 3f });
		final SensorEvent sensorValuesEvent2 = new SensorValuesEvent(1, 2, new float[] { 1f, 2f, 3f });
		final SensorEvent sensorValuesEvent3 = new SensorValuesEvent(1, 2, new float[] { 1f, 2f, 4f });
		final SensorEvent sensorAccuracyEvent1 = new SensorAccuracyEvent(1, 2, 3);
		final SensorEvent sensorAccuracyEvent2 = new SensorAccuracyEvent(1, 2, 3);
		final SensorEvent sensorAccuracyEvent3 = new SensorAccuracyEvent(1, 2, 4);

		sensorEventStreamWriter.writeSensorEvent(sensorValuesEvent1);
		sensorEventStreamWriter.writeSensorEvent(sensorAccuracyEvent1);
		sensorEventStreamWriter.writeSensorEvent(sensorAccuracyEvent2);
		sensorEventStreamWriter.writeSensorEvent(sensorValuesEvent2);
		sensorEventStreamWriter.writeSensorEvent(sensorValuesEvent3);
		sensorEventStreamWriter.writeSensorEvent(sensorAccuracyEvent3);

		baos.close();

		final ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		final SensorEventStreamReader sensorEventStreamReader = new SensorEventStreamReader(bais);

		assertEquals(sensorValuesEvent1, sensorEventStreamReader.readSensorEvent());
		assertEquals(sensorAccuracyEvent1, sensorEventStreamReader.readSensorEvent());
		assertEquals(sensorAccuracyEvent2, sensorEventStreamReader.readSensorEvent());
		assertEquals(sensorValuesEvent2, sensorEventStreamReader.readSensorEvent());
		assertEquals(sensorValuesEvent3, sensorEventStreamReader.readSensorEvent());
		assertEquals(sensorAccuracyEvent3, sensorEventStreamReader.readSensorEvent());

		assertNull(sensorEventStreamReader.readSensorEvent());
	}

}
