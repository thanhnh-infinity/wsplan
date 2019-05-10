/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/util/PlannerData.java,v 1.2 2004/12/01 16:14:53 joepeer Exp $
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

package org.mcm.sws.util;

import java.util.*;
import org.mcm.sws.planners.*;


import org.apache.log4j.Logger;


/**
 *  Uitlity class for the config class which holds data representing
 *  information about the AI planners available to the system.
 *
 *  <br>example confiurations:<br>
 *  <pre>
 *	<planner name="VHPOP" features=":strips :types :equal" default="true">
 *		<param name= org.mcm.sws.planners.VHPOPInterface"/>
 *		<param name="path" value="d:\cygwin\home\_jpeer\vhpop-2.2\vhpop.exe"/>
 *		<param name="temp-dir" value="d:\pf\examples\tmp"/>
 *  </planner>
 *
 *	<planner name="LPG" features=":strips, :types, :equal, :fluents">
 *		<param name="class"org.mcm.sws.planners.VHPOPInterface"/>
 *		<param name="path" value="d:\cygwin\home\_jpeer\vhpop-2.2\vhpop.exe"/>
 *		<param name="temp-dir" value="d:\pf\examples\tmp"/>
 *  </planner>
 * </pre>
 * @author    Joachim Peer
 */

public class PlannerData {
	private static Logger log = Logger.getLogger(PlannerData.class);

	protected HashMap params;
	protected String name;
	protected String isDefault;
	protected HashSet supportedFeatures;
	protected String features;
	protected Planner implementation;

	public PlannerData() {
		params = new HashMap();
		supportedFeatures = new HashSet();
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setIsDefault(String isDefault) {
		this.isDefault = isDefault;
	}

	public String getIsDefault() {
		return isDefault;
	}

	public void setFeatures(String features) {
		this.features = features;

		StringTokenizer st = new StringTokenizer(features, " ,:");
		while(st.hasMoreTokens()) {
			supportedFeatures.add(st.nextToken());
		}
	}

	public String getFeatures() {
		return features;
	}

	public Set getSupportedFeatures() {
		return supportedFeatures;
	}

	public void addParam(String name, String value) {
		params.put(name, value);
	}

	public Map getParams() {
		return params;
	}

	public Planner getImplementation() {
		if(implementation == null) {
			try {
				String className = (String) params.get("class");
				Class clazz = Class.forName(className);
				implementation = (Planner) clazz.newInstance();
				implementation.configure(params);
			} catch(ClassNotFoundException cnfe) {
				cnfe.printStackTrace();
			} catch(InstantiationException inste) {
				inste.printStackTrace();
			} catch(IllegalAccessException ille) {
				ille.printStackTrace();
			}
		}
		return implementation;
	}

	/*
		protected HashMap params;
	protected String name;
	protected String isDefault;
	protected HashSet supportedFeatures;
	protected String features;
	protected Planner implementation;
	*/

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("Plannerdata: ");
		buf.append(name);
		buf.append("isDefault:"+isDefault);

		buf.append("params:");
		for(Iterator iter = params.entrySet().iterator(); iter.hasNext(); ) {
			Map.Entry me = (Map.Entry) iter.next();
			buf.append("param name=");
			buf.append(me.getKey());
			buf.append(" value=");
			buf.append(me.getValue());
		}

		return buf.toString();
	}

}
