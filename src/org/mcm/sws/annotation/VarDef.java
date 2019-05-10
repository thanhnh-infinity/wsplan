/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/annotation/VarDef.java,v 1.2 2004/12/01 16:14:51 joepeer Exp $
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


import org.apache.log4j.Logger;
import org.jdom.*;

import org.mcm.sws.*;
import org.mcm.sws.wsdl.*;
import org.mcm.sws.pddl.*;
import org.mcm.sws.util.*;


/**
 * This class represents the "op-def/input/var" and "op-def/output/var" elements
 * in SESMA descriptions
 * (c.f. http://elektra.mcm.unisg.ch/pbwsc/docs/sd_ecows.pdf )
 *
 * @author    Joachim Peer
 */

//			  <var-def var="?stocksymbol" message="tns:GetLastTradePriceInput"
//								 part="body" path="/tickerSymbol"
//								 concept="xyz:StockSymbol" />

public class VarDef implements org.mcm.sws.URIIdentifiable {
	private static Logger log = Logger.getLogger(VarDef.class);

	protected String name, part, path, message;
	protected String owlClass, owlIsProperty, owlOf;
	protected boolean isKey;
	protected String uri;
	protected int type;
	protected boolean syntaxOnly;
	protected String defaultValue;

	public Annotation parentAnnotation;
	public OpDef parentOpDef;
	private VariableKey key=null;


	public static final int INPUT = 0;
	public static final int OUTPUT = 1;

