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
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.creeptd.client.game.ContextListener;
import com.creeptd.client.game.GameContext;
import com.creeptd.common.IConstants;

/**
 * Panel with Buttons to select Tower.
 * 
 * @author sven
 */
public class TowerSelPanel extends JPanel implements ContextListener {

    private static final long serialVersionUID = -1273757461985784088L;
    private final int height;
    private final int width;
    private JButton button1;
    private JButton button2;
    private JButton button3;
    private JButton button4;
    private JButton button5;
    private JButton button6;
    private GamePanel gamepanel;
    private GameContext context;

    // private Grid[][] gridArray;
    /**
     * sets Layout to Gridlayout. adds Buttons to the Panel
     *
     * @param width
     *            of the panel
     * @param height
     *            of the panel
     * @param gamepanel
     *            for getting object Gamepanel
     */
    public TowerSelPanel(GamePanel gamepanel, int width, int height) {
        this.height = height;
        this.width = width;
        this.gamepanel = gamepanel;
        this.setBackground(Color.BLACK);
        this.setPreferredSize(new Dimension(this.width, this.height));
        this.setSize(this.width, this.height);

        setLayout(new GridLayout(2, 3));

        button1 = new TowerBuyButton(gamepanel, IConstants.Towers.tower1,
                "towerIconButton1");
        button2 = new TowerBuyButton(gamepanel, IConstants.Towers.tower2,
                "towerIconButton2");
        button3 = new TowerBuyButton(gamepanel, IConstants.Towers.tower3,
                "towerIconButton3");
        button4 = new TowerBuyButton(gamepanel, IConstants.Towers.tower4,
                "towerIconButton4");
        button5 = new TowerBuyButton(gamepanel, IConstants.Towers.tower5,
                "towerIconButton5");
        button6 = new TowerBuyButton(gamepanel, IConstants.Towers.tower6,
                "towerIconButton6");
        add(button1);
        add(button2);
        add(button3);
        add(button4);
        add(button5);
        add(button6);
    }

    /**
     * Enable/disable Buttons according to income.
     */
    public void updateButtons() {
        if (context == null) {
            return;
        }
        int credits = context.getCredits();

        if ((credits >= IConstants.Towers.tower1.getPrice())) {
            this.button1.setEnabled(true);
        } else {
            this.button1.setEnabled(false);
        }

        if ((credits >= IConstants.Towers.tower2.getPrice())) {
            this.button2.setEnabled(true);
        } else {
            this.button2.setEnabled(false);
        }
        if ((credits >= IConstants.Towers.tower3.getPrice())) {
            this.button3.setEnabled(true);
        } else {
            this.button3.setEnabled(false);
        }
        if ((credits >= IConstants.Towers.tower4.getPrice())) {
            this.button4.setEnabled(true);
        } else {
            this.button4.setEnabled(false);
        }
        if ((credits >= IConstants.Towers.tower5.getPrice())) {
            this.button5.setEnabled(true);
        } else {
            this.button5.setEnabled(false);
        }
        if ((credits >= IConstants.Towers.tower6.getPrice())) {
            this.button6.setEnabled(true);
        } else {
            this.button6.setEnabled(false);
        }
    }

    /**
     * @return the context
     */
    public GameContext getContext() {
        return context;

    }

    /**
     * @param context
     *            the context to set
     */
    public void setContext(GameContext context) {
        this.context = context;
        context.addContextListener(this);
    }

    /**
     * {@inheritDoc}
     */
    public void creditsChanged(GameContext context) {
        this.updateButtons();

    }

    /**
     * {@inheritDoc}
     */
    public void incomeChanged(GameContext context) {
        // TODO Auto-generated method stub
    }

    /**
     * {@inheritDoc}
     */
    public void livesChanged(GameContext context) {
        // TODO Auto-generated method stub
    }

    /**
     * {@inheritDoc}
     */
    public void selectedChanged(GameContext context, String message) {
        // TODO Auto-generated method stub
    }
}
