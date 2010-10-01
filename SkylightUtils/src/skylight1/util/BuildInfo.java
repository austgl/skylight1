package skylight1.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

public class BuildInfo {
	public static String getVersionName(Context context) {
		try {
			PackageInfo pInfo = context.getPackageManager().getPackageInfo(
					context.getPackageName(), PackageManager.GET_META_DATA);
			if (pInfo != null) {
				return pInfo.versionName;
			}
		} catch (NameNotFoundException e) {
			Log.w("getVersionName()", Log.getStackTraceString(e));
		}
		return "";
	}
	public static String getBuildInfo() { // minimal info for 1.5 support
		final String buildInfo = String.format("AndroidVersion= %s Product= %s ID= %s Model= %s",
				android.os.Build.VERSION.RELEASE, android.os.Build.PRODUCT, android.os.Build.ID, android.os.Build.MODEL);
		return buildInfo;
	}
}
