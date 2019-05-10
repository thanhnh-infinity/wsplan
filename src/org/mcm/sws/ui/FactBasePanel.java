/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/ui/FactBasePanel.java,v 1.2 2004/12/01 16:14:52 joepeer Exp $
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
import java.util.*;

import chris.eclipse.core.views.ViewPanel;

import org.mcm.sws.util.Config;
import org.mcm.sws.pddl.*;
import org.mcm.sws.ExecEnvironment;

/**
 *  A panel for displaying the fact base.
 *
 * @author    Joachim Peer
 */
public class FactBasePanel
	extends ViewPanel
	implements ActionListener {

	DefaultListModel facts;
	JButton  button_add, button_remove;
	JTextField tf_newFact;
	JList l_facts;

	public FactBasePanel() {
		super("User Agent Fact Base");

		facts = new DefaultListModel();

		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());

		p.add(new JLabel("Known facts:"), BorderLayout.NORTH);

	  l_facts = new JList(facts);
		p.add(new JScrollPane(l_facts), BorderLayout.CENTER);

		JPanel inputBar = new JPanel();
		inputBar.setLayout(new BorderLayout());

		tf_newFact = new JTextField();
		inputBar.add(tf_newFact, BorderLayout.CENTER);

		Box buttonBar = Box.createHorizontalBox();
		button_add = new JButton("Add fact");
		button_add.addActionListener(this);
		buttonBar.add(button_add);
		button_remove = new JButton("Remove fact");
		button_remove.addActionListener(this);
		buttonBar.add(button_remove);
		inputBar.add(buttonBar, BorderLayout.SOUTH);

		p.add(inputBar, BorderLayout.SOUTH);

		add(p);
	}

	/**
	used to bring list in sync with factbase
	(assumes a list of predicates)
	*/
	public void init(java.util.List l) {
		this.facts.clear();
		for(Iterator iter = l.iterator(); iter.hasNext(); ) {
			Atom a = (Atom) iter.next();
			facts.addElement(a.toString());
		}
		l_facts.repaint();
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == button_add) {
			String factToAdd = tf_newFact.getText();
			Map namespaces = Config.getInstance().getNamespaces();
			Atom a = Predicate.parseInstance(factToAdd, namespaces);
			ExecEnvironment.getInstance().addToFactBase(a);
			facts.addElement(a.toString());
		} else if(e.getSource() == button_remove) {
			int selIndex = l_facts.getSelectedIndex();
			if(selIndex != -1) {
				facts.removeElementAt(selIndex);
				ExecEnvironment.getInstance().getFactBase().remove(selIndex);
			}
		}
	}

}
