/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/xmlschema/XSAttribute.java,v 1.2 2004/12/01 16:14:54 joepeer Exp $
 * $Date: 2004/12/01 16:14:54 $
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

package org.mcm.sws.xmlschema;

import java.util.*;
import java.io.*;
import org.jdom.*;
import org.mcm.sws.util.*;


import org.apache.log4j.Logger;


public class XSAttribute extends XSNode {
	private static Logger log = Logger.getLogger(XSAttribute.class);

	public static final String OPTIONAL = "optional";
	public static final String REQUIRED = "required";

	protected XSType typeObj;
	protected String type;
	protected String ref;
	protected String defoult;
	protected String use;
	protected String fixed;

	public XSAttribute() {
		use = OPTIONAL;
	}

	public void addType(XSSimpleType type) {
		this.typeObj = type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public String getRef() {
		return ref;
	}

	public void setDefoult(String defoult) {
		this.defoult = defoult;
	}

	public String getDefoult() {
		return defoult;
	}

	public void setUse(String ref) {
		this.ref = ref;
	}

	public String getUse() {
		return ref;
	}

	public void setFixed(String fixed) {
		this.fixed = fixed;
	}

	public String getFixed() {
		return fixed;
	}

	public boolean needsInstantiation(String currentPath, Map values) {
		return 	(super.needsInstantiation(currentPath, values)) ||
						(REQUIRED.equals(use));
	}

	public void instantiate(javax.xml.soap.SOAPEnvelope env, javax.xml.soap.SOAPElement parentElem, Map values, String currPath, String encoding)
	throws javax.xml.soap.SOAPException {
				String valueToAssign = null;

		//do we have to assign default value?
		if(REQUIRED.equals(use) && fixed!=null) {
			valueToAssign = fixed;
		} else {
			//can we assign value from map?
			String matchingValue = StrUtils.matchingValuePath(currPath+"/@"+name, values);
			if(matchingValue != null) {
				valueToAssign = matchingValue;
			}
		}

		if(valueToAssign != null) {
			javax.xml.soap.Name attName = env.createName(name);
			parentElem.addAttribute(attName, valueToAssign);
		}
	}

}
