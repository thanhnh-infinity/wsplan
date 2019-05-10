/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/wsdl/VariableKey.java,v 1.2 2004/12/01 16:14:53 joepeer Exp $
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

public class VariableKey {
	private String serviceNS, ptName, operationName, variableName;

	public VariableKey(String serviceNS, String ptName, String operationName, String variableName) {
		this.serviceNS = serviceNS;
		this.ptName = ptName;
		this.operationName = operationName;
		this.variableName = variableName;
	}

	public boolean equals(Object o) {
		if((o == null) || !(o instanceof VariableKey)) {
			return false;
		}
		VariableKey vk = (VariableKey) o;
		return ( vk.serviceNS.equals(this.serviceNS)
						 && vk.ptName.equals(this.ptName)
						 && vk.operationName.equals(this.operationName)
						 && vk.variableName.equals(this.variableName) );
	}

	public int hashCode() {
		return serviceNS.hashCode() + ptName.hashCode() + operationName.hashCode() + variableName.hashCode();
	}

	public String toString() {
		return "<" + serviceNS + ", " + ptName + ", " + operationName + ", " + variableName + ">";
	}
}

