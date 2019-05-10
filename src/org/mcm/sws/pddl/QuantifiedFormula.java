/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/pddl/QuantifiedFormula.java,v 1.2 2004/12/01 16:14:51 joepeer Exp $
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
 * An abstract superclass for both types of quantified formulas.
 *
 * @author    Joachim Peer
 */

public abstract class QuantifiedFormula extends Formula {
	protected List parameters;
	protected Formula body;

	public QuantifiedFormula() {
		this.parameters = new ArrayList();
	}

	public void addParameter(Variable var) {
		parameters.add(var);
	}

	public void setParameters(List parameters) {
		this.parameters = parameters;
	}

	public List getParameters() {
		return parameters;
	}

	public void setBody(Formula f) {
		this.body = f;
	}

	public Formula getBody() {
		return body;
	}

	public List getFormulaeWith(String param) {
		return null;
	}



	public List expandToFacts() {
		return body.expandToFacts();
	}
}
