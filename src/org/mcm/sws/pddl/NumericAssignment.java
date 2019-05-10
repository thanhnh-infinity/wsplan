/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/pddl/NumericAssignment.java,v 1.2 2004/12/01 16:14:51 joepeer Exp $
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

public class NumericAssignment extends NumericFormula {

	public static final int ASSIGN = 0;
	public static final int INCREASE = 1;
	public static final int DECREASE = 2;
	public static final int SCALE_UP = 3;
	public static final int SCALE_DOWN = 4;

	protected int assignmentType;
	protected Function left;
	protected NumericFormula right;

	public void setAssignmentType(int assignmentType) {
		this.assignmentType = assignmentType;
	}

	public int getAssignmentType() {
		return assignmentType;
	}

	public void setLeft(Function left) {
		this.left = left;
	}

	public Function getLeft() {
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
		switch(assignmentType) {
			case ASSIGN : buf.append("assign"); break;
			case INCREASE : buf.append("increase"); break;
			case DECREASE : buf.append("decrease"); break;
			case SCALE_UP : buf.append("scale-up"); break;
			case SCALE_DOWN : buf.append("scale-down"); break;
		}

		buf.append(" ");
		buf.append(left.getDefinition().getPDDL());
		buf.append(" ");
		buf.append(right.getPDDL());

		buf.append(")");
		return buf.toString();
	}

}
