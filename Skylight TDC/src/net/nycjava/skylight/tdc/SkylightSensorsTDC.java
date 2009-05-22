package net.nycjava.skylight.tdc;

import static java.lang.String.format;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import net.nycjava.skylight.dependencyinjection.Dependency;
import net.nycjava.skylight.dependencyinjection.DependencyInjectingObjectFactory;
import net.nycjava.skylight.dependencyinjection.DependencyInjector;
import net.nycjava.skylight.mocks.file.SensorEventStreamWriter;
import net.nycjava.skylight.mocks.sensor.SensorValuesEvent;
import android.app.Activity;
import android.content.Intent;
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
import android.widget.TextView;

public class SkylightSensorsTDC extends Activity {
	private static TextView balance;
	private static final class FileWritingSensorListener implements SensorListener {
		private SensorEventStreamWriter sensorEventStreamWriter;

		private long startTime;
		// physics state
		long old_tick;

		long tick;

		long delta_t;
		
		double y_dist = 0.0f; // y-distance traveled in meters

		double y_vel = 0.0f; // y-velocity in meters/sec

		double y_accel = 0.0f; // y-acceleration in meter/sec^2


		public FileWritingSensorListener(OutputStream anOutputStream) {
			sensorEventStreamWriter = new SensorEventStreamWriter(anOutputStream);
			startTime = System.currentTimeMillis();
		}

		public void onAccuracyChanged(int aSensorId, int anAccuracy) {
			// TODO write these to the file too - create super type for sensor event - use polymorphism
		}

		public void onSensorChanged(int aSensorId, float[] anArrayOfValues) {
			// do physics here
			tick = System.currentTimeMillis();
			float x = anArrayOfValues[0];
			float y = anArrayOfValues[1];
			float z = anArrayOfValues[2];
			if (Math.abs(x) >= 1.0) {
				delta_t = tick - old_tick;
				y_dist += y_vel * ((delta_t) / 1000.0);
				y_vel += y_accel * ((delta_t) / 1000.0);
				y_accel = x;
			}
			old_tick = tick;
			balance.setText(String.format("%f", y_vel));
			balance.invalidate();
			sensorEventStreamWriter.writeSensorEvent(new SensorValuesEvent(tick - startTime,
					aSensorId, anArrayOfValues));
		}
	}

	@Dependency
	private SensorManager sensorManager;

	private SensorListener sensorListener;

	private OutputStream outputStream;

	private volatile boolean recording;

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

		setContentView(R.layout.sensors);

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

		final Spinner orientationSpinner = (Spinner) findViewById(R.id.Spinner03);
		ArrayAdapter<CharSequence> orientationAdapter = ArrayAdapter.createFromResource(this, R.array.delays,
				android.R.layout.simple_spinner_item);
		orientationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		orientationSpinner.setAdapter(magnetometerAdapter);		
		
		final Button recordButton = (Button) findViewById(R.id.recordButton);
		final Button backButton = (Button) findViewById(R.id.backButton);
		
		final TextView text = (TextView) findViewById(R.id.sensorFilename);
		balance = (TextView) findViewById(R.id.balance);

		final AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				updateRecordButton();
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				updateRecordButton();
			}

			private void updateRecordButton() {
				// only possible to record if at least one of the sensors has a rate selected
				recordButton.setEnabled(accelerometerSpinner.getSelectedItemId() != 0
						|| magnetometerSpinner.getSelectedItemId() != 0
						|| orientationSpinner.getSelectedItemId() != 0);
			}
		};
		accelerometerSpinner.setOnItemSelectedListener(onItemSelectedListener);
		magnetometerSpinner.setOnItemSelectedListener(onItemSelectedListener);
		orientationSpinner.setOnItemSelectedListener(onItemSelectedListener);

		recordButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if (!recording) {
					try {
						Date dt = new Date();
						String s = format("%1$tY%1$tm%1$td_%1$tH%1$tM%1$tS", dt);
						outputStream = new FileOutputStream("/sdcard/testData_" + s + ".tdc");
						sensorListener = new FileWritingSensorListener(outputStream);
						text.setText(s);
					} catch (FileNotFoundException e) {
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

					if (orientationSpinner.getSelectedItemId() != 0) {
						sensorManager.registerListener(sensorListener, SensorManager.SENSOR_ORIENTATION,
								getRate(orientationSpinner));
					}
					// TODO add other sensors

					accelerometerSpinner.setEnabled(false);
					magnetometerSpinner.setEnabled(false);
					orientationSpinner.setEnabled(false);
					recordButton.setText(R.string.stop_button_label);
					backButton.setEnabled(false);

					recording = true;
				} else {
					sensorManager.unregisterListener(sensorListener);
					try {
						outputStream.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						Log.e("tdc", null, e);
					}
					accelerometerSpinner.setEnabled(true);
					magnetometerSpinner.setEnabled(true);
					recordButton.setText(R.string.record_button_label);
					backButton.setEnabled(true);

					recording = false;
				}
			}

			private int getRate(Spinner aSpinner) {
				return 4 - (int) aSpinner.getSelectedItemId();
			}
		});

		backButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				final Intent intent = new Intent(SkylightSensorsTDC.this, SkylightTDC.class);
				startActivity(intent);
				finish();
			}
		});
	}
}