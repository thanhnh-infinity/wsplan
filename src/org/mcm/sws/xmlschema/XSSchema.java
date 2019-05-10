/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/xmlschema/XSSchema.java,v 1.2 2004/12/01 16:14:54 joepeer Exp $
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

/**
 * This XML schema implementation is used for correclty
 * instantiating outgoing XML messages. This does NOT require
 * very fine grained validity checks; therefore, this implementation does NOT
 * provide the means to correctly validate XML documents.
 *
 *
 * @author Joachim Peer
 */
public class XSSchema extends XSNode {
	private static Logger log = Logger.getLogger(XSSchema.class);

	public static final String NS_XSD = "http://www.w3.org/2001/XMLSchema";
	public static final String NS_XSI = "http://www.w3.org/2001/XMLSchema-instance";

	protected String targetNamespace;
	protected Map elements;
	protected Map attributes;
	protected Map types;
	protected List containers; // GROUPS, respectively
	protected Map additionalNamespaces;

	public XSSchema() {
		elements = new LinkedHashMap();
		attributes = new LinkedHashMap();
		types = new LinkedHashMap();
		containers = new ArrayList();
		//additionalNamespaces = new HashMap();
	}

	public void setAdditionalNamespaces(Map ans) {
		this.additionalNamespaces = ans;
	}

	public Map getAdditionalNamespaces() {
		return this.additionalNamespaces;
	}

	public void addAdditionalNamespace(Namespace ns) {
		this.additionalNamespaces.put(ns.getPrefix(), ns);
	}

	public void setTargetNamespace(String targetNamespace) {
		this.targetNamespace = targetNamespace;
	}

	public String getTargetNamespace() {
		return targetNamespace;
	}

	public void addElement(XSElement e) {
		elements.put(e.getName(), e);
	}

	public XSElement getElement(String name) {
		String[] qName = splitQName(name);
		if(targetNamespace.equals(qName[0])) {
			return (XSElement) elements.get(name);
		} else {
			XSSchema otherSchema = XSRegistry.getSchema(qName[0]);
			if(otherSchema != null) {
				return otherSchema.getElement(name);
			} else return null;
		}
	}

	public void addType(XSType t) {
		types.put(t.getName(), t);
	}

	public String findPrefix(String namespaceURI) {
		for(Iterator iter = additionalNamespaces.entrySet().iterator(); iter.hasNext(); ) {
			Namespace ns = (Namespace) ((Map.Entry) iter.next()).getValue();
			if(namespaceURI.equals(ns.getURI())) return ns.getPrefix();
		}
		return null;
	}

	public XSType getType(String typeQName) {
		String[] qName = splitQName(typeQName);

		if(targetNamespace.equals(qName[0])) {
			return (XSType) types.get(qName[1]);
		} else {
			XSSchema otherSchema = XSRegistry.getSchema(qName[0]);
			if(otherSchema != null) {
				return otherSchema.getType(typeQName);
			} else return null;
		}
	}

	public void addAttribute(XSAttribute a) {
		attributes.put(a.getName(), a);
	}

	public XSAttribute getAttribute(String name) {
		return (XSAttribute) attributes.get(name);
	}

	public void addContainer(XSContainer c) {
		containers.add(c);
	}

	public void register(XSNode node) {
		// first, invoke callback:
		node.setParentSchema(this);

		// then, see if we need to store the object
		String name = node.getName();
		if(name == null) return;

		if(node instanceof XSType) {
			addType((XSType) node);
		} else if(node instanceof XSElement) {
			addElement((XSElement) node);
		}
		// attriubtes do not get added, because we store only top-level attributes
	}

	private String[] splitQName(String qName) {
		String[] result = new String[2];

		int protoPos = qName.indexOf("://");
		if(protoPos != -1) {
			int crossPos = qName.indexOf('#');
			if(crossPos != -1) {
				result[0] = qName.substring(0,crossPos);
				result[1] = qName.substring(crossPos+1);
			} else {
				int lastSlashPos = qName.lastIndexOf('/');
				result[0] = qName.substring(0,lastSlashPos);
				result[1] = qName.substring(lastSlashPos+1);
			}
		} else { // prefixed foo:bar or name only
			int colonPos = qName.indexOf(':');
			if(colonPos != -1) {
				Namespace baseURI = (Namespace) getAdditionalNamespaces().get(qName.substring(0,colonPos));
				result[0] = (baseURI == null) ? null : baseURI.getURI();
				result[1] = qName.substring(colonPos+1);
			} else {
				//result[0] = null;
				result[0] = targetNamespace;
				result[1] = qName;
			}
		}
		return result;
	}

	private String[] splitQName(String qName, String defaultNamespace) {
		String[] result = splitQName(qName);
		if(result[0] == null) result[0] = defaultNamespace;
		return result;
	}


	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("XML Schema targetNamespace = ");
		buf.append(targetNamespace);
		buf.append("\ntypes:\n");
		buf.append(printXSNodes(types));
		buf.append("\nelements:\n");
		buf.append(printXSNodes(elements));
		buf.append("\ntop-level attributes:\n");
		buf.append(printXSNodes(attributes));
		return buf.toString();
	}

	protected String printXSNodes(Map m) {
		StringBuffer buf = new StringBuffer();
		for(Iterator iter=m.entrySet().iterator(); iter.hasNext(); ) {
			XSNode xn = (XSNode) ((Map.Entry) iter.next()).getValue();
			buf.append(xn.getName());
			if(iter.hasNext()) buf.append(", ");
		}
		return buf.toString();
	}




}
