/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/pddl/Predicate.java,v 1.2 2004/12/01 16:14:51 joepeer Exp $
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

import java.util.*;


import org.apache.log4j.Logger;

import org.mcm.sws.URIIdentifiable;
import org.mcm.sws.Registry;
import org.mcm.sws.util.StrUtils;

/**
 * A predicate symbol. Each predicate symbol represents a predicate, i.e.
 * a mapping from a domain Dn to the set of truth values {true, false}, where
 * n &gt; 0;
 *
 * @author    Joachim Peer
 */
public class Predicate extends RelationDefinition {
	private static Logger log = Logger.getLogger(Predicate.class);

	/**
	 *  Gets the definition attribute of the Predicate object
	 *
	 * @return    The definition value
	 */
	public String getPDDL() {
		StringBuffer buf = new StringBuffer();
		buf.append("(");
		buf.append(surfaceName);
		buf.append("_");
		buf.append(Registry.getInstance().getIntCode(Registry.RELATION_DEF, getURI()));

		for (int i = 0; i < arguments.size(); i++) {
			buf.append(" ");
			buf.append("?var" + (i + 1));
			//buf.append( ((Term) terms.get(i)).toString() );

			// todo: add TYPE info if needed
		}

		buf.append(")");
		return buf.toString();
	}

/*
	public static Atom parseInstance(String s, String nsPrefix, String nsUri) {
		HashMap m = new HashMap();
		m.put(nsPrefix, nsUri);
		return parseInstance(s, m);
	}
*/
	// e.g (foo bar bar)
	// FIXME: currently parses only FACTS (i.e. ignores variableS)
	/**
	 *  Description of the Method
	 *
	 * @param  s  Description of the Parameter
	 * @return    Description of the Return Value
	 */
	public static Atom parseInstance(String s, Map namespaces) {
		Atom atom = new Atom();

		StringTokenizer st = new StringTokenizer(s.trim(), "() =");
		String _uri;
		if (!st.hasMoreTokens()) throw new RuntimeException("unexpected end of string");

		_uri = st.nextToken(); // may be a full uri or some abbreviation, eg. foo_343434
		_uri = StrUtils.expandNS(_uri, namespaces);

		Predicate pdef = (Predicate) Registry.getInstance().getObject(Registry.RELATION_DEF, _uri);
		// set def
		atom.setPredicate(pdef);

		int argCnt = pdef.getArguments().size();
		if(argCnt > (st.countTokens() / 2))
			throw new IllegalArgumentException("wrong number of arguments for predicate: "+pdef);

		List alreadyHandled = new ArrayList();
		for(int i = 0; i < argCnt; i++) {

				String propName = st.nextToken();
				String value = st.nextToken();

				if(!alreadyHandled.contains(propName)) {
					if(pdef.arguments.get(propName) != null) {
						Name n = new Name();
						n.setString(value);
						atom.addTerm(propName, n);
					} else throw new IllegalArgumentException("property '"+propName+"' not recognized");
				} else throw new IllegalArgumentException("double property:"+propName);

		}
		return atom;
	}

	public boolean equals(Object o) {
		if (o == null || !(o instanceof Predicate)) {
			return false;
		}
		Predicate p = (Predicate) o;

		return this.getURI().equals(p.getURI());
	}

}

