package skylight1.util;

import static java.lang.String.format;
import android.os.SystemClock;
import android.util.Log;

/**
 * Provides a facility for logging frames per second.
 */
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

	final private int numberOfFramesBetweenLogging;

	private long timeStartedCountingFrames;

	private int framesUntilNextLogStatement;

	private int fPS;

	private boolean started;

	/**
	 * Constructs an FPSLogger.
	 *
	 * @param aTag
	 *            The tag to use in the Android logging API.
	 * @param aNumberOfFramesBetweenLogging
	 *            The number of calls to frameRendered between each log statement.
	 */
	public FPSLogger(final String aTag, final int aNumberOfFramesBetweenLogging) {
		loggerName = aTag;
		numberOfFramesBetweenLogging = aNumberOfFramesBetweenLogging;
		framesUntilNextLogStatement = 1;
	}

	/**
	 * Call this method once per frame rendered. Note: in the interests of making this method very low cost, if it is
	 * called more than 120 times per second, it will throw an ArrayOutOfBoundsException.
	 */
	public int frameRendered() {
		framesUntilNextLogStatement--;

		if (framesUntilNextLogStatement == 0) {
			final long currentTimeMillis = SystemClock.uptimeMillis();
			if (!started) {
				started = true;
			} else {
				fPS = numberOfFramesBetweenLogging * 1000
						/ (int) (currentTimeMillis - timeStartedCountingFrames);
				final String fPSMessage = FPS_MESSAGES[fPS];
				Log.i(loggerName, fPSMessage);
			}
			timeStartedCountingFrames = currentTimeMillis;
			framesUntilNextLogStatement = numberOfFramesBetweenLogging;
		}
		return fPS;
	}

	public boolean isStarted() {
		return started;
	}
}
