package skylight1.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiManager;
import android.provider.Settings.Secure;
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
	public static boolean isDebuggable(Context context) {
		try {
			PackageInfo pInfo = context.getPackageManager().getPackageInfo(
			context.getPackageName(), PackageManager.GET_META_DATA);
			if (pInfo != null) {
				if ( 0 != (pInfo.applicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE)) {
					return true;
				}
			}
		} catch (NameNotFoundException e) {
			Log.w("isDebuggable()", Log.getStackTraceString(e));
		}
		return false;
	}
	/*
	 * add macaddress to default id to take care of those duplicates...
	 */
	public static String getAndroidID(Context context) {
		String id = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
		if(id!=null) {
			PackageManager pm = context.getPackageManager();
			if(PackageManager.PERMISSION_GRANTED ==
				    pm.checkPermission(android.Manifest.permission.ACCESS_NETWORK_STATE, context.getPackageName())) {
				WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
				if(wm!=null) {
					id = String.format("%s-%s", id, wm.getConnectionInfo().getMacAddress());
				}
			}
		}
		return id;
	}
	public static String getBuildInfo() { // minimal info for 1.5 support
		final String buildInfo = String.format("AndroidVersion= %s Product= %s ID= %s Model= %s",
				android.os.Build.VERSION.RELEASE, android.os.Build.PRODUCT, android.os.Build.ID, android.os.Build.MODEL);
		return buildInfo;
	}
}
