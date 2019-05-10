/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/pddl/NegAtom.java,v 1.2 2004/12/01 16:14:51 joepeer Exp $
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
import org.mcm.sws.*;
import org.mcm.sws.util.*;

/**
 * A negated atom
 *
 * @see org.mcm.sws.pddl.Atom
 * @see org.mcm.sws.pddl.Literal
 * @author    Joachim Peer
 */

public class NegAtom extends Literal {

	public Formula negate() {  // TODO clone?
		Atom atom = new Atom();
		atom.setPredicate(predicate);
		atom.setTerms(terms);
		return atom;
	}

	public Formula cloneAndSubstitute(Map bindings) {
		NegAtom a = new NegAtom();
		a.predicate = this.predicate;

		for (Iterator iter = this.terms.entrySet().iterator(); iter.hasNext(); ) {
			Map.Entry me  = (Map.Entry) iter.next();
			String property = (String) me.getKey();
			Term t = (Term) me.getValue();

			a.addTerm(property, t.cloneAndSubstitute(bindings));
		}
		return a;
	}

	public String getProlog(String queryHead) {
		StringBuffer buf = new StringBuffer();

		if(queryHead != null) {
			buf.append(queryHead);
			buf.append(" :- ");
		}

		buf.append("not(");

		buf.append(predicate.getSurfaceName());
		buf.append("_");
		buf.append(Registry.getInstance().getIntCode(Registry.RELATION_DEF, predicate.getURI()));

		if(predicate.getArguments().size() > 0) {
			buf.append("(");
			// the ordering in the predicate definition is authorative
			for(Iterator iter = predicate.getArguments().entrySet().iterator(); iter.hasNext(); ) {
				Map.Entry me = (Map.Entry) iter.next();
				String propertyName = (String) me.getKey();

				Term t = (Term) terms.get(propertyName);
				if(t != null) {
					buf.append(t.getProlog());
				} else throw new RuntimeException(predicate.getSurfaceName()+": property '"+propertyName+"' may not be null");

				if(iter.hasNext()) buf.append(",");
			}
			buf.append(")");
		}

		buf.append(")"); // closing the NOT() pred

		//buf.append(".");
		return buf.toString();
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();

		buf.append("(not (");
		buf.append(predicate.getURI());

		for(Iterator iter = predicate.getArguments().entrySet().iterator(); iter.hasNext(); ) {
			Map.Entry me = (Map.Entry) iter.next();
			String propertyName = (String) me.getKey();
			Term term = (Term) terms.get(propertyName);

			buf.append(propertyName);
			buf.append("=");
			buf.append(term.toString());
			if(iter.hasNext()) buf.append(" ");
		}

		buf.append("))");
		return buf.toString();
	}

	public String getPDDL() {
		StringBuffer buf = new StringBuffer();

		buf.append("(not (");
		buf.append(predicate.getSurfaceName());
		buf.append("_");
		buf.append(Registry.getInstance().getIntCode(Registry.RELATION_DEF, predicate.getURI()));


		buf.append(" ");
		// the ordering in the predicate definition is authorative
		for(Iterator iter = predicate.getArguments().entrySet().iterator(); iter.hasNext(); ) {
			Map.Entry me = (Map.Entry) iter.next();
			String propertyName = (String) me.getKey();

			Term t = (Term) terms.get(propertyName);
			if(t != null) {
				buf.append(t.getString());
			} else throw new RuntimeException(predicate.getSurfaceName()+": property '"+propertyName+"' may not be null");

			if(iter.hasNext()) buf.append(" ");
		}

		buf.append("))");
		return buf.toString();
	}
}
