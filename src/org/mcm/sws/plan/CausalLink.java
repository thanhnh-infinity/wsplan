/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/plan/CausalLink.java,v 1.2 2004/12/01 16:14:51 joepeer Exp $
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

import org.apache.log4j.Logger;

import org.mcm.sws.pddl.*;

public class CausalLink {

	protected ActionInstance from, to;
	protected Literal condition;

	public void setFrom(ActionInstance from) {
		this.from = from;
	}

	public ActionInstance getFrom() {
		return from;
	}

	public void setTo(ActionInstance to) {
		this.to = to;
	}

	public ActionInstance getTo() {
		return to;
	}

	public void setCondition(Literal condition) {
		this.condition = condition;
	}

	public Literal getCondition() {
		return condition;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("<");

		if(from.getOperation() != null)
			buf.append(from.getOperation().getURI());
		else
			buf.append(from.getStepID());

		buf.append(", ");
		buf.append(condition.toString());
		buf.append(", ");

		if(to.getOperation() != null)
			buf.append(to.getOperation().getURI());
		else
			buf.append(to.getStepID());

		buf.append(">");
		return buf.toString();
	}

}
