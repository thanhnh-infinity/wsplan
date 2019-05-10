/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/pddl/Literal.java,v 1.2 2004/12/01 16:14:51 joepeer Exp $
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
 * A literal is an atom (P t1,...,tn) or a negated atom (not(P t1,...,tn)).
 *
 * @author    Joachim Peer
 */
public abstract class Literal extends Formula {
	private static Logger log = Logger.getLogger(Literal.class);

	public static final int ACHIEVE = 0;
	public static final int FINDOUT = 1;

	protected Predicate predicate;
	protected Map terms;
	// goalAnnotation is used only if literal is used as part of a declarative goal specification
	// is set to ACHIEVE automatically by compiler
	protected int goalAnnotation;

	public Literal() {
		this.terms = new HashMap();
	}

	public void setGoalAnnotation(int goalAnnotation) {
		this.goalAnnotation = goalAnnotation;
	}

	public int getGoalAnnotation() {
		return this.goalAnnotation;
	}

	public void setPredicate(Predicate predicate) {
		this.predicate = predicate;
	}

	public Predicate getPredicate() {
		return predicate;
	}

	public void addTerm(String property, Term term) {
		terms.put(property, term);
	}

	public Map getTerms() {
		return terms;
	}

	public Term getTerm(String property) {
		return (Term) terms.get(property);
	}

	public void setTerms(Map terms) {
		this.terms = terms;
	}

	public String getTermAt(int pos) {
		// check in definition which property is at pos
		String propertyName = predicate.getPropertyAt(pos);
		Term t = (Term) terms.get(propertyName);
		return (t != null) ? t.getString() : null;
	}

	public List getFreeVariables() {
		List result = new ArrayList();
		for(Iterator iter = terms.values().iterator(); iter.hasNext(); ) {
			Term t = (Term) iter.next();
			if(t instanceof Variable)
				result.add(t);
		}
		return result;
	}

	public boolean isGround() {
		for(Iterator iter = terms.values().iterator(); iter.hasNext(); ) {
			Term t = (Term) iter.next();
			if(t instanceof Variable) return false;
		}
		return true;
	}

	/*
	public int getParamIndex(String param) {
		for (int i = 0; i < terms.size(); i++) {
			Term term = (Term) terms.get(i);
			if (param.equals(term.getString())) {
				return i;
			}
		}
		return -1;
	}*/

	public String getPropertyOf(String param) {
		for(Iterator iter = terms.entrySet().iterator(); iter.hasNext(); ) {
			Map.Entry me = (Map.Entry) iter.next();
			Term t = (Term) me.getValue();
			if(param.equals(t.getString())) {
				return (String) me.getKey(); // return the property
			}
		}
		return null;
	}

	public List getFormulaeWith(String param) {
		for (Iterator iter = terms.entrySet().iterator(); iter.hasNext(); ) {
			Map.Entry me = (Map.Entry) iter.next();
			Term term = (Term) me.getValue();
			if (term.getString().equals(param)) {
				List result = new ArrayList();
				result.add(this);
				return result;
			}
		}

		return new ArrayList();
	}


	public List expandToFacts() {
		List l = new ArrayList();
		l.add(this);
		return l;
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
	 * PS: and we do not distinguish between Atoms and NegAtoms!
	 *
	 * @param  p  Description of the Parameter
	 * @return    Description of the Return Value
	 */
	public boolean canUnifyWith(Literal l) {
		if(  !(this.getClass().getName().equals(l.getClass().getName()))
			|| (!this.predicate.equals(l.predicate))
		  || (this.terms.size() != l.terms.size())) return false;

		for (Iterator iter = predicate.arguments.entrySet().iterator(); iter.hasNext(); ) {
			String prop = (String) ((Map.Entry) iter.next()).getKey();
			Term t1 = (Term) this.terms.get(prop);
			Term t2 = (Term) l.terms.get(prop);

			// this should not be allowed anyway !
			//if((t1 == null) && (t2 == null)) return true;

			if ((t1 instanceof Name) && (t2 instanceof Name)) {
				if (!t1.getString().equals(t2.getString())) {
					// we can not unify
					return false;
				}
			}
		}
		// TODO : check for variable mismatch (??)
		return true;
	}

	public boolean equals(Object o) {
		if(!this.getClass().equals(o.getClass())) return false;

		Literal l = (Literal) o;
    if((!this.predicate.equals(l.predicate))
		  || (this.terms.size() != l.terms.size())) return false;

		for (Iterator iter = predicate.arguments.entrySet().iterator(); iter.hasNext(); ) {
			String property = (String) ((Map.Entry) iter.next()).getKey();
			Term t1 = (Term) this.getTerm(property);
			Term t2 = (Term) l.getTerm(property);
		  if(!t1.equals(t2)) return false;
		}

		return true;
	}

	public int hashCode() {
		return this.predicate.getURI().hashCode() + this.terms.hashCode();
	}



}

