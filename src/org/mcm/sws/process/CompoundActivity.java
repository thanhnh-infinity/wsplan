package org.mcm.sws.process;

import java.util.*;

/**
 * abstract superclass of activities that DO contain (user defined) 
 * sub-activities.
 *
 * @author    Joachim Peer
 */

public abstract class CompoundActivity extends Activity {

	protected List children;
	
	public CompoundActivity() {
		children = new ArrayList();
	}
	
	public void addActivity(Activity activity) {
		children.add(activity);
	}
	

}
