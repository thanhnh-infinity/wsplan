package org.mcm.sws.process;

/**
 * this exception is thrown during process execution if a critical error 
 * occurs.
 *
 * @author    Joachim Peer
 */

public class ExecutionException extends Exception {
	public ExecutionException(String msg) {
		super(msg);		
	}
}
