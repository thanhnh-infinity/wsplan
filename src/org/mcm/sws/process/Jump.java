package org.mcm.sws.process;

/**
 * Jump to another specified activitiy.
 *
 * @author    Joachim Peer
 */

public class Jump extends AtomicActivity {

	protected String ref;
	
	public void setRef(String ref) {
		this.ref = ref;
	}
	
	public String getRef() {
		return ref;
	}
	
	public void execute(ProcessInstance pi) throws ExecutionException {
		Activity refAct = rootProcess.getActivity(ref);
		if(refAct == null) { 
			throw new ExecutionException("jump: ref'd activity '"+ref+"' not found");
		}
		
		System.out.println("About to execute Jump..");
		pi.addCompletedActivity(this, ProcessInstance.SUCCESS);
		refAct.execute(pi);				
	}
	
}
