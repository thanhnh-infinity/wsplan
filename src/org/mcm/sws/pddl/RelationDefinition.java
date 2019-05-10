/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/pddl/RelationDefinition.java,v 1.2 2004/12/01 16:14:51 joepeer Exp $
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

package org.mcm.sws.pddl;

import java.util.*;
import org.mcm.sws.URIIdentifiable;
import org.mcm.sws.Registry;

/**
 * an abstact superclass for the definition of predicate and function symbols.
 *
 * @author    Joachim Peer
 */
public abstract class RelationDefinition implements URIIdentifiable {
	/**
	 *  Description of the Field
	 */
	protected String localName, prefix, uri;
	/**
	 *  Description of the Field
	 */
	protected String surfaceName;
	// e.g. foo_bar => foobar

	/**
	 *  Description of the Field
	 */
	protected LinkedHashMap arguments;


	/**
	 *Constructor for the Predicate object
	 */
	public RelationDefinition() {
		arguments = new LinkedHashMap();
	}

	public void addArgument(String property, TypeDefinition td) {
		arguments.put(property, td);
	}

	public Map getArguments() {
		return arguments;
	}

	public boolean hasProperty(String property) {
		return (arguments.get(property) != null);
	}

	public String getPropertyAt(int pos) {
		int cnt=0;
		for(Iterator iter = arguments.entrySet().iterator(); iter.hasNext(); ) {
			Map.Entry me = (Map.Entry) iter.next();
			if(cnt==pos) {
				return (String) me.getKey();
			}
			cnt++;
		}
		return null;
	}

	/**
	 *  Sets the uRI attribute of the Predicate object
	 *
	 * @param  uri  The new uRI value
	 */
	public void setURI(String uri) {
		this.uri = uri;

		// extract readable local name
		int lastSlashIndex = uri.lastIndexOf('/');
		int lastCrossIndex = uri.lastIndexOf('#');
		int cutoff = (lastSlashIndex > lastCrossIndex) ? lastSlashIndex : lastCrossIndex;
		String sname = uri.substring(cutoff + 1);
		this.surfaceName = org.apache.commons.lang.StringUtils.replaceChars(sname, "_", "");
	}


	/**
	 *  Gets the uRI attribute of the Predicate object
	 *
	 * @return    The uRI value
	 */
	public String getURI() {
		return uri;
	}


	/**
	 *  Gets the surfaceName attribute of the Predicate object
	 *
	 * @return    The surfaceName value
	 */
	public String getSurfaceName() {
		return surfaceName;
	}


	public void setPrefixUriName(String s, HashMap namespaces) {
		int colon = s.indexOf(':');
		int proto = s.indexOf("://");
		if(colon != -1 && proto == -1) { // prefix
			String prefix = s.substring(0,colon);
			String name = s.substring(colon+1);

			String uriStub = (String) namespaces.get(prefix);
			if(uriStub != null) {
				this.setURI( uriStub.endsWith("/") ? uriStub + name : uriStub + "/" + name );
			}

		} else if(colon != -1 && proto != -1) { // plain uri
			this.setURI(s);
		} else {  // just a local name
			//p.setLocalName(s);
			this.setURI( "local://" + s); // should the targetNS of the referred service be used?
		}
	}


	/**
	 *  Description of the Method
	 *
	 * @param  o  Description of the Parameter
	 * @return    Description of the Return Value
	 */
	public boolean equals(Object o) {
	  if (o == null || !(o instanceof RelationDefinition)) {
	    return false;
	  }
	  RelationDefinition rd = (RelationDefinition) o;

	  for(Iterator iter=this.arguments.entrySet().iterator(); iter.hasNext(); ) {
	    Map.Entry me = (Map.Entry) iter.next();
	    String key = (String) me.getKey();
	    TypeDefinition type = (TypeDefinition) me.getValue();

	    TypeDefinition other = (TypeDefinition) rd.arguments.get(key);
	    if( (other == null) || (!other.equals(type)) ) return false;
	  }

	  return true;
	}


	/**
	 *  Description of the Method
	 *
	 * @return    Description of the Return Value
	 */
	public int hashCode() {
		return this.getURI().hashCode() + this.arguments.size();
	}
}

