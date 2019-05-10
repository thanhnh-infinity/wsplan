/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/ui/NamespaceDialog.java,v 1.2 2004/12/01 16:14:52 joepeer Exp $
 * $Date: 2004/12/01 16:14:52 $
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

package org.mcm.sws.ui;

import javax.swing.*;
import javax.swing.table.*;

import java.util.*;
import java.awt.*;
import java.awt.event.*;

import org.mcm.sws.util.Config;

/**
 *  A dialog for displaying the (prefix,namespace) pairs
 *  currently present in the system.
 *
 * @author    Joachim Peer
 */
public class NamespaceDialog extends JDialog {
  public NamespaceDialog(Frame owner, String title, boolean modal) {
    super(owner, title, modal);
  }

  protected JRootPane createRootPane() {
		JRootPane rootPane = new JRootPane();
		rootPane.setLayout(new BorderLayout());

		String[] columnNames = {"Prefix", "URI"};
		Map m = Config.getInstance().getNamespaces();
		DefaultTableModel dtm = new DefaultTableModel(columnNames, m.size());

		for(Iterator iter=m.entrySet().iterator(); iter.hasNext(); ) {
			Map.Entry me = (Map.Entry) iter.next();
			Object[] data = new Object[2];
			data[0] = me.getKey();
			data[1] = me.getValue();
			dtm.addRow(data);
		}

		JTable table = new JTable(dtm);

		rootPane.add(table, BorderLayout.CENTER);

    return rootPane;
  }
}


