/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/xmlschema/XMLSchemaParser.java,v 1.2 2004/12/01 16:14:54 joepeer Exp $
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
import org.mcm.sws.util.*;


import org.apache.log4j.Logger;


/**
 * The XML schema parser
 *
 * @see org.mcm.sws.xmlschema.XSSchema
 * @author Joachim Peer
 */

public class XMLSchemaParser {
	private static Logger log = Logger.getLogger(XMLSchemaParser.class);

	public static void main(String[] args) {
		System.out.println("trying to parse: "+args[0]);
		try {
			InputStream is = Files.getInputStream(args[0]);
			XMLSchemaParser parser = new XMLSchemaParser();
			XSSchema schema = parser.parseSchema(is);
			log.debug("--- PARSING RESULT ---\n"+schema.toString());
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public XSSchema parseSchema(String s) {
		return this.parseSchema(s, new HashMap());
	}

	public XSSchema parseSchema(String s, Map add) {
		InputStream is = new ByteArrayInputStream(s.getBytes());
		return this.parseSchema(is, add);
	}


	public XSSchema parseSchema(InputStream is) {
		return this.parseSchema(is, new HashMap());
	}

	public XSSchema parseSchema(InputStream is, Map additionalNamespaces) {
		XSSchema schema = new XSSchema();
		schema.setAdditionalNamespaces(additionalNamespaces);

		// Initialize a new Digester instance
		org.mcm.sws.util.NSDigester digester = new org.mcm.sws.util.NSDigester();
		digester.setNamespaceAware(true);
		//digester.setLogger(log);
		digester.setRuleNamespaceURI(XSSchema.NS_XSD);
		digester.push(schema);

		//SetObjectValueRule sov = new SetObjectValueRule("setParentSchema", schema);

		// set schema attributes
		digester.addSetProperties("schema", "targetNamespace", "targetNamespace");

		// parse element definition
		digester.addObjectCreate("*/element", "org.mcm.sws.xmlschema.XSElement");
		String[] attributeNames = { "name", "maxOccurs", "minOccurs", "ref", "type" };
		String[] propertyNames = { "name", "maxOccurs", "minOccurs", "ref", "type" };
		digester.addSetProperties("*/element", attributeNames, propertyNames);

		digester.addSetRoot("*/element", "register");
		digester.addSetNext("*/element", "addElement");


		// parse attribute definition
		digester.addObjectCreate("*/attribute", "org.mcm.sws.xmlschema.XSAttribute");
		String[] attributeNames2 = { "name", "ref", "type" };
		String[] propertyNames2 = { "name", "ref", "type" };
		digester.addSetProperties("*/attribute", attributeNames2, propertyNames2);
		digester.addSetRoot("*/attribute", "register");
		digester.addSetNext("*/attribute", "addAttribute");

		// parse simpleType definition
		digester.addObjectCreate("*/simpleType", "org.mcm.sws.xmlschema.XSSimpleType");
		digester.addSetProperties("*/simpleType", "name", "name");
		digester.addSetRoot("*/simpleType", "register");
		digester.addSetNext("*/simpleType", "addType");

		// parse union
		digester.addObjectCreate("*/union", "org.mcm.sws.xmlschema.XSSimpleContentUnion");
		digester.addSetNext("*/union", "setSimpleContentUnion");
		// parse list
		digester.addObjectCreate("*/list", "org.mcm.sws.xmlschema.XSSimpleContentList");
		digester.addSetNext("*/list", "setSimpleContentList");


		// parse complexType definition
		digester.addObjectCreate("*/complexType", "org.mcm.sws.xmlschema.XSComplexType");
		digester.addSetProperties("*/complexType", "name", "name");
		digester.addSetProperties("*/complexType", "mixed", "mixed");
		digester.addSetRoot("*/complexType", "register");
		digester.addSetNext("*/complexType", "addType");

		//parse simpleContent/complexContent - (and nested extension/restriction elements)
		// and simpleType/restriction
		digester.addObjectCreate("*/complexContent/restriction", "org.mcm.sws.xmlschema.XSComplexContentRestriction");
		digester.addSetNext("*/complexContent/restriction", "setComplexContentRestriction");
		digester.addObjectCreate("*/complexContent/extension", "org.mcm.sws.xmlschema.XSComplexContentExtension");
		digester.addSetNext("*/complexContent/extension", "setComplexContentExtension");
		digester.addObjectCreate("*/simpleContent/restriction", "org.mcm.sws.xmlschema.XSSimpleContentRestriction");
		digester.addSetNext("*/simpleContent/restriction", "setSimpleContentRestriction");
		digester.addObjectCreate("*/simpleContent/extension", "org.mcm.sws.xmlschema.XSSimpleContentExtension");
		digester.addSetNext("*/simpleContent/extension", "setSimpleContentExtension");
		digester.addObjectCreate("*/simpleType/restriction", "org.mcm.sws.xmlschema.XSSimpleContentRestriction");
		digester.addSetNext("*/simpleType/restriction", "setSimpleContentRestriction");

		digester.addSetProperties("*/restriction", "base", "base");
		digester.addSetRoot("*/restriction", "register");
		digester.addSetNext("*/restriction", "setRestriction");

		digester.addSetProperties("*/extension", "base", "base");
		digester.addSetRoot("*/extension", "register");
		digester.addSetNext("*/extension", "setExtension");

		// Parse Constraining Facets
		String[] facets = {	"length", "minLength", "maxLength", "pattern", "whiteSpace",
		"maxInclusive", "maxExclusive", "minExclusive", "minInclusive",
		"totalDigits", "fractionDigits", "enumeration" };
		for(int i=0; i<facets.length; i++) {
			String s = facets[i];
			String cs = StrUtils.capitalizeFirstChar(s);
			digester.addObjectCreate("*/"+s, "org.mcm.sws.xmlschema.facets.XS"+cs+"Facet");
			if(!"enumeration".equals(s)) digester.addSetProperties("*/"+s, "fixed", "fixed");
			digester.addSetProperties("*/"+s, "value", "value");
			digester.addSetNext("*/"+s, "add"+cs+"Facet");
		}

		// parse "container" types
		digester.addObjectCreate("*/group", "org.mcm.sws.xmlschema.XSGroup");
		digester.addSetProperties("*/group", "name", "name");
		digester.addSetProperties("*/group", "maxOccurs", "maxOccurs");
		digester.addSetProperties("*/group", "minOccurs", "minOccurs");
		digester.addSetRoot("*/group", "register");
		digester.addSetNext("*/group", "addContainer");

		digester.addObjectCreate("*/all", "org.mcm.sws.xmlschema.XSAll");
		digester.addSetProperties("*/all", "maxOccurs", "maxOccurs");
		digester.addSetProperties("*/all", "minOccurs", "minOccurs");
		digester.addSetRoot("*/all", "register");
		digester.addSetNext("*/all", "addContainer");

		digester.addObjectCreate("*/choice", "org.mcm.sws.xmlschema.XSChoice");
		digester.addSetProperties("*/choice", "maxOccurs", "maxOccurs");
		digester.addSetProperties("*/choice", "minOccurs", "minOccurs");
		digester.addSetRoot("*/choice", "register");
		digester.addSetNext("*/choice", "addContainer");

		digester.addObjectCreate("*/sequence", "org.mcm.sws.xmlschema.XSSequence");
		digester.addSetProperties("*/sequence", "maxOccurs", "maxOccurs");
		digester.addSetProperties("*/sequence", "minOccurs", "minOccurs");
		digester.addSetRoot("*/sequence", "register");
		digester.addSetNext("*/sequence", "addContainer");

		try {
			digester.parse(is);
			XSRegistry.addSchema(schema);
			System.out.println("adding schema at ns="+schema.getTargetNamespace());
			//config.setNamespaces(digester.getNamespacePrefixes());
			return schema;
		} catch (org.xml.sax.SAXException e) {
			e.printStackTrace();
			throw new RuntimeException(e.toString());
		} catch (IOException e1) {
			e1.printStackTrace();
			throw new RuntimeException(e1.toString());
		}
	}

}
