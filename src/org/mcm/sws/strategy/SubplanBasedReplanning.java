/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/strategy/SubplanBasedReplanning.java,v 1.2 2004/12/01 16:14:52 joepeer Exp $
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


import org.apache.log4j.Logger;

import org.mcm.sws.*;
import org.mcm.sws.plan.*;
import org.mcm.sws.pddl.*;
import org.mcm.sws.util.*;
import org.mcm.sws.planners.*;
import org.mcm.sws.ui.*;

/**
 *  This class implements the SubPlan based sensing and replanning
 *  method as outlined in the WSPlan paper at
 *  http://elektra.mcm.unisg.ch/pbwsc/docs/ppswr04.pdf.
 *
 * @author    Joachim Peer
 */
public class SubplanBasedReplanning implements PlanningStrategy {
	private static Logger log = Logger.getLogger(SubplanBasedReplanning.class);

	/**
	 *  Starts the planning process. Interactions with user interface
	 *  be possible (e.g. to update the graph panel of the GUI)
	 *
	 * @param  callback               UI that may need to be updated to reflect progress etc.
	 * @param  goalSpec               declaratice description of the goal
	 * @exception  PlanningException
	 */
	public void startPlanning(WSPlanUI callback, String goalSpec) throws PlanningException {

		HashSet alreadyQueried = new HashSet();
		HashSet alreadyQueriedOpDefs = new HashSet();
		Plan sensingSubPlan = null;

		try {

			do {
				Plan p = Config.getInstance().getDefaultPlanner().invokePlanner(goalSpec, alreadyQueriedOpDefs);

				if (p != null) {
					System.out.println(p.toString());

					sensingSubPlan = p.getSensingSubplan();
					if (sensingSubPlan == null) {

						if (callback != null) {
							callback.addPlanChart(PlanChart.createPlanChart(p, callback), "Plan to achieve goals");
						}

						ServiceExec2.executePlan(p, callback);
					} else {

						if (callback != null) {
							callback.addPlanChart(PlanChart.createPlanChart(sensingSubPlan, callback), "Sensing subplan");
						}

						List ais = sensingSubPlan.getActionInstances();

						int cnt = 0;
						for (Iterator iter = ais.iterator(); iter.hasNext(); ) {
							ActionInstance anAi = (ActionInstance) iter.next();

							//System.out.println("++ **** printin out alreadyQueried ++");
							//CollUtils.printObjects(alreadyQueried, System.out);

							if (!alreadyQueried.contains(anAi)) {
								ServiceExec2.callWSOperation(anAi, new HashMap(), new HashMap(), callback);
								alreadyQueried.add(anAi);
								alreadyQueriedOpDefs.add(anAi.getOpDef());
							} else {
								cnt++;
							}
						}

						if (cnt == ais.size()) {
							throw new RuntimeException("sensing loop?");
						}
					}

				} else {
					log.info("no plan");
					break;
				}

			} while (sensingSubPlan != null);
		} catch (Exception e) {
			e.printStackTrace();
			throw new PlanningException(e);
		}

	}
}

