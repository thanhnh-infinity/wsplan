/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/annotation/Annotation.java,v 1.2 2004/12/01 16:14:51 joepeer Exp $
 * $Date: 2004/12/01 16:14:51 $
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

package org.mcm.sws.annotation;

import java.util.*;
import java.io.*;
import org.jdom.*;
import org.jdom.input.*;


import org.apache.log4j.Logger;


import org.mcm.sws.*;
import org.mcm.sws.pddl.*;
import org.mcm.sws.util.*;

/**
 * This class represents the root element of SESMA descriptions
 * (c.f. http://elektra.mcm.unisg.ch/pbwsc/docs/sd_ecows.pdf )
 *
 * @author    Joachim Peer
 */
public class Annotation implements URIIdentifiable {
	private static Logger log = Logger.getLogger(Annotation.class);

	public static final Namespace NS_WSDL_ANNO = Namespace.getNamespace("http://schemata.org/sws/wsdl");
	public static final Namespace NS_OWL_ANNO = Namespace.getNamespace("http://schemata.org/sws/owl");
  public static final Namespace NS_SESMA = Namespace.getNamespace("http://schemata.org/sws/sesma");

	protected String uri;
	protected String targetNamespace, serviceNamespace, serviceName, url;
	protected Requirements requirements;
	protected HashMap additionalNamespaces;
	protected List opDefs;


	/**
	 *Constructor for the Annotation object
	 */
	public Annotation() {
		opDefs = new ArrayList();
		additionalNamespaces = new HashMap();
		requirements = new Requirements();
	}



	public void setTargetNamespace(String targetNamespace) {
		this.targetNamespace = StrUtils.slashed(targetNamespace);
	}

	public String getTargetNamespace() {
		return targetNamespace;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceNamespace(String serviceNamespace) {
		this.serviceNamespace = serviceNamespace;
	}

	public String getServiceNamespace() {
		return serviceNamespace;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public String getURI() {
		if (uri == null) {
			uri = StrUtils.slashed(targetNamespace);
		}
		return uri;
	}
/*
	public void setRequirements(String req) {
		StringTokenizer st = new StringTokenizer(req, " ,");
		while(st.hasMoreTokens()) {
			String token = st.nextToken();
			requirements.add(token);
		}
	}*/

	public Requirements getRequirements() {
		return requirements;
	}

	public void addOpDef(OpDef opDef) {
		opDefs.add(opDef);
	}

	public void addAdditionalNamespace(org.jdom.Namespace additionalNamespace) {
		this.additionalNamespaces.put(additionalNamespace.getPrefix(), additionalNamespace.getURI());
	}

	public HashMap getAdditionalNamespaces() {
		return additionalNamespaces;
	}

	public String getAdditionalNamespace(String prefix) {
		org.jdom.Namespace ns = (org.jdom.Namespace) additionalNamespaces.get(prefix);
		return (ns != null) ? ns.getURI() : null;
	}

	public void finishParsing() {

		for (Iterator iter = opDefs.iterator(); iter.hasNext(); ) {
			OpDef od = (OpDef) iter.next();
			od.finishParsing(this, additionalNamespaces);
		}
	}


	public void register() {
		Registry.getInstance().addObject(Registry.ANNO_DOC, this);

		for (Iterator iter = opDefs.iterator(); iter.hasNext(); ) {
			OpDef od = (OpDef) iter.next();
			od.register();
		}
	}


	// ======================== parsing ========================

	/**
	 * This method parses the document at the specified location.
	 */
	public static Annotation parseAnnotation(String input) throws IOException {

		try {
			InputStream is = Files.getInputStream(input);
			Document doc = new SAXBuilder().build(is);
			Element rootElem = doc.getRootElement();

			Annotation anno = new Annotation();

			List nss = rootElem.getAdditionalNamespaces();
			for (Iterator iter = nss.iterator(); iter.hasNext(); ) {
				Namespace ns = (Namespace) iter.next();
				anno.addAdditionalNamespace(ns);
			}

			anno.setTargetNamespace(rootElem.getAttributeValue("targetNamespace"));
			anno.setServiceNamespace(rootElem.getAttributeValue("serviceNamespace"));
			anno.setServiceName(rootElem.getAttributeValue("serviceName"));
			anno.setUrl(rootElem.getAttributeValue("url"));

			for(Iterator iter = rootElem.getChildren("import-voc" , NS_SESMA).iterator(); iter.hasNext(); )  {
			  Element importElem = (Element) iter.next();
			  String vocUrl = importElem.getAttributeValue("url");
				List l = OntologyParser.parseRelationFile(vocUrl);
				if(l.size() > 0) {
					Registry.getInstance().addCollection(Registry.RELATION_DEF, l);
				}
			}

			Element functionalProfileElem = rootElem.getChild("functional-profile", NS_SESMA);
			for(Iterator iter = functionalProfileElem.getChildren("op-def", NS_SESMA).iterator(); iter.hasNext(); ) {
				Element opDefElem = (Element) iter.next();
				OpDef opDef = OpDef.parse(opDefElem, anno);
				anno.addOpDef(opDef);
			}

			anno.finishParsing();

			return anno;
		} catch(JDOMException je) {
			throw new IOException(je.getMessage());
		}
	}

}

