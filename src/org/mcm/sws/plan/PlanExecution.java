/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/plan/PlanExecution.java,v 1.2 2004/12/01 16:14:51 joepeer Exp $
 * $Date: 2004/12/01 16:14:51 $
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

package org.mcm.sws.plan;

import java.util.*;

import org.mcm.sws.*;
import org.mcm.sws.pddl.*;

public class PlanExecution {

	Plan plan;
	public Map stepData; // the in/out bindings of each step
	//public List stepDataList;

	public PlanExecution(Plan plan) {
		this.plan = plan;
		this.stepData = new LinkedHashMap();
	//	this.stepDataList = new ArrayList();
	}

	public Plan getPlan() {
		return plan;
	}

	public void addStepData(int stepID, Map data) {
		stepData.put(new Integer(stepID), data);
		//stepDataList.add(data);
	}
/*
	public Map getStepDataForPos(int pos) {
		return (Map) stepDataList.get(pos);
	}*/


	public Map getStepDataForStepID(int id) {
		return (Map) stepData.get(new Integer(id));
	}

}
