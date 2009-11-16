package skylight1.util;

import static java.lang.String.format;
import android.os.SystemClock;
import android.util.Log;

public class FPSLogger {
	// perhaps some future h/w will have even faster frame rates
	private static final int MAXIMUM_FRAMES_PER_SECOND = 120;

	private final static String[] FPS_MESSAGES = new String[MAXIMUM_FRAMES_PER_SECOND];

	static {
		for (int fps = 0; fps < MAXIMUM_FRAMES_PER_SECOND; fps++) {
			FPS_MESSAGES[fps] = format("FPS = %d", fps);
		}
	}

	final private String loggerName;

	private final long millisecondsBetweenLoggingFPS;

	private long nextTimeToLogFPS;

	private long framesSinceLastLoggedFPS;

	private boolean started;

	public FPSLogger(final String aLoggerName, final long aMillisecondsBetweenLoggingFPS) {
		loggerName = aLoggerName;
		millisecondsBetweenLoggingFPS = aMillisecondsBetweenLoggingFPS;
	}

	public void frameRendered() {
		final long currentTimeMillis = SystemClock.uptimeMillis();
		if (!started) {
			nextTimeToLogFPS = currentTimeMillis + millisecondsBetweenLoggingFPS;
			started = true;
		} else {
			if (currentTimeMillis > nextTimeToLogFPS) {
				final int fPS = (int) (framesSinceLastLoggedFPS * 1000L / millisecondsBetweenLoggingFPS);
				final String fPSMessage = FPS_MESSAGES[Math.min(fPS, MAXIMUM_FRAMES_PER_SECOND - 1)];
				Log.i(loggerName, fPSMessage);

				framesSinceLastLoggedFPS = 0;
				nextTimeToLogFPS += millisecondsBetweenLoggingFPS;
			}
		}
		framesSinceLastLoggedFPS++;
	}
}
