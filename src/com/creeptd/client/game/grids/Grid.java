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

import java.awt.Graphics2D;

import com.creeptd.client.game.towers.Tower;

/**
 * Interface for the grid.
 * @author Philipp
 *
 */
public interface Grid {

    int SIZE = 20;

    /**
     * Paints the grid with its outline.
     * This should only be called when creating the map. For Highlighting
     * when placing a tower, the paintHighlight method should be called.
     * @param g the graphics object
     */
    void paint(Graphics2D g);

    /**
     * Indicates if the grid is empty.
     * @return true if no tower resides in the grid
     */
    boolean isFree();

    /**
     * Sets a tower into the grid.
     * @param t the tower
     */
    void setTower(Tower t);

    /**
     * returns the tower object.
     * @return tower object
     */
    Tower getTower();

    /**
     * removes a tower from the grid.
     * Used for selling towers.
     */
    void removeTower();

    /**
     * Gets the location of the grid in x and y coordinates.
     * @return an int[] with x and y
     */
    int[] getLocation();

    /**
     * Getter for the x position.
     * @return the x pos
     */
    int getX();

    /**
     * Getter for the y position.
     * @return the y pos
     */
    int getY();

    void setOccupiedStatus(boolean status);

    boolean isOccupied();
}
