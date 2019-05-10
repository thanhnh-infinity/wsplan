/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/util/Config.java,v 1.2 2004/12/01 16:14:53 joepeer Exp $
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

import java.io.*;
import java.util.*;

import org.apache.commons.digester.*;
import org.xml.sax.SAXException;


import org.apache.log4j.Logger;


import org.jdom.*;
import org.jdom.output.*;

import org.mcm.sws.*;
import org.mcm.sws.pddl.*;
import org.mcm.sws.planners.*;
import org.mcm.sws.strategy.*;

/**
 *  The WSPlan configuration class. This class reads the config.xml (or similar)
 *  file, which contains links to WSDL documents, SESMA Annotations, ontologies,
 *  fact bases and goal descriptions. This class is used and needed by
 *  all applications in the WSPlan toolset.
 *
 * @author    Joachim Peer
 */
public class Config {
	private static Logger log = Logger.getLogger(Config.class);

	protected Map namespaces;
	protected ArrayList wsdlPaths, annotationPaths, factPaths, predicatePaths, typePaths;
	protected String goal;
	protected String storageLocation;
	protected String planningStrategyClass;
	protected PlanningStrategy planningStrategy;
	protected ArrayList plannerData;
	protected PlannerData defaultPlanner;

	private static HashMap instances;

	static {
		instances = new HashMap();
	}

	/**
	 *Constructor for the Config object
	 */
	public Config() {
		wsdlPaths = new ArrayList();
		annotationPaths = new ArrayList();
		factPaths = new ArrayList();
		plannerData = new ArrayList();
		typePaths = new ArrayList();
		predicatePaths = new ArrayList();
	}

	public void setNamespaces(Map namespaces) {
		this.namespaces = namespaces;
	}

	public Map getNamespaces() {
		return namespaces;
	}

	public void addWSDLPath(String path) {
		wsdlPaths.add(path);
	}

	public List getWSDLPaths() {
		return wsdlPaths;
	}

	public void addFactPath(String path) {
		factPaths.add(path);
	}

	public List getFactPaths() {
		return factPaths;
	}

	public void addAnnotationPath(String path) {
		annotationPaths.add(path);
	}

	public List getAnnotationPaths() {
		return annotationPaths;
	}

	public void addPredicatePath(String path) {
		predicatePaths.add(path);
	}

	public List getPredicatePaths() {
		return predicatePaths;
	}

	public void addTypePath(String path) {
		typePaths.add(path);
	}

	public List getTypePaths() {
		return typePaths;
	}

	public void setGoal(String goal) {
		this.goal = goal;
	}

	public String getGoal() {
		return goal;
	}

	public void setStorageLocation(String loc) {
		storageLocation = loc;
	}

	public String getStorageLocation() {
		return storageLocation;
	}

	public void setPlanningStrategyClass(String planningStrategyClass) {
		this.planningStrategyClass = planningStrategyClass;
	}

	public String getPlanningStrategyClass() {
		return planningStrategyClass;
	}

	public org.mcm.sws.strategy.PlanningStrategy getPlanningStrategy() {
		if (planningStrategy == null) {
			try {
				Class psClass = Class.forName(planningStrategyClass);
				this.planningStrategy = (PlanningStrategy) psClass.newInstance();
			} catch (InstantiationException e1) {
				e1.printStackTrace();
			} catch (IllegalAccessException e2) {
				e2.printStackTrace();
			} catch (ClassNotFoundException e3) {
				e3.printStackTrace();
			}
		}
		return this.planningStrategy;
	}

	public synchronized void addPlannerData(PlannerData pd) {
		plannerData.add(pd);
		if("true".equalsIgnoreCase(pd.getIsDefault())) {
			defaultPlanner = pd;
		}
	}

	public Planner getDefaultPlanner() {
		if(defaultPlanner != null) {
			return defaultPlanner.getImplementation();
		} else {
			return ((PlannerData) plannerData.get(0)).getImplementation();
		}
	}

	public List getPlannersWithFeatures(Set features) {
		// TO DO
		return null;
	}


