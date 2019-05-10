/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/ExecEnvironment.java,v 1.2 2004/12/01 16:14:51 joepeer Exp $
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

package org.mcm.sws;

import java.io.*;
import java.util.*;

import org.jdom.*;
import org.jdom.input.*;


import org.apache.log4j.Logger;

import org.mcm.sws.pddl.*;
import org.mcm.sws.util.*;
import org.mcm.sws.annotation.*;

/**
 * The ExecEnvironment contains the agent's FACT BASE.
 *
 * @author    Joachim Peer
 */
public class ExecEnvironment {
	private static Logger log = Logger.getLogger(ExecEnvironment.class);

	private ArrayList factBase;
	private HashSet objects;

	private static HashMap instances;

	static {
		instances = new HashMap();
	}


	/**
	 *Constructor for the ExecEnvironment
	 */
	public ExecEnvironment() {

		factBase = new ArrayList();


		// some built in fact, for testing purposes...
		/*
		 *  String[] s1 = {"site001", "joepeer"};
		 *  Predicate p1 = new Predicate("username", s1);
		 *  addToFactBase(p1);
		 *  String[] s2 = {"site001", "secret123"};
		 *  Predicate p2 = new Predicate("password" ,s2);
		 *  addToFactBase(p2);
		 */
	}


	/**
	 *  gets the fact base
	 *
	 * @return    The factBase
	 */
	public List getFactBase() {
		return factBase;
	}


	public void clearFactBase() {
		factBase.clear();
	}

	/**
	 *  Adds a fact
	 *
	 * @param  p  The fact to be added
	 */
	public void addToFactBase(Atom a) {
		factBase.add(a);

		//maintain object list

		for (Iterator iter = a.getTerms().values().iterator(); iter.hasNext(); ) {
			Term t = (Term) iter.next();
			if (t instanceof Name) {
				Registry.getInstance().addObject(Registry.CONSTANT_DEF, (Name) t);
			}
		}

		Registry.getInstance().addObject(Registry.RELATION_DEF, a.getPredicate());
	}


