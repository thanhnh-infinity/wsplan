/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/ui/PlanChartPanel.java,v 1.2 2004/12/01 16:14:52 joepeer Exp $
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
import java.awt.*;

import chris.eclipse.core.views.ViewPanel;

/**
 *  The panel for displaying (plan-) graphmodels
 *
 * @author    Joachim Peer
 */
public class PlanChartPanel extends ViewPanel {

	protected JTabbedPane tabbedPane;
	protected JTextArea ta_info;

	public PlanChartPanel() {
		super("Web Service Execution Plans");

		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		add(p);

		tabbedPane = new JTabbedPane();
		//p.add(tabbedPane, BorderLayout.CENTER);

		ta_info = new JTextArea(20, 6);
		JScrollPane scrollPane =
				new JScrollPane(ta_info,
												JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
												JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		ta_info.setEditable(false);
		ta_info.setWrapStyleWord(true);
		ta_info.setLineWrap(true);

		//p.add(ta_info, BorderLayout.SOUTH);


		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                           tabbedPane, scrollPane);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(250);
		p.add(splitPane);
	}

	public void addPlanChart(JComponent c, String title) {
		tabbedPane.addTab(title, c);
		tabbedPane.setSelectedComponent(c);
	}

	public void displayInfo(String infoText) {
		ta_info.setText(infoText);
		System.out.println(infoText);
	}

}



