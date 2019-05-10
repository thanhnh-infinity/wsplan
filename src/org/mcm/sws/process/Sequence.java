package org.mcm.sws.process;

import java.util.*;

/**
 * A sequence of activities.
 *
 * @author    Joachim Peer
 */


public class Sequence extends CompoundActivity {

	public void execute(ProcessInstance pi) throws ExecutionException {
		System.out.println("About to execute Sequence"+getId());
		
		for(Iterator iter=children.iterator(); iter.hasNext(); ) {
			Activity act = (Activity) iter.next();
			System.out.println("Sequence child:"+act.getId());
			act.execute(pi);
		}
	}

}
