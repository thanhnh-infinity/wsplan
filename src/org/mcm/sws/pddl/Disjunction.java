/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/pddl/Disjunction.java,v 1.2 2004/12/01 16:14:51 joepeer Exp $
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
 * a disjunction of formulas.
 *
 * @author    Joachim Peer
 */

public class Disjunction extends Formula {
	private static Logger log = Logger.getLogger(Disjunction.class);

	private ArrayList disjuncts;

	public Disjunction() {
		disjuncts = new ArrayList();
	}

	public void addDisjunct(Formula disjunct) {
		disjuncts.add(disjunct);
	}

	public List getFormulaeWith(String param) {
		return null;
	}

	public Formula cloneAndSubstitute(Map bindings) {

		Disjunction newDisjunction = new Disjunction();
		for(Iterator iter = this.disjuncts.iterator(); iter.hasNext(); ) {
			newDisjunction.disjuncts.add( ((Formula) iter.next()).cloneAndSubstitute(bindings) );
		}

		return newDisjunction;
	}

	public List expandToFacts() {
		return null;
	}

	public Formula negate() {
		if(disjuncts.size()==1) {
			Formula f = (Formula) disjuncts.get(0);
			return f.negate();
		}

		Conjunction con = new Conjunction(); // De Morgan's law
		for(Iterator iter = disjuncts.iterator(); iter.hasNext(); ) {
			Formula dis = (Formula) iter.next();
			//Negation neg = new Negation();
			//neg.setBody(dis);
			con.addConjunct(dis.negate());
		}
		return con;
	}
	/*
	public Formula getDNF() {
		if(disjuncts.size() == 1) {
			return (Formula) disjuncts.get(0);
		}

		for(Iterator iter = disjuncts.iterator(); iter.hasNext(); ) {
			Formula f = (Formula) iter.next();
			if(f instanceof Disjunction) {
				iter.remove(f);
				for(Iterator iter2 = ((Disjunction) f).disjuncts.iterator(); iter2.hasNext(); ) {
					disjuncts.add((Formula) iter2.next());
				}
			} else {
				iter.remove(f);
				disjuncts.add(f.getDNF());
			}
		}
	}
	*/


	public String getProlog(String queryHead) {

		StringBuffer buf = new StringBuffer();

		if(queryHead != null) {
			buf.append(queryHead);
			buf.append(" :- ");
		}

		for(Iterator iter = this.disjuncts.iterator(); iter.hasNext(); ) {
			buf.append(((Formula) iter.next()).getProlog(null));
		}

		return  buf.toString();
	}

	public String getPDDL() {
		StringBuffer buf = new StringBuffer();
		buf.append("(or ");
		for(Iterator iter = this.disjuncts.iterator(); iter.hasNext(); ) {
			buf.append( ((Formula) iter.next()).getPDDL() );
		}
		buf.append(")");
		return buf.toString();
	}



	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("(or ");
		for(Iterator iter = this.disjuncts.iterator(); iter.hasNext(); ) {
			buf.append( ((Formula) iter.next()).toString() );
		}
		buf.append(")");
		return  buf.toString();
	}


}
