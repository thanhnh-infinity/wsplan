/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/planners/PDDLGenerator.java,v 1.2 2004/12/01 16:14:52 joepeer Exp $
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
import java.util.logging.*;


import org.apache.log4j.Logger;

import org.apache.commons.digester.*;
import org.apache.commons.lang.StringUtils;
import org.xml.sax.SAXException;

import org.jdom.*;
import org.jdom.input.*;
import org.jdom.output.*;

import org.mcm.sws.wsdl.*;
import org.mcm.sws.util.*;
import org.mcm.sws.pddl.*;
import org.mcm.sws.plan.*;
import org.mcm.sws.annotation.*;
import org.mcm.sws.Registry;
import org.mcm.sws.ExecEnvironment;

/**
 * This class generated the PDDL domain and problem definition files to be
 * used by the AI planner(s).
 *
 * @author    Joachim Peer
 */
public class PDDLGenerator {
	private static Logger log = Logger.getLogger(PDDLGenerator.class);

	/**
	 *TODO: plug in planner specific interface implementations (to deal with
	 *some of the little syntactical problems e.g regarding numbers
	 *
	 * @param  goalSpec  Description of the Parameter
	 * @return           Description of the Return Value
	 */
	public static String generateDomainPDDL(String goalSpec, Set findoutLiterals, Set ignore) {

		// first, deal with the GOAL

		// the "Goal" action
		//goalSpec = StrUtils.encode(goalSpec);
		Formula goalSpecObj = null;

		ByteArrayInputStream is = new ByteArrayInputStream(goalSpec.getBytes());
		HashSet goalVars = new HashSet();
		HashSet goalConstants = new HashSet();
		HashSet goalPreds = new HashSet();

		Map namespaces = Config.getInstance().getNamespaces();
		PDDLFormulaeParser parser = new PDDLFormulaeParser(namespaces, is, goalVars, goalConstants, goalPreds);
		try {
			goalSpecObj = parser.gd();
			Registry.getInstance().addCollection(Registry.CONSTANT_DEF, goalConstants);
		} catch (ParseException pe) {
			pe.printStackTrace();
		} finally {
			try {
			is.close();
			} catch(IOException ioe) {
				ioe.printStackTrace();
			}
		}

		// extract literals from goal and seperate by annotation
		List goalLiterals = goalSpecObj.expandToFacts();

		for(Iterator iter=goalLiterals.iterator(); iter.hasNext(); ) {
			Literal l = (Literal) iter.next();
			findoutLiterals.add(l);
		}

		// identify potential conflicts
		/*
		for(Iterator iter=findoutPreds.iterator(); iter.hasNext(); ){
			Predicate pred = (Predicate) iter.next();
			if(achievementPreds.contains(pred)) {
				log.warn("WARNING potential for bad plan: predicate "+pred.getURI()+" is used for ACHIEVE and FINDOUT in the same goal");
				// probably we should even throw exception?? TODO: PlanninException??
			}
		}*/

		// NOW WE CAN BUILD THE DOMAIN DESCRIPTION

		StringBuffer buf = new StringBuffer();
		buf.append("(define (domain test1)\n");

		Collection annotations = Registry.getInstance().getCollection(Registry.ANNO_DOC);
		Requirements allReqs = new Requirements();
		for(Iterator iter = annotations.iterator(); iter.hasNext(); ) {
			allReqs.merge(((Annotation) iter.next()).getRequirements());
		}

		buf.append(allReqs.toPDDL());

		// predicates: derviced from the merge of all pred's found during
		// annotation (pre/postcond.) parsing
		Collection predicates = Registry.getInstance().getCollection(Registry.RELATION_DEF);
		if (predicates.size() > 0) {
			buf.append("  (:predicates\n");
			for (Iterator iter = predicates.iterator(); iter.hasNext(); ) {
				buf.append("    ");
				buf.append(((Predicate) iter.next()).getPDDL());
				buf.append("\n");
			}
			buf.append("(be-satisfied)\n  )\n");  // goal pred
		}

		// actions derived from the web service operations / annotations


		for (Iterator iter = Registry.getInstance().getCollection(Registry.ANNO_OPDEF).iterator(); iter.hasNext(); ) {
			OpDef od = (OpDef) iter.next();

			//if(!od.hasEffectWith(findoutPreds) && !ignore.contains(od)) {
				buf.append(od.toPDDL());
				buf.append("\n");
			//	log.info("INcluding operation"+od.getName());
			//} else {
			//	log.info("EXcluding operation"+od.getName());
			//}


		}



		buf.append("(:action achieve-goal\n");

		buf.append("  :parameters (");
		for (Iterator iter = goalVars.iterator(); iter.hasNext(); ) {
			buf.append(" ");
			buf.append((String) iter.next());
		}
		buf.append(")\n");

		buf.append(":precondition ");
		buf.append(goalSpecObj.getPDDL());
		buf.append("");
		buf.append(":effect (be-satisfied)\n");
		buf.append(")");

		buf.append(")");


		return buf.toString();

	}





