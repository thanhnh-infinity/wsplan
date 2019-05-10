/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/wsdl/SOAPBodyDef.java,v 1.2 2004/12/01 16:14:53 joepeer Exp $
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
 * This class captures the information of the soap:body element.
 * see (http://www.w3.org/TR/wsdl#_soap:body)
 *
 * <p>The soap:body element specifies how the message parts appear inside the
 * SOAP Body element.
 * The soap:body binding element provides information on how to assemble the
 * different message parts inside the Body element of the SOAP message. The
 * soap:body element is used in both RPC-oriented and document-oriented
 *  messages, but the style of the enclosing operation has important effects on
 *  how the Body section is structured:</p>
 *<ul>
 *<li>If the operation style is rpc each part is a parameter or a return value and
 * appears inside a wrapper element within the body (following Section 7.1 of
 * the SOAP specification). The wrapper element is named identically to the
 * operation name and its namespace is the value of the namespace attribute.
 * Each message part (parameter) appears under the wrapper, represented by an
 * accessor named identically to the corresponding parameter of the call. Parts
 * are arranged in the same order as the parameters of the call.
 *
 * <li>If the operation style is document there are no additional wrappers, and the
 * message parts appear directly under the SOAP Body element.
 * </ul>
*/

public class SOAPBodyDef extends SOAPFragmentDef {

	public SOAPBodyDef(WSDLDocument parent, SOAPOperationBinding soapOperationBinding) {
		super(parent, soapOperationBinding);
	}

	protected String parts;

	/*
	If the parts attribute is omitted, then all parts defined by the message are
	assumed to be included in the SOAP Body portion.
	*/
	public void setParts(String parts) {
		this.parts = parts;
	}

	public String getParts() {
		return parts;
	}

	/*
	1. if use is encoded, then each message part references an abstract type using the type attribute.
	2. f use is literal, then each part references a concrete schema definition using either the element or type attribute
	*/
	public boolean isConsistentWithMessageParts(Message m) {
		if(use == USE_LITERAL) return true;

		// in case use==encoded, we have to check each part:
		for(Iterator iter=m.getParts().entrySet().iterator(); iter.hasNext(); ) {
			Part part = (Part) ((Map.Entry) iter.next()).getValue();
			if(part.getTypeName() == null) return false;
		}
		return true;
	}
}
