/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/util/SOAPSender.java,v 1.2 2004/12/01 16:14:53 joepeer Exp $
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

import java.util.*;
import java.io.*;
import java.net.*;

import javax.xml.soap.*;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

import org.apache.log4j.Logger;

/**
 * A class for sending SOAP messages. It had become necessary due to a
 * limitation in the SAAJ (SOAP with Attachments API for Java) API, which
 * does not allow for low level control of HTTP headers.
 *
 * @author    Joachim Peer
 */

public class SOAPSender {
  private static Logger log = Logger.getLogger(SOAPSender.class);

	public static SOAPMessage call(InputStream msg, String urlEndpoint, String soapAction) throws IOException {
		try {
			MessageFactory mf = MessageFactory.newInstance();

			PostMethod post = new PostMethod(urlEndpoint);
			post.setRequestBody(msg);
			post.setRequestHeader("Content-type", "text/xml; charset=ISO-8859-1");
			//if(soapAction != null && soapAction.length()>0)
				post.setRequestHeader("SOAPAction", soapAction);

			HttpClient httpclient = new HttpClient();
			// Execute request
			try {
				int result = httpclient.executeMethod(post);
				// Display status code
				log.debug("Response status code: " + result);
				// Display response
				//System.out.println("Response body: ");
				//System.out.println(post.getResponseBodyAsString());

				MimeHeaders mh = new MimeHeaders();
				mh.addHeader("Content-type", "text/xml; charset=ISO-8859-1");

				return mf.createMessage(mh, post.getResponseBodyAsStream());
			} finally {
					// Release current connection to the connection pool once you are done
				post.releaseConnection();
			}

		} catch(Exception e) {
			throw new IOException(e.getMessage());
		}

	}

}
