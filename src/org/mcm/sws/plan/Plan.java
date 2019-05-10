/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/plan/Plan.java,v 1.2 2004/12/01 16:14:51 joepeer Exp $
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

package org.mcm.sws.plan;

import java.util.*;
import org.apache.log4j.Logger;

import org.mcm.sws.ExecEnvironment;
import org.mcm.sws.pddl.*;

public class Plan {
	private static Logger log = Logger.getLogger(Plan.class);

	protected List steps;
	protected Map stepsMap;

	protected List causalLinks;
	protected Map from, to;
	protected List bindingConstraints;

	protected List orderings;

	public Plan() {
		steps = new ArrayList();
		stepsMap = new HashMap();

		causalLinks = new ArrayList();
		from = new HashMap();
		to = new HashMap();

		orderings = new ArrayList();
	}


	public void addActionInstance(ActionInstance ai) {
		steps.add(ai);
		stepsMap.put(new Integer(ai.getStepID()), ai);
	}

	public List getActionInstances() {
		return this.steps;
	}

	public ActionInstance getActionInstanceAt(int i) {
		return (ActionInstance) steps.get(i);
	}

	public ActionInstance getStep(int id) {
		return (ActionInstance) stepsMap.get(new Integer(id));
	}

	public int getOrderOfStepID(int id) {
		ActionInstance ai = (ActionInstance) stepsMap.get(new Integer(id));
		return steps.indexOf(ai);
	}

	/**
	returns a plan consisting of actioninstances with knowledge effects
	or null if the plan does not contain such actioninstances
	*/
	public Plan getSensingSubplan() {
		Plan result = null;

		for(Iterator iter = steps.iterator(); iter.hasNext(); ) {
			ActionInstance ai = (ActionInstance) iter.next();
			if(ai.getOperation() == null) continue; // initial or goal
			if(ai.getOpDef().isSensingAction()) {
				if(result == null) { result = new Plan(); }
				result.addActionInstance(ai);
			}
		}

		return result;
	}

	public int size() {
		return steps.size();
	}


	public void addCausalLink(CausalLink cl) {
		causalLinks.add(cl);
		from.put(new Integer(cl.getFrom().getStepID()), cl);
		to.put(new Integer(cl.getTo().getStepID()), cl);
	}


	public void addOrdering(int before, int after) {
		int[] ordering = new int[2];
		ordering[0] = before;
		ordering[1] = after;
		orderings.add(ordering);
	}

	public boolean isConsistentWithOrderings() {
		// check all steps
		for(int i=0; i<steps.size(); i++) {
			int step = ((ActionInstance) steps.get(i)).getStepID();
			System.out.println("testing pos "+i+" i.e. step id "+step);

			// test each step against all orderings
			for(Iterator iter=orderings.iterator(); iter.hasNext(); ) {
				int[] ordering = (int[]) iter.next();
				// current step is required to come after some other step
				if(ordering[1] == step) {
					int preceedingPos = getOrderOfStepID(ordering[0]);
					if(preceedingPos > i) {
						System.out.println("violation of ordering detected!");
						return false;
					}
				}
			}
		}
		System.out.println("serialisiation consistent!");
		return true;
	}


