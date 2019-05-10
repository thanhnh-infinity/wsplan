/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/annotation/Result.java,v 1.2 2004/12/01 16:14:51 joepeer Exp $
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

package org.mcm.sws.annotation;

import java.util.*;
import java.io.*;
import org.jdom.*;
import org.jdom.input.*;

import org.apache.log4j.Logger;

import org.mcm.sws.*;
import org.mcm.sws.pddl.*;
import org.mcm.sws.util.*;

public class Result {

	protected Formula formula;
	protected int lang;
	protected String successCondition;
	protected String successConditionLang;
	protected Requirements requirements;

	public void setFormula(Formula formula) {
		this.formula = formula;
	}

	public Formula getFormula() {
		return formula;
	}

	public void setLang(int lang) {
		this.lang = lang;
	}

	public int getLang() {
		return lang;
	}

	public void setRequirements(Requirements requirements) {
		this.requirements = requirements;
	}

	public Requirements getRequirements() {
		return requirements;
	}

	public void setSuccessCondition(String successCondition) {
		this.successCondition = successCondition;
	}

	public String getSuccessCondition() {
		return successCondition;
	}

	public void setSuccessConditionLang(String successConditionLang) {
		this.successConditionLang = successConditionLang;
	}

	public String getSuccessConditionLang() {
		return successConditionLang;
	}

	public static Result parse(Element effectElem) {
		Result result = null;

		String name = effectElem.getName();
		if("effect".equals(name)) result = new Effect();
		else if("knowledge-effect".equals(name)) result = new KnowledgeEffect();

		result.lang = ("pddl".equalsIgnoreCase(effectElem.getAttributeValue("lang"))) ? OpDef.F_PDDL : OpDef.F_XML;

		Element successConditionElem = effectElem.getChild("success-condition", Annotation.NS_SESMA);
		if(successConditionElem != null) {
			result.successConditionLang = successConditionElem.getAttributeValue("lang");
			result.successCondition = successConditionElem.getText();
			successConditionElem.detach();
		}

		PDDLFormulaeParser parser;
		XMLFormulaParser xmlFormulaParser;

		HashSet predicates = new HashSet();
		HashSet constants = new HashSet();
		/*
		if(result.lang == OpDef.F_PDDL) {
			parser = new PDDLFormulaeParser(namespaces, new ByteArrayInputStream(effectElem.getText()).getBytes(), new HashSet(), constants, predicates);
			try {
				result.formula = parser.effect();
				//Registry.getInstance().addCollection(Registry.PREDICATE, predicates);
				Registry.getInstance().addCollection(Registry.CONSTANT_DEF, constants);
				this.requirements = parser.parsedRequirements;
			} catch (ParseException pe) {
				pe.printStackTrace();
			}
		} else {
			*/
		if(result.lang == OpDef.F_XML) {
			try {
				xmlFormulaParser = new XMLFormulaParser((Element) effectElem, constants);
				result.formula = xmlFormulaParser.parseEffect();
				Registry.getInstance().addCollection(Registry.CONSTANT_DEF, constants);
				result.requirements = xmlFormulaParser.parsedRequirements;
			} catch (ParseException pe) {
				pe.printStackTrace();
			}
		}

		return result;
	}


}
