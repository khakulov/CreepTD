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

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import com.creeptd.client.Core;
import com.creeptd.client.grid.EmptyGrid;
import com.creeptd.client.sound.SoundManagement;
import com.creeptd.client.tower.AbstractTower;

/**
 * Base class for all screens in the game that represent a state in the game.
 * (Like the login, register, lobby, game etc.)
 * 
 * @author Philipp
 * 
 */
public abstract class GameScreen extends JPanel implements MouseListener, MouseMotionListener {

    private static final long serialVersionUID = 1L;
    private boolean initialized = false;
    private Core core;
    private boolean focusAlreadySet = false;
    private JComponent foucusThisComponent = null;
    public SoundManagement soundM = null;
    private long timeAtStart;

    /**
     * Initializes the screen. Adds EventListeners to the panel.
     *
     * @param core
     *            the game's core
     */
    @SuppressWarnings("serial")
    public final void initialize(Core core) {
        if (!initialized) {
            this.initialized = true;
            this.core = core;
            this.soundM = core.getSoundManagement();
            this.addMouseListener(this);
            this.addMouseMotionListener(this);
        }
        focusAlreadySet = false;
        timeAtStart = System.currentTimeMillis();

        /* addShortcut("F1", new AbstractAction() {
        public void actionPerformed(ActionEvent event) {
        JDialog frame = new HelpGamePanel((JFrame) getRootPane().getParent(),
        "CreepTD - Help");
        frame.setBounds(getCore().getX() + 152, getCore().getY() + 30, 630, 710);
        frame.setVisible(true);
        }
        });

        addShortcut("F2", new AbstractAction() {
        public void actionPerformed(ActionEvent event) {
        if (soundM != null) {
        soundM.toggleMute();
        }
        }
        });

        // Seems not to work well with alpha transparency
        addShortcut("F3", new AbstractAction() {
        public void actionPerformed(ActionEvent event) {
        BoardPanel.ANTIALIAS = !BoardPanel.ANTIALIAS;
        AbstractTower.ALPHA = !AbstractTower.ALPHA;
        }
        }); */

    }

    public void addShortcut(String key, Action action) {
        KeyStroke keyStroke = KeyStroke.getKeyStroke(key);
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, key);
        getActionMap().put(key, action);
    }

    /**
     * Method gets called when this screen gets displayed.
     */
    public abstract void start();

    /**
     * Method gets called when this screen is deactivated.
     */
    public abstract void end();

    /**
     * Method waits < than 2000m secs since initialize(core) was called.
     *
     * @return diff returns the slept time
     */
    public long waitFromInitToThis100ms() {
        int timeToWaitInMilliS = 100;
        long diff = 0;
        if (System.currentTimeMillis() < (timeAtStart + timeToWaitInMilliS)) {
            diff = System.currentTimeMillis() - timeAtStart;
            do {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while ((timeAtStart + timeToWaitInMilliS) >= System.currentTimeMillis());
        }
        return diff;
    }

    /**
     * Getter for the core.
     *
     * @return the core using this screen
     */
    public final Core getCore() {
        return this.core;
    }

    /**
     * {@inheritDoc}
     */
    public void mouseClicked(MouseEvent e) {
        this.requestFocusInWindow();
    }

    /**
     * {@inheritDoc}
     */
    public void mouseEntered(MouseEvent e) {
    }

    /**
     * {@inheritDoc}
     */
    public void mouseExited(MouseEvent e) {
    }

    /**
     * {@inheritDoc}
     */
    public void mousePressed(MouseEvent e) {
    }

    /**
     * {@inheritDoc}
     */
    public void mouseReleased(MouseEvent e) {
    }

    /**
     * {@inheritDoc}
     */
    public void mouseDragged(MouseEvent e) {
    }

    /**
     * {@inheritDoc}
     */
    public void mouseMoved(MouseEvent e) {
    }

    /**
     * Set the Focus to this Component.
     *
     * @param j
     *            the focusable Component
     */
    public void setGameScreenFocus(JComponent j) {
        foucusThisComponent = j;
    }

    /**
     * Overwritten paint method to set focus after an update. Otherwise, some
     * windowmanagers don't have problems.
     *
     * @param g
     *            Graphic Object
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);

        if (foucusThisComponent != null && !focusAlreadySet) {
            focusAlreadySet = true;
            foucusThisComponent.requestFocus();
        }

    }
}
