/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/wsdl/PortType.java,v 1.2 2004/12/01 16:14:53 joepeer Exp $
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

package org.mcm.sws.wsdl;

import java.util.*;

import org.mcm.sws.*;
import org.mcm.sws.util.*;


import org.apache.log4j.Logger;


/**
 * This class models a WSDL &lt;PortType&gt; element (see also
 * http://www.w3.org/TR/wsdl12)
 *
 * @author Joachim Peer
 */
public class PortType implements URIIdentifiable {
	private static Logger log = Logger.getLogger(PortType.class);

	protected String name, uri;
	protected LinkedHashMap operations;
	protected List concreteBindings;

	protected int id; // the id associated with that portType

	public WSDLDocument parent;

	public PortType(WSDLDocument parent) {
		this.parent = parent;
		operations = new LinkedHashMap();
		concreteBindings = new ArrayList();
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void addOperation(Operation op) {
		operations.put(op.getName(), op);
	}

	public Map getOperations() {
		return operations;
	}
/*
	public Vector getOperationsVector() {
		return new Vector(operations.values());
	}*/

	public Operation getOperation(String op) {
		return (Operation) operations.get(op);
	}


	/*
	public void addConcreteBinding(BindingForPortType bdg) {
		concreteBindings.add(bdg);
	}

	// returns first concrete Binding found
	public BindingForPortType getConcreteBinding() {
		if(concreteBindings.size() > 0) {
			return (BindingForPortType) concreteBindings.get(0);
		} else {
			return null;
		}
	}
	*/

	public void register() {
		Registry.getInstance().addObject(Registry.WSDL_PORTTYPE, this);
		for(Iterator iter = operations.entrySet().iterator(); iter.hasNext(); ) {
			Map.Entry m = (Map.Entry) iter.next();
			Operation op = (Operation) m.getValue();
			op.register();
		}
	}

	public String getURI() {
		if(uri == null) {
			String stub = "portType("+name+")";
			uri = StrUtils.slashed(parent.targetNamespace) + stub;
		}
		return uri;
	}

	public static String createURI(String ns, String name) {
		return StrUtils.slashed(ns) + "portType(" + name + ")";
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("portType name=");
		buf.append(name);
		buf.append("\n");
		for(Iterator iter=operations.entrySet().iterator(); iter.hasNext(); ) {
			Operation op = (Operation) ((Map.Entry) iter.next()).getValue();
			buf.append("Operation:");
			buf.append(op.toString());
			buf.append("\n");
		}


		return buf.toString();
	}



}
