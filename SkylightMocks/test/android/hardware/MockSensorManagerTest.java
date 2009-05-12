package android.hardware;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import junit.framework.TestCase;
import net.nycjava.skylight.mocks.file.SensorEventStreamWriter;
import net.nycjava.skylight.mocks.sensor.SensorAccuracyEvent;
import net.nycjava.skylight.mocks.sensor.SensorEvent;
import net.nycjava.skylight.mocks.sensor.SensorValuesEvent;

public class MockSensorManagerTest extends TestCase {
	public void test() throws IOException {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final SensorEventStreamWriter sensorEventStreamWriter = new SensorEventStreamWriter(baos);

		final SensorEvent sensorValuesEvent1 = new SensorValuesEvent(1, SensorManager.SENSOR_ACCELEROMETER,
				new float[] { 1f, 2f, 3f });
		final SensorEvent sensorValuesEvent2 = new SensorValuesEvent(1, SensorManager.SENSOR_ACCELEROMETER,
				new float[] { 1f, 2f, 3f });
		final SensorEvent sensorValuesEvent3 = new SensorValuesEvent(1, SensorManager.SENSOR_ACCELEROMETER,
				new float[] { 1f, 2f, 4f });
		final SensorEvent sensorAccuracyEvent1 = new SensorAccuracyEvent(1, SensorManager.SENSOR_ACCELEROMETER,
				SensorManager.SENSOR_STATUS_ACCURACY_LOW);
		final SensorEvent sensorAccuracyEvent2 = new SensorAccuracyEvent(1, SensorManager.SENSOR_ACCELEROMETER,
				SensorManager.SENSOR_STATUS_ACCURACY_HIGH);
		final SensorEvent sensorAccuracyEvent3 = new SensorAccuracyEvent(1, SensorManager.SENSOR_ACCELEROMETER,
				SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM);

		sensorEventStreamWriter.writeSensorEvent(sensorValuesEvent1);
		sensorEventStreamWriter.writeSensorEvent(sensorAccuracyEvent1);
		sensorEventStreamWriter.writeSensorEvent(sensorAccuracyEvent2);
		sensorEventStreamWriter.writeSensorEvent(sensorValuesEvent2);
		sensorEventStreamWriter.writeSensorEvent(sensorValuesEvent3);
		sensorEventStreamWriter.writeSensorEvent(sensorAccuracyEvent3);

		baos.close();

		final ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());

		SensorManager mockSensorManager = new SensorManager(bais);

		mockSensorManager.registerListener(new SensorListener() {

			public void onAccuracyChanged(int arg0, int arg1) {
				// TODO put real tests here
			}

			public void onSensorChanged(int arg0, float[] arg1) {
				// TODO put real tests here
			}
		}, SensorManager.SENSOR_ALL);
	}
}
