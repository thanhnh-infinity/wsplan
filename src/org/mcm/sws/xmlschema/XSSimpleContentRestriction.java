/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/xmlschema/XSSimpleContentRestriction.java,v 1.2 2004/12/01 16:14:54 joepeer Exp $
 * $Date: 2004/12/01 16:14:54 $
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

package org.mcm.sws.xmlschema;

import java.util.*;
import java.io.*;
import org.mcm.sws.xmlschema.facets.*;

public class XSSimpleContentRestriction extends XSDerivedContent {

	protected XSSimpleType simpleType;

	protected XSLengthFacet lengthFacet;
	protected XSMinLengthFacet minLengthFacet;
	protected XSMaxLengthFacet maxLengthFacet;
	protected XSPatternFacet patternFacet;
	protected XSWhiteSpaceFacet whiteSpaceFacet;
	protected XSMaxInclusiveFacet maxInclusiveFacet;
	protected XSMaxExclusiveFacet maxExclusiveFacet;
	protected XSMinInclusiveFacet minInclusiveFacet;
	protected XSMinExclusiveFacet minExclusiveFacet;
	protected XSTotalDigitsFacet totalDigitsFacet;
	protected XSFractionDigitsFacet fractionDigitsFacet;
	protected ArrayList enumerationFacets;

	public XSSimpleContentRestriction() {
		enumerationFacets = new ArrayList();
	}


	public void addLengthFacted(XSLengthFacet facet) {
		this.lengthFacet = facet;
	}

	public void setSimpleType(XSSimpleType simpleType) {
		this.simpleType = simpleType;
	}

	public XSSimpleType getSimpleType() {
		return simpleType;
	}

	public void addType(XSSimpleType simpleType) {
		this.simpleType = simpleType;
	}

	public XSSimpleType getType() {
		return simpleType;
	}

	// -- facets

	public void addLengthFacet(XSLengthFacet lengthFacet) {
		this.lengthFacet = lengthFacet;
	}

	protected XSLengthFacet getLengthFacet() {
		return lengthFacet;
	}

	public void addMinLengthFacet(XSMinLengthFacet minLengthFacet) {
		this.minLengthFacet = minLengthFacet;
	}

	public XSMinLengthFacet getMinLengthFacet() {
		return minLengthFacet;
	}

	public void addMaxLengthFacet(XSMaxLengthFacet maxLengthFacet) {
		this.maxLengthFacet=maxLengthFacet;
	}

	public XSMaxLengthFacet getMaxLengthFacet() {
		return maxLengthFacet;
	}

	public void addPatternFacet(XSPatternFacet patternFacet) {
		this.patternFacet=patternFacet;
	}

	public XSPatternFacet getPatternFacet() {
		return patternFacet;
	}

	public void addWhiteSpaceFacet(XSWhiteSpaceFacet whiteSpaceFacet) {
		this.whiteSpaceFacet=whiteSpaceFacet;
	}

	public XSWhiteSpaceFacet getWhiteSpaceFacet() {
		return whiteSpaceFacet;
	}

	public void addMaxInclusiveFacet(XSMaxInclusiveFacet maxInclusiveFacet) {
		this.maxInclusiveFacet=maxInclusiveFacet;
	}

	public XSMaxInclusiveFacet getMaxInclusiveFacet() {
		return maxInclusiveFacet;
	}

	public void addMaxExclusiveFacet(XSMaxExclusiveFacet maxExclusiveFacet) {
		this.maxExclusiveFacet=maxExclusiveFacet;
	}

	public XSMaxExclusiveFacet getMaxExclusiveFacet() {
		return maxExclusiveFacet;
	}

	public void addMinInclusiveFacet(XSMinInclusiveFacet minInclusiveFacet) {
		this.minInclusiveFacet=minInclusiveFacet;
	}

	public XSMinInclusiveFacet getMinInclusiveFacet() {
		return minInclusiveFacet;
	}

	public void addMinExclusiveFacet(XSMinExclusiveFacet minExclusiveFacet) {
		this.minExclusiveFacet=minExclusiveFacet;
	}

	public XSMinExclusiveFacet getMinExclusiveFacet() {
		return minExclusiveFacet;
	}

	public void addTotalDigitsFacet(XSTotalDigitsFacet totalDigitsFacet) {
		this.totalDigitsFacet=totalDigitsFacet;
	}

	public XSTotalDigitsFacet getTotalDigitsFacet() {
		return totalDigitsFacet;
	}

	public void addFractionDigitsFacet(XSFractionDigitsFacet fractionDigitsFacet) {
		this.fractionDigitsFacet=fractionDigitsFacet;
	}

	public XSFractionDigitsFacet getFractionDigitsFacet() {
		return fractionDigitsFacet;
	}

	public void addEnumerationFacet(XSEnumerationFacet enumerationFacet) {
		this.enumerationFacets.add(enumerationFacet);
	}

	public List getEnumerationFacets() {
		return enumerationFacets;
	}

}
