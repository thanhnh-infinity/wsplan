/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/xmlschema/XSSimpleType.java,v 1.2 2004/12/01 16:14:54 joepeer Exp $
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


import org.mcm.sws.util.*;

public class XSSimpleType extends XSType {
	private static Logger log = Logger.getLogger(XSSimpleType.class);

	protected XSSimpleContentRestriction simpleContentRestriction;
	protected XSSimpleContentUnion simpleContentUnion;
	protected XSSimpleContentList simpleContentList;

	public void setSimpleContentRestriction(XSSimpleContentRestriction simpleContentRestriction) {
		this.simpleContentRestriction = simpleContentRestriction;
	}

	public XSSimpleContentRestriction getSimpleContentRestriction() {
		return simpleContentRestriction;
	}

	public void setSimpleContentUnion(XSSimpleContentUnion simpleContentUnion) {
		this.simpleContentUnion = simpleContentUnion;
	}

	public XSSimpleContentUnion getSimpleContentUnion() {
		return simpleContentUnion;
	}

	public void setSimpleContentList(XSSimpleContentList simpleContentList) {
		this.simpleContentList = simpleContentList;
	}

	public XSSimpleContentList getSimpleContentList() {
		return simpleContentList;
	}

	public void instantiate(javax.xml.soap.SOAPEnvelope env, javax.xml.soap.SOAPElement parentElem, Map values, String currPath, String encoding)
	throws javax.xml.soap.SOAPException {

		System.out.println("st about to instantiate: "+values);

		//can we assign value from map?
		String matchingValue = StrUtils.matchingValuePath(currPath, values);
		if(matchingValue != null) {
			parentElem.addTextNode(matchingValue);
		} else System.out.println("no value found");

		//add XSI:TYPE attribute
		if(encoding!=null && encoding.length()>0) {
			//String prefix = parentSchema.findPrefix();

			parentElem.addNamespaceDeclaration("ltp", this.getParentSchema().getTargetNamespace());

			//String typeRef = (prefix != null && prefix.length() > 0) ? prefix + ":" + this.getName() : this.getName();

			String typeRef = "ltp:" + this.getName();
			javax.xml.soap.Name attName = env.createName("type", "xsi", XSSchema.NS_XSI);
			parentElem.addAttribute(attName, typeRef);
		}
	}

}
