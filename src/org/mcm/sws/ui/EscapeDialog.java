/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/ui/EscapeDialog.java,v 1.2 2004/12/01 16:14:52 joepeer Exp $
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

/**
 *  A dialog that can be closed by hitting the 'escape' key
 *
 * @author    Joachim Peer
 */
public class EscapeDialog extends JDialog {
  public EscapeDialog() {
    this((Frame)null, false);
  }
  public EscapeDialog(Frame owner) {
    this(owner, false);
  }
  public EscapeDialog(Frame owner, boolean modal) {
    this(owner, null, modal);
  }
  public EscapeDialog(Frame owner, String title) {
    this(owner, title, false);
  }
  public EscapeDialog(Frame owner, String title, boolean modal) {
    super(owner, title, modal);
  }
  public EscapeDialog(Dialog owner) {
    this(owner, false);
  }
  public EscapeDialog(Dialog owner, boolean modal) {
    this(owner, null, modal);
  }
  public EscapeDialog(Dialog owner, String title) {
    this(owner, title, false);
  }
  public EscapeDialog(Dialog owner, String title, boolean modal) {
    super(owner, title, modal);
  }
  protected JRootPane createRootPane() {
    ActionListener actionListener = new ActionListener() {
      public void actionPerformed(ActionEvent actionEvent) {
        setVisible(false);
      }
    };
    JRootPane rootPane = new JRootPane();
    KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
    rootPane.registerKeyboardAction(actionListener, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
    return rootPane;
  }
}


