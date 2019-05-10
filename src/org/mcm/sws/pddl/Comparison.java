/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/pddl/Comparison.java,v 1.2 2004/12/01 16:14:51 joepeer Exp $
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
 * a comparison formula, as needed for PDDL2.1 Level 2
 *
 * @author    Joachim Peer
 */

public class Comparison extends Formula {
	private static Logger log = Logger.getLogger(Comparison.class);

	public static final int LESS = 0;
	public static final int GREATER = 1;
	public static final int EQUAL = 2;
	public static final int LESS_EQ = 3;
	public static final int GREATER_EQ = 4;

	protected int comparisonType;
	 // can be: TERM (only in case of EQUAL check) or a NUMERICAL FORMULA
	protected Object left, right;

  public void setComparisonType(int comparisonType) {
    this.comparisonType = comparisonType;
  }

  public int getComparisonType() {
      return comparisonType;
  }

	public void setLeft(Object o) {
		left = o;
	}

	public void setRight(Object o) {
		right = o;
	}

	public Formula negate() {
		return null;
	}

	public List expandToFacts() {
		return null;
	}

	public List getFormulaeWith(String param) {
		return null;
	}

	public Formula cloneAndSubstitute(Map m) {
		return null;
	}

	public String getPDDL() {
		/*
		StringBuffer buf = new StringBuffer();
		buf.append("(");
		buf.append(comparisonType);
		buf.append" ");
		buf.append(leftExp.toPDDL());
		buf.append" ");
		buf.append(rightExp.toPDDL());
		buf.append(")");*/

		return "to be done";
	}

}
