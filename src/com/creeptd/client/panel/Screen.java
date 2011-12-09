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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * Base class for all screens in the game that represent a state in the game.
 * (Like the login, register, lobby, game etc.)
 * 
 * @author Philipp
 * 
 */
public abstract class Screen extends JPanel implements MouseListener, MouseMotionListener {

    private static final long serialVersionUID = 1L;
    private boolean initialized = false;
    private boolean focusAlreadySet = false;
    private JComponent foucusThisComponent = null;

    /**
     * Initializes the screen. Adds EventListeners to the panel.
     *
     * @param core
     *            the game's core
     */
    public final void initialize() {
        if (!initialized) {
            this.initialized = true;
            this.addMouseListener(this);
            this.addMouseMotionListener(this);
        }
        focusAlreadySet = false;
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
