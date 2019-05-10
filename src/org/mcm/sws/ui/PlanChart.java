/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/ui/PlanChart.java,v 1.2 2004/12/01 16:14:52 joepeer Exp $
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

import java.awt.Color;
import javax.swing.*;
import java.util.*;

import org.jgraph.*;
import org.jgraph.graph.*;
import org.jgraph.pad.*;

import org.mcm.sws.plan.*;
import org.mcm.sws.pddl.*;

/**
 * A graph model for visually representing plans.
 *
 * @author    Joachim Peer
 */
public class PlanChart {

	public static final String KEY_DATA = "sws_datakey";

	private static GraphModel createGraphModel(Plan plan, WSPlanUI callback) {
		DefaultGraphModel model = new DefaultGraphModel();

		if(plan == null) {
		ConnectionSet cs = new ConnectionSet();
			//Map attributes = new Hashtable();
			Map attributes = new Hashtable();

			DefaultGraphCell msgCell = new DefaultGraphCell("NO PLAN FOUND");
			msgCell.add(new DefaultPort("User Agent/Center"));

			DefaultGraphCell clls[] = new DefaultGraphCell[1];
			clls[0] = msgCell;

			attributes.put(msgCell, MyJGraph.createBounds(110, 1 + 50, 100, 30, Color.red));


			model.insert(clls, attributes, cs, null, null);
			return model;
		}



		ConnectionSet cs = new ConnectionSet();
		Map attributes = new Hashtable();


		// Styles For Implement/Extend/Aggregation
		Map implementStyle = GraphConstants.createMap();
		GraphConstants.setLineBegin(
			implementStyle,
			GraphConstants.ARROW_CLASSIC);
		GraphConstants.setBeginSize(implementStyle, 10);
		GraphConstants.setDashPattern(implementStyle, new float[] { 3, 3 });
		GraphConstants.setFont(implementStyle, GraphConstants.defaultFont.deriveFont(10));

		// Styles For Implement/Extend/Aggregation
		Map callStyle = GraphConstants.createMap();
		GraphConstants.setLineBegin(
			callStyle,
			GraphConstants.ARROW_TECHNICAL);
		GraphConstants.setBeginSize(callStyle, 10);
		//GraphConstants.setDashPattern(callStyle, new float[] { 3, 3 });
		//GraphConstants.setFont(implementStyle, GraphConstants.defaultFont.deriveFont(10));


		//
		// The Swing MVC Pattern
		//

		List ais = plan.getActionInstances();

		ArrayList cells = new ArrayList();
		ArrayList edges = new ArrayList();
		ArrayList eclipseCells = new ArrayList();

		int pos = 0;
		//for(Iterator iter = ais.iterator(); iter.hasNext(); ) {
		for(int i=1; i<ais.size()-1; i++) {
			//ActionInstance ai = (ActionInstance) iter.next();
			ActionInstance ai = (ActionInstance) ais.get(i);
			System.out.println("ai:"+ai);
			//caller (user agent)
			DefaultGraphCell userCell = new DefaultGraphCell("User Agent");
			attributes.put(userCell, MyJGraph.createBounds(110, 1 + (pos*140), 100, 30, Color.green));
			userCell.add(new DefaultPort("User Agent/Center"));
			userCell.getAttributes().put(KEY_DATA, "user agent (this program)");
			cells.add(userCell);

			//action instance (service)
			String caption  = ai.getOperation().getURI();
			DefaultGraphCell gm = new DefaultGraphCell(caption);
			attributes.put(gm, MyJGraph.createBounds(110, 75 + (pos*140), 100, 30, Color.blue));
			gm.add(new DefaultPort());
			gm.getAttributes().put(KEY_DATA, ai);
			cells.add(gm);

			// precond of ai
			EllipseCell gmUp = new EllipseCell();
			attributes.put(gmUp, MyJGraph.createBounds(10, 80 + (pos*140), 20, 20, Color.red));
			gmUp.add(new DefaultPort());

			Object o = ai.getOpDef().getPreconditionObj();
			if(o != null) {
				gmUp.getAttributes().put(KEY_DATA, ((Formula) o).toString());
			}
			eclipseCells.add(gmUp);

			// effect of ai
			EllipseCell gmDown = new EllipseCell();
			attributes.put(gmDown, MyJGraph.createBounds(290, 80 + (pos*140), 20, 20 , Color.red));
			gmDown.add(new DefaultPort());
			/*if(ai.getOpDef().isSensingAction()) {
				gmDown.getAttributes().put(KEY_DATA, "knowledge-effect: TODO");
			} else {
				gmDown.getAttributes().put(KEY_DATA, "effect: TODO");
			}*/

			gmDown.getAttributes().put(KEY_DATA, "Result(s): "+ai.getOpDef().getResultsText());

			eclipseCells.add(gmDown);

			DefaultEdge dgeIn = new DefaultEdge("precond.");
			cs.connect(dgeIn,
								gm.getChildAt(0),
								gmUp.getChildAt(0));
			attributes.put(dgeIn, implementStyle);
			edges.add(dgeIn);

			DefaultEdge dgeOut = new DefaultEdge("effect");
			cs.connect(dgeOut,
								gmDown.getChildAt(0),
								gm.getChildAt(0));
			attributes.put(dgeOut, implementStyle);
			edges.add(dgeOut);

			pos++;
		}

		DefaultGraphCell userCell2 = new DefaultGraphCell("User Agent");
		attributes.put(userCell2, MyJGraph.createBounds(110, 10 + (pos*140), 100, 30, Color.green));
		userCell2.getAttributes().put(KEY_DATA, "user agent (this program)");
		userCell2.add(new DefaultPort("User Agent/Center"));
		cells.add(userCell2);


		for(int i = 0; i<cells.size()-1; i++) {

			DefaultGraphCell c1 = (DefaultGraphCell) cells.get(i);
			DefaultGraphCell c2 = (DefaultGraphCell) cells.get(i+1);

			int aipos = 1 + i / 2;
			ActionInstance ai = (ActionInstance) ais.get(aipos);
			if(ai.getOperation()==null) continue;

			String caption = null;

			if(i % 2 == 0)
					caption = ai.getOperation().getInput();
			else
					caption = ai.getOperation().getOutput();

			DefaultEdge dgeUserInput = new DefaultEdge(caption);
			cs.connect(dgeUserInput,
								c2.getChildAt(0),
								c1.getChildAt(0));
			attributes.put(dgeUserInput, callStyle);

			Object o;
			if(i % 2 == 0) {
				o = ai.getOperation().getInputMsg();
				if(o != null)
					dgeUserInput.getAttributes().put(KEY_DATA, o);
			} else {
				o = ai.getOperation().getOutputMsg();
				if(o != null)
					dgeUserInput.getAttributes().put(KEY_DATA, o);
			}

			edges.add(dgeUserInput);

		}

		cells.addAll(eclipseCells);
		cells.addAll(edges);


		model.insert(cells.toArray(), attributes, cs, null, null);

		return model;
	}

	public static JScrollPane createPlanChart(Plan p, WSPlanUI callback) {

		//JFrame frame = new JFrame("Proposed Plan");
		//frame.setSize(380,400);

	 	JGraph graph = new MyJGraph(createGraphModel(p, callback));
		graph.setPortsVisible(true);
		graph.addGraphSelectionListener(callback);
		JScrollPane graphLayoutCache = new JScrollPane(graph);

		//frame.getContentPane().add(graphLayoutCache);

		//frame.show();
		return graphLayoutCache;
	}


}
