/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/util/Files.java,v 1.2 2004/12/01 16:14:53 joepeer Exp $
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
import java.net.*;


import org.apache.log4j.Logger;

/**
 * Utilities for File/Stream handling.
 *
 * @author    Joachim Peer
 */

public class Files {
	private static Logger log = Logger.getLogger(Files.class);

	public static InputStream getInputStream(String s)
	throws IOException {
		URL url= null;
		try {
			url = new URL(s);
			return url.openStream();
		} catch (MalformedURLException ignore) {
		} // this may also throw an IOException

		return new FileInputStream(s);
	}

	public static String getFileContent(String f)
	throws IOException {
		InputStream is = getInputStream(f);
		return getFileContent(is);
	}

	public static String getFileContent(InputStream is)
	throws IOException {

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		byte[] buf = new byte[256]; int len;

		while( (len = is.read(buf,0,256)) != -1 ) {
			os.write(buf, 0, len);
		}

		System.out.println("end");

		is.close();

		return os.toString();
	}

	public static synchronized void writeToFile(File f, String content)
	throws IOException{
			FileWriter fw = new FileWriter(f);
			fw.write(content, 0, content.length());
			fw.close();
	}

}
