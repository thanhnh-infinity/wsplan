/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/util/XMLUtils.java,v 1.2 2004/12/01 16:14:53 joepeer Exp $
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

import org.jdom.*;
import org.jdom.xpath.*;

import org.apache.log4j.Logger;

/**
 * Utility functions for working with XML trees, XML QName strings, etc.
 *
 * @author    Joachim Peer
 */

public class XMLUtils {
	private static Logger log = Logger.getLogger(XMLUtils.class);


	public static void stripRefs(Document doc) throws JDOMException {

		for (Iterator attIter = XPath.newInstance("//@href").selectNodes(doc).iterator(); attIter.hasNext(); ) {
			Attribute att = (Attribute) attIter.next();
			Element badElem = att.getParent();
			String attValue = att.getValue();
			log.debug("**** found @href attribute: " + attValue);

			// now find the multiref elem.
			String mr = "//multiRef[@id='" + attValue.substring(1) + "']";
			Element multiRef = (Element) XPath.newInstance(mr).selectSingleNode(doc);
			if (multiRef == null) {
				log.warn("**** malformed XML, could not find " + mr);
				continue;
			}

			Attribute typeAttr = multiRef.getAttribute("type", Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance"));
			if (typeAttr != null) {
				System.out.println("**** found type!");
				String type = typeAttr.getValue();
				int colonPos = type.indexOf(':');
				String namespacePrefix = null;
				if (colonPos != -1) {
					namespacePrefix = type.substring(0, colonPos);
					type = type.substring(colonPos + 1);
				}

				multiRef.setName(type);
				// TODO - NAMESPACES!!!
				multiRef.detach();

				//now replace badElem by multiRef:
				if("item".equals(badElem.getName())) {
					//System.out.println("++ REPLACING AXIS DUMMY MULTIREF (item)");

					Parent p=badElem.getParent();
					int badIndex = p.indexOf(badElem);
					badElem.detach();
					p.removeContent(badElem);
					p.addContent(badIndex, multiRef);

				} else {
					//System.out.println("++ NOT REPLACING MEANINGFUL MULTIREF");

					badElem.addContent(multiRef);
					//multiRef.setParent(badElem);
				}


			} else {
				log.warn("**** NOT found type!");
			}
		}
	}


	/**
	 *  Description of the Method
	 *
	 * @param  doc                Description of the Parameter
	 * @exception  JDOMException  Description of the Exception
	 */
	public static void stripNamespaces(Document doc) throws JDOMException {
		for (Iterator it = XPath.newInstance("//*").selectNodes(doc).iterator(); it.hasNext(); ) {
			Element elem = (Element) it.next();
			elem.setNamespace(null);
		}
	}


	public static Map namespaceMap(Element elem) {
				Map m = namespaceListToMap(elem.getAdditionalNamespaces());
				Namespace n = elem.getNamespace();
				m.put(n.getPrefix(), n.getURI());
				return m;
	}

	public static Map namespaceListToMap(List ns) {
		HashMap result = new HashMap();
		for(Iterator iter = ns.iterator(); iter.hasNext(); ) {
			Namespace n = (Namespace) iter.next();
			result.put(n.getPrefix(), n.getURI());
		}
		return result;
	}



	public static String[] splitQName(String qName, String targetNamespace, Map additionalNamespaces) {
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
				String baseURI = (String) additionalNamespaces.get(qName.substring(0,colonPos));
				result[0] = (baseURI == null) ? null : baseURI;
				result[1] = qName.substring(colonPos+1);
			} else {
				//result[0] = null;
				result[0] = targetNamespace;
				result[1] = qName;
			}
		}
		return result;
	}

	private String[] splitQName(String qName, String targetNamespace, Map additionalNamespaces, String defaultNamespace) {
		String[] result = splitQName(qName, targetNamespace, additionalNamespaces);
		if(result[0] == null) result[0] = defaultNamespace;
		return result;
	}


}