	/**
	pos ... the operation about to be executed
	*/
	public boolean hasCausalLinkViolation(PlanExecution pe, int pos, ExecEnvironment env, Set violations, Map bindings) {

		Plan p = pe.getPlan();
		ActionInstance ai = p.getActionInstanceAt(pos);
		int stepID = ai.getStepID();

	  boolean violationFound = false;

		for(Iterator iter = p.causalLinks.iterator(); iter.hasNext(); ) {
			CausalLink cl = (CausalLink) iter.next();
			log.info("CHECKING CAUSAL LINK: "+cl.toString());

			ActionInstance clFrom = cl.getFrom();
			int clFromPos = p.getOrderOfStepID(clFrom.getStepID());

			ActionInstance clTo = cl.getTo();
			int clToPos = p.getOrderOfStepID(clTo.getStepID());

			Literal lit = cl.getCondition();

			// A *relevant* causal link
			if(clFromPos < pos && clToPos >= pos) {
				log.info("this link is relevant");

				Literal litToTest = null;

				List unboundVars = lit.getFreeVariables();

				// a ground literal can be tested immediately
				if(unboundVars.size() == 0) {
					litToTest = lit;
				} else {

					// for each unbound variable we see if we can narrow it to
					// binding constraint
					litToTest = lit;
					for(Iterator iter2 = unboundVars.iterator(); iter2.hasNext(); ) {
						Variable v = (Variable) iter2.next();
						String varName = v.getString();

						BindingConstraint bc = p.getBindingConstraintFor(varName, stepID);
						// check preceeding operation, we need value binding from there
						// unclear - multiple values => permutations??

						if(bc != null) {
							String value = getBindingValueFromPreceedingSteps(varName, bc, pe, pos);

							log.debug("ValueFromPreceedingSteps for "+varName+" is: "+value);

							// replace this variable by the identified binding
							if(value != null) {
								Map mBd = new HashMap();
								mBd.put(v.getString(), value);
								litToTest = (Literal) litToTest.cloneAndSubstitute(mBd);

								bindings.put(v.getString(), value);
							}
						}
					}

				}


				// now the literal is ground and can be tested against the KB
				log.info("Performing reality check");
				if(!env.contains(litToTest)) {
					violationFound = true;
					log.info("causal link - violation found: "+cl.toString());
					violations.add(cl);
				} else {
					log.info("link okay, lit in FB");
				}


			} else {
				log.info("this link is currently NOT relevant");
			}

		}

		return violationFound;
	}

	public void setBindingConstraints(List bindingConstraints) {
		this.bindingConstraints = bindingConstraints;
	}

	public List getBindingConstraints() {
		return bindingConstraints;
	}

	/**
	search bc where {stepVars} contain var(id)
	*/
	public BindingConstraint getBindingConstraintFor(String var, int stepId) {
		for(Iterator iter = bindingConstraints.iterator(); iter.hasNext(); ) {
			BindingConstraint bc = (BindingConstraint) iter.next();

			if(bc.containsStepVar(var, stepId))
				return bc;
		}

		return null;
	}

	/**
	search binding value from preceeding steps
	*/
	protected String getBindingValueFromPreceedingSteps(String var, BindingConstraint bc, PlanExecution pe, int pos) {
		Plan plan = pe.getPlan();

		// search until we find a bound stepVar of
		for(Iterator iter=bc.getStepVars().iterator(); iter.hasNext(); ) {
			StepVar vs = (StepVar) iter.next();
			int stepID = vs.getStepID();
			int oPos = plan.getOrderOfStepID(stepID);

			// if a varStep involving a PREceeding step was found
			if(oPos < pos) {
				Map stepData = pe.getStepDataForStepID(stepID);
				Set s = (Set) stepData.get(var);
				if(s==null) continue;

				return (String) s.iterator().next();
			}
		}

		log.error("this should not be reached"); // TODO - explain precisely WHY
		return null;
	}


	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("** plan: **\n");

		for(Iterator iter=steps.iterator(); iter.hasNext(); ) {
			buf.append(iter.next().toString());
			buf.append("\n");
		}
		buf.append("\n* clausal links: *\n");
		for(Iterator iter=causalLinks.iterator(); iter.hasNext(); ) {
			buf.append(iter.next().toString());
			buf.append("\n");
		}
		buf.append("\n* binding constraints: *\n");
		for(Iterator iter=bindingConstraints.iterator(); iter.hasNext(); ) {
			buf.append(iter.next().toString());
			buf.append("\n");
		}

		buf.append("** end of plan data. **");
		return buf.toString();
	}
}
