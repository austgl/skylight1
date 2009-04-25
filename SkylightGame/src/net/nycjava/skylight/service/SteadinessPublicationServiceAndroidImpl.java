package net.nycjava.skylight.service;

import java.util.HashSet;
import java.util.Set;

import net.nycjava.skylight.dependencyinjection.Dependency;
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
            for (int i = 0; i < 3; i++) 
            	  // only notify if unsteady
                if (Math.abs(values[i]) > THRESHOLD) 
                	notifyObservers(values[i]);
                
        }
        public void onAccuracyChanged(int sensor, int accuracy) {
            // ???    
        }
    };
    
   public void init() {
     int mask = 0;
     mask |= SensorManager.SENSOR_ACCELEROMETER; 
     mSensorManager.registerListener(mListener, mask, SensorManager.SENSOR_DELAY_FASTEST);
   }
   
   public void close() {
     mSensorManager.unregisterListener(mListener);
   }
   
   public void addObserver(SteadinessObserver anObserver) {
	 steadinessObservers.add(anObserver);
   }
   
   public boolean removeObserver(SteadinessObserver anObserver) {
     final boolean existed = steadinessObservers.remove(anObserver);
	 return existed;
   }
   
   private void notifyObservers(float steady) {
		for (SteadinessObserver steadinessObserver : steadinessObservers) {
			steadinessObserver.steadinessNotification(steady);
		}
	}
   
}

