/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/util/NSDigester.java,v 1.2 2004/12/01 16:14:53 joepeer Exp $
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

import java.util.*;
import org.apache.commons.digester.*;


import org.apache.log4j.Logger;

/**
 * A subclass of the Apache Commons Digester that stores the namespace
 * declarations it discovers in a Map.
 *
 * @author    Joachim Peer
 */
public class NSDigester extends Digester {
	private static Logger log = Logger.getLogger(NSDigester.class);

	protected HashMap namespacePrefixes;

	public NSDigester() {
		super();
		namespacePrefixes = new HashMap();
		setNamespaceAware(true);
	}

  public void startPrefixMapping(java.lang.String prefix, java.lang.String uri)
	throws org.xml.sax.SAXException {
		super.startPrefixMapping(prefix, uri);
		if(namespacePrefixes.get(prefix) == null) {
			namespacePrefixes.put(prefix, uri);
		}
	}

	public Map getNamespacePrefixes() {
		return namespacePrefixes;
	}
}
