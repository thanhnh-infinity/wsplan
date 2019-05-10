/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/ui/GoalPanel.java,v 1.2 2004/12/01 16:14:52 joepeer Exp $
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
import java.awt.event.*;

import chris.eclipse.core.views.ViewPanel;

import org.mcm.sws.WSPlan;
import org.mcm.sws.util.Config;

/**
 *  A panel for entering and displaying the declarative goal specification.
 *
 * @author    Joachim Peer
 */
public class GoalPanel
	extends ViewPanel
	implements ActionListener {

	JTextArea textArea;
	JButton button_start;
	WSPlanGUI parent;

	public GoalPanel(WSPlanGUI parent) {
		super("Goal");

		this.parent = parent;

		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());

		textArea = new JTextArea(20, 6);
		JScrollPane scrollPane =
				new JScrollPane(textArea,
												JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
												JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		textArea.setEditable(true);
		textArea.setWrapStyleWord(true);
		textArea.setLineWrap(true);


		p.add(scrollPane, BorderLayout.CENTER);
		button_start = new JButton("find a plan!");
		button_start.addActionListener(this);
		p.add(button_start, BorderLayout.SOUTH);

		add(p);
	}

	public void setGoal(String goal) {
		this.textArea.setText(goal);
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == button_start) {
			try {
				Config.getInstance().getPlanningStrategy().startPlanning(parent, textArea.getText());
			} catch (org.mcm.sws.PlanningException pe) {
				pe.printStackTrace();
			}
		}
	}

}
