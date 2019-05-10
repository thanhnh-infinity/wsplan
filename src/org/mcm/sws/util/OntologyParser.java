/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/util/OntologyParser.java,v 1.2 2004/12/01 16:14:53 joepeer Exp $
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
import java.io.*;

import org.jdom.*;
import org.jdom.input.*;

import org.mcm.sws.*;
import org.mcm.sws.pddl.*;


import org.apache.log4j.Logger;

/**
 * Utilities for parsing ontologies. Currently,only our proprietary format
 * is supported (not OWL)
 *
 * @author    Joachim Peer
 */
public class OntologyParser {
	private static Logger log = Logger.getLogger(OntologyParser.class);
	/*
<Definitions targetNamespace="&foo;">

  <Predicate name="purchase">
    <parameter name="itemNo" type="&bar;nr"/>
    <parameter name="amount" type="&bar;nr"/>
  </Predicate>

  <Function name="possess">
    <parameter name="item" type="&pddl;object"/>
  </Function>

  <Function name="available-money" />

  <Function name="fuel">
    <parameter name="v" type="&pddl;Vehicle"/>
  </Function>
</Definitions>
  */

	public static List parseRelationFile(String path)
	throws IOException, JDOMException {
		ArrayList result = new ArrayList();

		InputStream is = Files.getInputStream(path);
		Document doc = (new SAXBuilder()).build(is);
		Element root = doc.getRootElement();
		String targetNamespace = root.getAttributeValue("targetNamespace");

		for(Iterator iter = root.getChildren().iterator(); iter.hasNext(); ) {
			Element rDef = (Element) iter.next();
			RelationDefinition rd = null;
			if("Predicate".equals(rDef.getName())) {
				rd = new Predicate();
			} else if("Function".equals(rDef.getName())) {
				rd = new FunctionDefinition();
			}
			String uri = targetNamespace + rDef.getAttributeValue("name");
			rd.setURI(uri);

			for(Iterator iter2 = rDef.getChildren().iterator(); iter2.hasNext(); ) {
				Element param = (Element) iter2.next();

				String paramName =
				param.getAttributeValue("name");

				String paramType = param.getAttributeValue("type");
				if(paramType!=null) { // if some type was specified
					TypeDefinition def = (TypeDefinition) Registry.getInstance().getObject(Registry.TYPE_DEF, paramType);
					if(def == null) {
						log.warn("*** warning: type not found in registry, assuming type OBJECT ***");
						def = TypeDefinition.OBJECT;
					}
					rd.addArgument(paramName, def);
				} else { // if the type spec was ommited
					rd.addArgument(paramName, TypeDefinition.OBJECT);
				}
			}
			result.add(rd);
		}
		return result;

	}

	/*
<Definitions
  targetNamespace="http://joe.com/Types/">

  <Type name="Foo" />

  <Type name="Bar"/>

</Definitions>
	*/
	public static List parseTypeFile(String path)
	throws IOException, JDOMException {
		ArrayList result = new ArrayList();

		InputStream is = Files.getInputStream(path);
		Document doc = (new SAXBuilder()).build(is);
		Element root = doc.getRootElement();
		String targetNamespace = root.getAttributeValue("targetNamespace");

		for(Iterator iter = root.getChildren().iterator(); iter.hasNext(); ) {
			Element tDef = (Element) iter.next();
			TypeDefinition td = new TypeDefinition();
			String uri = targetNamespace + tDef.getAttributeValue("name");
			td.setURI(uri);
			result.add(td);
		}

		return result;
	}

	/**
	 * for testing purposes
	 */
	public static void main(String[] args) {
		try {
			List l = parseRelationFile(args[0]);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
