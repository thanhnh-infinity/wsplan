/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/strategy/ExecutionMonitoringReplanning.java,v 1.2 2004/12/01 16:14:52 joepeer Exp $
 * $Date: 2004/12/01 16:14:52 $
 *
 * WSPlan - Automatic Web Service Composition
 * Copyright (C) MCM institute, University of St. Gallen
 * Written by Joachim Peer
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.mcm.sws.strategy;

import java.util.*;
import java.io.*;

import org.apache.log4j.Logger;

import org.mcm.sws.*;
import org.mcm.sws.plan.*;
import org.mcm.sws.pddl.*;
import org.mcm.sws.util.*;
import org.mcm.sws.planners.*;
import org.mcm.sws.ui.*;

/**
 *
 * @author    Joachim Peer
 */
public class ExecutionMonitoringReplanning implements PlanningStrategy {
	private static Logger log = Logger.getLogger(ExecutionMonitoringReplanning.class);
	private int threshold = 5;

	/**
	 *  Starts the planning process. Interactions with user interface
	 *  be possible (e.g. to update the graph panel of the GUI)
	 *
	 * @param  callback               UI that may need to be updated to reflect progress etc.
	 * @param  goalSpec               declaratice description of the goal
	 * @exception  PlanningException
	 */
	public void startPlanning(WSPlanUI callback, String goalSpec) throws PlanningException {
		org.mcm.sws.util.Timer.planningTime = 0;
		long t1 = new java.util.Date().getTime();

		HashSet violation = new HashSet();
		Plan plan = null;
		int attempts=0;
		boolean solved = false;
		ExecEnvironment env = ExecEnvironment.getInstance();

		while(!solved && attempts<threshold) {
			log.info("****** new attempt...******");
			try {
				log.info("planning with violation set = "+violation);
				plan = Config.getInstance().getDefaultPlanner().invokePlanner(goalSpec, violation);

				if (callback != null) {
				  callback.addPlanChart(PlanChart.createPlanChart(plan, callback), "Plan "+(attempts+1));
				}

				if(plan == null) {
					long t2 = new java.util.Date().getTime();
					long td = t2 - t1;
					System.out.println("*** no plan, giving up");
					System.out.println("*** Timer.execTime="+td);
					System.out.println("*** Timer.planningTime="+org.mcm.sws.util.Timer.planningTime);

					return;
				}

			} catch(IOException ioe) {
				throw new PlanningException(ioe);
			}
			log.info("plan: "+plan);

			PlanExecution pe = new PlanExecution(plan);

			// first pos (0) is the intial situation, the first real action is at pos=1
			// last pos is the goal situation
			for(int pos=1; pos<plan.size(); pos++) {
				HashMap bindings = new HashMap(); // additional bingins that we gather during CL checking (e.g. ?item in buyItem)
				if(!plan.hasCausalLinkViolation(pe, pos, env, violation, bindings)) {
					if(pos == plan.size()-1) {
						solved = true;
						break; // we don't have to "execute" the "final" step
					}

					ActionInstance nextAi = plan.getActionInstanceAt(pos);


					log.info("******************************************************");
					log.info("POS = "+pos+", NEXTAI = "+nextAi.getOpDef().getName());
					log.info("******************************************************");

					Map allParamBindings = new HashMap();
					ServiceExec2.callWSOperation(nextAi, bindings, allParamBindings, null);

					pe.addStepData(nextAi.getStepID(), allParamBindings);

					if (callback != null) {
						callback.updateFactBaseView(env.getFactBase());
					}


				} else {
					log.info("ran into runtime problems");
					attempts++;
					break;
				}
			}
		}

		if(solved) {
			log.info("Problem is solved");
			env.printFactBase();
		} else {
			log.info("Problem is NOT solved, threshold limit reached: "+attempts);
		}


		long t2 = new java.util.Date().getTime();
		long td = t2 - t1;

		System.out.println("*** Timer.execTime="+td);
		System.out.println("*** Timer.planningTime="+org.mcm.sws.util.Timer.planningTime);
	}


}

