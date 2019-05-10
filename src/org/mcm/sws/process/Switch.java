package org.mcm.sws.process;

import java.util.*;


/**
 * A selection which offers different possible branches depending on 
 * the conditions (cases) met.
 *
 * @see 		org.mcm.sws.process.Case
 *
 * @author    Joachim Peer
 */

public class Switch extends Activity {

	protected List cases;
	
	public Switch() {
		cases = new ArrayList();
	}
	
	public void addCase(Case c) {
		cases.add(c);
	}

	public void execute(ProcessInstance pi) throws ExecutionException {
		System.out.println("About to execute Switch"+getId());
		
		for(Iterator iter=cases.iterator(); iter.hasNext(); ) {
			Case c = (Case) iter.next();
			if(c.isSatisfied(pi)) {
				String ref = c.getRef();
				if(ref != null && ref.length()>0) {
					Activity refAct = rootProcess.getActivity(ref);
					refAct.execute(pi);
					return;
				}
				else
					throw new ExecutionException("problem in switch '"+getId()+"': case does not contain 'ref' attribute");
			}
		}
	}
	
}