	/**
	 * This method parses the document at the specified location.
	 *
	 * @param  storageLocation  Description of the Parameter
	 * @return                  Description of the Return Value
	 * @exception  IOException  Description of the Exception
	 */
	public static Config createInstance(String storageLocation) throws IOException {
		InputStream input = input = Files.getInputStream(storageLocation);

		Config config = new Config();
		config.storageLocation = storageLocation;

		// Initialize a new Digester instance

		// Initialize a new Digester instance
		org.mcm.sws.util.NSDigester digester = new org.mcm.sws.util.NSDigester();
		digester.push(config);

		digester.addCallMethod("wsplan-config/service-descriptions/wsdl-file", "addWSDLPath", 0);
		digester.addCallMethod("wsplan-config/service-descriptions/annotation-file", "addAnnotationPath", 0);

		// Initialize ontolgy-definitions (predicates, types)
		digester.addCallMethod("wsplan-config/ontology-definitions/predicate-file", "addPredicatePath", 0);
		digester.addCallMethod("wsplan-config/ontology-definitions/type-file", "addTypePath", 0);

		// init fact base
//	digester.addCallMethod("wsplan-config/knowledge-base/fact", "addFact", 0);
		digester.addCallMethod("wsplan-config/fact-file", "addFactPath", 0);

		// define goal
		digester.addCallMethod("wsplan-config/goal", "setGoal", 0);

		// set planning strategy
 		digester.addCallMethod("wsplan-config/planning-strategy", "setPlanningStrategyClass", 0);

		/*
	<planner name="VHPOP" supported-requirements="strips, types, equal" default="true">
		<param name= org.mcm.sws.planners.VHPOPInterface"/>
		<param name="path" value="d:\cygwin\home\_jpeer\vhpop-2.2\vhpop.exe"/>
		<param name="temp-dir" value="d:\pf\examples\tmp"/>
  </planner>

	<planner name="LPG" features="strips, types, equal, fluents">
		<param name="class"org.mcm.sws.planners.VHPOPInterface"/>
		<param name="path" value="d:\cygwin\home\_jpeer\vhpop-2.2\vhpop.exe"/>
		<param name="temp-dir" value="d:\pf\examples\tmp"/>
  </planner>
*/

		digester.addObjectCreate("wsplan-config/planner", "org.mcm.sws.util.PlannerData");
		digester.addSetProperties("wsplan-config/planner");
		digester.addCallMethod("wsplan-config/planner/param", "addParam", 2);
		digester.addCallParam("wsplan-config/planner/param", 0, "name");
		digester.addCallParam("wsplan-config/planner/param", 1, "value");
		digester.addSetNext("wsplan-config/planner", "addPlannerData");

		// Parse the input stream to mappings
		try {
			digester.parse(input);
			//config.finishParsing();

			instances.put(Thread.currentThread().getThreadGroup(), config);

			config.setNamespaces(digester.getNamespacePrefixes());

			return config;
		} catch (SAXException e) {
			e.printStackTrace();

			throw new RuntimeException(e.toString());
		} catch (IOException e1) {
			System.out.println("bar");
			throw new RuntimeException(e1.toString());
		}

	}

	/**
	 *  Gets the instance attribute of the Config class
	 *
	 * @return    The instance value
	 */
	public static Config getInstance() {
		return (Config) instances.get(Thread.currentThread().getThreadGroup());
	}


	/*
	 *  <wsplan-config>
	 *  <service-descriptions>
	 *  <wsdl-file>d:\pf\examples\new2\buystock.wsdl</wsdl-file>
	 *  <annotation-file>d:\pf\examples\new2\buystock-anno2.xml</annotation-file>
	 *  </service-descriptions>
	 *  <knowledge-base>
	 *  <fact>(have-money lots)</fact>
	 *  <fact>(possess-stock boo.com 23244)</fact>
	 *  </knowledge-base>
	 *  <temp-dir>d:\pf\examples\new2</temp-dir>
	 *  <planner>d:\cygwin\home\_jpeer\vhpop-2.2\vhpop.exe</planner>
	 *  </wsplan-config>
	 */
	/**
	 *  Description of the Method
	 */
	public void writeToDisk() {
		writeToDisk(storageLocation);
	}


	/**
	 *  Description of the Method
	 *
	 * @param  outputPath  Description of the Parameter
	 */
	public void writeToDisk(String outputPath) {

		System.out.println("wrting to " + storageLocation);
		// todo - write to storageLocation

		Element root = new Element("wsplan-config");

		// service-descriptions
		Element serviceDescriptionsElem = new Element("service-descriptions");
		for (Iterator iter = wsdlPaths.iterator(); iter.hasNext(); ) {
			serviceDescriptionsElem.addContent((Element) new Element("wsdl-file").addContent((String) iter.next()));
		}
		for (Iterator iter = annotationPaths.iterator(); iter.hasNext(); ) {
			serviceDescriptionsElem.addContent((Element) new Element("annotation-file").addContent((String) iter.next()));
		}
		root.addContent(serviceDescriptionsElem);

		//knowledge-base
		Element knowledgeBaseElem = new Element("knowledge-base");
		for (Iterator iter = ExecEnvironment.getInstance().getFactBase().iterator(); iter.hasNext(); ) {
			String factStr = ((Predicate) iter.next()).toString();
			knowledgeBaseElem.addContent((Element) new Element("fact").addContent(factStr));
		}
		root.addContent(knowledgeBaseElem);

		//goal
		root.addContent((Element) new Element("goal").addContent(goal));

		// planning strategy
		root.addContent((Element) new Element("planning-strategy").addContent(planningStrategyClass));

		// planner data
		for(Iterator iter = plannerData.iterator(); iter.hasNext(); ) {
			PlannerData pd = (PlannerData) iter.next();
			Element plannerElem = new Element("planner");
			root.addContent(plannerElem);
			plannerElem.setAttribute("name", pd.getName());
			plannerElem.setAttribute("features", pd.getFeatures());

			if(pd.getIsDefault()!=null) {
				plannerElem.setAttribute("isDefault", pd.getIsDefault());
			}

			for(Iterator iter2 = pd.getParams().entrySet().iterator(); iter2.hasNext(); ) {
				Map.Entry me = (Map.Entry) iter2.next();
				Element paramElem = new Element("param");
				plannerElem.addContent(paramElem);
				paramElem.setAttribute("name", (String) me.getKey());
				paramElem.setAttribute("value", (String) me.getValue());
			}
		}

		Document doc = new Document(root);

		try {
			FileOutputStream fos = new FileOutputStream(new File(outputPath));
			new XMLOutputter(Format.getCompactFormat()).output(doc, fos);
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

