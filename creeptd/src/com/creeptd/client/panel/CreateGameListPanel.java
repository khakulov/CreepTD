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
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.creeptd.common.IConstants;

/**
 * The Create GameList Panel
 * 
 * @author Contex
 * 
 */
public class CreateGameListPanel extends JFrame {

    /**
     * @param args
     */
    private static final long serialVersionUID = 4L;
    private JScrollPane jScrollCreateGameListPanel = new JScrollPane();
    private JPanel content = new JPanel();
    private CreateGamePanel creategamePanel;

    public CreateGameListPanel(CreateGamePanel createGamePanel, String title) {
        super();
        this.setTitle(title);
        this.creategamePanel = createGamePanel;
        this.setIconImage(Core.getInstance().getIconImage());
        this.init();
    }

    /**
     * Initialize the Panel.
     *
     */
    private void init() {

        this.setBackground(Color.BLACK);
        this.setResizable(false);
        this.setBounds(this.creategamePanel.getCore().getX(),
                this.creategamePanel.getCore().getY() + 30, 500, 670);

        int GridX = 4;

        int GridY = Math.round((IConstants.Map.values().length / GridX) + 1);

        content.setLayout(new GridLayout(GridY, GridX));
        content.setBackground(Color.BLACK);
        content.setDoubleBuffered(true);
        jScrollCreateGameListPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollCreateGameListPanel.setViewportView(content);
        jScrollCreateGameListPanel.getVerticalScrollBar().setUnitIncrement(18);

        this.add(jScrollCreateGameListPanel);

        final CreateGameListPanel TR = this;

        new Thread() {

            @Override
            public void run() {
                for (IConstants.Map m : IConstants.Map.values()) {
                    content.add(new CreateGameListThumailBoxPanel(TR, m));
                    content.revalidate();
                }
            }
        }.start();
    }

    /**
     * Select the map in creategamePanel
     *
     */
    public void selectMap(int id) {

        this.creategamePanel.selectMap(id);
        // this.dispose();

    }
}
