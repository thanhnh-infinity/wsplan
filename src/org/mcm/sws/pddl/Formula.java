/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/pddl/Formula.java,v 1.2 2004/12/01 16:14:51 joepeer Exp $
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

/**
 * The abstract superclass of all Formula objects.
 *
 * @author    Joachim Peer
 */

public abstract class Formula {

	/**
	* get the atoms contained in the formula that contain the term &quot;param&qout;
	*/
	public abstract List getFormulaeWith(String param);

	/**
	 * replicate the formula (clone it and all sub-objects) and
	 * substitute the variables by values given in the map.
	 * this method is used when materializing effects of operations after
	 * successful service interaction.
	 */
	public abstract Formula cloneAndSubstitute(Map bindings);

	/**
	 *take a formula and try to extract factual value from it.
	 *we assume that all variables are bound to values
	 */
	public abstract List expandToFacts();

	/**
	 * get the negation of the formula
	 */
	public abstract Formula negate();

	/** get disjunctive normal formula **/
	public Formula getDNF() {
		return this;
	}

	// default..
	public String toString() {
		return getPDDL();
	}

	/**
	 * get the PDDL representation of the formula
	 */
	public abstract String getPDDL();

	public String getProlog(String queryHead) {
		return null;
	}

	/**
	 * check if the formula contains a universal quantified formula, and if
	 * so return it. when materializing effects of operations after
	 * successful service interaction, if the effect contains quantified formulas.
	 * each forall formula may yield several atomic formulae to be instantiated.
	 * (for instance lists of catalog items, etc.)
	 */
	public ForallFormula findForallFormula(Variable var) { return null; }


}
