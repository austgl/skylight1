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
	public static String getBuildInfo() {
		final String buildInfo = String.format(
				"AndroidVersion= %s Product= %s ID= %s Manufacturer= %s Model= %s CpuAbi= %s",
				android.os.Build.VERSION.RELEASE, android.os.Build.PRODUCT, android.os.Build.ID,
				android.os.Build.MANUFACTURER, android.os.Build.MODEL, android.os.Build.CPU_ABI);
		return buildInfo;
	}
}
