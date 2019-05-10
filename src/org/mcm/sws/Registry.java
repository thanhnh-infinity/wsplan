/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/Registry.java,v 1.2 2004/12/01 16:14:51 joepeer Exp $
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

package org.mcm.sws;

import java.util.*;
import java.io.*;

import org.mcm.sws.wsdl.*;
import org.mcm.sws.util.*;
import org.mcm.sws.pddl.*;
import org.mcm.sws.annotation.*;


import org.apache.log4j.Logger;

/**
 *  Central data container of the system. Holds data like WSDL documents, Annoations, operation definitions, etc.
 *
 * @author    Joachim Peer
 */
public class Registry {

	private static Logger log = Logger.getLogger(Registry.class);

	public final static int WSDL_DOC = 0;
	public final static int WSDL_MESSAGE = 1;
	public final static int WSDL_PORTTYPE = 2;
	public final static int WSDL_OPERATION = 3;
	public final static int WSDL_BINDING = 10;
	public final static int WSDL_PORT = 11;
	public final static int ANNO_DOC = 4;
	public final static int ANNO_OPDEF = 5;
	public final static int ANNO_VARDEF = 6;
	public final static int RELATION_DEF = 7;
	public final static int CONSTANT_DEF = 8;
	public final static int TYPE_DEF = 9;
	public final static int REGSIZE = 12;

	protected HashMap[][] reg;
	//map_uri_obj, map_uri_int, map_int_uri;
	protected final int MAP_URI_OBJ = 0;
	protected final int MAP_URI_INT = 1;
	protected final int MAP_INT_URI = 2;

	protected HashMap aliasNames;

	private static HashMap instances;


	static {
		instances = new HashMap();
	}


	/**
	 *Constructor for the Registry object
	 */
	public Registry() {
		reg = new HashMap[REGSIZE][3];
		for (int i = 0; i < REGSIZE; i++) {
			for (int j = 0; j < 3; j++) {
				reg[i][j] = new HashMap();
			}
		}

		aliasNames = new HashMap();
	}


	//  --- data maintainance ---

	// i've traded small, generice implementation for a nice typed API..
	// a matter of taste / mood i guess

	/**
	 *  Adds a feature to the Object attribute of the Registry object
	 *
	 * @param  type  The feature to be added to the Object attribute
	 * @param  obj   The feature to be added to the Object attribute
	 */
	public synchronized void addObject(int type, URIIdentifiable obj) {
		String uri = obj.getURI();
		if (reg[type][MAP_URI_OBJ].get(uri) == null) {
			reg[type][MAP_URI_OBJ].put(uri, obj);
			Integer ii = new Integer(reg[type][MAP_URI_OBJ].size());
			reg[type][MAP_URI_INT].put(uri, ii);
			reg[type][MAP_INT_URI].put(ii, uri);
		}
	}


	/**
	 *  Adds a feature to the Collection attribute of the Registry object
	 *
	 * @param  type  The feature to be added to the Collection attribute
	 * @param  coll  The feature to be added to the Collection attribute
	 */
	public synchronized void addCollection(int type, Collection coll) {
		for (Iterator iter = coll.iterator(); iter.hasNext(); ) {
			URIIdentifiable o = (URIIdentifiable) iter.next();
			String uri = o.getURI();
			if (reg[type][MAP_URI_OBJ].get(uri) == null) {
				reg[type][MAP_URI_OBJ].put(uri, o);
				Integer ii = new Integer(reg[type][MAP_URI_OBJ].size());
				reg[type][MAP_URI_INT].put(uri, ii);
				reg[type][MAP_INT_URI].put(ii, uri);
			}
		}
	}


	// --- data access ---

	/**
	 *  Gets the object attribute of the Registry object
	 *
	 * @param  type  Description of the Parameter
	 * @param  uri   Description of the Parameter
	 * @return       The object value
	 */
	public Object getObject(int type, String uri) {
		return reg[type][MAP_URI_OBJ].get(uri);
	}


	/**
	 *  Gets the object attribute of the Registry object
	 *
	 * @param  type  Description of the Parameter
	 * @param  i     Description of the Parameter
	 * @return       The object value
	 */
	public Object getObject(int type, int i) {
		String uri = (String) reg[type][MAP_INT_URI].get(new Integer(i));
		if (uri == null) {
			return null;
		} else {
			return reg[type][MAP_URI_OBJ].get(uri);
		}
	}


	public Object getObjectByURIorMnemo(int type, String s) {

		if(s.indexOf("://") != -1) { // URI
			return getObject(type, s);
		} else { // abbreviation
			int mnemo = Integer.parseInt(s.substring( s.lastIndexOf('_')+1 ));
			return getObject(type, mnemo);
		}
	}


