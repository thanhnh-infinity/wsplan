/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/wsdl/SOAPHeaderDef.java,v 1.2 2004/12/01 16:14:53 joepeer Exp $
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
 * <p>Represents info of the soap:header element; it allows header to be defined
 * that  are transmitted inside the Header element of the SOAP Envelope. It is
 * structured after the soap:body element</p>
 *
 * <p>Together, the message attribute (of type QName) and the part attribute (of
 *  type nmtoken) reference the message part that defines the header type</p>
 *
 * @author Joachim Peer
 */

public class SOAPHeaderDef extends SOAPFragmentDef {

	protected Message message;
	protected Part part;

	public SOAPHeaderDef(WSDLDocument parent, SOAPOperationBinding soapOperationBinding) {
		super(parent, soapOperationBinding);
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	public Message getMessage() {
		return message;
	}

	public void setPart(Part part) {
		this.part = part;
	}

	public Part getPart() {
		return part;
	}
}
