package android.util;

import static java.lang.String.format;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Log {

	public static final int VERBOSE = 2;

	public static final int DEBUG = 3;

	public static final int INFO = 4;

	public static final int WARN = 5;

	public static final int ERROR = 6;

	public static final int ASSERT = 7;

	public static int v(String aTag, String aMessage) {
		return println(VERBOSE, aTag, aMessage);
	}

	public static int v(String aTag, String aMessage, Throwable aThrowable) {
		return println(VERBOSE, aTag, aMessage, aThrowable);
	}

	public static int d(String aTag, String aMessage) {
		return println(DEBUG, aTag, aMessage);
	}

	public static int d(String aTag, String aMessage, Throwable aThrowable) {
		return println(DEBUG, aTag, aMessage, aThrowable);
	}

	public static int i(String aTag, String aMessage) {
		return println(INFO, aTag, aMessage);
	}

	public static int i(String aTag, String aMessage, Throwable aThrowable) {
		return println(INFO, aTag, aMessage, aThrowable);
	}

	public static int w(String aTag, String aMessage) {
		return println(WARN, aTag, aMessage);
	}

	public static int w(String aTag, String aMessage, Throwable aThrowable) {
		return println(WARN, aTag, aMessage, aThrowable);
	}

	public static boolean isLoggable(String aTag, int aPriority) {
		Logger.getLogger(aTag).isLoggable(convertToJavaLoggingLevel(aPriority));
		return true;
	}

	public static int w(String aTag, Throwable aThrowable) {
		return println(WARN, aTag, null, aThrowable);
	}

	public static int e(String aTag, String aMessage) {
		return println(ERROR, aTag, aMessage);
	}

	public static int e(String aTag, String aMessage, Throwable aThrowable) {
		return println(ERROR, aTag, aMessage, aThrowable);
	}

	public static java.lang.String getStackTraceString(java.lang.Throwable tr) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		tr.printStackTrace(pw);
		pw.close();
		return sw.toString();
	}

	public static int println(int aPriority, java.lang.String aTag, java.lang.String aMessage) {
		return println(aPriority, aTag, aMessage, null);
	}

	private static Level convertToJavaLoggingLevel(int aPriority) {
		switch (aPriority) {
		case ASSERT:
			return Level.FINEST;
		case VERBOSE:
			return Level.FINER;
		case DEBUG:
			return Level.FINE;
		case INFO:
			return Level.INFO;
		case WARN:
			return Level.WARNING;
		case ERROR:
			return Level.SEVERE;
		}
		throw new IllegalArgumentException(format("passed invalid priority %d", aPriority));
	}

	private static int println(int aPriority, java.lang.String aTag, java.lang.String aMessage, Throwable aThrowable) {
		Logger.getLogger(aTag).log(convertToJavaLoggingLevel(aPriority), aMessage, aThrowable);
		return aTag == null ? 0 : aTag.length() + aMessage == null ? 0 : aMessage.length();
	}
}
