/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/util/MathUtils.java,v 1.2 2004/12/01 16:14:53 joepeer Exp $
 * $Date: 2004/12/01 16:14:53 $
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

package org.mcm.sws.util;

/**
 * Utilities for calculations.
 *
 * @author    Joachim Peer
 */
public class MathUtils {

	public static boolean less(String s1, String s2) {
		try {
			if(s1==null && s2!=null) return false;
			else if (s1!=null && s2==null) return true;

			int i1 = Integer.parseInt(s1);
			int i2 = Integer.parseInt(s2);
			return i1<i2;
		} catch(NumberFormatException nfe) {
			nfe.printStackTrace();
			return false;
		}
	}
}
