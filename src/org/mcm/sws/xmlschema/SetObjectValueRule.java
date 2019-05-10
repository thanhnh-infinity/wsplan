/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/xmlschema/SetObjectValueRule.java,v 1.2 2004/12/01 16:14:54 joepeer Exp $
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


import org.apache.commons.digester.*;
import java.beans.PropertyDescriptor;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.PropertyUtils;
import org.xml.sax.Attributes;

public class SetObjectValueRule extends Rule {


    // ----------------------------------------------------------- Constructors
   public SetObjectValueRule(Digester digester, String methodName, Object value) {
        this(methodName, value);
    }

    public SetObjectValueRule(String methodName, Object value) {
        this.methodName = methodName;
        this.value = value;
    }

    // ----------------------------------------------------- Instance Variables

    protected String methodName = null;
    protected Object value = null;

    // --------------------------------------------------------- Public Methods


    /**
     * Process the beginning of this element.
     */
//    public void begin(Attributes attributes) throws Exception {
    public void begin(String namespace, String name, Attributes attributes)
        throws Exception {

        // Get a reference to the top object
        Object top = digester.peek();

				MethodUtils.invokeMethod(top, methodName, value);
    }


    /**
     * Render a printable version of this Rule.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer("SetObjectValue[");
        sb.append("methodName=");
        sb.append(methodName);
        sb.append("]");
        return (sb.toString());
    }

}
