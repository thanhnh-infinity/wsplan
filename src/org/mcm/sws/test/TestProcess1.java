package org.mcm.sws.test;

import java.io.*;
import java.util.*;

import org.mcm.sws.*;
import org.mcm.sws.wsdl.*;
import org.mcm.sws.annotation.*;
import org.mcm.sws.util.*;
import org.mcm.sws.process.*;

/**
 *  Test the process execution of SESMA process definitions.
 *
 *  @author    Joachim Peer
 */
public class TestProcess1 {
	
	public static void main(String[] args) {
		if(args.length < 2) {
			System.out.println("USAGE: java org.mcm.sws.TestProcess1 <config-path> <processURL>"); 
		}
		
		try {		
			Config config = Config.createInstance(args[0]);
			Registry.createInstance(config);
			
			Registry.getInstance().printContents(System.out);
			
			ExecEnvironment env = ExecEnvironment.createInstance(config);		
			String processUrl = args[1];

			org.mcm.sws.process.Process process = org.mcm.sws.process.Process.parse(processUrl);
			
			ProcessInstance processInstance = new ProcessInstance(process);
			
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));			
			for(Iterator iter=process.getProcessInputs().iterator(); iter.hasNext(); ) {
				ProcessInput pi = (ProcessInput) iter.next();
				System.out.println("Enter "+pi.getLabel());
				String value = in.readLine();
				processInstance.addProcessInput(pi, value);
				System.out.println("ok, set."); 
			}

			System.out.println("***FACTS BEFORE PROCESS EXECUTION ***"); 
			for(Iterator iter=env.getFactBase().iterator(); iter.hasNext(); ) {
				System.out.println(iter.next());
			}

			System.out.println("*** now executing process ***");
			process.execute(processInstance);
						
			System.out.println("***FACTS AFTER PROCESS EXECUTION ***"); 
			for(Iterator iter=env.getFactBase().iterator(); iter.hasNext(); ) {
				System.out.println(iter.next());
			}
		} catch(IOException ioe) {
			ioe.printStackTrace();
		} catch(ExecutionException ee) {
			ee.printStackTrace();
		}
	}
}
