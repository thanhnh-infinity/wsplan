/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/wsdl/Service.java,v 1.2 2004/12/01 16:14:53 joepeer Exp $
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
 * A service groups a set of related ports together
 * (See http://www.w3.org/TR/wsdl#_services)
 *
 * @author Joachim Peer
 */


public class Service implements URIIdentifiable {

	protected HashMap ports;
	protected WSDLDocument parent;
	protected String name;
	protected String uri;

	public Service(WSDLDocument parent) {
		this.parent = parent;
		this.ports = new HashMap();
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void addPort(Port port) {
		ports.put(port.getURI(), port);
	}

	public Map getPorts() {
		return ports;
	}

	public Port getFirstPort() {
		return (Port) ((Map.Entry) ports.entrySet().iterator().next()).getValue();
	}

	public String getURI() {
		if(uri==null) {
			uri = StrUtils.slashed(parent.getTargetNamespace()) + "service(" + name + ")";
		}
		return uri;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("service name=");
		buf.append(name);
		buf.append(", \n ports=");
		for(Iterator iter=ports.entrySet().iterator(); iter.hasNext(); ) {
			Map.Entry me = (Map.Entry) iter.next();
			Port p = (Port) me.getValue();
			buf.append(p.toString());
			buf.append("\n");
		}
		return buf.toString();
	}
}
