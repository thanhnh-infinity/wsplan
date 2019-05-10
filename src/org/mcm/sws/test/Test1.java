package org.mcm.sws.test;

import java.io.*;
import java.util.*;

import org.mcm.sws.*;
import org.mcm.sws.wsdl.*;
import org.mcm.sws.annotation.*;
import org.mcm.sws.util.*;

/**
 *  Test the direct invokation of annotated operations.
 *
 * @author    Joachim Peer
 */
public class Test1 {

	public static Map parseParams(String s) {
		HashMap result = new HashMap();
		StringTokenizer st = new StringTokenizer(s, "=,");
		while(st.hasMoreTokens()) {
			String key = st.nextToken();
			String value = st.nextToken();
			result.put(key, value);
		}
		return result;
	}
	
	public static void main(String[] args) {
		if(args.length < 4) {
			System.out.println("USAGE: java org.mcm.sws.Test1 <config-path> <serviceURI> <serviceName> <operationName> {<var>'='<value>}"); 
		}
		
		try {		
			Config config = Config.createInstance(args[0]);
			Registry.createInstance(config);
			
			Registry.getInstance().printContents(System.out);
			
			ExecEnvironment env = ExecEnvironment.createInstance(config);		
			String serviceURI = args[1];
			String serviceName = args[2];
			String operationName = args[3];
			
			Map params = (args.length == 5) ? parseParams(args[4]) : new HashMap();
			
			ServiceExec2.callWSOperation(serviceURI, serviceName, operationName, params);
			
			System.out.println("***FACTS***"); 
			for(Iterator iter=env.getFactBase().iterator(); iter.hasNext(); ) {
				System.out.println(iter.next());
			}
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}

}
