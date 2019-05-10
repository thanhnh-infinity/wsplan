package org.mcm.sws.process;

import java.util.*;

class ActivityRunner extends Thread {
	protected Activity activity;
	protected ProcessInstance pi;
	
	public ActivityRunner(Activity activity, ProcessInstance pi) {
		this.activity = activity;
		this.pi = pi;
	}
	
	public void run() {
		if(activity != null) {
			try {
				activity.execute(pi);
			} catch(ExecutionException ee) {
				throw new RuntimeException("problem in fork: "+ee.getMessage());
			}
		}
	}
}

/**
 * Using a fork node, a thread can be split into multiple parall execution
 * threads 
 *
 * @author    Joachim Peer
 */
public class Fork extends CompoundActivity {

	public void execute(ProcessInstance pi) throws ExecutionException {
		System.out.println("About to execute Fork"+getId());
		
		ActivityRunner[] runners = new ActivityRunner[children.size()];
		int i=0;
		for(Iterator iter=children.iterator(); iter.hasNext(); i++) {
			Activity act = (Activity) iter.next();
			runners[i] = new ActivityRunner(act, pi);
			runners[i].start();
		}
	
		// wait until all forked threads are finished (dead)
		while(true) {
			boolean stillRunning = false;
			for(i=0; i<runners.length; i++) {
				stillRunning |= runners[i].isAlive();
			}
			if(!stillRunning) 
				return;
			else {
				try { 
					Thread.sleep(100); // wait 0.1 seconds to save ressources
				} catch (Exception ignore) {}
			}
		}
		
	}
}
