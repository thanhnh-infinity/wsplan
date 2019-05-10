/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/planners/VHPOPPlanner.java,v 1.2 2004/12/01 16:14:52 joepeer Exp $
 * $Date: 2004/12/01 16:14:52 $
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
import org.mcm.sws.annotation.*;
import org.mcm.sws.wsdl.*;
import org.mcm.sws.*;

/**
 * A wrapper for VHPOP (ersatile Heuristic Partial Order Planner).
 * see http://www-2.cs.cmu.edu/~lorens/vhpop.html
 *
 * @author    Joachim Peer
 */
public class VHPOPPlanner implements Planner {
	private static Logger log = Logger.getLogger(VHPOPPlanner.class);
	private static int counter = 0;

	String tempDirPath;
	String plannerPath;

	public void configure(Map params) {
		this.tempDirPath = (String) params.get("temp-dir");
		this.plannerPath = (String) params.get("path");
	}

  public Plan invokePlanner(String goalSpec, Set violations) throws IOException {
		counter++;
		Set handsOffPreds = new HashSet();
		String domainSpec = PDDLGenerator.generateDomainPDDL(goalSpec, handsOffPreds,  new HashSet());


		File domainSpecFile = new File(tempDirPath, "domain_"+counter+".pddl");
		Files.writeToFile(domainSpecFile, domainSpec);

		String problemSpec = PDDLGenerator.generateProblemPDDL(violations, handsOffPreds);
		File problemSpecFile = new File(tempDirPath, "problem_"+counter+".pddl");
		Files.writeToFile(problemSpecFile, problemSpec);

		/*
		File violationsFile = null;
		if(violations != null && violations.size()>0) {
			String fileName = "violations" + (new java.util.Date()).getTime();
			violationsFile = new File("examples/tmp", fileName);
			writeViolations(violations, violationsFile);
		}*/

		return invokePlanner(domainSpecFile, problemSpecFile);
	}

	private Plan invokePlanner(File domainSpec, File problemSpec)
	throws IOException {

		String[] cmdArray = new String[4];


		cmdArray[0] = plannerPath;
		cmdArray[1] = domainSpec.getAbsolutePath();
		cmdArray[2] = problemSpec.getAbsolutePath();

		File ioFile = new File(tempDirPath, "tmp"+(new java.util.Date()).getTime()+".txt");


		cmdArray[3] = ioFile.getAbsolutePath();

		long t1 = new java.util.Date().getTime();
		Process proc = Runtime.getRuntime().exec(cmdArray);

		// it seems that we MUST reasd the error stream FIRST, otherwise
		// we experience a nasty I/O blocking problem
		InputStream stdErr = proc.getErrorStream();
		System.out.println("**** GENERATED Errors ****");
		System.out.println(Files.getFileContent(stdErr));

		long t2 = new java.util.Date().getTime();
		long td = t2 - t1;
		org.mcm.sws.util.Timer.planningTime += td;
		System.out.println("* Time: "+td);
		System.out.println("* Planning Time now adds up to: "+org.mcm.sws.util.Timer.planningTime);

		stdErr.close();

		System.out.println("** result: **");
		//InputStream stdIn = proc.getInputStream();
		InputStream stdIn = new FileInputStream(ioFile);

		String result = Files.getFileContent(stdIn);
		System.out.println("*** Generated Plan ***");
		log.debug(result); // this must be handed over to a VHPOP command line parser
		Plan p = parseOutput(result);

		stdIn.close();

		return p;
	}




	private Plan parseOutput(String s) {

		if(s == null || s.length() == 0) return null;

		Plan plan = new Plan();

		ActionInstance initial = new ActionInstance();
		initial.setStepID(ActionInstance.INITIAL);
		plan.addActionInstance(initial);

		nextstep:
		for(StringTokenizer st = new StringTokenizer(s, "\n\r"); st.hasMoreTokens(); ) {
			String line = st.nextToken().trim();

			if(line.startsWith(";") || line.length() == 0) {
				continue; // we don't process comments or empty lines
			}

			if(line.startsWith("no plan")) {
				return null;
			}

			if(line.indexOf("bindings")!=-1) {
				List bindingConstraints = parseBindingConstraints(st);
				plan.setBindingConstraints(bindingConstraints);
				//return plan;
				continue;
			}

			if(line.indexOf("orderings = ")!=-1) {
				parseOrderings(line.substring("orderings =".length()), plan);

				if(plan.isConsistentWithOrderings()) {
					System.out.println("orderings are consistent!");
				} else {
					System.out.println("orderins are NOT consistent!");
				}

				return plan; // nothing more to parse!
			}

			int cpos = line.indexOf(':');

			int stepID=-1;
			try {
				String leftSide = line.substring(0,cpos).trim();
				if(leftSide.indexOf("Goal") != -1) return plan;
				stepID = Integer.parseInt(leftSide);
			} catch(java.lang.NumberFormatException nfe) {
				continue;
			}

			String asInstance = line.substring(cpos+1);
			ActionInstance ai = parseActionInstance(asInstance);
			if(ai != null) {
				ai.setStepID(stepID);
				plan.addActionInstance(ai);

				while(true) {
					String causalLinkLine = st.nextToken().trim();
					if(causalLinkLine.indexOf("->")!=-1) {
						CausalLink cl = parseCausalLink(plan, ai, causalLinkLine);
						if(cl != null) {
							plan.addCausalLink(cl);
						}
					} else continue nextstep;
				}
			}
		}

		return plan;
	}


