/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/xmlschema/XSRegistry.java,v 1.2 2004/12/01 16:14:54 joepeer Exp $
 * $Date: 2004/12/01 16:14:54 $
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

package org.mcm.sws.xmlschema;

import java.util.*;

// temporary solution

public class XSRegistry {

	protected static Map schemas;

	static {
		schemas = new HashMap();
	}

	public static void addSchema(XSSchema xs) {
		schemas.put(xs.getTargetNamespace(), xs);
	}

	public static XSSchema getSchema(String tns) {
		return (XSSchema) schemas.get(tns);
	}
}
