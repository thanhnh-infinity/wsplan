/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/annotation/OpDef.java,v 1.2 2004/12/01 16:14:51 joepeer Exp $
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


import org.apache.log4j.Logger;

import org.mcm.sws.*;
import org.mcm.sws.util.*;
import org.mcm.sws.wsdl.*;
import org.mcm.sws.pddl.*;

import org.jdom.*;
/**
 * This class represents an "op-def element of SESMA descriptions
 * (c.f. http://elektra.mcm.unisg.ch/pbwsc/docs/sd_ecows.pdf )
 *
 * @author    Joachim Peer
 */
public class OpDef implements URIIdentifiable {
	public static final String BEANSHELL = "beanshell";
	public static final String JAVA = "java";
	public static final String JEXL = "JEXL";

	// representation of formulas
	public static final int F_XML = 0;
	public static final int F_PDDL = 1;

	private static Logger log = Logger.getLogger(OpDef.class);

	protected String name, portType, uri;
	protected LinkedHashMap inputVarDefs, outputVarDefs;
	protected ArrayList varDefsForParams;

	protected Object precondition;
	protected Formula preconditionObj;
	protected int preconditionLang;
	protected List results;

	public Annotation parent;
	private OperationKey key;


	/**
	 *Constructor for the OpDef object
	 */
	public OpDef(Annotation parent) {
		this.parent = parent;
		this.inputVarDefs = new LinkedHashMap();
		this.outputVarDefs = new LinkedHashMap();
		this.varDefsForParams = new ArrayList();
		this.results = new ArrayList();
	}

