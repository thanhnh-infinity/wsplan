/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/xmlschema/XSNode.java,v 1.2 2004/12/01 16:14:54 joepeer Exp $
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
import javax.xml.namespace.*;
import org.jdom.*;

import org.mcm.sws.util.*;


import org.apache.log4j.Logger;


public class XSNode {
	private static Logger log = Logger.getLogger(XSNode.class);

	protected String id;
	protected String name;
	protected XSSchema parentSchema;
	protected QName qName;
	protected String path;

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setParentSchema(XSSchema parentSchema) {
		this.parentSchema = parentSchema;
	}

	public XSSchema getParentSchema() {
		return parentSchema;
	}

	public javax.xml.namespace.QName getQName() {
		if(qName == null) {
			qName = new QName(parentSchema.getTargetNamespace(), name);
		}
		return qName;
	}

	/*
	e.g.
	currentPath = "/"
	values = "/order/price"
	name = order
	=> true

	currentPath = "/foo"
	values = "/foo/price"
	name = order
	=> false

	Attribute:
	currentPath = "/foo"
	values = "/foo/price/@val"
	name = val
	=> true
	*/
	public boolean needsInstantiation(String currentPath, Map values) {

		for(Iterator iter = values.keySet().iterator(); iter.hasNext(); ) {
			String paramPath = (String) iter.next(); // path to vardef value

			if(StrUtils.containsPath(paramPath, currentPath))
				 return true;
		}
		return false;
	}

	public void instantiate(javax.xml.soap.SOAPEnvelope env, javax.xml.soap.SOAPElement parentElem, Map values, String currPath, String encoding)
	throws javax.xml.soap.SOAPException {}

}
