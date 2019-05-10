/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/wsdl/Operation.java,v 1.2 2004/12/01 16:14:53 joepeer Exp $
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
 * This class models a WSDL &lt;Operation&gt; element (see also
 * http://www.w3.org/TR/wsdl12)
 *
 * @author Joachim Peer
 */
public class Operation implements URIIdentifiable {

	public static final int INPUT = 0;
	public static final int OUTPUT = 1;
	public static final int FAULT = 2;

	private static final int NS = 0;
	private static final int NAME = 1;


	protected String name, uri;
	//protected String input, output, fault;
	//protected String inputNS, outputNS, faultNS;
	protected String[][] in_out_fault;


	protected Message inputMsg, outputMsg, faultMsg;

	public WSDLDocument parent;
	public PortType portType;

	public Operation(WSDLDocument parent, PortType portType) {
		this.parent = parent;
		this.portType = portType;
		in_out_fault = new String[3][2];
	}

	public WSDLDocument getParent() {
		return parent;
	}

	public PortType getPortType() {
		return portType;
	}


	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setInput(String ns, String input, Message m) {
		this.in_out_fault[INPUT][NS] = ns;
		this.in_out_fault[INPUT][NAME] = input;
		inputMsg = m;
	}

	public String getInput() {
		return this.in_out_fault[INPUT][NAME];
	}

	public String getInputNS() {
		return this.in_out_fault[INPUT][NS];
	}

	public Message getInputMsg() {
		return inputMsg;
	}


	public void setOutput(String ns, String output, Message m) {
		this.in_out_fault[OUTPUT][NS] = ns;
		this.in_out_fault[OUTPUT][NAME] = output;
		outputMsg = m;
	}

	public String getOutput() {
		return this.in_out_fault[OUTPUT][NAME];
	}

	public Message getOutputMsg() {
		return outputMsg;
	}



	public void setFault(String ns, String fault, Message m) {
		this.in_out_fault[FAULT][NS] = ns;
		this.in_out_fault[FAULT][NAME] = fault;
		faultMsg = m;
	}

	public String getFault() {
		return this.in_out_fault[FAULT][NAME];
	}

	public Message getFaultMsg() {
		return faultMsg;
	}




	public void register() {
		Registry registry = Registry.getInstance();
		registry.addObject(Registry.WSDL_OPERATION, this);
	}

	public static String createURI(String portTypeName, String ns, String name) {
		return StrUtils.slashed(ns)+"operation("+portTypeName+"/"+name+")";
	}

	public String getURI() {
		if(uri == null) {
			String stub = "operation("+portType.name+"/"+name+")";
			uri = StrUtils.slashed(parent.targetNamespace) + stub;
		}
		return uri;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();

		buf.append("Operation - name=");
		buf.append(name);
		buf.append("input="); buf.append(in_out_fault[INPUT][NAME]); buf.append("\n");
		buf.append("output="); buf.append(in_out_fault[OUTPUT][NAME]); buf.append("\n");
		buf.append("fault="); buf.append(in_out_fault[FAULT][NAME]); buf.append("\n");

		return buf.toString();
	}


}
