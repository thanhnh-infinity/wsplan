/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/pddl/NumericOperation.java,v 1.2 2004/12/01 16:14:51 joepeer Exp $
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

public class NumericOperation extends NumericFormula {

  public static final int ADD = 0;
  public static final int SUB = 1;
  public static final int MUL = 2;
  public static final int DIV = 3;

	protected int operationType;
	protected NumericFormula left;
	protected NumericFormula right;

	public void setOperationType(int operationType) {
		this.operationType = operationType;
	}

	public int getOperatoinType() {
		return operationType;
	}

	public void setLeft(NumericFormula left) {
		this.left = left;
	}

	public NumericFormula getLeft() {
		return left;
	}

	public void setRight(NumericFormula right) {
		this.right = right;
	}

	public NumericFormula getRight() {
		return right;
	}

	public String getPDDL() {
		StringBuffer buf = new StringBuffer();
		buf.append("(");
		switch(operationType) {
			case ADD : buf.append("+"); break;
			case SUB : buf.append("-"); break;
			case MUL : buf.append("*"); break;
			case DIV : buf.append("/"); break;
		}

		buf.append(" ");
		buf.append(left.getPDDL());
		buf.append(" ");
		buf.append(right.getPDDL());

		buf.append(")");
		return buf.toString();
	}

}
