/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/strategy/PlanningStrategy.java,v 1.2 2004/12/01 16:14:52 joepeer Exp $
 * $Date: 2004/12/01 16:14:52 $
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

package org.mcm.sws.strategy;

/**
 *  Interface to strategies of Web Service composition (cf. white paper)
 *
 * @author    Joachim Peer
 */
public interface PlanningStrategy {

	/**
	 *  Starts the planning process. Interactions with user interface
	 *  be possible (e.g. to update the graph panel of the GUI)
	 *
	 * @param  callback                           UI that may need to be updated to reflect progress etc.
	 * @param  goalSpec                           declaratice description of the goal
	 * @exception  org.mcm.sws.PlanningException  Description of the Exception
	 */

	public void startPlanning(org.mcm.sws.ui.WSPlanUI callback, String goalSpec)
			 throws org.mcm.sws.PlanningException;
}

