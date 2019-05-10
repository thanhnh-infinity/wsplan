/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/util/StrUtils.java,v 1.2 2004/12/01 16:14:53 joepeer Exp $
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

import java.io.*;
import java.util.*;
import org.apache.commons.lang.StringUtils;

import org.apache.log4j.Logger;


/**
 * Utility functions for working with Strings
 *
 * @author    Joachim Peer
 */

public class StrUtils {
	private static Logger log = Logger.getLogger(StrUtils.class);

	public static String slashed(String s) {
		if(s.endsWith("/")) return s;
		else return s + "/";
	}

	/*
	this method makes sure that the returned string will be the string s
	but with leading/tailingslashes as specified by "begin", "end"
	*/
	public static String slashed(String s, boolean begin, boolean end) {
		int len = s.length();
		boolean hasAtBegin = (s.charAt(0) == '/');
		boolean hasAtEnd = (s.charAt(len-1) == '/');
		String core = s.substring((hasAtBegin) ? 1 : 0, (hasAtEnd) ? len-1 : len );

		StringBuffer buf = new StringBuffer();
		if(begin) buf.append('/');
		buf.append(core);
		if(end) buf.append('/');

		return buf.toString();
	}

	public static String capitalizeFirstChar(String s) {
		StringBuffer buf = new StringBuffer(s);
		char uc = Character.toUpperCase(s.charAt(0));
		buf.setCharAt(0, uc);
		return buf.toString();
	}

	public static void printList(List list, PrintStream out) {
		for(Iterator iter=list.iterator(); iter.hasNext(); ) {
			out.println("Element="+iter.next().toString());
		}
	}

	public static String printKeys(Map m) {
		StringBuffer buf = new StringBuffer();
		buf.append("{");
		int i=0;
		for(Iterator iter = m.keySet().iterator(); iter.hasNext(); i++) {
			if(i>0) buf.append(", ");
			buf.append(iter.next());
		}
		buf.append("}");
		return buf.toString();
	}

	public static boolean containsList(List target, List sub) {
		for(int i=0; i<sub.size(); i++) {
			String s1 = (String) sub.get(i);
			String s2 = (String) target.get(i);
			if(!s1.equals(s2)) return false;
		}
		return true;
	}

	public static boolean equalsList(List list1, List list2) {
		if(list1.size() != list2.size()) return false;
		for(int i=0; i<list1.size(); i++) {
			String s1 = (String) list1.get(i);
			String s2 = (String) list2.get(i);
			if(!s1.equals(s2)) return false;
		}
		return true;
	}


	public static boolean containsPath(String targetPath, String subPath) {
		List l1 = chopPath(targetPath);
		List l2 = chopPath(subPath);

		return containsList(l1, l2);
	}

	public static boolean equalsPath(String targetPath, String path) {
		List l1 = chopPath(targetPath);
		List l2 = chopPath(path);

		return equalsList(l1, l2);
	}


	public static String matchingValuePath(String currPath, Map values) {
		for(Iterator iter = values.keySet().iterator(); iter.hasNext(); ) {
			String path = (String) iter.next();
			if(equalsPath(currPath, path))
				return (String) values.get(path);
		}
		return null;
	}

	public static List chopPath(String path) {
		ArrayList result = new ArrayList();
		StringTokenizer st = new StringTokenizer(path, "/");
		while(st.hasMoreTokens()) {
			String t = st.nextToken();
			result.add(t);
		}
		return result;
	}

	/**
	 *  Description of the Method
	 *
	 * @param  s  Description of the Parameter
	 * @return    Description of the Return Value
	 */
	public static String encode(String s) {
		s = s.trim();

		if(s.length()==0) return s;

		char c = s.charAt(0);
		if(Character.isDigit(c)) s = "num_"+s;

		s = StringUtils.replace(s, "@", "_at_");
		s = StringUtils.replace(s, "#", "num_");
		s = StringUtils.replace(s, ".", "_dot_");
		s = StringUtils.replace(s, " ", "_space_");

		return s;
	}

	public static String decode(String s) {
		s = StringUtils.replace(s, "_at_", "@");
		s = StringUtils.replace(s, "num_", " ");
		s = StringUtils.replace(s, "_dot_", ".");
		s = s.trim();
		return s;
	}


	public static String expandNS(String s, Map nsmap) {

		int colonPos = s.indexOf(':');
		int protoPos = s.indexOf("://");

		if(protoPos != -1) { // an URI, nothing needs to be done
			return s;
		} else if(colonPos != -1) { // a prefixed string

			String prefix = s.substring(0, colonPos);
			String localName = s.substring(colonPos + 1);
			String ns = (String) nsmap.get(prefix);
			if(ns == null) {
				ns = "http://local.ch/";
				log.warn("warning - prefix '"+prefix+"' not found");
			}
			return ns + localName;

		} else { // a local name
			log.warn("warning - no namespace give for '"+s+"'");
			return "http://local.ch/" + s;
		}
	}

}
