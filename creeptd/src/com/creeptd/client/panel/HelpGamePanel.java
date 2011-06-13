/**
CreepTD is an online multiplayer towerdefense game
formerly created under the name CreepSmash as a project
at the Hochschule fuer Technik Stuttgart (University of Applied Science)

CreepTD (Since version 0.7.0+) Copyright (C) 2011 by
 * Daniel Wirtz, virtunity media
http://www.creeptd.com

CreepSmash (Till version 0.6.0) Copyright (C) 2008 by
 * Andreas Wittig
 * Bernd Hietler
 * Christoph Fritz
 * Fabian Kessel
 * Levin Fritz
 * Nikolaj Langner
 * Philipp Schulte-Hubbert
 * Robert Rapczynski
 * Ron Trautsch
 * Sven Supper
http://creepsmash.sf.net/

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 **/
package com.creeptd.client.panel;

import com.creeptd.client.Core;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 * The HelpGamePanel gives a help for players. It describes how the game works.
 * It gives also informations about our team.
 * 
 * @Robert
 */
public class HelpGamePanel extends JDialog {

    /**
     * @param args
     */
    private static final long serialVersionUID = 4L;
    private JEditorPane htmlDisplay = new JEditorPane();
    private JScrollPane jScrollPaneHelp = new JScrollPane();
    private JPanel content = new JPanel();
    private JButton quit;
    private URL index;

    /**
     * Constructor for the HelpGamePanel.
     * @param owner
     * @param title
     */
    public HelpGamePanel() {
        this.init();
        this.setTitle("CreepTD - Help");
        this.index = getClass().getClassLoader().getResource("com/creeptd/client/resources/help/index.html");
    }

    /**
     * This method initializes htmlDisplay.
     *
     * @return htmlDisplay an EditorPane
     */
    private JEditorPane gethtmlDisplay() {

        htmlDisplay.setEditable(false);
        htmlDisplay.setCaretPosition(0);
        htmlDisplay.setContentType("text/html");
        htmlDisplay.setOpaque(false);

        return htmlDisplay;
    }

    /**
     * This method initializes jScrollPane.
     *
     * @return jScrollPaneHelp an JScrollPane
     */
    private JScrollPane getJScrollPaneHelp() {

        jScrollPaneHelp.setBounds(10, 10, 600, 600);
        jScrollPaneHelp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPaneHelp.setViewportView(gethtmlDisplay());
        jScrollPaneHelp.getViewport().setOpaque(false);
        jScrollPaneHelp.setOpaque(false);
        jScrollPaneHelp.setBorder(new EmptyBorder(0, 0, 0, 0));

        return jScrollPaneHelp;
    }

    /**
     * Initialize the Panel.
     *
     */
    private void init() {
        this.setLayout(null);
        this.setBackground(Color.BLACK);
        this.setResizable(false);
        this.setBounds(Core.getInstance().getCreatorX()+140, Core.getInstance().getCreatorY(), 620, 690);

        content.setLayout(null);
        content.setBounds(0, 0, 620, 690);
        content.setBackground(Color.BLACK);

        this.quit = new JButton("Exit Help");
        this.quit.setBackground(Color.BLACK);
        this.quit.setForeground(Color.GREEN);
        this.quit.setBounds(265, 620, 100, 30);
        this.quit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        try {
            htmlDisplay.setPage(this.index);
        } catch (IOException ex) {
            // ex.printStackTrace();
            // System.out.println("HTML-Seite konnte nicht geladen werden!");
        }

        htmlDisplay.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent e) {
                try {
                    if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                        htmlDisplay.setPage(e.getURL());
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    System.out.println("HTML-Seite konnte nicht geladen werden!");
                }
            }
        });

        content.add(getJScrollPaneHelp());
        content.add(quit);
        this.add(content);
    }
    // /**
    // * displays the screen.
    // */
    // @Override
    // public void start() {
    //
    //
    // }
    //
    // /**
    // * method for disappearing the screen.
    // */
    // @Override
    // public void end() {
    //
    // }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            try {
                htmlDisplay.setPage(this.index);
            } catch (IOException ex) {
                ex.printStackTrace();
                System.out.println("HTML-Seite konnte nicht geladen werden!");
            }
        }
    }
}

