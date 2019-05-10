/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/pddl/NumericComparison.java,v 1.2 2004/12/01 16:14:51 joepeer Exp $
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

public class NumericComparison extends Comparison {


	public void setLeft(NumericFormula o) {
		left = o;
	}

	public void setRight(NumericFormula o) {
		right = o;
	}

  public NumericFormula getLeftNE() {
      return (NumericFormula) left;
  }

  public NumericFormula getRightNE() {
      return (NumericFormula) right;
  }


/*
	public String getPDDL() {
		StringBuffer buf = new StringBuffer();
		buf.append("(");
		buf.append(comparisonType);
		buf.append" ");
		buf.append(leftExp.toPDDL());
		buf.append" ");
		buf.append(rightExp.toPDDL());
		buf.append(")");
	}*/

}
