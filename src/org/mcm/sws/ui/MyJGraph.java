/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/ui/MyJGraph.java,v 1.2 2004/12/01 16:14:52 joepeer Exp $
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

import java.util.*;

import java.awt.*;
import javax.swing.*;

import org.jgraph.*;
import org.jgraph.graph.*;
import org.jgraph.pad.*;

/**
 *  A JGraph model that captures plans consisting of web service operations.
 *
 * @author    Joachim Peer
 */
public class MyJGraph extends JGraph {

	public MyJGraph(GraphModel model) {
		super(model);
	}


	protected VertexView createVertexView(Object v, CellMapper cm) {

    // Return an EllipseView for EllipseCells
    if (v instanceof EllipseCell)
      return new EllipseView(v, this, cm);
    // Else Call Superclass
    return super.createVertexView(v, cm);
  }

	public static Map createBounds(int x, int y, int w, int h, Color c) {
		Map map = GraphConstants.createMap();
		GraphConstants.setBounds(map, new Rectangle(x, y, w, h));
		GraphConstants.setBorder(map, BorderFactory.createRaisedBevelBorder());
		GraphConstants.setBackground(map, c.darker());
		GraphConstants.setForeground(map, Color.white);
		GraphConstants.setFont(map, GraphConstants.defaultFont.deriveFont(Font.BOLD, 12));
		GraphConstants.setOpaque(map, true);
		return map;
	}


}
