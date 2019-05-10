/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/wsdl/Port.java,v 1.2 2004/12/01 16:14:53 joepeer Exp $
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



/**
 * A port defines an individual endpoint by specifying a single address for a
 * binding. (see http://www.w3.org/TR/wsdl#_ports)
 *
 * @author    Joachim Peer
 */


public class Port implements URIIdentifiable {

	protected String uri;

	// the name attribute provides a unique name among all ports defined within in the enclosing WSDL document.
	protected String name;

	//  binding attribute (of type QName) refers to the binding using the linking rules defined by WSDL
	protected Binding binding;

	// a service groups a set of related ports together
	protected Service service;

	// The (SOAP) address binding is used to give a port an address (a URI)
	protected String address;

	protected WSDLDocument parent;


	public Port(WSDLDocument parent, Service service) {
		this.parent = parent;
		this.service = service;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setBinding(Binding binding) {
		this.binding = binding;
	}

	public Binding getBinding() {
		return binding;
	}

	// maybe should use subclassing (SOAPAddress, HTTPAddress, etc.)
	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddress() {
		return address;
	}

	public String getURI() {
		if(uri==null) {
			uri = StrUtils.slashed(parent.getTargetNamespace()) + service.getName() + "/" + "port(" + name + ")";
		}
		return uri;
	}

	public void register() {
		Registry.getInstance().addObject(Registry.WSDL_PORT, this);
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("port name=");
		buf.append(name);
		buf.append(", address=");
		buf.append(address);
		return buf.toString();
	}
}
