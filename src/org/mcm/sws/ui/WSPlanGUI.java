/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/ui/WSPlanGUI.java,v 1.2 2004/12/01 16:14:52 joepeer Exp $
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
import java.io.*;
import java.awt.*;
import java.awt.event.*;

import org.jgraph.graph.*;
import org.jgraph.event.*;

import org.mcm.sws.*;
import org.mcm.sws.util.*;

/**
 *  A user interface implementation of the WSPlan class
 *
 * @author    Joachim Peer
 */
public class WSPlanGUI
	extends JFrame
	implements WSPlanUI, ActionListener, GraphSelectionListener {

	GoalPanel p_goal;
	PlanChartPanel p_planCharts;
	FactBasePanel p_factBase;
	JMenuItem mi_save, mi_saveAs, mi_namespaces;

    // Possible Look & Feels
    private static final String mac      =
            "com.sun.java.swing.plaf.mac.MacLookAndFeel";
    private static final String metal    =
            "javax.swing.plaf.metal.MetalLookAndFeel";
    private static final String motif    =
            "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
    private static final String windows  =
            "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
    private static final String gtk  =
            "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";


	public WSPlanGUI(String[] args)  {

		loadConfiguration(args);

		try {
			UIManager.setLookAndFeel(windows);
		} catch(Exception cnfe) {}

		setTitle("WSPlan");

		// create menu
		JMenuBar menuBar;
		JMenu menu_file, menu_settings, menu_help;

		//Create the menu bar.
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		menu_file = new JMenu("File");

		mi_save = new JMenuItem("save config");
		mi_save.addActionListener(this);
		mi_save.setMnemonic(KeyEvent.VK_S);
		menu_file.add(mi_save);

		mi_saveAs = new JMenuItem("save config as...");
		mi_saveAs.addActionListener(this);
		mi_saveAs.setMnemonic(KeyEvent.VK_A);
		menu_file.add(mi_saveAs);

		menu_file.setMnemonic(KeyEvent.VK_F);
		//menu_file.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.ALT_MASK));
		menuBar.add(menu_file);

		menu_settings = new JMenu("Settings");
		menuBar.add(menu_settings);
		mi_namespaces = new JMenuItem("namespaces...");
		mi_namespaces.addActionListener(this);
		menu_settings.add(mi_namespaces);

		menu_help = new JMenu("(?)");
		menuBar.add(Box.createHorizontalGlue());
		menuBar.add(menu_help);


		Container cp = getContentPane();
		cp.setLayout(new BorderLayout(50,20));

		JComponent p_left = Box.createVerticalBox();
		p_goal = new GoalPanel(this);
		p_goal.setGoal(Config.getInstance().getGoal()); // init

		p_left.add(p_goal);

		p_factBase = new FactBasePanel();
		p_factBase.init(ExecEnvironment.getInstance().getFactBase()); // init
		p_left.add(p_factBase);

		JPanel p_center = new JPanel();
		p_center.setLayout(new BorderLayout(2, 5));
		p_planCharts = new PlanChartPanel();
		p_center.add(BorderLayout.CENTER, p_planCharts);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, p_left, p_center);
    splitPane.setContinuousLayout(true);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(200);
		cp.add(BorderLayout.CENTER, splitPane);

		setSize(600, 400);
		show();
	}

	protected void loadConfiguration(String[] args) {
		Config config = null;

		for (int i = 0; i < args.length; i++) {

			String arg = args[i];
			char line = arg.charAt(0);
			if (line != '-' || arg.length() < 3) {
				System.out.println("Skipping illegal argument '" + arg + "'");
				continue;
			}

			// check out command line options
			char c = arg.charAt(1);
			switch (c) {
							case 'c':
								try {
									config = Config.createInstance(arg.substring(3));
								} catch (Exception e) {
									e.printStackTrace();
								}
								break;

							case 'h':
								printHelp(System.out);
								System.exit(0);
								break;
							default:
								System.out.println("Ignoring unknown option/argument '" + arg + "'");
			}
		}

		if (config == null) {
			try {
				System.out.println("using default: config.xml");
				config = Config.createInstance("config.xml");
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (config == null) {
				System.out.println("Could not read config file");
				printHelp(System.out);
				System.exit(-1);
			}
		}

		// initialise the helper classes
		Registry.createInstance(config);
		ExecEnvironment.createInstance(config);
		Registry.getInstance().printContents(System.out);
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == mi_save) {
			Config.getInstance().writeToDisk();
		} else if(e.getSource() == mi_saveAs) {
			// ask user for filepath
      JFileChooser chooser = new JFileChooser();
			int returnVal = chooser.showOpenDialog(this);
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				File newLoc = chooser.getSelectedFile();
				Config config = Config.getInstance();
				config.setStorageLocation(newLoc.getAbsolutePath());
				config.writeToDisk();
			}

		} else if(e.getSource() == mi_namespaces) {
        NamespaceDialog dialog = new NamespaceDialog(this, "Namespaces", false);
				dialog.setSize(190,230);
        dialog.show();

		}
	}

	/**
	callback
	*/
	public void addPlanChart(JComponent c, String title) {
		p_planCharts.addPlanChart(c, title);
	}

	/**
	callback
	*/
	public void updateFactBaseView(java.util.List l) {
		p_factBase.init(l);
	}

	/**
	callback
	*/
	public void displayInfo(String infoText) {
		p_planCharts.displayInfo(infoText);
	}

	/**
	callback (GraphSelectionListener interface)
	*/
	public void valueChanged(GraphSelectionEvent e) {
		Object o = e.getCell();
		if(o != null && o instanceof GraphCell) {
			String s =((GraphCell) o).getAttributes().get(PlanChart.KEY_DATA).toString();
			if(s != null) {
				p_planCharts.displayInfo("Info: \n\n" + s);
			} else {
				p_planCharts.displayInfo("no info for this component");
			}
		} else {
			p_planCharts.displayInfo("no selection");
		}

	}

	private static void printHelp(PrintStream out) {
		out.println("USAGE: [-c=<configfile>] [-h]\n");
		out.println("-c=<configfile> ... specifies path to WSPlan XML config file");
		out.println("                    defaults to ./config.xml if not specified");
		out.println("-h=................ prints this info\n");
	}

	public static void main(String[] args) {
		new WSPlanGUI(args);
	}

}