	public ActionInstance parseActionInstance(String asInstance) {

			//(login secret123 joepeer)
			StringTokenizer st2 = new StringTokenizer(asInstance," ()");
			String actionName = st2.nextToken();

			if(actionName.equals("achieve-goal")) {
				//continue;
				return new ActionInstance();
			}

			int ul_1 = actionName.indexOf('_');

			if(ul_1 == -1) {
				log.error("could not tokenize:"+actionName);
				return null;
			}

			int ul_2 = actionName.indexOf('_', ul_1+1);

			int opMnemo = Integer.parseInt( actionName.substring(ul_1+1, ul_2) );
			OpDef opDef = (OpDef) Registry.getInstance().getObject(Registry.ANNO_OPDEF , opMnemo);
			String wsdlDocStr = opDef.getParentAnnotation().getServiceNamespace();
			String operationURI = Operation.createURI(opDef.getPortType(), wsdlDocStr, opDef.getName());
			Operation operation = (Operation) Registry.getInstance().getObject(Registry.WSDL_OPERATION , operationURI);

			ActionInstance ai = new ActionInstance();
			ai.setOperation(operation);
			ai.setOpDef(opDef);

			while(st2.hasMoreTokens()) {
				ai.addParam(StrUtils.decode(st2.nextToken()));
			}

			return ai;
	}

	/**
Step 5   : (op_4_addtocart ?sid ?item num_123456 ?cnt ?result)
          6   -> (has-ean_2 ?item num_123456)
          6   -> (in-catalog_1 service2 ?item)
          3   -> (active-session_11 client service2 ?sid)
	*/
	protected CausalLink parseCausalLink(Plan p, ActionInstance toAi, String line) {
		 int arrow = line.indexOf("->");

		 int fromStepID = Integer.parseInt(line.substring(0, arrow).trim());
		 ActionInstance fromAi = p.getStep(fromStepID);

		 String literalStr = line.substring(arrow+2).trim();
		 Literal lit = parseLiteral(literalStr);

		 CausalLink cl = new CausalLink();
		 cl.setFrom(fromAi);
		 cl.setTo(toAi);
		 cl.setCondition(lit);
		 return cl;
	}

	/* todo: handle negAtoms */
	protected Literal parseLiteral(String str) {
		Atom atom = new Atom();
		StringTokenizer st = new StringTokenizer(str.trim(), "() ");
		String predName = st.nextToken();
		int us = predName.lastIndexOf('_');
		int predID = Integer.parseInt(predName.substring(us+1));
		Predicate predicate = (Predicate) Registry.getInstance().getObject(Registry.RELATION_DEF, predID);
		atom.setPredicate(predicate);

		for(Iterator iter = predicate.getArguments().entrySet().iterator(); iter.hasNext(); ) {
			Map.Entry me = (Map.Entry) iter.next();
			String property = (String) me.getKey();
			String value = st.nextToken();
			atom.addTerm(property, Term.parse(value));
		}

		return atom;
	}

	/**
bindings =
{ ?ean(3) ?ean(2) } == num_123456
{ ?item(3) ?item(2) ?item(1) }
{ ?ccexp(2) } == neverever
{ ?cc(2) } == num_34324343
Time: 0
	*/
	public List parseBindingConstraints(StringTokenizer st) {
		List result = new ArrayList();
		while(st.hasMoreTokens()) {
			String line = st.nextToken();
			if(line.indexOf(";") != -1) return result;

			BindingConstraint bc = parseBindingConstraint(line);
			result.add(bc);
		}
		return result;
	}

	/**
	{ ?ean(3) ?ean(2) } == num_123456
	*/
	public BindingConstraint parseBindingConstraint(String line) {
		BindingConstraint bc = new BindingConstraint();

		int equ = line.indexOf("==");
		if(equ != -1) {
			String constant = line.substring(equ+2).trim();
			bc.setConstant(StrUtils.decode(constant));

			line = line.substring(0, equ);
		}

		line = line.trim();
		StringTokenizer st = new StringTokenizer(line, " {}");
		while(st.hasMoreTokens()) {
			String stepVarStr = st.nextToken(); // e.g. ?ean(2)
			int lBracket = stepVarStr.indexOf('(');
			int rBracket = stepVarStr.indexOf(')');
			String varName = stepVarStr.substring(0, lBracket);
			int stepID = Integer.parseInt(stepVarStr.substring(lBracket+1, rBracket));
			StepVar stepVar = new StepVar(stepID, varName);
			bc.addStepVar(stepVar);
		}

		return bc;
	}

	// e.g.  { 2<1 3<1 3<2 }
	public void parseOrderings(String s, Plan plan) {
		StringTokenizer st = new StringTokenizer(s, "{}< ");

		while(st.hasMoreTokens()) {
			int before = Integer.parseInt(st.nextToken());
			int after = Integer.parseInt(st.nextToken());
			plan.addOrdering(before, after);
		}
	}

	public static void main(String[] args) {
		String command[] = {
			"d:\\cygwin\\home\\JPeer\\vhpop-2.2\\vhpop.exe",
			"d:\\pf\\examples\\tmp\\domain.pddl",
			"d:\\pf\\examples\\tmp\\problem.pddl"
		};
		try {
			log.info("executing: "+command);
			Process child = Runtime.getRuntime().exec(command);
			// Get input stream to read from it
			InputStream err = child.getErrorStream();
			String s = Files.getFileContent(err);
			log.debug(s);

			InputStream in = child.getInputStream();
			int c;
			while ((c = in.read()) != -1) {
				System.out.print((char)c);
			}
			in.close();
			err.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
