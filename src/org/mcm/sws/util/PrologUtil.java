/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/util/PrologUtil.java,v 1.2 2004/12/01 16:14:53 joepeer Exp $
 * $Date: 2004/12/01 16:14:53 $
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

package org.mcm.sws.util;

import com.declarativa.interprolog.*;
import com.xsb.interprolog.*;

import java.util.*;
import java.io.*;
import org.mcm.sws.pddl.*;

public class PrologUtil {

	public static synchronized Object[] executeQuery(String query, List vars, String program) throws IOException {

  	PrologEngine engine = new NativeEngine("d:\\XSB\\config\\x86-pc-windows\\bin");

		File file = new File("d:\\temp\\pr.P");

		Files.writeToFile(file, program);


		engine.consultAbsolute(file);

		StringBuffer raBuf = new StringBuffer();
		raBuf.append("[");
		for(Iterator iter=vars.iterator(); iter.hasNext(); ) {
			raBuf.append("string(");
			Variable var = (Variable) iter.next();
			raBuf.append(var.getProlog());
			raBuf.append(")");
			if(iter.hasNext())
				raBuf.append(",");
		}
		raBuf.append("]");

		Object[] bindings = engine.deterministicGoal(
    	query,
			null,
			null,
			raBuf.toString());

		System.out.println("Prolog Result:");
		int i=0;
		for(Iterator iter=vars.iterator(); iter.hasNext(); i++) {
			Variable var = (Variable) iter.next();
			System.out.println(var.getString()+"="+bindings[i]);
		}

		return bindings;
	}
}
