/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/pddl/Variable.java,v 1.2 2004/12/01 16:14:51 joepeer Exp $
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
 * A variable to be used in formulas that are not ground.
 *
 * @author    Joachim Peer
 */

public class Variable extends Term {

	public Variable() {

	}

	public Variable(String s) {
		string = s;
	}

	public Term cloneAndSubstitute(Map bindings) {

		Term result = null;
		String b = null;
		if((bindings != null) && ((b=(String) bindings.get(this.string))!=null)) {
			result = new Name();
			result.setString(b);
		} else {
			result = new Variable();
			result.setString(this.string);
		}

		return result;
	}

	public boolean equals(Object o) {
		if(!(o instanceof Variable)) return false;
		Variable other = (Variable) o;
		if(this.string == null && other.string == null) return true;
		else if(this.string == null && other.string != null) return false;
		else return this.string.equals(other.string);
	}

	public String getProlog() {
		return string.substring(1).toUpperCase();
	}

}
