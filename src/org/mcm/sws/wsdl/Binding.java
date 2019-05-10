/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/wsdl/Binding.java,v 1.2 2004/12/01 16:14:53 joepeer Exp $
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
 * Abstract class to capture (service level-) binding data that can be found in
 * all bindings
 *
 * @author    Joachim Peer
 */
public abstract class Binding implements URIIdentifiable {
	private static Logger log = Logger.getLogger(Binding.class);

	protected String uri;
	// the PT this binding is defined for
	protected PortType portType;
	protected HashMap operationBindings;
	protected String name;
	protected WSDLDocument parent;

	public Binding(WSDLDocument parent, PortType portType) {
		this.parent = parent;
		this.portType = portType;
		this.operationBindings = new HashMap();
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public PortType getPortType() {
		return portType;
	}

	public void addOperationBinding(OperationBinding opBdg) {
		operationBindings.put(opBdg.getOperation().getName(), opBdg);
	}

	public OperationBinding getOperationBinding(String opName) {
		return (OperationBinding) operationBindings.get(opName);
	}

	public static String createURI(String ns, String name) {
		return StrUtils.slashed(ns) + "binding(" + name + ")";
	}

	public String getURI() {
		if(uri==null) {
			uri = StrUtils.slashed(parent.getTargetNamespace()) + "binding(" + name + ")";
		}
		return uri;
	}


	public abstract void register();

}
