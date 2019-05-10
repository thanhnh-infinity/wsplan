/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/pddl/XMLFormulaParser.java,v 1.2 2004/12/01 16:14:51 joepeer Exp $
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

package org.mcm.sws.pddl;

import org.jdom.*;
import java.util.*;
import java.io.*;
import org.mcm.sws.annotation.*;
import org.mcm.sws.*;

public class XMLFormulaParser {

	public static final int PRECONDITION = 0;
	public static final int EFFECT = 1;

	Element formulaElem;
	Set constants;
	public Requirements parsedRequirements;

	public XMLFormulaParser(Element formulaElem, Set constants) {
		this.formulaElem = formulaElem;
		this.constants = constants;
		this.parsedRequirements = new Requirements();
		this.parsedRequirements.strips = true;
	}

	public Formula parsePrecondition() throws ParseException {
		return parse((Element) this.formulaElem.getChildren().iterator().next(), PRECONDITION);
	}

	public Formula parseEffect() throws ParseException {
		return parse((Element) this.formulaElem.getChildren().iterator().next(), EFFECT);
	}


	public Formula parse(Element formulaElem, int type) throws ParseException {
		Namespace ns = formulaElem.getNamespace();
		String name = formulaElem.getName();
		if(ns.equals(Annotation.NS_SESMA)) {

			if("and".equalsIgnoreCase(name)) {
				return handleConjunction(formulaElem, type);
			} else if("or".equalsIgnoreCase(name)) {
				return handleDisjunction(formulaElem, type);
			} else if("not".equalsIgnoreCase(name)) {
				return handleNegation(formulaElem, type);
			} else if("exists".equalsIgnoreCase(name)) {
				return handleQuantifiedFormula(formulaElem, type);
			} else if("forall".equalsIgnoreCase(name)) {
				return handleQuantifiedFormula(formulaElem, type);
			} else if("implies".equalsIgnoreCase(name)) {
				return handleImplicationFormula(formulaElem, type);
			} else if("when".equalsIgnoreCase(name)) {
				return handleWhenFormula(formulaElem, type);
			} else throw new IllegalArgumentException("unknown formula construct: "+name);

		} else {
			return handleAtom(formulaElem);
		}
	}

	protected Atom handleAtom(Element formulaElem) throws ParseException {
		Namespace ns = formulaElem.getNamespace();
		String name = formulaElem.getName();

		String uri = ns.getURI() + name;

		Predicate p = (Predicate) Registry.getInstance().getObjectByURIorMnemo(Registry.RELATION_DEF, uri);
		if(p == null) {
			throw new ParseException("unknown predicate:"+uri);
		} else {
			Atom atom = new Atom();
			atom.setPredicate(p);
			for(Iterator iter=formulaElem.getAttributes().iterator(); iter.hasNext(); ) {
				Attribute att = (Attribute) iter.next();
				String attVal = att.getValue().trim();
				if(attVal.charAt(0) == '?') {
					atom.addTerm(att.getName(), new Variable(attVal));
				} else {
					atom.addTerm(att.getName(), Name.createName(attVal));
				}
			}

			return atom;
		}
	}

	protected Conjunction handleConjunction(Element formulaElem, int type) throws ParseException {

		Conjunction con = new Conjunction();
		for(Iterator iter = formulaElem.getChildren().iterator(); iter.hasNext(); ) {
			Element elem = (Element) iter.next();
		  con.addConjunct((Formula) parse(elem, type));
		}
		return con;
	}

	protected Disjunction handleDisjunction(Element formulaElem, int type) throws ParseException {
		switch(type) {
			case PRECONDITION : parsedRequirements.disjunctivePreconditions=true; break;
			case EFFECT : throw new IllegalArgumentException("Disjunctive Effects not allowed");
		}

		Disjunction dis = new Disjunction();
		for(Iterator iter = formulaElem.getChildren().iterator(); iter.hasNext(); ) {
			Element elem = (Element) iter.next();
		  dis.addDisjunct(parse(elem, type));
		}
		return dis;
	}

	protected Formula handleNegation(Element formulaElem, int type) throws ParseException {
		if(type==PRECONDITION) {
			parsedRequirements.negativePreconditions=true;
		}

		Element child = (Element) formulaElem.getChildren().iterator().next();

		return parse(child, type).negate();
	}

	protected QuantifiedFormula handleQuantifiedFormula(Element formulaElem, int type) throws ParseException {
		QuantifiedFormula qf = null;
		if ("exists".equals(formulaElem.getName()))
			qf = new ExistsFormula(); else qf= new ForallFormula();
		Element bodyElem = null;
		for(Iterator iter = formulaElem.getChildren().iterator(); iter.hasNext(); ) {
			Element varElem = (Element) iter.next();
			if("var".equals(varElem.getName())) {
				String varName = varElem.getAttributeValue("name");
				// todo: type
				qf.addParameter(new Variable(varName));
			} else {
				bodyElem = varElem;
			}
		}

		qf.setBody(parse(bodyElem, type));
		return qf;
	}

	protected Implication handleImplicationFormula(Element formulaElem, int type) throws ParseException {
		Iterator iter = formulaElem.getChildren().iterator();
		Implication imp = new Implication();
		imp.setAntecedent(parse((Element) iter.next(), type));
		imp.setConsequence(parse((Element) iter.next(), type));
		return imp;
	}

	protected WhenFormula handleWhenFormula(Element formulaElem, int type) throws ParseException {
		if(type==EFFECT) {
			parsedRequirements.conditionalEffects=true;
		}	else throw new IllegalArgumentException("WHEN-formula only allowed in effects");

		Iterator iter = formulaElem.getChildren().iterator();
		WhenFormula wf = new WhenFormula();
		wf.setCondition(parse((Element) iter.next(), type));
		wf.setConsequence(parse((Element) iter.next(), type));
		return wf;
	}
}
