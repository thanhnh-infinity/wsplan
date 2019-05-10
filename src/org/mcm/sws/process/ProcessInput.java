package org.mcm.sws.process;

import java.util.*;

/**
 * Input needed for instantiating the process. The actual gathering of inputs
 * is left to the process exection engine. In the present implementation, an mesage
 * is prompted to the user at stdout and the input is read via stdin. 
 *
 * @author    Joachim Peer
 */

public class ProcessInput {

	protected String id;
	protected String label;
	protected Map useFor;
	
	public ProcessInput() {
		this.useFor = new HashMap(); 
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}


	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void addFor(String activity, String varName) {
		useFor.put(activity, varName);
	}
	
	public String usedFor(String activityId) {
		return (String) useFor.get(activityId);
	}
	

}
