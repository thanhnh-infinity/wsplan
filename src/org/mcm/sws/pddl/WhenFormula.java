/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/pddl/WhenFormula.java,v 1.2 2004/12/01 16:14:51 joepeer Exp $
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
 * A formula representing conditional effects.
 *
 * <p>example:</p>
 *
 * <pre>
 *     <effect>
 *       (forall (?item)
 *         (when (s:in-cart client service2 ?item ?cnt)
 *               (and (s:possessn client ?item ?cnt)
 *                    (s:possess client ?item)
 *                    (not (s:in-cart client service2 ?item ?cnt)))))
 *     </effect>
 * </pre>
 * @author    Joachim Peer
 */
public class WhenFormula extends Formula {
	private static Logger log = Logger.getLogger(Conjunction.class);

	protected Formula condition;
	protected Formula consequence;

	public Formula getCondition()	{
		return condition;
	}

	public void setCondition(Formula condition)	{
		this.condition = condition;
	}

	public Formula getConsequence()	{
		return consequence;
	}

	public void setConsequence(Formula consequence)	{
		this.consequence = consequence;
	}


	public List getFormulaeWith(String param) {
		return consequence.getFormulaeWith(param);
	}

	public Formula cloneAndSubstitute(Map bindings) {
		WhenFormula when = new WhenFormula();
		when.setCondition(this.condition.cloneAndSubstitute(bindings));
		when.setConsequence(this.consequence.cloneAndSubstitute(bindings));
		return when;
	}

	public List expandToFacts() {
		return consequence.expandToFacts();
	}

	public Formula negate() {
		return null;
	}

	/*
	(when
		(and (in ?z) (not (= ?z B)))
		(and (at ?z ?l) (not (at ?z ?m)))
	)
	*/
	public String getPDDL() {
		StringBuffer buf = new StringBuffer();
		buf.append("(when\n");
		buf.append(condition.getPDDL());
		buf.append("\n");
		buf.append(consequence.getPDDL());
		buf.append(")");
		return buf.toString();
	}


}
