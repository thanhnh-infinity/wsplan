/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/wsdl/Part.java,v 1.2 2004/12/01 16:14:53 joepeer Exp $
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

import org.mcm.sws.*;
import org.mcm.sws.util.*;


/**
 * This class models a WSDL &lt;Part&gt; element (see also
 * http://www.w3.org/TR/wsdl12)
 *
 * @author Joachim Peer
 */
public class Part {

	public static final boolean XSD_ELEMENT = true;
	public static final boolean XSD_TYPE = false;

	protected String name;
	protected String namespace;
	protected String elementName;
	protected String typeName;

	public WSDLDocument parent;
	public Message message;

	public Part(WSDLDocument parent, Message message) {
		this.parent = parent;
		this.message = message;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getNamespace() {
		return this.namespace;
	}

	public void setElementName(String elementName) {
		this.elementName = elementName;
	}

	public String getElementName() {
		return this.elementName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getTypeName() {
		return this.typeName;
	}


	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("name=");
		buf.append(name);
		buf.append(", namespace=");
		buf.append(namespace);
		buf.append("elementMame=");
		buf.append(elementName);
		buf.append("typeName=");
		buf.append(typeName);

		return buf.toString();
	}

}
