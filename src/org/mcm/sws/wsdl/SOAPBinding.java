/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/wsdl/SOAPBinding.java,v 1.2 2004/12/01 16:14:53 joepeer Exp $
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
 * A SOAP Binding
 * (See http://www.w3.org/TR/wsdl#_soap-b)
 *
 * @author Joachim Peer
 */

public class SOAPBinding extends Binding {

	public static final boolean STYLE_RPC = true;
	public static final boolean STYLE_DOCUMENT = false;

	protected String transport; // an URI
	protected boolean style;


	public SOAPBinding(WSDLDocument parent, PortType portType) {
		super(parent, portType);
	}

	public void setTransport(String transport) {
		this.transport = transport;
	}

	public String getTransport() {
		return transport;
	}

	public void setStyle(boolean style) {
		this.style = style;
	}

	public boolean getStyle() {
		return style;
	}

	public void register() {
		Registry.getInstance().addObject(Registry.WSDL_BINDING, this);
	}

}

