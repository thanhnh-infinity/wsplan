/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/xmlschema/XSElement.java,v 1.2 2004/12/01 16:14:54 joepeer Exp $
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
import org.mcm.sws.util.*;


import org.apache.log4j.Logger;


public class XSElement extends XSNode {
	private static Logger log = Logger.getLogger(XSElement.class);

	protected XSType typeObj;
	protected String type;
	protected int minOccurs;
	protected int maxOccurs;
	protected String fixed;
	protected boolean nillable;
	protected String ref; // needs to be resolved in second parse step

	public XSElement() {
		maxOccurs=1;
		minOccurs=1;
	}

	public void addType(XSType type) {
		this.typeObj = type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public String getRef() {
		return ref;
	}

	public void setMaxOccurs(int maxOccurs) {
		this.maxOccurs = maxOccurs;
	}

	public int getMaxOccurs() {
		return maxOccurs;
	}

	public void setMinOccurs(int minOccurs) {
		this.minOccurs = minOccurs;
	}

	public int getMinOccurs() {
		return minOccurs;
	}

	public void setFixed(String fixed) {
		this.fixed = fixed;
	}

	public String getFixed() {
		return fixed;
	}

	public void setNillable(boolean nillable) {
		this.nillable = nillable;
	}

	public boolean getNillable() {
		return nillable;
	}

	public boolean needsInstantiation(String currentPath, HashMap values) {
		return 	(super.needsInstantiation(currentPath, values)) ||
						(minOccurs > 0);
	}

	// --
	public void instantiate(javax.xml.soap.SOAPEnvelope env, javax.xml.soap.SOAPElement parentElem, Map values, String currPath, String encoding)
	throws javax.xml.soap.SOAPException {
		if(ref!=null) {
			XSElement refElem = parentSchema.getElement(ref);
			if(refElem != null) {
				refElem.instantiate(env, parentElem, values, currPath, encoding);
			}
			return;
		}

		javax.xml.soap.SOAPElement elem = parentElem.addChildElement(name, "cs", parentSchema.getTargetNamespace()); // todo: set corr. namespace?

		//do we have to assign default value?
		if(fixed!=null) {
			elem.addTextNode(fixed);
		} else {
			//can we assign value from map?
			String matchingValue = StrUtils.matchingValuePath(currPath+"/"+name, values);
			if(matchingValue != null) {
				elem.addTextNode(matchingValue);
			}
		}

		// we can go down here unconditionally, because if this element is
		// eligble for/required to instantiation, then the associate type
		// automatically is
		XSType typeOfElem = null;

		if(typeObj != null) typeOfElem = typeObj;
		else if(type != null) {
			typeOfElem = parentSchema.getType(type);
			if(typeOfElem!=null)
				log.debug("== found type: "+typeOfElem.getName());
			else
				log.debug("== found nothing");
		}

		//add XSI:TYPE attribute
		if(typeOfElem != null && typeOfElem.getName()!=null && encoding!=null && encoding.length()>0) {
			String prefix = parentSchema.findPrefix(typeOfElem.getParentSchema().getTargetNamespace());
			String typeRef = (prefix != null && prefix.length() > 0) ?
											 prefix + ":" + typeOfElem.getName() : typeOfElem.getName();
			javax.xml.soap.Name attName = env.createName("type", "xsi", XSSchema.NS_XSI);
			elem.addAttribute(attName, typeRef);
		}

		if(typeOfElem != null && typeOfElem instanceof XSComplexType) {
			typeOfElem.instantiate(env, elem, values, currPath+"/"+name, encoding);
		}
	}
}
