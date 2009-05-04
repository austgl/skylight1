package net.nycjava.skylight.service;
import java.util.HashSet;
import java.util.Set;

enum counter_status {uninitialized, running, stopped,finished};


public class CountdownPublicationServiceImpl extends Thread implements CountdownPublicationService
{

	private int duration=0;
	private int current_count=0;
	private boolean stopRequested =  false;
	
	private Set<CountdownObserver> countdownObservers = new HashSet<CountdownObserver>();

	private boolean TIME_IS_UP=false;
	private counter_status current_status=counter_status.uninitialized;

	public counter_status getStatus()
	{
		return current_status;
	}
	public void addObserver(CountdownObserver anObserver) {
		countdownObservers.add(anObserver);
		if (this.current_status==counter_status.running)
		{
			int remain=getRemainingTime();
			notifyObservers(remain);
		}
	}

	public boolean removeObserver(CountdownObserver anObserver) {
		final boolean existed = countdownObservers.remove(anObserver);
		return existed;
	}

	public void setDuration(int seconds)
	{
		duration=seconds;
	}

	private int getRemainingTime()
	{
		int aRemainingTime= this.duration - current_count;
		return aRemainingTime;
	}
	
	public void run()
	{
		if (this.duration == 0)
			return;
		this.current_status=counter_status.running;
		try{
			while (current_count < duration && stopRequested==false)
			{
				current_count=current_count+1;
				notifyObservers(getRemainingTime());
				System.out.println("start counting: " + current_count);
				Thread.sleep(1*1000);
			}
			if (!stopRequested)
			{
				TIME_IS_UP=true;
				this.current_status=counter_status.finished;
			}
		
			else
			{
				TIME_IS_UP=false;
				this.current_status=counter_status.stopped;
				
			}
		}
		catch(InterruptedException e)
		{
			//assuming interruption called in from cancel
			//not sure if there is anything to be done.
			this.current_status=counter_status.stopped;
		}
	}

	public void startCountdown()
	{
		if (this.duration==0 )
	    {
		//should be an assertion here
		return;
	    }
		start();
	}
	
	public void stopCountdown()
	//should send interruption signal to thread.sleep
	{
		stopRequested = true;
		if (current_status==counter_status.running)
		    interrupt();
	}
	

	private void notifyObservers(int aRemainingTime) {

		for (CountdownObserver countdownObserver : countdownObservers) {
			countdownObserver.countdownNotification(aRemainingTime);
		}
	}

	public void joinThread()
	throws java.lang.InterruptedException
	{

		this.join();
	}
}