	public Annotation getParentAnnotation() {
		return parent;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setPortType(String portType) {
		this.portType = portType;
	}

	public String getPortType() {
		return portType;
	}

	public void addInputVarDef(VarDef varDef) {
		inputVarDefs.put(varDef.getName(), varDef);
	}

	public void addOutputVarDef(VarDef varDef) {
		outputVarDefs.put(varDef.getName(), varDef);
	}

	public Map getInputVarDefs() {
		return inputVarDefs;
	}

	public Map getOutputVarDefs() {
		return outputVarDefs;
	}

	public VarDef getDefForInputVar(String var) {
		return (VarDef) inputVarDefs.get(var);
	}

	public VarDef getDefForOutputVar(String var) {
		return (VarDef) outputVarDefs.get(var);
	}


	public void setPrecondition(Object precondition) {
		this.precondition = precondition;
	}

	public Object getPrecondition() {
		return precondition;
	}

	public Formula getPreconditionObj() {
		return preconditionObj;
	}

	public void addResult(Result result) {
		results.add(result);
	}

	public List getResults() {
		return results;
	}

	public String getResultsText() {
		StringBuffer buf = new StringBuffer();
		for(Iterator iter = results.iterator(); iter.hasNext(); ) {
			Result r = (Result) iter.next();
			if(r instanceof Effect) {
				buf.append("* effect: ");
			} else {
				buf.append("* knowledge-effect: ");
			}
			buf.append(r.getFormula().toString());
			buf.append("\n");
		}
		return buf.toString();
	}

	public boolean isSensingAction() {
		for(Iterator iter = results.iterator(); iter.hasNext(); ) {
			if(iter.next() instanceof KnowledgeEffect)
				return true;
		}
		return false;
	}

		// only for process??
	public Map determineBindings() {
		/*
		HashMap result = new HashMap();
		ExecEnvironment env = ExecEnvironment.getInstance();
		for (Iterator iter = varDefsForParams.iterator(); iter.hasNext();) {
			VarDef vd = (VarDef) iter.next();
			String value = env.evaluate(this, vd.getName());
			if (value != null) {
				log.info("opdef.determineBindings - found binding for " + vd.getName() + " : " + value);
				result.put(vd.getName(), value);
				System.out.println("opdef.determineBindings: pos 1");
			} else {
				log.info("opdef.determineBindings - no binding found for value"+vd.getName());
				System.out.println("opdef.determineBindings: pos 2");
			}
		}
		return result;*/
		return null;
	}

	/**
	 *returns a set of tuples (<part, path>, value)
	 *or (varDef, value) ???
	 *with values from
	 *-- either the ActionInstance
	 *-- or the FactBase
	 *
	 * @param  ai  Description of the Parameter
	 * @return     Description of the Return Value
	 */
	public Map determineBindings(ActionInstance ai, Map bindingsFromCL) {
		ExecEnvironment env = ExecEnvironment.getInstance();

		List paramVals = ai.getParams();
		// either values or names

		HashMap instantiated = new HashMap();

		ArrayList unInstantiated = new ArrayList();

		StrUtils.printList(varDefsForParams, System.out);

		if (paramVals.size() != varDefsForParams.size()) {
			log.warn("@@Warning - - troubles in determineBindings -- paramVals.size()="+paramVals.size()+" varDefsForParams.size()="+varDefsForParams.size());
			return null;
		}

		if (paramVals.size() == 0) {
			return null;
		}

		Iterator varDefIter = varDefsForParams.iterator();
		for (Iterator paramValsIter = paramVals.iterator(); paramValsIter.hasNext(); ) {
			VarDef vd = (VarDef) varDefIter.next();
			//note: this is safe, because precedence of params is derived from vardefs ...

			String value = (String) paramValsIter.next();
			// value set by planner
			if (value.charAt(0) != '?') {
				instantiated.put(vd.getName(), value);
				// take value from planner
				log.info("found ai binding for " + vd.getName() + " : " + value);
			} else if(vd.getType()==VarDef.INPUT) {

				String s = (String) bindingsFromCL.get(vd.getName());
				if(s != null) {
					instantiated.put(vd.getName(), s);
				} else {
					unInstantiated.add(new Variable(vd.getName()));
				}

			}
		}

  	// take values for the remaining values from fact base
		if(unInstantiated.size() > 0) {
			Map retrievedKBValues = null;

			if(this.preconditionObj != null) {
				Formula partiallyInstPrecond = this.preconditionObj.cloneAndSubstitute(instantiated);
				retrievedKBValues = env.evaluatePrecondition(partiallyInstPrecond, unInstantiated);
			}

			if(retrievedKBValues.size() < unInstantiated.size()) {
				log.warn("not everything could be instantiated: rv="+retrievedKBValues.size() +" uninst="+ unInstantiated.size());
				// throw exception?
			}

			if(retrievedKBValues != null)
				instantiated.putAll(retrievedKBValues);
		}

		log.info("*** DETERMINED BINDINGS: "+instantiated);
		return instantiated;
	}

	/**
	 *  Description of the Method
	 *
	 * @param  parent      Description of the Parameter
	 * @param  namespaces  Description of the Parameter
	 */
	public void finishParsing(Annotation parent, Map namespaces) {
		this.parent = parent;

		if (this.precondition != null) {
			PDDLFormulaeParser parser;
			XMLFormulaParser xmlFormulaParser;

			HashSet predicates = new HashSet();
			HashSet constants = new HashSet();

			if(this.preconditionLang == F_PDDL) {
				parser = new PDDLFormulaeParser(namespaces, new ByteArrayInputStream(((String) precondition).getBytes()), new HashSet(), constants, predicates);
				try {
					preconditionObj = parser.gd();
					//Registry.getInstance().addCollection(Registry.PREDICATE, predicates);
					Registry.getInstance().addCollection(Registry.CONSTANT_DEF, constants);
					parent.requirements.merge(parser.parsedRequirements);
				} catch (ParseException pe) {
					pe.printStackTrace();
				}
			} else {
				try {
					xmlFormulaParser = new XMLFormulaParser((Element) precondition, constants);
					preconditionObj = xmlFormulaParser.parsePrecondition();
					Registry.getInstance().addCollection(Registry.CONSTANT_DEF, constants);
					parent.requirements.merge(xmlFormulaParser.parsedRequirements);
				} catch (ParseException pe) {
					pe.printStackTrace();
				}
			}
		}


		/*
		for (Iterator iter = inputVarDefs.entrySet().iterator(); iter.hasNext(); ) {
			VarDef vd = (VarDef) ((Map.Entry) iter.next()).getValue();
			vd.finishParsing(parent, this);
		}
		for (Iterator iter = outputVarDefs.entrySet().iterator(); iter.hasNext(); ) {
			VarDef vd = (VarDef) ((Map.Entry) iter.next()).getValue();
			vd.finishParsing(parent, this);
		}	*/

		// build a structure which contains the params as they appear in the PDDL paramter list
		for (Iterator iter = inputVarDefs.values().iterator(); iter.hasNext(); ) {
			VarDef vd = (VarDef) iter.next();
			if (inForallClause(vd.getName()) || vd.getSyntaxOnly()) {
				continue;
			} else {
				varDefsForParams.add(vd);
			}
		}
		for (Iterator iter = outputVarDefs.values().iterator(); iter.hasNext(); ) {
			VarDef vd = (VarDef) iter.next();
			if (varDefsForParams.contains(vd) || inForallClause(vd.getName()) || vd.getSyntaxOnly()) {
				continue;
			} else {
				varDefsForParams.add(vd);
			}
		}
	}


	/**
	 *  Description of the Method
	 */
	public void register() {
		Registry.getInstance().addObject(Registry.ANNO_OPDEF, this);

		for (Iterator iter = inputVarDefs.values().iterator(); iter.hasNext(); ) {
			VarDef vd = (VarDef) iter.next();
			vd.register();
		}
		for (Iterator iter = outputVarDefs.values().iterator(); iter.hasNext(); ) {
			VarDef vd = (VarDef) iter.next();
			vd.register();
		}
	}

	/**
	 * interprets XML stream of (SESMA) annotation document
	 */
	public static OpDef parse(Element opDefElem, Annotation parentAnnotation) {
	    OpDef opDef = new OpDef(parentAnnotation);

	    // parse opdef info
	    opDef.setName(opDefElem.getAttributeValue("name"));
	    opDef.setPortType(opDefElem.getAttributeValue("portType", Annotation.NS_WSDL_ANNO));

	    Element preconditionElem = opDefElem.getChild("precondition", Annotation.NS_SESMA);
			if(preconditionElem != null) {
				opDef.preconditionLang = ("pddl".equalsIgnoreCase(preconditionElem.getAttributeValue("lang"))) ? F_PDDL : F_XML;
				opDef.precondition = preconditionElem;
			}

	    Element effectElem = opDefElem.getChild("effect", Annotation.NS_SESMA);
	    if(effectElem != null) {
				Result effect = Result.parse(effectElem);
				opDef.addResult(effect);
	    }

	    Element knowledgeEffectElem = opDefElem.getChild("knowledge-effect", Annotation.NS_SESMA);
	    if(knowledgeEffectElem != null) {
				Result knowledgeEffect = Result.parse(knowledgeEffectElem);
				opDef.addResult(knowledgeEffect);
	    }

	    Element inputElem = opDefElem.getChild("input", Annotation.NS_SESMA);
	    if(inputElem!=null) {
		    for(Iterator iter = inputElem.getChildren("var", Annotation.NS_SESMA).iterator(); iter.hasNext(); ) {
			    Element varDefElem = (Element) iter.next();
			    VarDef varDef = VarDef.parse(varDefElem, VarDef.INPUT, opDef.parent, opDef);
			    opDef.addInputVarDef(varDef);
		    }
	    }

	    Element outputElem = opDefElem.getChild("output", Annotation.NS_SESMA);
	    if(outputElem!=null) {
		    for(Iterator iter = outputElem.getChildren("var", Annotation.NS_SESMA).iterator(); iter.hasNext(); ) {
			    Element varDefElem = (Element) iter.next();
			    VarDef varDef = VarDef.parse(varDefElem, VarDef.OUTPUT, opDef.parent, opDef);
			    opDef.addOutputVarDef(varDef);
		    }
	    }

	    return opDef;
	}

	//TODO: NAME should refer to OPDEF name not to the WSDL-OPERATIONs name!
	public static String createURI(String tns, String name) {
		return StrUtils.slashed(tns) + "opdef(" + name + ")";
	}

	 public String getURI() {
		if (uri == null) {
			// TODO - wron name?
			uri = createURI(parent.targetNamespace,  name);
		}
		return uri;
	}

	public boolean hasEffectWith(Set preds) {
		for(Iterator iter = results.iterator(); iter.hasNext(); ) {
			Result r = (Result) iter.next();
			if(r instanceof Effect) {
				List lit = r.getFormula().expandToFacts();
				for(Iterator iter2 = lit.iterator(); iter2.hasNext(); ) {
					Predicate p = ((Literal) iter2.next()).getPredicate();
					if(preds.contains(p)) return true;
				}
			}
		}
		return false;
	}

	public boolean hasEffectInvolving(Literal litToProtect) {
		for(Iterator iter = results.iterator(); iter.hasNext(); ) {
			Result r = (Result) iter.next();
			if(r instanceof Effect) {
				List lit = r.getFormula().expandToFacts();
				for(Iterator iter2 = lit.iterator(); iter2.hasNext(); ) {
					//Predicate p = ((Literal) iter2.next()).getPredicate();
					//if(preds.contains(p)) return true;
					Literal literal = (Literal) iter2.next();
					if(literal.canUnifyWith(litToProtect)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 *  Description of the Method
	 *
	 * @param  var  Description of the Parameter
	 * @return      Description of the Return Value
	 */
	private boolean inForallClause(String var) {

		for(Iterator iter=results.iterator(); iter.hasNext(); ) {
			Result r = (Result) iter.next();
			if(r.getFormula() instanceof ForallFormula) {

				ForallFormula f = (ForallFormula) r.getFormula();
				for (Iterator iter2 = f.getParameters().iterator(); iter2.hasNext(); ) {
					if (((Variable) iter2.next()).getString().equals(var)) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public String getPDDLName() {
		StringBuffer buf = new StringBuffer();

		int mnemo1 = Registry.getInstance().getIntCode(Registry.ANNO_OPDEF, getURI());

		buf.append("op_");
		buf.append(mnemo1);
		buf.append("_");
		buf.append(name);
		return buf.toString();
	}

	/**
	 *  Description of the Method
	 *
	 * @return    Description of the Return Value
	 */
	public String toPDDL() {
		if(results.size() == 0) return "";

		StringBuffer buf = new StringBuffer();

		int mnemo1 = Registry.getInstance().getIntCode(Registry.ANNO_OPDEF, getURI());

		buf.append("(:action op_");
		buf.append(mnemo1);
		buf.append("_");
		buf.append(name);
		buf.append("\n");

		//important: the precedence of VarDefs determines precedence of action-parameters (as used by ActionInstance)
		buf.append(":parameters (");

		for (Iterator iter = varDefsForParams.iterator(); iter.hasNext(); ) {
			VarDef vd = (VarDef) iter.next();
			if(!vd.getSyntaxOnly()) {
				String varName = vd.getName();
				buf.append(" ");
				buf.append(varName);
			}
		}
		buf.append(")");

		if (this.preconditionObj != null) {
			buf.append(":precondition ");
			buf.append(preconditionObj.getPDDL());
			buf.append("\n");
		}

		int s = results.size();
		if(s > 0) {
			buf.append(":effect ");
			if(s==1) {
				Formula f = ((Result) results.get(0)).getFormula();
			  buf.append(f.getPDDL());
			} else if(s > 1) {
				Conjunction con = new Conjunction();
				for(Iterator iter = results.iterator(); iter.hasNext(); ) {
					Result r = (Result) iter.next();
					con.addConjunct(r.getFormula());
				}
				buf.append(con.getPDDL());
			}
			buf.append("\n");
		}
		buf.append(")\n");

		return buf.toString();
	}
}

