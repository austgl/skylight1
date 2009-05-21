package net.nycjava.skylight.service;

public interface BalancedObjectPublicationService extends Observable<BalancedObjectObserver> {
	void applyForce(float anAngleInRadians, float aForceInNewtons);
	void resetCurrentCondition(float anAngleInRadians, float aForceInNewtons);
	float getCurrentAngle();	
	float getCurrentMagnitude();	
	public float getCurrentLeanInRadians();

}
