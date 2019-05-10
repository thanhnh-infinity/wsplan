/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/planners/FFMetricPlanner.java,v 1.2 2004/12/01 16:14:51 joepeer Exp $
 * $Date: 2004/12/01 16:14:51 $
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

package org.mcm.sws.planners;

import java.io.*;
import java.util.*;


import org.apache.log4j.Logger;

import org.mcm.sws.plan.*;
import org.mcm.sws.pddl.*;
import org.mcm.sws.util.*;
import org.mcm.sws.wsdl.*;
import org.mcm.sws.annotation.*;
import org.mcm.sws.*;

/**
 * A wrapper for the MetricFF Planner.
 * see http://www.informatik.uni-freiburg.de/~hoffmann/metric-ff.html
 *
 * @author    Joachim Peer
 */

public class FFMetricPlanner implements Planner {
	private static Logger log = Logger.getLogger(FFMetricPlanner.class);

	String tempDirPath;
	String plannerPath;

	public void configure(Map params) {
		this.tempDirPath = (String) params.get("temp-dir");
		this.plannerPath = (String) params.get("path");
	}

  public Plan invokePlanner(String goalSpec, Set ignore) throws IOException {
		Set handsOff = null;
		String domainSpec = PDDLGenerator.generateDomainPDDL(goalSpec, handsOff, ignore);
		File domainSpecFile = new File(tempDirPath, "domain.pddl");
		Files.writeToFile(domainSpecFile, domainSpec);

		String problemSpec = PDDLGenerator.generateProblemPDDL(ignore, handsOff);
		File problemSpecFile = new File(tempDirPath, "problem.pddl");
		Files.writeToFile(problemSpecFile, problemSpec);

		return invokePlanner(domainSpecFile, problemSpecFile);
	}

	private Plan invokePlanner(File domainSpec, File problemSpec)
	throws IOException {


		/*
		if(!domainSpec.isfile() || !problemSpec.isfile() || !vhpopDir.isDir {
			System.out.println("could not call planner, due to I/O problems");
			return;
		}*/


		String[] cmdArray = new String[5];


		cmdArray[0] = plannerPath;
		cmdArray[1] = "-o";
		cmdArray[2] = domainSpec.getAbsolutePath();
		cmdArray[3] = "-f";
		cmdArray[4] = problemSpec.getAbsolutePath();

		log.info("runing command: <"+cmdArray[0]+" "+cmdArray[1]+" "+cmdArray[2]+" "+cmdArray[3]+" "+cmdArray[4]+">");

		Process proc = Runtime.getRuntime().exec(cmdArray);

		InputStream stdIn = proc.getInputStream();
		InputStream stdErr = proc.getErrorStream();

		log.debug("** result: **");
		String result = Files.getFileContent(stdIn);
		log.debug("FF METRIC RESULT:");
		log.debug(result); // this must be handed over to a VHPOP command line parser
		Plan p = parseOutput(result);

		return p;
	}

/*
ff: found legal plan as follows

step    0: SWITCH_ON INSTRUMENT2 SATELLITE1
        1: SWITCH_ON INSTRUMENT0 SATELLITE0
        2: TURN_TO SATELLITE1 STAR2 STAR6
        3: CALIBRATE SATELLITE1 INSTRUMENT2 STAR2
        4: TURN_TO SATELLITE1 PLANET4 STAR2
        5: TAKE_IMAGE SATELLITE1 PLANET4 INSTRUMENT2 THERMOGRAPH2
        6: TURN_TO SATELLITE1 STAR6 PLANET4
        7: TAKE_IMAGE SATELLITE1 STAR6 INSTRUMENT2 THERMOGRAPH2
        8: TURN_TO SATELLITE1 STAR7 STAR6
        9: TAKE_IMAGE SATELLITE1 STAR7 INSTRUMENT2 INFRARED3
       10: TURN_TO SATELLITE1 STAR9 STAR7
       11: TAKE_IMAGE SATELLITE1 STAR9 INSTRUMENT2 INFRARED1
       12: TURN_TO SATELLITE1 PLANET4 STAR9
       13: TURN_TO SATELLITE1 STAR10 PLANET4
       14: TAKE_IMAGE SATELLITE1 STAR10 INSTRUMENT2 INFRARED3
       15: TURN_TO SATELLITE0 PLANET5 PHENOMENON8
       16: TURN_TO SATELLITE0 STAR1 PLANET5
       17: CALIBRATE SATELLITE0 INSTRUMENT0 STAR1
       18: TURN_TO SATELLITE0 STAR10 STAR1
       19: TURN_TO SATELLITE0 PLANET5 STAR10
       20: TAKE_IMAGE SATELLITE0 PLANET5 INSTRUMENT0 SPECTROGRAPH0
       21: TURN_TO SATELLITE0 STAR10 PLANET5
       22: TURN_TO SATELLITE0 PHENOMENON8 STAR10
       23: TAKE_IMAGE SATELLITE0 PHENOMENON8 INSTRUMENT0 SPECTROGRAPH0


time spent:
*/

	private Plan parseOutput(String s) {

		if(s == null || s.length() == 0) return null;

		Plan plan = new Plan();
		boolean reachedPlanSection = false;
		for(StringTokenizer st = new StringTokenizer(s, "\n\r"); st.hasMoreTokens(); ) {
			String line = st.nextToken().trim();

			log.debug("processing FFMETRIC line: "+line);

			if(!reachedPlanSection) {
				if("ff: found legal plan as follows".equals(line)) {
					reachedPlanSection = true;
				}
				continue;
			}

			if(reachedPlanSection) {

				int cpos = line.indexOf(':');
				// ignore empty lines
				if(cpos == -1) continue;

				String preColonSubstr = line.substring(0,cpos);
				// we are finished with parsing
				if("time spent".equals(preColonSubstr)) return plan;

				// parsing step id
				int lastSpacePos = line.lastIndexOf(' ', cpos-1);
				int stepID=-1;
				try {
					stepID = Integer.parseInt(line.substring(lastSpacePos,cpos));
				} catch(java.lang.NumberFormatException nfe) {
					continue;
				}

				String asInstance = line.substring(cpos+1);
				ActionInstance ai = parseActionInstance(asInstance);
				if(ai != null) {
					ai.setStepID(stepID);
					plan.addActionInstance(ai);
				}

			}

		}

		return plan;
	}

	public ActionInstance parseActionInstance(String asInstance) {

			//TURNTO_21 SATELLITE1 STAR2 STAR6
			StringTokenizer st2 = new StringTokenizer(asInstance," ");
			String actionName = st2.nextToken();

			if(actionName.equalsIgnoreCase("achieve-goal")) {
				//continue; // we dont't add the helper action
				return null;
			}

			int ul_1 = actionName.indexOf('_');

			if(ul_1 == -1) {
				log.error("could not tokenize:"+actionName);
				return null;
			}

			int opMnemo = Integer.parseInt( actionName.substring(ul_1+1) );
			Operation operation = (Operation) Registry.getInstance().getObject(Registry.WSDL_OPERATION , opMnemo);
			OpDef opDef = (OpDef) Registry.getInstance().getObject(Registry.ANNO_OPDEF , operation.getURI());

			ActionInstance ai = new ActionInstance();
			ai.setOperation(operation);
			ai.setOpDef(opDef);

			while(st2.hasMoreTokens()) {
				ai.addParam(st2.nextToken());
			}

			return ai;
	}

}
