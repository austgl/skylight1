package net.nycjava.skylight.service;

import net.nycjava.skylight.dependencyinjection.Dependency;
import android.hardware.SensorListener;
import android.hardware.SensorManager;

public class SensorAppliedForceAdapterServiceAndroidImpl     
  implements  SensorAppliedForceAdapter{
  
  @Dependency
  BalancedObjectPublicationService balancedPublicationService;

  @Dependency
  private SensorManager mSensorManager;
	  //mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
	
  private final SensorListener mListener = new SensorListener() {          
        public void onSensorChanged(int sensor, float[] values) {
            float anAngleInRadians = 0;
            float aForceInNewtons = 0;
        	 // calc angle & force ...
            balancedPublicationService.applyForce(anAngleInRadians, aForceInNewtons);      
        }
        
        public void onAccuracyChanged(int sensor, int accuracy) {
            // ???    
        }
    };

public void start() {
    int mask = 0;
    mask |= SensorManager.SENSOR_ACCELEROMETER; 
    mSensorManager.registerListener(mListener, mask, SensorManager.SENSOR_DELAY_FASTEST);
}

public void stop() {
	mSensorManager.unregisterListener(mListener);
}
   
}


