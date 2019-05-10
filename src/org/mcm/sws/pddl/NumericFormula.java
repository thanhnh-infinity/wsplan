/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/pddl/NumericFormula.java,v 1.2 2004/12/01 16:14:51 joepeer Exp $
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

package org.mcm.sws.pddl;

import java.util.*;

public abstract class NumericFormula extends Formula {

	protected boolean negated;

	public void setNegated(boolean negated) {
		this.negated = negated;
	}

	public boolean getNegated() {
		return negated;
	}

	public List getFormulaeWith(String param) { return null; }

	public Formula cloneAndSubstitute(Map bindings) { return null; }

	/**
	take a formula and try to extract factual value from it.
	we assume that all variables are bound to values
	*/
	public List expandToFacts() { return null; }

	public Formula negate() { return null; }


}
