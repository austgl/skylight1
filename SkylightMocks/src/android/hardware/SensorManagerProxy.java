package android.hardware;

import java.io.InputStream;

public class SensorManagerProxy {
	public SensorManagerProxy() {
	}

	public SensorManager getSensorManager(InputStream anInputStream) {
		return new SensorManager(anInputStream);
	}
}
