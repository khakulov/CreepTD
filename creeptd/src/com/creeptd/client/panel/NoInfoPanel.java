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

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;

import com.creeptd.client.game.ContextListener;
import com.creeptd.client.game.GameContext;

/**
 * Panel that shows nothing or maybe a picture later on.
 * If the user deselects a tower or klicks on an empty field
 * this Panel will be shown.
 * @author mi7hr4ndir
 *
 */
public class NoInfoPanel extends JPanel implements ContextListener {

    /**
     *
     */
    private static final long serialVersionUID = 6047703887589539745L;
    private final int height;
    private final int width;
    private GameContext context;
    private GamePanel gamepanel;

    /**
     * Creates new NoInfoPanel.
     * @param gamepanel gamepanle
     * @param width	width of the Panel
     * @param height height of the Panel
     */
    public NoInfoPanel(GamePanel gamepanel, int width, int height) {
        this.height = height;
        this.width = width;
        this.gamepanel = gamepanel;
        this.setPreferredSize(new Dimension(this.width, this.height));
        this.setSize(this.width, this.height);
        this.setBackground(Color.BLACK);

    }

    /**
     * sets the Context and adds this Panel as a ContextListener.
     * @param gameContext context
     */
    public void setContext(GameContext gameContext) {
        this.context = gameContext;
        context.addContextListener(this);
    }

    /**
     * Not used Contextlistener Method.
     * @param context context
     */
    public void creditsChanged(GameContext context) {
        // TODO Auto-generated method stub
    }

    /**
     * Not used Contextlistener Method.
     * @param context context
     */
    public void incomeChanged(GameContext context) {
        // TODO Auto-generated method stub
    }

    /**
     * Not used Contextlistener Method.
     * @param context context
     */
    public void livesChanged(GameContext context) {
        // TODO Auto-generated method stub
    }

    /**
     * changes the TowerInfoPanels.
     * @param context context
     * @param message which Panel
     */
    public void selectedChanged(GameContext context, String message) {
        // show SelectTowerInfoPanel
        if (message.equals("empty")) {
            gamepanel.getSelectTowerInfoPanel().setVisible(false);
            gamepanel.getBuildTowerInfoPanel().setVisible(false);
            gamepanel.setLastTowerInfoPanel(this);
            setVisible(true);

        }

    }
}
