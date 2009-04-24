package net.nycjava.skylight.tdc;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import net.nycjava.skylight.dependencyinjection.Dependency;
import net.nycjava.skylight.dependencyinjection.DependencyInjectingObjectFactory;
import net.nycjava.skylight.dependencyinjection.DependencyInjector;
import net.nycjava.skylight.mocks.file.SensorEventStreamWriter;
import net.nycjava.skylight.mocks.sensor.SensorValuesEvent;
import android.app.Activity;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class SkylightTDC extends Activity {
	private static final class FileWritingSensorListener implements SensorListener {
		private SensorEventStreamWriter sensorEventStreamWriter;

		private long startTime;

		public FileWritingSensorListener(OutputStream anOutputStream) {
			sensorEventStreamWriter = new SensorEventStreamWriter(anOutputStream);
			startTime = System.currentTimeMillis();
		}

		public void onAccuracyChanged(int aSensorId, int anAccuracy) {
			// TODO write these to the file too - create super type for sensor event - use polymorphism
		}

		public void onSensorChanged(int aSensorId, float[] anArrayOfValues) {
			sensorEventStreamWriter.writeSensorEvent(new SensorValuesEvent(System.currentTimeMillis() - startTime,
					aSensorId, anArrayOfValues));
		}
	}

	@Dependency
	private SensorManager sensorManager;

	private SensorListener sensorListener;

	private OutputStream outputStream;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// create a dependency injecting object factory
		DependencyInjectingObjectFactory dependencyInjectingObjectFactory = new DependencyInjectingObjectFactory();
		dependencyInjectingObjectFactory.registerImplementationObject(SensorManager.class,
				(SensorManager) getSystemService(SENSOR_SERVICE));

		// since activities are instantiated by the framework, use the dependency injector directly to inject any
		// dependencies this activity may have
		new DependencyInjector(dependencyInjectingObjectFactory).injectDependenciesForClassHierarchy(this);

		setContentView(R.layout.main);

		final Spinner accelerometerSpinner = (Spinner) findViewById(R.id.Spinner01);
		ArrayAdapter<CharSequence> accelerometerAdapter = ArrayAdapter.createFromResource(this, R.array.delays,
				android.R.layout.simple_spinner_item);
		accelerometerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		accelerometerSpinner.setAdapter(accelerometerAdapter);

		final Spinner magnetometerSpinner = (Spinner) findViewById(R.id.Spinner02);
		ArrayAdapter<CharSequence> magnetometerAdapter = ArrayAdapter.createFromResource(this, R.array.delays,
				android.R.layout.simple_spinner_item);
		magnetometerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		magnetometerSpinner.setAdapter(magnetometerAdapter);

		final Button recordButton = (Button) findViewById(R.id.Button01);
		final Button stopButton = (Button) findViewById(R.id.Button02);

		final AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				updateRecordButton();
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				updateRecordButton();
			}

			private void updateRecordButton() {
				recordButton.setEnabled(accelerometerSpinner.getSelectedItemId() != 0
						|| magnetometerSpinner.getSelectedItemId() != 0);
			}
		};
		accelerometerSpinner.setOnItemSelectedListener(onItemSelectedListener);
		magnetometerSpinner.setOnItemSelectedListener(onItemSelectedListener);

		recordButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				try {
					outputStream = new FileOutputStream("/sdcard/testData" + System.currentTimeMillis() + ".tdc");
					sensorListener = new FileWritingSensorListener(outputStream);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					Log.e("tdc", null, e);
				}

				if (accelerometerSpinner.getSelectedItemId() != 0) {
					sensorManager.registerListener(sensorListener, SensorManager.SENSOR_ACCELEROMETER,
							getRate(accelerometerSpinner));
				}

				if (magnetometerSpinner.getSelectedItemId() != 0) {
					sensorManager.registerListener(sensorListener, SensorManager.SENSOR_MAGNETIC_FIELD,
							getRate(magnetometerSpinner));
				}

				// TODO add other sensors

				accelerometerSpinner.setEnabled(false);
				magnetometerSpinner.setEnabled(false);
				recordButton.setEnabled(false);
				stopButton.setEnabled(true);
			}

			private int getRate(Spinner aSpinner) {
				return 4 - (int) aSpinner.getSelectedItemId();
			}
		});

		stopButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				sensorManager.unregisterListener(sensorListener);
				try {
					outputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.e("tdc", null, e);
				}
				accelerometerSpinner.setEnabled(true);
				magnetometerSpinner.setEnabled(true);
				recordButton.setEnabled(true);
				stopButton.setEnabled(false);
			}
		});
	}
}