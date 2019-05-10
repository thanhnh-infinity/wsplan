package org.mcm.sws.process;

/**
 * Abstract superclass of Activities. An activity is an executable element of a
 * process. The semantic of activities is derived from UML activities in UML 
 * activity diagrams.
 *
 * @author    Joachim Peer
 */

public abstract class Activity {
	
	protected String id;
	protected String uri;
	protected Process rootProcess;
	protected String targetNamespace;
	
	protected static int automaticIDs=100;
	
	/**
	 * executes this activity 
	 */
	public abstract void execute(ProcessInstance pi) throws ExecutionException;
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getId() {
		if(id == null) {
			id = "act" + automaticIDs++;
		}
		
		return id;
	}
	
	public void setRootProcess(Process rootProcess) {
		this.rootProcess = rootProcess;
	}
	
	public Process getRootProcess() {
		return rootProcess;
	}
	
	public void setTargetNamespace(String targetNamespace) {
		this.targetNamespace = targetNamespace;
	}
		
	public String getTargetNamespace() {
		return targetNamespace;
	}

	
}
