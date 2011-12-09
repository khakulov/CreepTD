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
package com.creeptd.client.panel.game;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;

/**
 * The GamePanel contains all player maps. Main purpose is to manage fast
 * drawing.
 * 
 * @author Philipp
 * 
 */
public class GameCanvas extends Canvas {

    private final int height;
    private final int width;
    private static final long serialVersionUID = 7914499578562656875L;
    private BufferStrategy strategy;
    private boolean firstTime = true;
    public static boolean ANTIALIAS = false;

    /**
     * Creates a new BoardPanel with the specified height and width.
     *
     * @param width
     *            the width of the panel
     * @param height
     *            the height of the panel
     */
    public GameCanvas(int width, int height) {
        this.height = height;
        this.width = width;
        this.setPreferredSize(new Dimension(this.width, this.height));
        this.setBounds(0, 0, width, height);
        this.setForeground(Color.BLACK);
        this.setBackground(Color.BLACK);
        this.setIgnoreRepaint(true);
    }

    /**
     * Getter for the BufferStrategy. You must call show() on the strategy to
     * see your drawings.
     *
     * @return the BufferStrategy used by this canvas
     */
    public BufferStrategy getStrategy() {
        return this.strategy;
    }

    /**
     * Gets the Graphics2D object for the game.
     *
     * @return the Graphics2D object
     */
    public Graphics2D getImgGraphics() {
        if (firstTime) {
            this.createBufferStrategy(2);
            strategy = getBufferStrategy();
            firstTime = false;
        }

        try {
            Graphics2D g2 = (Graphics2D) strategy.getDrawGraphics();
            // paint the background black
            g2.clearRect(0, 0, width, height);
            return g2;
        } catch (Exception ex) {
            // Component must have a valid peer
        }
        return null;
    }
}
