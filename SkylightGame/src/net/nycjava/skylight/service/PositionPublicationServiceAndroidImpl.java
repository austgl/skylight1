package net.nycjava.skylight.service;

import java.util.HashSet;
import java.util.Set;

import net.nycjava.skylight.dependencyinjection.Dependency;
import android.hardware.SensorListener;
import android.hardware.SensorManager;

public class PositionPublicationServiceAndroidImpl     
  implements PositionPublicationService {
  
  final static float THRESHOLD = 0.1F;  //guess
  
  private Set<PositionObserver> positionObservers = new HashSet<PositionObserver>();
  
  @Dependency
  private SensorManager mSensorManager;
	  //mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
	
  private final SensorListener mListener = new SensorListener() {
            
        public void onSensorChanged(int sensor, float[] values) {
        	if(SensorManager.SENSOR_ACCELEROMETER==sensor) //todo: is this check needed?
        		notifyObservers(new Position(
         			values[SensorManager.DATA_X],
         			values[SensorManager.DATA_Y],
         			values[SensorManager.DATA_Z])
         	);                
        }
        public void onAccuracyChanged(int sensor, int accuracy) {
            // todo: ???    
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
   
   public void addObserver(PositionObserver anObserver) {
	 positionObservers.add(anObserver);
   }
   
   public boolean removeObserver(PositionObserver anObserver) {
     final boolean existed = positionObservers.remove(anObserver);
	 return existed;
   }
   
   private void notifyObservers(Position aPosition) {
		for (PositionObserver positionObserver : positionObservers) {
			positionObserver.positionNotification(aPosition);
		}
	}
   
}

