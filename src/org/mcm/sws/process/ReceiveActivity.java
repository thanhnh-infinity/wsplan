package org.mcm.sws.process;


/**
 * not implemented yet
 *
 * @see org.mcm.sws.process.InvokeActivity
 * @author    Joachim Peer
 */
 
public class ReceiveActivity extends WebServiceActivity {

	public void execute(ProcessInstance pi) {
		System.out.println("About to execute ReceiveActivity "+getId());
	}
}
