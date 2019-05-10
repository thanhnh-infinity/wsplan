/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/WSPlan.java,v 1.2 2004/12/01 16:14:51 joepeer Exp $
 * $Date: 2004/12/01 16:14:51 $
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

package org.mcm.sws;

import java.util.*;
import java.io.*;


import org.apache.log4j.Logger;

import org.mcm.sws.util.*;
import org.mcm.sws.pddl.*;
import org.mcm.sws.ui.*;
import org.mcm.sws.strategy.*;
import org.mcm.sws.planners.*;


/**
 *  This class privodes the point of entry for both GUI and NON-GUI based mode.
 *
 * @author    Joachim Peer
 */
public class WSPlan {

	private static Logger log = Logger.getLogger(WSPlan.class);

	/**
	 *  The main routine for WSPlan
	 *
	 * @param  args  The command line arguments
	 */
	public static void main(String[] args) {
		WSPlan wsplan = new WSPlan(args);
	}


	/**
	 *Constructor for the WSPlan object
	 *
	 * @param  args  command line options
	 */
	public WSPlan(String[] args) {

		boolean has_gui = true;
		String problemSpecLocation = null;
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
							case 'n':
								has_gui = false;
								System.out.println("-- nogui option detected");

							case 'h':
								printHelp();
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
				printHelp();
				System.exit(-1);
			}
		}

		// initialise the helper classes
		Registry.createInstance(config);
		ExecEnvironment.createInstance(config);
		Registry.getInstance().printContents(System.out);

		// load the GUI, and eventually start planning if non-GUI mode and if goal given.
		if (has_gui) {
		//	WSPlanGUI gui = new WSPlanGUI(this);
		} else {
			String goal = config.getGoal();
			if (goal != null) {
				try {
					Config.getInstance().getPlanningStrategy().startPlanning(null, goal);
				} catch (PlanningException pe) {
					pe.printStackTrace();
				}
			} else {
				System.out.println("no goal specified");
			}
		}

	}



	/**
	 *  prints out usage instructions to stdout.
	 */
	private static void printHelp() {
		System.out.println("USAGE: [-c=<configfile>] [-n] [-h]\n");
		System.out.println("-c=<configfile> ... specifies path to WSPlan XML config file");
		System.out.println("                    defaults to ./config.xml if not specified");
		System.out.println("-n=................ NoGUI option, WSPlan runs in Console mode");
		System.out.println("-h=................ prints this info\n");
	}
}

