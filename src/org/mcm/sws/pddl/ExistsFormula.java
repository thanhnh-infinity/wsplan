/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/pddl/ExistsFormula.java,v 1.2 2004/12/01 16:14:51 joepeer Exp $
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

import org.apache.log4j.Logger;

/**
 * an existential quantification formula.
 * (current limitation: only one quantified variable allowed)
 *
 * @author    Joachim Peer
 */

public class ExistsFormula extends QuantifiedFormula {
	private static Logger log = Logger.getLogger(ExistsFormula.class);

	public Formula cloneAndSubstitute(Map bindings) {
		ExistsFormula f = new ExistsFormula();
		for(Iterator iter = this.parameters.iterator(); iter.hasNext(); ) {
			f.addParameter( (Variable) ((Variable) iter.next()).cloneAndSubstitute(null) );
		}
		f.setBody(body.cloneAndSubstitute(bindings));
		return f;
	}

	public Formula negate() {
		ForallFormula uf = new ForallFormula();
		for(Iterator iter = this.parameters.iterator(); iter.hasNext(); ) {
			uf.addParameter((Variable) iter.next());
		}

		uf.setBody(body.negate());
		return uf;
	}

	public String getPDDL() {
		StringBuffer buf = new StringBuffer("(exists(");
		for(int i=0; i<parameters.size(); i++) {
			Term t = (Term) parameters.get(i);
			if(i > 0) buf.append(",");
			buf.append(t.getString()); // FIXME - future needs typed list
		}
		buf.append(")");
		buf.append(body.getPDDL());
		buf.append(")");
		return buf.toString();
	}
}
