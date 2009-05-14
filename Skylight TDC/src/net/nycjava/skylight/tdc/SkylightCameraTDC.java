package net.nycjava.skylight.tdc;

import static java.lang.Integer.parseInt;
import static java.lang.String.format;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import net.nycjava.skylight.dependencyinjection.Dependency;
import net.nycjava.skylight.dependencyinjection.DependencyInjectingObjectFactory;
import net.nycjava.skylight.dependencyinjection.DependencyInjector;
import net.nycjava.skylight.mocks.camera.CameraParametersEvent;
import net.nycjava.skylight.mocks.camera.CameraPreviewEvent;
import net.nycjava.skylight.mocks.file.CameraEventStreamWriter;
import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class SkylightCameraTDC extends Activity {
	private static final class FileWritingCameraListener implements Camera.PreviewCallback {
		private CameraEventStreamWriter cameraEventStreamWriter;

		private long startTime;

		private boolean parametersWritten;

		public FileWritingCameraListener(OutputStream anOutputStream) {
			cameraEventStreamWriter = new CameraEventStreamWriter(anOutputStream);
		}

		public void onPreviewFrame(byte[] aData, Camera aCamera) {
			if (!parametersWritten) {
				startTime = System.currentTimeMillis();
				cameraEventStreamWriter.writeCameraEvent(new CameraParametersEvent(0, aCamera.getParameters()));

				parametersWritten = true;
			}
			cameraEventStreamWriter.writeCameraEvent(new CameraPreviewEvent(System.currentTimeMillis() - startTime,
					aData));
		}
	}

	private final static int previewFormats[] = { PixelFormat.YCbCr_420_SP, PixelFormat.RGB_565, PixelFormat.JPEG };

	@Dependency
	private Camera camera;

	@Dependency
	private View view;

	private OutputStream outputStream;

	private volatile boolean recording;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// create a dependency injecting object factory
		DependencyInjectingObjectFactory dependencyInjectingObjectFactory = new DependencyInjectingObjectFactory();
		dependencyInjectingObjectFactory.registerImplementationObject(Camera.class, Camera.open());
		dependencyInjectingObjectFactory.registerImplementationObject(View.class, getLayoutInflater().inflate(
				R.layout.camera, null));

		// since activities are instantiated by the framework, use the dependency injector directly to inject any
		// dependencies this activity may have
		new DependencyInjector(dependencyInjectingObjectFactory).injectDependenciesForClassHierarchy(this);

		View previewSurfaceView = view.findViewById(R.id.previewSurfaceView);
		new DependencyInjector(dependencyInjectingObjectFactory)
				.injectDependenciesForClassHierarchy(previewSurfaceView);

		setContentView(view);

		final Spinner previewFormatSpinner = (Spinner) findViewById(R.id.previewFormatSpinner);
		ArrayAdapter<CharSequence> previewFormatAdapter = ArrayAdapter.createFromResource(this,
				R.array.preview_formats, android.R.layout.simple_spinner_item);
		previewFormatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		previewFormatSpinner.setAdapter(previewFormatAdapter);

		final EditText previewFrameRate = (EditText) findViewById(R.id.previewFrameRate);
		final EditText previewWidth = (EditText) findViewById(R.id.previewWidth);
		final EditText previewHeight = (EditText) findViewById(R.id.previewHeight);

		final Button recordButton = (Button) findViewById(R.id.recordButton);
		final Button backButton = (Button) findViewById(R.id.backButton);

		final AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				updateRecordButton();
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				updateRecordButton();
			}

			private void updateRecordButton() {
				// only possible to record if at least one of the sensors has a rate selected
				// recordButton.setEnabled(previewFormatSpinner.getSelectedItemId() != 0);
				// TODO check that width, height and rate are valid too
				Log.i("me", "selected item is " + getPreviewFormatAsInteger(previewFormatSpinner));
				camera.getParameters().setPreviewFormat(getPreviewFormatAsInteger(previewFormatSpinner));
			}
		};
		previewFormatSpinner.setOnItemSelectedListener(onItemSelectedListener);

		recordButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if (!recording) {
					final Parameters parameters = camera.getParameters();
					parameters.setPreviewFormat(getPreviewFormatAsInteger(previewFormatSpinner));
					parameters.setPreviewSize(parseInt(previewWidth.getText().toString()), parseInt(previewHeight
							.getText().toString()));
					parameters.setPreviewFrameRate(parseInt(previewFrameRate.getText().toString()));
					camera.setParameters(parameters);
					try {
						Date dt = new Date();
						String s = format("%1$tY%1$tm%1$td_%1$tH%1$tM%1$tS", dt);
						outputStream = new FileOutputStream("/sdcard/testData_camera_" + s + ".tdc");

						camera.setPreviewCallback(new FileWritingCameraListener(outputStream));
					} catch (FileNotFoundException e) {
						Log.e("tdc", null, e);
					}

					recording = true;
				} else {
					camera.setPreviewCallback(null);
					try {
						outputStream.close();
					} catch (IOException e) {
						Log.e("tdc", null, e);
					}

					recording = false;
				}

				previewFormatSpinner.setEnabled(!recording);
				previewFrameRate.setEnabled(!recording);
				previewWidth.setEnabled(!recording);
				previewHeight.setEnabled(!recording);
				recordButton.setText(recording ? R.string.stop_button_label : R.string.record_button_label);
				backButton.setEnabled(!recording);

				recording = true;
			}
		});

		backButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				final Intent intent = new Intent(SkylightCameraTDC.this, SkylightTDC.class);
				startActivity(intent);
				finish();
			}
		});
	}

	private static int getPreviewFormatAsInteger(Spinner aSpinner) {
		return previewFormats[(int) aSpinner.getSelectedItemId()];
	}
}