	/**
	 *  Gets the integerCode attribute of the Registry object
	 *
	 * @param  type  Description of the Parameter
	 * @param  uri   Description of the Parameter
	 * @return       The integerCode value
	 */
	public Integer getIntegerCode(int type, String uri) {
		return (Integer) reg[type][MAP_URI_INT].get(uri);
	}


	/**
	 *  Gets the intCode attribute of the Registry object
	 *
	 * @param  type  Description of the Parameter
	 * @param  uri   Description of the Parameter
	 * @return       The intCode value
	 */
	public int getIntCode(int type, String uri) {
		Integer ii = getIntegerCode(type, uri);
		if (ii == null) {
			return -1;
		} else {
			return ii.intValue();
		}
	}


	/**
	 *  Gets the uriObjectMap attribute of the Registry object
	 *
	 * @param  type  Description of the Parameter
	 * @return       The uriObjectMap value
	 */
	public Map getUriObjectMap(int type) {
		return reg[type][MAP_URI_OBJ];
	}


	/**
	 *  Gets the collection attribute of the Registry object
	 *
	 * @param  type  Description of the Parameter
	 * @return       The collection value
	 */
	public Collection getCollection(int type) {
		return reg[type][MAP_URI_OBJ].values();
	}


	// *************** init ******************

	/**
	 *  Description of the Method
	 *
	 * @param  config  Description of the Parameter
	 * @return         Description of the Return Value
	 */
	public static Registry createInstance(Config config) {
		Registry registry = new Registry();
		instances.put(Thread.currentThread().getThreadGroup(), registry);

		for(Iterator iter = config.getTypePaths().iterator(); iter.hasNext(); ) {
			registry.addTypesFile((String) iter.next());
		}

		for(Iterator iter= config.getPredicatePaths().iterator(); iter.hasNext(); ) {
			registry.addPredicatesFile((String) iter.next());
		}

		for (Iterator iter = config.getWSDLPaths().iterator(); iter.hasNext(); ) {
			registry.addWSDLFile((String) iter.next());
		}

		for (Iterator iter = config.getAnnotationPaths().iterator(); iter.hasNext(); ) {
			registry.addAnnotationFile((String) iter.next());
		}

		return registry;
	}


	/**
	 *  Adds a feature to the WSDLFile attribute of the Registry object
	 *
	 * @param  s  The feature to be added to the WSDLFile attribute
	 */
	public void addWSDLFile(String s) {

		WSDLDocument wsdlDoc = WSDLDocument.parseWSDL(s);
		if (wsdlDoc == null) {
			log.warn("WSDL document '" + s + "' was not parsed");
			return;
		}

	}


	/**
	 *  Adds a feature to the AnnotationFile attribute of the Registry object
	 *
	 * @param  s  The feature to be added to the AnnotationFile attribute
	 */
	public void addAnnotationFile(String s) {
		try {
			Annotation annotation = Annotation.parseAnnotation(s);
			if (annotation == null) {
				log.warn("Anno document '" + s + "' was not parsed");
				return;
			}
			annotation.register();

		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public void addTypesFile(String s) {
		try {
			List types = OntologyParser.parseTypeFile(s);
			addCollection(TYPE_DEF, types);
		} catch(java.io.IOException ioe) {
			ioe.printStackTrace();
		} catch(org.jdom.JDOMException jde) {
			jde.printStackTrace();
		}

	}

	public void addPredicatesFile(String s) {
		try {
			List relations = OntologyParser.parseRelationFile(s);

			addCollection(RELATION_DEF, relations);
		} catch(java.io.IOException ioe) {
			ioe.printStackTrace();
		} catch(org.jdom.JDOMException jde) {
			jde.printStackTrace();
		}

	}



	/**
	 *  Gets the instance attribute of the Registry class
	 *
	 * @return    The instance value
	 */
	public static Registry getInstance() {
		Thread t = Thread.currentThread();
		return (Registry) instances.get(t.getThreadGroup());
	}

	// *********** alias name management *************

	public void addAliasName(String alias, Object o) {
		aliasNames.put(alias, o);
	}

	public Object getAliasObject(String aliasName) {
		return aliasNames.get(aliasName);
	}

	// *********** debugging ****************

	/**
	 *  Description of the Method
	 */
	public void printContents(PrintStream out) {
		out.println("** REGISTRY **");
		for (int i = 0; i < reg.length; i++) {
			out.println("** CATEGORY " + i + " **");
			for (Iterator iter = reg[i][MAP_URI_OBJ].entrySet().iterator(); iter.hasNext(); ) {
				Map.Entry m = (Map.Entry) iter.next();
				String uri = (String) m.getKey();
				Object o = m.getValue();
				out.println("*) uri=" + uri + ", obj=" + ((URIIdentifiable) o).getURI());
			}
		}
	}
}