	public VarDef(Annotation parentAnnotation, OpDef parentOpDef) {
		this.parentAnnotation = parentAnnotation;
		this.parentOpDef = parentOpDef;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public void setIsKey(boolean isKey) {
		this.isKey = isKey;
	}

	public boolean isKey() {
		return isKey;
	}

	public void setSyntaxOnly(boolean syntaxOnly) {
		this.syntaxOnly = syntaxOnly;
	}

	public boolean getSyntaxOnly() {
		return syntaxOnly;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getDefaultValue() {
		return defaultValue;
	}


/*
	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}*/

	public void setPart(String part) {
		this.part = part;
	}

	public String getPart() {
		return part;
	}

	public void setPath(String path) {
		if(path==null) { this.path=null; return; }
		this.path = StrUtils.slashed(path, true, false);
	}

	public String getPath() {
		return path;
	}

	public String getAbsPath(OperationBinding opBinding) {

		boolean style=false;
		if(opBinding instanceof SOAPOperationBinding) {
			SOAPOperationBinding soapOpBdg = (SOAPOperationBinding) opBinding;
			style = soapOpBdg.getStyle();
		}


		StringBuffer buf = new StringBuffer();

		buf.append("/");
		if(style == SOAPBinding.STYLE_RPC) {
			buf.append(part);
		}
		if(path != null) {
			buf.append(path);
		}
		return buf.toString();
	}

	public void setOwlClass(String owlClass) {
		this.owlClass = owlClass;
	}

	public String getOwlClass() {
		return owlClass;
	}

	public void setOwlIsProperty(String owlIsProperty) {
		this.owlIsProperty = owlIsProperty;
	}

	public String getOWLIsProperty() {
		return owlIsProperty;
	}

	public void setOwlOf(String owlOf) {
		this.owlOf = owlOf;
	}

	public String getOwlOf() {
		return owlOf;
	}

	public void finishParsing(Annotation parent, OpDef opDef) {
		this.parentAnnotation = parent;
		this.parentOpDef = opDef;

		/*
		if(isKey) {
			Variable var = new Variable();
			var.setString(name);

			ForallFormula core = findForallFormula(parentOpDef.knowledgeEffectObj, var);

			if(core == null) {
				ForallFormula ff = new ForallFormula();
				ArrayList params = new ArrayList();
				params.add(var);
				ff.setParameters(params);

				if(type == OUTPUT) {
					// TODO: handle case of NESTED forall-clauses
					// currently we put it on the top
					Formula body = parentOpDef.knowledgeEffectObj;
					parentOpDef.knowledgeEffectObj = ff;
					if(body!=null) ff.setBody(body);
				}
			}

			parentAnnotation.requirements.strips = true;

		} else if(owlIsProperty != null && owlOf != null) {
			System.out.println("checking vardef: "+name);

			Atom af = new Atom();
			String predName = StrUtils.expandNS(owlIsProperty, parentAnnotation.additionalNamespaces);
			Predicate pdef = (Predicate) Registry.getInstance().getObject(Registry.RELATION_DEF, predName);
			af.setPredicate(pdef);
			Variable var1 = new Variable();
			var1.setString(owlOf);
			Variable var2 = new Variable();
			var2.setString(name);
			af.addTerm(var1);
			af.addTerm(var2);
			if(type == INPUT) {
				Formula f = addConjunction(parentOpDef.preconditionObj, af);
				parentOpDef.preconditionObj = f;
			} else if(type == OUTPUT) {
				if(parentOpDef.effectObj!=null) {
					Formula f = addConjunction(parentOpDef.effectObj, af);
					parentOpDef.effectObj = f;
				} else { // knowledge effect
					ForallFormula core = findForallFormula(parentOpDef.knowledgeEffectObj, var1);
					if(core != null) {
						Formula f = addConjunction(core.getBody(), af);
						core.setBody(f);
					} else {
						System.out.println("*** adding now: "+af.toString());
						Formula f = addConjunction(parentOpDef.knowledgeEffectObj, af);
						parentOpDef.knowledgeEffectObj = f;
						System.out.println("*** indeed  we have: "+f.toString());
					}
				}
			}
			parentAnnotation.requirements.strips = true;
		}
		*/
	}

	protected Formula addConjunction(Formula body, Atom af) {
		if(body == null) return af;

		if(body instanceof Conjunction) {
			((Conjunction) body).addConjunct(af);
			return body;
		} else {
			Conjunction c = new Conjunction();
			c.addConjunct(body);
			c.addConjunct(af);
			return c;
		}
	}

	protected ForallFormula findForallFormula(Formula f, Variable var) {
		if(f==null) return null;
		else return f.findForallFormula(var);
	}


		public String getURI() {
			if(uri == null) {
				String s = (type==INPUT) ? "input" : "output";
				uri = StrUtils.slashed(parentAnnotation.getURI()) + "vardef("+parentOpDef.portType+"/"+parentOpDef.name+"/"+s+"/"+name+")";
			}
			return uri;
		}


		/**
		 * interprets XML stream of (SESMA) annotation document
		 */
		public static VarDef parse(Element varDefElem, int type, Annotation parentAnnotation, OpDef parentOpDef) {
			VarDef varDef = new VarDef(parentAnnotation, parentOpDef);
			varDef.setType(type);
			varDef.setName(varDefElem.getAttributeValue("name"));
			varDef.setPart(varDefElem.getAttributeValue("part", Annotation.NS_WSDL_ANNO));
			varDef.setPath(varDefElem.getAttributeValue("path", Annotation.NS_WSDL_ANNO));
			varDef.setOwlClass(varDefElem.getAttributeValue("class", Annotation.NS_OWL_ANNO));
			varDef.setOwlIsProperty(varDefElem.getAttributeValue("isProperty", Annotation.NS_OWL_ANNO));
			varDef.setOwlOf(varDefElem.getAttributeValue("of", Annotation.NS_OWL_ANNO));
			varDef.setIsKey("true".equals(varDefElem.getAttributeValue("isKey", Annotation.NS_OWL_ANNO)));
			varDef.setSyntaxOnly("true".equals(varDefElem.getAttributeValue("syntaxOnly")));
			varDef.setDefaultValue(varDefElem.getAttributeValue("defaultValue"));


			return varDef;
		}

	public void register() {
		Registry.getInstance().addObject(Registry.ANNO_VARDEF, this);
	}

	public String toString() {
		return "vardef: "+name;
	}

}
