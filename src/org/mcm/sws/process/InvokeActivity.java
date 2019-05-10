package org.mcm.sws.process;

import java.util.*;

import org.mcm.sws.*;
import org.mcm.sws.util.*;
import org.mcm.sws.annotation.*;

/**
 * This activity defines the invokation of a certain web servic operation. 
 * Currently two-way messages (request-response) are assumed.
 *
 * @author    Joachim Peer
 */

public class InvokeActivity extends WebServiceActivity {


	public void execute(ProcessInstance pi) throws ExecutionException {
		System.out.println("About to execute Invocation "+getId());
		
		System.out.println("op: "+op);
		System.out.println("tns: "+targetNamespace);
		System.out.println("rp: "+rootProcess);		
		
		// retrieve opdef
		String[] opstr = XMLUtils.splitQName(op, targetNamespace, rootProcess.getNamespaces());
		String opDefUri = OpDef.createURI(opstr[0], opstr[1]);		
		OpDef opDef = (OpDef) Registry.getInstance().getObject(Registry.ANNO_OPDEF, opDefUri);
		
		if(opDef == null) {
			throw new ExecutionException("opdef "+opDefUri+" not found in registry");
		}
		
		// prepare variable bindings
		// from KB
		//Map params = opDef.determineBindings();
		Map params = null; //FIXME
		// now, we add the relevant input data
		for(Iterator iter=rootProcess.getProcessInputs().iterator(); iter.hasNext(); ) {
			ProcessInput p = (ProcessInput) iter.next();
			String varName = p.usedFor(this.getId());
			if(varName != null) {
				String runtimeValue = pi.getInputRuntimeValue(p.getId());
				if(runtimeValue != null) {
					params.put(varName, runtimeValue);
				}
			}
		}
		
		// call operation
		ServiceExec2.callWSOperation(opDef, params);
		
		// TO DO - output params!!
		pi.addCompletedActivity(this, ProcessInstance.SUCCESS, params, null);
	}	

}
