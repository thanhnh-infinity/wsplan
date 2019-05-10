package org.mcm.sws.process;

/**
 * An atomic activity which suspends the respective branch of the 
 * process execution for a given period of time.
 *
 * @author    Joachim Peer
 */


public class WaitActivity extends AtomicActivity {

	protected long ms;
	
	public void setMs(long ms) {
		this.ms = ms;
	}
	
	public long getMs() {
		return ms;
	}
	
	public void execute(ProcessInstance pi) {
		System.out.println("About to execute Wait activity"+getId()+", waiting for "+ms+" milliseconds");
		try {
			Thread.sleep(ms);
			pi.addCompletedActivity(this, ProcessInstance.SUCCESS);
		} catch(Exception ignore) {
			pi.addCompletedActivity(this, ProcessInstance.FAILURE);
		}		
	}
}
