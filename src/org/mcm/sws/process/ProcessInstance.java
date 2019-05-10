package org.mcm.sws.process;

import java.util.*;
import org.apache.commons.lang.*; 

class CompletedActivity {
	protected Activity activity;
	protected int state;
	protected Map inputParams, outputParams; // for WSActivities only
	
	public CompletedActivity(Activity a) {
		this.activity = a;
	}
	
	public Activity getActivity() {
		return activity;
	}
	
	public void setState(int s) {
		this.state = s;
	}
	
	public int getState() {
		return state;
	}
	
	public void setInputParams(Map in) {
		this.inputParams = in;
	}
	
	public void setOutputParams(Map out) {
		this.outputParams = out;
	}	
}

/**
 * An instance of a SESMA process
 * 
 * @see org.mcm.sws.process.Process
 * @author    Joachim Peer
 */
public class ProcessInstance {

	public static final int SUCCESS = 0;
	public static final int FAILURE = 1;
	
	protected Process process;
	protected List completedActivities;
	protected Map processInputs;
	
	public ProcessInstance(Process process) {
		this.process = process;
		completedActivities = new ArrayList();
		processInputs = new HashMap();
	}
		
	public void addCompletedActivity(Activity a, int state) {
		addCompletedActivity(a, state, null, null);
	}
	
	public void addCompletedActivity(Activity a, int state, Map in, Map out) {
		CompletedActivity ca = new CompletedActivity(a);
		ca.setState(state);
		if(in != null) ca.setInputParams(in);
		if(out != null) ca.setOutputParams(out);		
		completedActivities.add(ca);
	}
		
	public CompletedActivity mostRecent() {
		int s = completedActivities.size();
		return (s > 0) ? (CompletedActivity) completedActivities.get(s-1) : null;		
	}

	public CompletedActivity findFirst(String name) {
		for(int i=0; i<completedActivities.size(); i++) {
			CompletedActivity ca = (CompletedActivity) completedActivities.get(i);
			if(name.equals(ca.getActivity().getId())) 
				return ca;
		}
		return null;
	}
	
	public CompletedActivity findLast(String name) {
		for(int i=completedActivities.size()-1; i>=0; i--) {
			CompletedActivity ca = (CompletedActivity) completedActivities.get(i);
			if(name.equals(ca.getActivity().getId())) 
				return ca;
		}
		return null;
	}

	public void addProcessInput(ProcessInput pi, String value) {
		processInputs.put(pi.getId(), value);
	}
	
	public String getInputRuntimeValue(String id) {
		return (String) processInputs.get(id);
	}
	
	public String substituteVariables(String s) {
		for(Iterator iter = processInputs.entrySet().iterator(); iter.hasNext(); ) {
			Map.Entry me = (Map.Entry) iter.next();
			String varName = (String) me.getKey();
			String varValue = (String) me.getValue();
			System.out.println("trying to subsitute:"+varName+" by "+varValue);
			s = StringUtils.replace(s, varName, varValue); 
		}
		return s;
	}
	
}