	/**
	 *  Description of the Method
	 *
	 * @return    Description of the Return Value
	 */
	public static String generateProblemPDDL(Set violations, Set findoutLiterals) {
		ExecEnvironment env = ExecEnvironment.getInstance();

		StringBuffer buf = new StringBuffer();


		buf.append("(define (problem generic_problem1) \n");
		buf.append("   (:domain test1) \n");
		buf.append("   (:objects \n");

		// e.g. site001 joepeer secret
		for (Iterator iter = Registry.getInstance().getCollection(Registry.CONSTANT_DEF).iterator(); iter.hasNext(); ) {
			Name object = (Name) iter.next();
			/*
			 *  try { // todo - seek less expensive and more generic method
			 *  Integer.parseInt(object);
			 *  buf.append("num_"+object); // masking the number, becasVHPOP does not like numbers
			 *  } catch(NumberFormatExcep	tion ignore) {
			 *  buf.append(object);
			 *  }
			 */
			buf.append(StrUtils.encode(object.getString()));
			buf.append(" ");
		}

		buf.append("   )\n");
		buf.append("  (:init\n");

		// e.g. (username site001 joepeer)

		for (Iterator iter = env.getFactBase().iterator(); iter.hasNext(); ) {
			Atom atom = (Atom) iter.next();
			buf.append(atom.getPDDL());
			buf.append("\n");
		}

		buf.append("	 )\n");
		buf.append("	 (:goal (be-satisfied))\n");

		if(violations != null && violations.size() > 0) {
		  buf.append("(:avoid-links ");

			// violations from ...
		  for(Iterator iter = violations.iterator(); iter.hasNext(); ) {
		    CausalLink cl = (CausalLink) iter.next();

		    ActionInstance from = cl.getFrom();
		    ActionInstance to = cl.getTo();

		    buf.append("(");
		    buf.append(getStrID(from));
		    buf.append(" ");
		    buf.append(cl.getCondition().getPDDL());
		    buf.append(" ");
		    buf.append(getStrID(to));
		    buf.append(")\n");
		  }

			for (Iterator iter = Registry.getInstance().getCollection(Registry.ANNO_OPDEF).iterator(); iter.hasNext(); ) {
				OpDef od = (OpDef) iter.next();
				for(Iterator iter2 = findoutLiterals.iterator(); iter2.hasNext(); ) {
					Literal lit = (Literal) iter2.next();
					if(od.hasEffectInvolving(lit)) {
						buf.append("(");
						buf.append(od.getPDDLName());
						buf.append(" ");
						buf.append(lit.getPDDL());
						buf.append(" ANY)\n");
					}
				}
			}

		  buf.append(")");
		}

		buf.append(")");

		return buf.toString();
	}

	/** return name of operation */
	protected static String getStrID(ActionInstance ai) {
		OpDef opDef = ai.getOpDef();
		if(opDef == null) {
			int id = ai.getStepID();
			if(id == ActionInstance.INITIAL) return "INITIAL";
			else if(id == ActionInstance.FINAL) return "achieve-goal";
			else throw new RuntimeException("getStrID - no id given");
		} else {
			return opDef.getPDDLName();
		}
	}

}
