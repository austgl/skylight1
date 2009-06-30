package net.nycjava.skylight.service.impl;

import java.util.HashSet;
import java.util.Set;

import net.nycjava.skylight.dependencyinjection.Dependency;
import net.nycjava.skylight.service.old.SteadinessObserver;
import net.nycjava.skylight.service.old.SteadinessPublicationService;
import android.hardware.SensorListener;
import android.hardware.SensorManager;

public class SteadinessPublicationServiceAndroidImpl     
  implements SteadinessPublicationService {
  
  final static float THRESHOLD = 0.1F;  //guess
  
  private Set<SteadinessObserver> steadinessObservers = new HashSet<SteadinessObserver>();
  
  @Dependency
  private SensorManager mSensorManager;
	  //mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
	
  private final SensorListener mListener = new SensorListener() {
            
        public void onSensorChanged(int sensor, float[] values) {
            float force = 0;
        	for (int i = 0; i < 3; i++) 
                force += Math.abs(values[i]); 
            notifyObservers(force);
                
        }
        public void onAccuracyChanged(int sensor, int accuracy) {
            // ???    
        }
    };
    
   private void open() {
     int mask = 0;
     mask |= SensorManager.SENSOR_ACCELEROMETER; 
     mSensorManager.registerListener(mListener, mask, SensorManager.SENSOR_DELAY_FASTEST);
   }
   
   private void close() {
     mSensorManager.unregisterListener(mListener);
   }
   
   public void addObserver(SteadinessObserver anObserver) {
	 steadinessObservers.add(anObserver);
		if (steadinessObservers.size() == 1)
			open();
   }
   
   public boolean removeObserver(SteadinessObserver anObserver) {
     final boolean existed = steadinessObservers.remove(anObserver);
     if (steadinessObservers.isEmpty()) 
    	 close();
     return existed;
   }
   
   private void notifyObservers(float force) {
		for (SteadinessObserver steadinessObserver : steadinessObservers) {
			steadinessObserver.steadinessNotification(force);
			if (force > THRESHOLD)
				steadinessObserver.unsteadinessNotification();
		}
	}
   
}


