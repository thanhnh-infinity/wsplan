package org.mcm.sws.process;

import java.util.*;

/**
 * helper class for process parser
 */

public class ProcessRegistry {

	protected static Map processes;
	protected static List urls;
	
	static {
		processes = new HashMap();
		urls = new ArrayList();
	}
	
	public static void addProcess(Process p, String url) {
		processes.put(p.getTargetNamespace(), p);
		if(!urls.contains(url))
			urls.add(url);	
	}

	public static Process getProcess(String tns) {
		return (Process) processes.get(tns);
	}
	
	// helps to avoid circular or unnecessary imports
	public static boolean isDocumentLoaded(String url) {
		return urls.contains(url);
	}
}
