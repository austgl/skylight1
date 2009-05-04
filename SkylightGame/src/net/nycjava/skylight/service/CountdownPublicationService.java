package net.nycjava.skylight.service;


public interface CountdownPublicationService extends Observable<CountdownObserver> {

	public void startCountdown();
	public void setDuration(int time); //in seconds	
	public void stopCountdown();
	public counter_status getStatus();
	
	public void addObserver(CountdownObserver anObserver);
	public boolean removeObserver(CountdownObserver anObserver);
	
	public void joinThread() throws InterruptedException; //for testing purpose
}
