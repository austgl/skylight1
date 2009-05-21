package net.nycjava.skylight.service;

import java.util.HashSet;
import java.util.Set;


public class BalancedObjectPublicationServiceImpl implements
BalancedObjectPublicationService {

	float currentAngleInRadians = 0;
	float currentMagnitudeInNewton = 0;
	float currentLeanInRadians = 0; // don't know if 0 makes sense
	
	final static float startAngle= (float) Math.PI/2; //starting upright
	final static float startMag = 0; //  seems to be good enough

	private Set<BalancedObjectObserver> balancedObjectObservers = new HashSet<BalancedObjectObserver>();


	public void resetCurrentCondition(float anAngle, float aMag)
	{
		currentAngleInRadians=anAngle;
		currentMagnitudeInNewton=aMag;
	}

	public float getCurrentAngle()
	{
		return currentAngleInRadians;
	}
	
	public float getCurrentMagnitude()
	{
		return currentMagnitudeInNewton;
	}
	
	public float getCurrentLeanInRadians()
	{
		return currentLeanInRadians;
	}

	private void addForceCartisian(float anAngleInRadians, float aMagnitudeInNewtons)
	{
		float xPos = (float) (currentMagnitudeInNewton * Math.cos( this.currentAngleInRadians) + aMagnitudeInNewtons * Math.cos(anAngleInRadians));
		float yPos = (float) (currentMagnitudeInNewton * Math.sin( this.currentAngleInRadians)+  aMagnitudeInNewtons * Math.sin(anAngleInRadians));


		currentMagnitudeInNewton= (float) Math.hypot(xPos, yPos);
		currentAngleInRadians = (float) Math.atan2(yPos, xPos);
		currentLeanInRadians = (float) Math.atan(currentMagnitudeInNewton);
	}


	@Override
	public void applyForce(float anAngleInRadians, float aMagnitudeInNewtons) {
		this.addForceCartisian(anAngleInRadians, aMagnitudeInNewtons);
		// in case we ever add force using other methods
	}

	@Override
	public void addObserver(BalancedObjectObserver anObserver) 
	{
		if (this.balancedObjectObservers.isEmpty())
		{
			resetCurrentCondition(startAngle, startMag);
		}
		this.balancedObjectObservers.add(anObserver);
		notifyObservers(this.currentAngleInRadians, this.currentLeanInRadians);

	}

	@Override
	public boolean removeObserver(BalancedObjectObserver anObserver) {

		final boolean existed = this.balancedObjectObservers.remove(anObserver);
		return existed;
	}

	private void notifyObservers(float anAngleInRadians, float anAngleOfLeanInRadians) 
	{
		for (BalancedObjectObserver observer : this.balancedObjectObservers)
		{
			observer.balancedObjectNotification((float)anAngleInRadians, (float)anAngleOfLeanInRadians );
		}
	}
}
