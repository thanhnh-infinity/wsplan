/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/pddl/Implication.java,v 1.2 2004/12/01 16:14:51 joepeer Exp $
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
 * a material implication (antecedent ==&gt; consequence), where both
 * antecedent and consequence are formulas.
 *
 * @author    Joachim Peer
 */

public class Implication extends Formula {
	Formula antecedent;
	Formula consequence;

	public void setAntecedent(Formula f) {
		this.antecedent = f;
	}

	public void setConsequence(Formula f) {
		this.consequence = f;
	}

	public List getFormulaeWith(String param) {
		return null;
	}

	public Formula cloneAndSubstitute(Map bindings) {
		Implication imp = new Implication();
		imp.setAntecedent(this.antecedent.cloneAndSubstitute(bindings));
		imp.setConsequence(this.consequence.cloneAndSubstitute(bindings));
		return imp;
	}

	public Formula negate() {
		return null;
	}

	public List expandToFacts() {
		return null;
	}

	public String getPDDL() {
		StringBuffer buf = new StringBuffer();
		buf.append("(implies ");
		buf.append(antecedent.getPDDL());
		buf.append(consequence.getPDDL());
		buf.append(")");
		return buf.toString();
	}
}
