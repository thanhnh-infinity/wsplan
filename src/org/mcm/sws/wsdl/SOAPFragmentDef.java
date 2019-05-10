/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/wsdl/SOAPFragmentDef.java,v 1.2 2004/12/01 16:14:53 joepeer Exp $
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
 * Captures the information stored in soap:header, soap:body, soap:headerfault
 * elements in the soap binding/operation/input,  binding/operation/output and
 * binding/operation/fault elements.
 *
 * @author Joachim Peer
 */
public abstract class SOAPFragmentDef {

	public static final boolean USE_LITERAL = true;
	public static final boolean USE_ENCODED = false;

	/*
	If use is encoded, then each message part references an abstract type using
	the type attribute. These abstract types are used to produce a concrete
	message by applying an encoding specified by the encodingStyle attribute.

	If use is literal, then each part references a concrete schema definition
	using either the element or type attribute.
	*/
	protected boolean use;

	protected String namespace;
	protected String encodingStyle;

	protected WSDLDocument parent;
	protected SOAPOperationBinding soapOperationBinding;

	public SOAPFragmentDef(WSDLDocument parent, SOAPOperationBinding soapOperationBinding) {
		this.parent = parent;
		this.soapOperationBinding = soapOperationBinding;
	}

	public void setUse(boolean use) {
		this.use = use;
	}

	public boolean getUse() {
		return use;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setEncodingStyle(String encodingStyle) {
		this.encodingStyle = encodingStyle;
	}

	public String getEncodingStyle() {
		return encodingStyle;
	}

}
