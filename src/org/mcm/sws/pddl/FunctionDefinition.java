/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/pddl/FunctionDefinition.java,v 1.2 2004/12/01 16:14:51 joepeer Exp $
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
import org.mcm.sws.URIIdentifiable;
import org.mcm.sws.Registry;

/**
 * Function def.
 *
 * @author    Joachim Peer
 */
public class FunctionDefinition extends RelationDefinition {

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
		}

		buf.append(")");
		return buf.toString();
	}

	// e.g (foo bar bar)
	// FIXME: currently parses only FACTS (i.e. ignores variableS)
	/**
	 *  Description of the Method
	 *
	 * @param  s  Description of the Parameter
	 * @return    Description of the Return Value
	 */
	public static Function parseInstance(String s) {
		/*
		Function function = new Function();

		StringTokenizer st = new StringTokenizer(s.trim(), "() ");
		String _uri;
		if (st.hasMoreTokens()) {
			_uri = st.nextToken();
			if (_uri.indexOf("://") == -1) {
				_uri = "local://" + _uri;
			}
			// set url and make sure that surface label is produced, too
			function.setURI(_uri);
		}

		while (st.hasMoreTokens()) {
			Name n = new Name();
			n.setString(st.nextToken());
			pred.addTerm(n);
		}

		return pred;*/
		return null;
	}
}

