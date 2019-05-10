/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/pddl/Requirements.java,v 1.2 2004/12/01 16:14:51 joepeer Exp $
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

/**
 * This class stores a unique set of PDDL-style requirements, which might
 * be <i>required</i> by a certain problem or which may be <i>provided</i> by a
 * certain planner.
 *
 * @author    Joachim Peer
 */

public class Requirements {

  /* Required action style. */
  public boolean strips;
  /* Whether support for types is required. */
  public boolean typing;
  /* Whether support for negative preconditions is required. */
  public boolean negativePreconditions;
  /* Whether support for disjunctive preconditions is required. */
  public boolean disjunctivePreconditions;
  /* Whether support for equality predicate is required. */
  public boolean equality;
  /* Whether support for existentially quantified preconditions is
     required. */
  public boolean existentialPreconditions;
  /* Whether support for universally quantified preconditions is
     required. */
  public boolean universalPreconditions;
  /* Whether support for conditional effects is required. */
  public boolean conditionalEffects;
  /* Whether support for durative actions is required. */
  public boolean durativeActions;
  /* Whether support for duration inequalities is required. */
  public boolean durationInequalities;

  /* Enables quantified preconditions. */
  public void enableQuantifiedPreconditions() {
    existentialPreconditions = true;
    universalPreconditions = true;
  }

  /* Enables ADL style actions. */
  void enableADL() {
    strips = true;
    typing = true;
    negativePreconditions = true;
    disjunctivePreconditions = true;
    equality = true;
    enableQuantifiedPreconditions();
    conditionalEffects = true;
  }

	public Requirements merge(Requirements r) {
		this.strips |= r.strips;
		this.typing |= r.typing;
		this.negativePreconditions |= r.negativePreconditions;
		this.disjunctivePreconditions |= r.disjunctivePreconditions;
		this.equality |= r.equality;
		this.existentialPreconditions |= r.existentialPreconditions;
		this.universalPreconditions |= r.universalPreconditions;
		this.conditionalEffects |= r.conditionalEffects;
		this.durativeActions |= r.durativeActions;
		this.durationInequalities |= r.durationInequalities;
		return this;
	}

	public String toPDDL() {
		StringBuffer buf = new StringBuffer();
		buf.append("  (:requirements");
		if(strips) buf.append(" :strips");
		if(typing) buf.append(" :typing");
		if(negativePreconditions) buf.append(" :negativePreconditions");
		if(disjunctivePreconditions) buf.append(" :disjunctivePreconditions");
		if(equality) buf.append(" :equality");
		if(existentialPreconditions) buf.append(" :existentialPreconditions");
		if(universalPreconditions) buf.append(" :universalPreconditions");
		if(conditionalEffects) buf.append(" :conditionalEffects");
		if(durativeActions) buf.append(" :durativeActions");
		if(durationInequalities) buf.append(" :durationInequalities");
		buf.append(")\n");
		return buf.toString();
	}

}
