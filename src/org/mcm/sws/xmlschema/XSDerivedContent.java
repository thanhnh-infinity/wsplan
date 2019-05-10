/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/xmlschema/XSDerivedContent.java,v 1.2 2004/12/01 16:14:54 joepeer Exp $
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
import java.io.*;
import org.jdom.*;


import org.apache.log4j.Logger;


public class XSDerivedContent extends XSNode {
	private static Logger log = Logger.getLogger(XSDerivedContent.class);

	public String base;
	protected LinkedHashMap attributes;

	public XSDerivedContent() {
		attributes = new LinkedHashMap();
	}

	public void setBase(String base) {
		this.base = base;
	}

	public String getBase() {
		return base;
	}

	public void addAttribute(XSAttribute a) {
		attributes.put(a.getName(), a);
	}

	public XSAttribute getAttribute(String name) {
		return (XSAttribute) attributes.get(name);
	}

	public void instantiate(javax.xml.soap.SOAPEnvelope env, javax.xml.soap.SOAPElement parentElem, Map values, String currPath, String encoding)
	throws javax.xml.soap.SOAPException {

		for(Iterator iter=attributes.entrySet().iterator(); iter.hasNext(); ) {
			XSAttribute att = (XSAttribute) ((Map.Entry) iter.next()).getValue();
			if(att.needsInstantiation(currPath, values)) {
				att.instantiate(env, parentElem, values, currPath, encoding);
			}
		}
	}
}

