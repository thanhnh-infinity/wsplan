package org.mcm.sws.process;

/**
 * an abstract superclass for process activities that deal with 
 * (the interaction with) web services.
 *
 * @author    Joachim Peer
 */


public abstract class WebServiceActivity extends AtomicActivity {

	protected String op;
	
	public void setOp(String op) {
		this.op = op;
	}
	
	public String getOp() {
		return op;
	}
	
	public void addVar(String name, String value, String ref) {
		System.out.println("addVar: TODO");
	}
	
}
