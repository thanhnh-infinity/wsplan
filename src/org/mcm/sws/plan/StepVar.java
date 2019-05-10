/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/plan/StepVar.java,v 1.2 2004/12/01 16:14:51 joepeer Exp $
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

public class StepVar {
	protected int stepID;
	protected String varName;

	public StepVar(int stepID, String varName) {
		this.stepID = stepID;
		this.varName = varName;
	}

	/*public void setStepID(int stepID) {
		this.stepID = stepID;
	}*/

	public int getStepID() {
		return stepID;
	}

	/*public void setVarName(String varName) {
		this.varName = varName;
	}*/

	public String getVarName() {
		return varName;
	}

	public boolean equals(Object o) {
		System.out.println("StepVar.equals...");
		if(!(o instanceof StepVar)) return false;
		StepVar sv = (StepVar) o;

		System.out.println("StepVar.equals:"+sv.toString()+" vs. "+this.toString()+" is: "+((this.varName.equals(sv.varName)) && (this.stepID == sv.stepID)) );

		return (this.varName.equals(sv.varName))
						&& (this.stepID == sv.stepID);
	}

	public int hashCode() {
		return varName.hashCode() + stepID;
	}

	public String toString() {
		return varName + "(" + stepID + ")";
	}
}
