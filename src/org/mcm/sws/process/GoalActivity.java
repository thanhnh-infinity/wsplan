package org.mcm.sws.process;

/**
 * represents an activity that is declaratively specified as a &qout;goal&out; 
 * to be reached. It is the task of the process execution engine to find
 * a solution to this goal, e.g. by applying AI planning techniques.
 *
 * @author    Joachim Peer
 */

public class GoalActivity extends Activity {

	protected String specification;
	
	public void setSpecification(String specification) {
		this.specification = specification;
	}
	
	public String getSpecification() {
		return specification;
	}
	
	// TODO - problem: additional namespace defined in process 
	// should be brought to config
	public void execute(ProcessInstance pi) throws ExecutionException {
		// first , we need to substitute variable values		
		specification = pi.substituteVariables(specification);
		
		
		System.out.println("About to execute GOALActiity "+getId()+":"+specification);		
		if (specification != null) {
			try {
				org.mcm.sws.util.Config.getInstance().getPlanningStrategy().startPlanning(null, specification);
				pi.addCompletedActivity(this, ProcessInstance.SUCCESS);
			} catch (org.mcm.sws.PlanningException pe) {
				pe.printStackTrace();
				pi.addCompletedActivity(this, ProcessInstance.FAILURE);
			}
		} else {
			throw new ExecutionException ("no goal specified");
		}		
	}	

}
