/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/wsdl/SOAPOperationBinding.java,v 1.2 2004/12/01 16:14:53 joepeer Exp $
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
 * Holds the SOAP specific information of an operation in binding if the
 * binding used is SOAP.
 *
 * @author Joachim Peer
 */
public class SOAPOperationBinding extends OperationBinding {

	protected String soapAction;
	protected boolean style;
	protected SOAPBodyDef inputSOAPBodyDef, outputSOAPBodyDef;
	protected List inputSOAPHeaderDefs, outputSOAPHeaderDefs;

	protected WSDLDocument parent;
	protected SOAPBinding binding;
	protected Operation operation;

	public SOAPOperationBinding(WSDLDocument parent, SOAPBinding binding) {
		this.parent = parent;
		this.binding = binding;
	}

	public void setOperation(Operation operation) {
		this.operation = operation;
	}

	public Operation getOperation() {
		return operation;
	}

	public void setSoapAction(String soapAction) {
		this.soapAction = soapAction;
	}

	public String getSoapAction() {
		return soapAction;
	}

	public void setStyle(boolean style) {
		this.style = style;
	}

	public boolean getStyle() {
		return style;
	}

	// SOAP header

	public void setInputSOAPHeaderDefs(List inputSOAPHeaderDefs) {
		this.inputSOAPHeaderDefs = inputSOAPHeaderDefs;
	}

	public List getInputSOAPHeaderDefs() {
		return inputSOAPHeaderDefs;
	}

	public void setOutputSOAPHeaderDefs(List outputSOAPHeaderDefs) {
		this.outputSOAPHeaderDefs = outputSOAPHeaderDefs;
	}

	public List getOutputSOAPHeaderDef() {
		return outputSOAPHeaderDefs;
	}

	// SOAP body

	public void setInputSOAPBodyDef(SOAPBodyDef inputSOAPBodyDef) {
		this.inputSOAPBodyDef = inputSOAPBodyDef;
	}

	public SOAPBodyDef getInputSOAPBodyDef() {
		return inputSOAPBodyDef;
	}

	public void setOutputSOAPBodyDef(SOAPBodyDef outputSOAPBodyDef) {
		this.outputSOAPBodyDef = outputSOAPBodyDef;
	}

	public SOAPBodyDef getOutputSOAPBodyDef() {
		return outputSOAPBodyDef;
	}


}