	protected void parseFactPath(String path) {
		try {
			InputStream is = Files.getInputStream(path);
			Document doc = new SAXBuilder().build(is);
			Element rootElem = doc.getRootElement();
			for(Iterator iter = rootElem.getChildren("fact").iterator(); iter.hasNext(); ) {
				Element elem = (Element) iter.next();
				String s = elem.getText();
				addToFactBase(Predicate.parseInstance(s, XMLUtils.namespaceMap(rootElem)));
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 *  Adds a fact to the factbase, e.g. (foo prop1=bar1 prop2=bar2)
	 *
	 * @param  fact  The fact to be added
	 */
	public void addToFactBase(String fact) {
		Map namespaces = Config.getInstance().getNamespaces();
		this.addToFactBase(Predicate.parseInstance(fact, namespaces));
	}


	/**
	 *it is assumed that this list only contains atomic formulas or
	 *negations of atomic formulas (=> as done by expandToFacts)
	 *
	 * @param  facts  The facts to be added
	 */
	public void addAllToFactBase(List facts) {
		for (Iterator iter = facts.iterator(); iter.hasNext(); ) {
			Object o = iter.next();
			if (o instanceof Atom) {
				Atom a = (Atom) o;
				log.info("** adding to factbase: " + a.toString());
				addToFactBase(a);
			} else if (o instanceof NegAtom) {
				NegAtom negx = (NegAtom) o;
				Atom neg = (Atom) negx.negate();

				for (ListIterator iter2 = factBase.listIterator(); iter2.hasNext(); ) {
					Atom entry = (Atom) iter2.next();
					if (neg.canUnifyWith(entry)) {
						iter2.remove();
						log.info("removed entry from factbase:" + entry);
					}
				}
			}
		}
	}


	/**
	 *  Gets the objects stored in the factbase
	 *
	 * @return    The objects value
	 */
	public Set getObjects() {
		return objects;
	}

	public void addObjects(Set newObjects) {
		objects.addAll(newObjects);
	}

	public void clearObjects() {
		objects.clear();
	}

	/**
	 *  look up a value from the factbase
	 *
	 * @param  p      the predicate to be unified
	 * @param  index  the index of the arguement to be unified
	 * @return        the value bound at the specified pos of a fact found
	 */
	public String lookup(Atom a, String property) {

		for (Iterator iter = factBase.iterator(); iter.hasNext(); ) {
			Atom aPred = (Atom) iter.next();
			if (a.canUnifyWith(aPred)) {
				Term t = aPred.getTerm(property);
				if (t != null) {
					return t.getString();
				} else {
					continue;
				}
			}
		}

		return null;
	}


	/**
	 *  Figure out the value of param given that precondition
	 * of given opDef is true
	 *
	 * @param  opDef  operation annotation
	 * @param  param  the parameter occuring in the precondition
	 * @return        the value to be assumed

	public String evaluate(OpDef opDef, String param) {
		System.out.println("** i am called for evaluation of "+param);
		Formula precondition = opDef.getPreconditionObj();

		if (precondition == null) {
			return null;
		}

		System.out.println("** pos 1 "+precondition.getPDDL());

		List atoms = precondition.getFormulaeWith(param);
		if (atoms == null || atoms.size() == 0) {
			return null;
		}

		System.out.println("** pos 2 "+param);

		for(Iterator iter = atoms.iterator(); iter.hasNext(); ) {
			Atom a = (Atom) iter.next();
			System.out.println("~~ TRYING OUT ATOM: "+a.toString());
			//int index = a.getParamIndex(param);
			String property = a.getPropertyOf(param);
			//String result = lookup(a, index);
			String result = lookup(a, property);
			if(result!=null) return result;
		}

		return null;
	}
	 */

	 /**
	 in: precondition, factbase
	 out: if found, variable bindings of factbase satisfying the precondition

	 uses Inter-PROLOG+XSB to do the tedious work
	 */
	 public Map evaluatePrecondition(Formula pre, List uninstantiatedVars) {
		 StringBuffer pBuf = new StringBuffer();

		 for(Iterator iter=factBase.iterator(); iter.hasNext(); ) {
			 Literal lit = (Literal) iter.next();

			 // only Atom (not NegAtom) written => closed world assumption of Prolog
			 if(lit instanceof Atom) {
				 pBuf.append(lit.getProlog(null));
				 pBuf.append(".");
				 pBuf.append("\n");
			 }
		 }

		 // we assume pre is in NDNF
		 StringBuffer queryBuf = new StringBuffer();
		 queryBuf.append("query(");
		 for(Iterator iter = uninstantiatedVars.iterator(); iter.hasNext(); ) {
			 Variable v = (Variable) iter.next();
			 queryBuf.append(v.getProlog());
			 if(iter.hasNext()) queryBuf.append(",");
		 }
		 queryBuf.append(")");

		 pBuf.append(pre.getProlog(queryBuf.toString()));
		 pBuf.append(".");

		 Object[] results = null;
		 try {
			 results = PrologUtil.executeQuery(queryBuf.toString(), uninstantiatedVars, pBuf.toString());
		 } catch(IOException ioe) {
			 ioe.printStackTrace();
		 }

		 Map result = new HashMap(); int i=0;
		 if(results != null) {
			 for(Iterator iter = uninstantiatedVars.iterator(); iter.hasNext(); ) {
				 Variable v = (Variable) iter.next();
				 result.put(v.getString(), (String) results[i++]);
			 }
		 }

		 return result;
	 }


	// assumption
	public boolean contains(Literal lit) {
		for(Iterator iter=factBase.iterator(); iter.hasNext(); ) {
			Literal l = (Literal) iter.next();
			if(l.canUnifyWith(lit)) return true;
		}

		return false;
	}


	/* todo: offer more generic solution(s), indexing, etc. */
	public String select(String atom) {

		StringTokenizer st = new StringTokenizer(atom, "() ");
		if(!st.hasMoreTokens()) return null;
		String predURI = st.nextToken(); // the predicate URI

		// create params list and find out pos of var to select
		int varPos=-1;
		ArrayList paramsList = new ArrayList();
		for(int i=0; st.hasMoreTokens(); i++) {
			String s = st.nextToken();
			System.out.println("s = "+s);
			if(s.charAt(0) == '?') varPos = i;
			else paramsList.add(s);
		}


		String[] params = new String[paramsList.size()];
		params = (String[]) paramsList.toArray(params);


		// go through factbase and lookup correct value
		nextLiteral:
		for(Iterator iter=factBase.iterator(); iter.hasNext(); ) {
			Literal l = (Literal) iter.next();

			if(!predURI.equals(l.getPredicate().getURI()))
				continue; // wrong relation, continue with next lit.

			// now test unification and bind value
			String boundValue = null;
			for(int i=0; i<l.getTerms().size(); i++) {
				String ithTerm = l.getTermAt(i);
				if(i==varPos) {
					boundValue = ithTerm;
				} else {
					String ithParam = params[i];
					// do the terms unify? if not, continue at next literal
					if(!ithParam.equals(ithTerm))
						continue nextLiteral;
				}
			}
			// successfully completed a full iteration
			// => must be the correct value
			return boundValue;
		}
		return null; // no matching literal could be found
	}

	public void printFactBase() {
		System.out.println("PRINTING FB: ");
		for(Iterator iter=factBase.iterator(); iter.hasNext(); ) {
			System.out.println("FACT: "+iter.next().toString());
		}
	}


	/**
	 *  creates an ExecEnv. instance (a singleton for each thread group)
	 *
	 * @param  config  the Config, which may contain facts, etc.
	 * @return         instance created
	 */
	public static ExecEnvironment createInstance(Config config) {
		ExecEnvironment instance = new ExecEnvironment();
		instances.put(Thread.currentThread().getThreadGroup(), instance);

		for (Iterator iter = config.getFactPaths().iterator(); iter.hasNext(); ) {
			String factPath = (String) iter.next();
			instance.parseFactPath(factPath);
		}

		return instance;
	}




	/**
	 *  Gets the instance attribute of the ExecEnvironment class
	 *
	 * @return    The instance value
	 */
	public static ExecEnvironment getInstance() {
		return (ExecEnvironment) instances.get(Thread.currentThread().getThreadGroup());
	}



}

