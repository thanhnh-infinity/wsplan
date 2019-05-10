/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/pddl/Function.java,v 1.2 2004/12/01 16:14:51 joepeer Exp $
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
import org.mcm.sws.URIIdentifiable;
import org.mcm.sws.Registry;

/**
 * A Funcion (PDDL 2.1 level 2)
 *
 * @author    Joachim Peer
 */
public class Function extends NumericFormula {
	private static Logger log = Logger.getLogger(Function.class);

	/**
	 *  Description of the Field
	 */
	protected FunctionDefinition definition;
	protected Map terms;
	protected float value; // a number

	/**
	 *Constructor for the Predicate object
	 */
	public Function() {
		terms = new LinkedHashMap();
	}

	/**
	 *  Gets the termAt attribute of the Predicate object
	 *
	 * @param  pos  Description of the Parameter
	 * @return      The termAt value
	 */
	public String getTermAt(int pos) {
		String propertyName = definition.getPropertyAt(pos);
		Term t = (Term) terms.get(propertyName);
		return (t != null) ? t.getString() : null;
	}


	/**
	 *  Adds a feature to the Term attribute of the Predicate object
	 *
	 * @param  term  The feature to be added to the Term attribute
	 */
	public void addTerm(String property, Term term) {
		terms.put(property, term);
	}


	/**
	 *  Gets the terms attribute of the Predicate object
	 *
	 * @return    The terms value
	 */
	public Map getTerms() {
		return terms;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public float getValue() {
		return value;
	}

	public void setDefinition(FunctionDefinition definition) {
		this.definition = definition;
	}

	public FunctionDefinition getDefinition() {
		return definition;
	}

	/**
	 *  Description of the Method
	 *
	 * @return    Description of the Return Value
	 */
	public String toString() {
		/*
		StringBuffer buf = new StringBuffer();
		buf.append("(");
		buf.append(uri);

		for (int i = 0; i < terms.size(); i++) {
			buf.append(" ");
			//buf.append("?var"+(i+1));
			buf.append(((Term) terms.get(i)).toString());
		}

		buf.append(")");
		return buf.toString();*/
		return "to be done";
	}




	/**
	 *  Gets the definition attribute of the Predicate object
	 *
	 * @return    The definition value
	 */
	public String getPDDL() {
		/*
		StringBuffer buf = new StringBuffer();
		buf.append("(");
		buf.append(definition.getSurfaceName());
		buf.append("_");
		buf.append(Registry.getInstance().getIntCode(Registry.RELATION_DEF, definition.getURI()));

		for (int i = 0; i < terms.size(); i++) {
			buf.append(" ");
			buf.append( ((Term) terms.get(i)).toString() );
		}

		buf.append(")");
		return buf.toString();*/
		return "to be done";
	}


	// e.g (foo bar bar)
	// FIXME: currently parses only FACTS (i.e. ignores variableS)
	/**
	 *  Description of the Method
	 *
	 * @param  s  Description of the Parameter
	 * @return    Description of the Return Value
	 */
	public static Function parseInstance(String s) {
		/*
		Predicate pred = new Predicate();

		StringTokenizer st = new StringTokenizer(s.trim(), "() ");
		String _uri;
		if (st.hasMoreTokens()) {
			_uri = st.nextToken();



			if (_uri.indexOf("://") == -1) {
				_uri = "local://" + _uri;
			}
			// set url and make sure that surface label is produced, too
			pred.setURI(_uri);
			return null;
		}

		while (st.hasMoreTokens()) {
			Name n = new Name();
			n.setString(st.nextToken());
			pred.addTerm(n);
		}

		return pred;*/
		return null;
	}


	/**
	 *  Description of the Method
	 *
	 * @return    Description of the Return Value
	 */
	public List expandToFacts() {
		List l = new ArrayList();
		l.add(this);
		return l;
	}


	/**
	 *  Description of the Method
	 *
	 * @return    Description of the Return Value
	 */
	public Formula negate() {
		//Negation neg = new Negation();
		//neg.setBody(this);
		// TODO - should we clone??
		//return neg;
		return null;
	}

	public Formula cloneAndSubstitute(HashMap m) {
		return null;
	}

	/**
	 *i think this is no real unification routine because
	 *the variable values are not available. So, it's rather
	 *a raw check to see if 2 preds CAN eventually unify (??)
	 *(foo bar) unifies with (foo ?x)
	 *(foo ?x) unifies with (foo ?y)
	 *(foo bar) does NOT unify with (foo some)
	 *(foo bar ?x) unifies with (foo bar ?y)
	 *(foo bar ?x) does NOT unify with (foo run ?y)
	 *
	 * @param  p  Description of the Parameter
	 * @return    Description of the Return Value
	 */
	public boolean canUnifyWith(Atom p) {
		if (!equals(p)) {
			return false;
		}

		for (Iterator iter = definition.arguments.entrySet().iterator(); iter.hasNext(); ) {
			String prop = (String) ((Map.Entry) iter.next()).getKey();
			Term t1 = (Term) this.terms.get(prop);
			Term t2 = (Term) p.terms.get(prop);

			// this should not be allowed anyway !
			//if((t1 == null) && (t2 == null)) return true;

			if ((t1 instanceof Name) && (t2 instanceof Name)) {
				if (!t1.getString().equals(t2.getString())) {
					// we can not unify
					return false;
				}
			}
		}


		return true;
	}


	/**
	 *  Description of the Method
	 *
	 * @param  o  Description of the Parameter
	 * @return    Description of the Return Value
	 */
	public boolean equals(Object o) {
		if (o == null || !(o instanceof Function)) {
			return false;
		}
		Function f = (Function) o;

		boolean b = (this.definition.equals(f.definition) &&
				this.terms.size() == f.terms.size());

		return b;
	}


	/**
	 *  Description of the Method
	 *
	 * @return    Description of the Return Value
	 */
	public int hashCode() {
		return this.definition.uri.hashCode() + this.terms.size();
	}
}

