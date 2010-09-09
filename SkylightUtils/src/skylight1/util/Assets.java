package skylight1.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.app.Activity;
import android.util.Log;

/*
 * util methods for working with assets
 */
public class Assets {
	/*
	 * get a String from a text file (whole file returned)\n
	 * e.g. from onCreate(): String userAgreement = getString(eula, this);
	 */
	public static String getString(String asset, Activity activity) {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(activity.getAssets()
					.open(asset)));
			String line;
			StringBuilder buffer = new StringBuilder();

			while ((line = in.readLine()) != null) {
				buffer.append(line).append('\n');
			}
			return buffer.toString().trim();
		} catch (IOException ioe) {
			Log.w("Assets.getString()",String.format("%s not available: %s",asset,ioe.getMessage()));
			return "";
		} catch (NullPointerException npe) {
			Log.w("Assets.getString()",String.format("%s not available: %s",asset,npe.getMessage()));
			return "";
		} finally {
			try {
				if(in!=null) {
					in.close();
				}
			} catch (IOException e) {
			}
			in = null;
		}
	}
}
