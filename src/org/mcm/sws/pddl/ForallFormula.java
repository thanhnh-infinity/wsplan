/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/pddl/ForallFormula.java,v 1.2 2004/12/01 16:14:51 joepeer Exp $
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
 * a universal quantification formula.
 * (current limitation: only one quantified variable allowed)
 *
 * @author    Joachim Peer
 */

public class ForallFormula extends QuantifiedFormula {
	private static Logger log = Logger.getLogger(ForallFormula.class);

	public Formula negate() {
		ExistsFormula ef = new ExistsFormula();
		for(Iterator iter = this.parameters.iterator(); iter.hasNext(); ) {
			ef.addParameter((Variable) iter.next());
		}

		ef.setBody(body.negate());
		return ef;
	}

	public ForallFormula findForallFormula(Variable var) {
		log.debug("--- findForallFormula: var="+var.toString());
		log.debug("--- parameters: "+parameters.toString());

		if(parameters.contains(var)) { return this; }
		else if(body!=null) { return body.findForallFormula(var); }
		else { return null; }
	}

	public Formula cloneAndSubstitute(Map bindings) {
		ForallFormula f = new ForallFormula();
		for(Iterator iter = this.parameters.iterator(); iter.hasNext(); ) {
			f.addParameter( (Variable) ((Variable) iter.next()).cloneAndSubstitute(null) );
		}
		f.setBody(body.cloneAndSubstitute(bindings));
		return f;
	}

	public String getPDDL() {
		StringBuffer buf = new StringBuffer("(forall(");
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
