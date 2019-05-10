/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/plan/BindingConstraint.java,v 1.2 2004/12/01 16:14:51 joepeer Exp $
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

public class BindingConstraint {
	protected Set stepVars;
	protected String constant;

	public BindingConstraint() {
		this.stepVars = new HashSet();
	}

	public void addStepVar(StepVar sv) {
		this.stepVars.add(sv);
	}

	public Set getStepVars() {
		return stepVars;
	}

	public void setConstant(String constant) {
		this.constant = constant;
	}

	public String getConstant() {
		return constant;
	}

	public boolean containsStepVar(String var, int stepID) {
		StepVar sv = new StepVar(stepID, var);
		return stepVars.contains(sv);
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("{ ");
		for(Iterator iter = stepVars.iterator(); iter.hasNext(); ) {
			StepVar sv = (StepVar) iter.next();
			buf.append(sv.toString());
			if(iter.hasNext()) { buf.append(" "); }
		}
		buf.append(" }");

		if(constant != null) {
			buf.append(" == ");
			buf.append(constant);
		}

		return buf.toString();
	}
}
