package skylight1.util;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class Adverts {
	public static void insertAdBanner(Context context, ViewGroup layout) {
//TODO: REFLECT!
	    //for admob: don't show ads in emulator
//	    AdManager.setTestDevices( new String[] { AdManager.TEST_EMULATOR
	    //,"your_debugging_phone_id_here" // add phone id if debugging on phone
//	    });
	    String adwhirl_id = Assets.getString("adwhirl_id",context);
	    if(adwhirl_id!=null && adwhirl_id.length()>0) {
//TODO: reflect! - and remove line following
//	    	AdWhirlLayout adWhirlLayout = new AdWhirlLayout(context, adwhirl_id);
RelativeLayout adWhirlLayout = new RelativeLayout(context); 	
	    	RelativeLayout.LayoutParams adWhirlLayoutParams = new RelativeLayout.LayoutParams(320, 52);
	    	layout.addView(adWhirlLayout, adWhirlLayoutParams);
	    }
    }
}