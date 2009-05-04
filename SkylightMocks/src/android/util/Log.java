package android.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Log {

	public static final int VERBOSE = 2;

	public static final int DEBUG = 3;

	public static final int INFO = 4;

	public static final int WARN = 5;

	public static final int ERROR = 6;

	public static final int ASSERT = 7;

	public static int v(java.lang.String tag, java.lang.String msg) {
		return 0;
	}

	public static int v(java.lang.String tag, java.lang.String msg, java.lang.Throwable tr) {
		return 0;
	}

	public static int d(java.lang.String tag, java.lang.String msg) {
		return 0;
	}

	public static int d(java.lang.String tag, java.lang.String msg, java.lang.Throwable tr) {
		return 0;
	}

	public static int i(java.lang.String tag, java.lang.String msg) {
		return 0;
	}

	public static int i(java.lang.String tag, java.lang.String msg, java.lang.Throwable tr) {
		return 0;
	}

	public static int w(java.lang.String tag, java.lang.String msg) {
		return 0;
	}

	public static int w(java.lang.String tag, java.lang.String msg, java.lang.Throwable tr) {
		return 0;
	}

	public static boolean isLoggable(java.lang.String arg0, int arg1) {
		return true;
	}

	public static int w(java.lang.String tag, java.lang.Throwable tr) {
		return 0;
	}

	public static int e(java.lang.String tag, java.lang.String msg) {
		return 0;
	}

	public static int e(java.lang.String tag, java.lang.String msg, java.lang.Throwable tr) {
		return 0;
	}

	public static java.lang.String getStackTraceString(java.lang.Throwable tr) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		tr.printStackTrace(pw);
		pw.close();
		return sw.toString();
	}

	public static int println(int arg0, java.lang.String arg1, java.lang.String arg2) {
		return 0;
	}
}
