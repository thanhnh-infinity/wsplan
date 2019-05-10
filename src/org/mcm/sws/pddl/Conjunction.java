/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/pddl/Conjunction.java,v 1.2 2004/12/01 16:14:51 joepeer Exp $
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


import org.apache.log4j.Logger;
import java.util.*;

/**
 * a conjunction of formulas.
 *
 * @author    Joachim Peer
 */

public class Conjunction extends Formula {
	private static Logger log = Logger.getLogger(Conjunction.class);

	private ArrayList conjuncts;

	public Conjunction() {
		conjuncts = new ArrayList();
	}

	public void addConjunct(Formula conjunct) {
		conjuncts.add(conjunct);
	}

	public List getConjuncts() {
		return conjuncts;
	}

	public List getFormulaeWith(String param) {
		List result = new ArrayList();
		for(Iterator iter = conjuncts.iterator(); iter.hasNext(); ) {
			Formula f = (Formula) iter.next();
			List l = f.getFormulaeWith(param);
			if(l != null) result.addAll(l);
		}
		return  (result.size() > 0) ? result : null;
	}

	public Formula cloneAndSubstitute(Map bindings) {

		Conjunction newConjunction = new Conjunction();
		for(Iterator iter = this.conjuncts.iterator(); iter.hasNext(); ) {
			newConjunction.conjuncts.add( ((Formula) iter.next()).cloneAndSubstitute(bindings) );
		}

		return newConjunction;
	}

	public List expandToFacts() {
		ArrayList result = new ArrayList();
		for(Iterator iter = conjuncts.iterator(); iter.hasNext(); ) {
				List l = ((Formula) iter.next()).expandToFacts();
				result.addAll(l);
		}
		return result;
	}

	public Formula negate() {
		if(conjuncts.size()==1) {
			Formula f = (Formula) conjuncts.get(0);
			return f.negate();
		}

		Disjunction dis = new Disjunction();  // De Morgan's law
		for(Iterator iter = conjuncts.iterator(); iter.hasNext(); ) {
			Formula con = (Formula) iter.next();
			//Negation neg = new Negation();
			//neg.setBody(con);
			dis.addDisjunct(con.negate());
		}
		return dis;
	}



	public ForallFormula findForallFormula(Variable var) {
		for(Iterator iter = this.conjuncts.iterator(); iter.hasNext(); ) {
			Formula f = (Formula) iter.next();
			ForallFormula fof = f.findForallFormula(var);
			if(fof != null) return fof;
		}
		return null;
	}

	public String getProlog(String queryHead) {
		StringBuffer buf = new StringBuffer();

		if(queryHead != null) {
			buf.append(queryHead);
			buf.append(" :- ");
		}


		for(Iterator iter = this.conjuncts.iterator(); iter.hasNext(); ) {
			buf.append( ((Formula) iter.next()).getProlog(null) );
			if(iter.hasNext()) buf.append(", ");
		}
		return buf.toString();
	}

	public String getPDDL() {
		StringBuffer buf = new StringBuffer();
		buf.append("(and ");
		for(Iterator iter = this.conjuncts.iterator(); iter.hasNext(); ) {
			buf.append( ((Formula) iter.next()).getPDDL() );
		}
		buf.append(")");
		return buf.toString();
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("(and ");
		for(Iterator iter = this.conjuncts.iterator(); iter.hasNext(); ) {
			buf.append( ((Formula) iter.next()).toString() );
		}
		buf.append(")");
		return buf.toString();
	}


}
