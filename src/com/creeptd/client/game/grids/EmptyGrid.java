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
package com.creeptd.client.game.grids;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import com.creeptd.client.game.GameContext;
import com.creeptd.client.game.towers.Tower;

/**
 * Grid for all the places the player can build his towers onto.
 * 
 * @author Philipp
 * 
 */
public class EmptyGrid implements Grid {

    @SuppressWarnings("unused")
	private GameContext context;
    private int[] location = new int[2];
    private boolean isOccupied;
    private Tower tower;
    private AffineTransform pos = new AffineTransform();
    private Color gridColor = Color.ORANGE;

    /**
     * Creates a new instance of EmptyGrid.
     *
     * @param x
     *            the x location on the board
     * @param y
     *            thy y location on the board
     * @param context
     *            the gameContext
     */
    public EmptyGrid(int x, int y, GameContext context) {
        this.context = context;
        this.location[0] = x;
        this.location[1] = y;
        this.isOccupied = false;
    }

    /**
     * Returns whether this field is busy or not (upgrading, selling etc.).
     * This status is only used for local use to make sure, multiple actions are not done.
     * @return boolean
     */
    public boolean isOccupied() {
        return this.isOccupied;
    }

    public void setOccupiedStatus(boolean status) {
        this.isOccupied = status;
    }

    /**
     * {@inheritDoc}
     */
    public int[] getLocation() {
        return this.location;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFree() {
        return (this.tower == null);
    }

    /**
     * {@inheritDoc}
     */
    public void paint(Graphics2D g) {
        // TODO Auto-generated method stub
        AffineTransform save = g.getTransform();

        pos.setToIdentity();
        pos.translate(this.location[0], this.location[1]);

        g.transform(pos);

        g.setColor(gridColor);
        g.drawLine(0, 0, SIZE, 0);
        g.drawLine(SIZE, 0, SIZE, SIZE);
        g.drawLine(0, SIZE, SIZE, SIZE);
        g.drawLine(0, 0, 0, SIZE);
        g.setTransform(save);
    }

    /**
     * {@inheritDoc}
     */
    public void removeTower() {
        this.tower = null;
    }

    /**
     * {@inheritDoc}
     */
    public void setTower(Tower t) {
        this.tower = t;
    }

    /**
     * Getter for the tower in the grid.
     *
     * @return the tower or null if no tower is in the grid
     */
    public Tower getTower() {
        return this.tower;
    }

    @Override
    public int getX() {
        return this.location[0];
    }

    @Override
    public int getY() {
        return this.location[1];
    }
}
