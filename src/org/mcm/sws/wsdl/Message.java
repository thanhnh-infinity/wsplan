/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/wsdl/Message.java,v 1.2 2004/12/01 16:14:53 joepeer Exp $
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
 * This class models a WSDL &lt;Message&gt; element (see also
 * http://www.w3.org/TR/wsdl12)
 *
 * @author Joachim Peer
 */
public class Message implements URIIdentifiable {

	protected String name;
	protected LinkedHashMap parts;
	protected String uri;

	public WSDLDocument parent;

	public Message(WSDLDocument parent) {
		this.parent = parent;
		parts = new LinkedHashMap();
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void addPart(Part p) {
		parts.put(p.getName(), p);
	}

	public Map getParts() {
		return parts;
	}

	public Part getPart(String name) {
		return (Part) parts.get(name);
	}

	public void register() {
		Registry.getInstance().addObject(Registry.WSDL_MESSAGE, this);
		//currently we are not registering the message parts
	}

	public String getURI() {
		if(uri==null) {
			uri = StrUtils.slashed(parent.getTargetNamespace()) + "message(" + name + ")";
		}
		return uri;
	}

	public static String createURI(String ns, String name) {
		return StrUtils.slashed(ns) + "message(" + name + ")";
	}


	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("name=");
		buf.append(name);
    buf.append(", parts=");
		for(Iterator iter=parts.entrySet().iterator(); iter.hasNext(); ) {
			Part p = (Part) ((Map.Entry) iter.next()).getValue();
			buf.append("Part:");
			buf.append(p.toString()); buf.append("\n");
		}
		return buf.toString();
	}


}
