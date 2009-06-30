package net.nycjava.skylight.service.impl;

import net.nycjava.skylight.dependencyinjection.Dependency;
import net.nycjava.skylight.service.BalancedObjectPublicationService;
import net.nycjava.skylight.service.SensorAppliedForceAdapter;
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
             // Not sure if I want to this here as it will be calculated quite frequently
            float x = values[0];
            float z = values[2];
            double mag = Math.sqrt(x * x + z * z);            
            double theta = Math.atan2(x,z);
            anAngleInRadians = (float) theta;
            aForceInNewtons = (float) mag;
            balancedPublicationService.applyForce(0, 0);      
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


