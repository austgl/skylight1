package skylight1.util;

import java.security.MessageDigest;

import android.content.Context;
import android.provider.Settings.Secure;
import android.util.Log;

public class PhoneIdHasher {
	static String hashedPhoneId;

	public String getHashedPhoneId(Context aContext) {
		if (hashedPhoneId == null) {
			// get the phone's unique id
			final String androidId = BuildInfo.getAndroidID(aContext);

			// if there is none, then it must be an emulator, which is interesting of itself
			if (androidId == null) {
				hashedPhoneId = "EMULATOR";

			} else {

				// otherwise hash the id using the package name, so as to make it possible to track exceptions from the same
				// device across many apps in the same package, but practically impossible to determine anything about the
				// identity of the user across packages
				try {
					final MessageDigest messageDigest = MessageDigest.getInstance("SHA");
					messageDigest.update(androidId.getBytes());
					messageDigest.update(aContext.getPackageName().getBytes());

					// format as a string of hexadecimal digits
					final StringBuilder stringBuilder = new StringBuilder();
					for (byte b : messageDigest.digest()) {
						stringBuilder.append(String.format("%02X", b));
					}

					// return the result
					hashedPhoneId = stringBuilder.toString();
				} catch (Exception e) {
					Log.e(LoggingExceptionHandler.class.getName(), "Unable to get phone id", e);
					hashedPhoneId = "Not Available";
				}
			}
		}
		return hashedPhoneId;
	}
}
