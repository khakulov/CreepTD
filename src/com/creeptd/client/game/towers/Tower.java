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
package com.creeptd.client.game.towers;

import java.awt.Graphics2D;

import com.creeptd.client.game.GameContext;
import com.creeptd.client.game.grids.Grid;
import com.creeptd.client.game.strategies.Strategy;
import com.creeptd.common.Constants;

/**
 * Interface for all towers.
 */
public interface Tower {

    /**
     * Updates the tower.
     *
     * @param tickNumber
     *            the current tick in the gameloop
     */
    void update(long tickNumber);

    /**
     * Paints the tower and his projectiles.
     *
     * @param g
     *            the graphics2D instance to draw at
     */
    void paint(Graphics2D g);

    /**
     * Paint the shooting effect.
     *
     * @param g
     *            the graphics context
     */
    void paintEffect(Graphics2D g);

    /**
     * Getter for the tower's id.
     *
     * @return the id from the tower
     */
    int getId();

    /**
     * Setter for the tower's id.
     *
     * @param id
     *            the id
     */
    void setId(int id);

    /**
     * @return the type
     */
    Constants.Towers getType();

    /**
     * Sets the build time for the tower. Used to correct the time if the server
     * sends a message.
     *
     * @param buildTime
     *            the remaining build time in rounds
     */
    void build(int buildTime);

    /**
     * Setter for the towers upgradeTime.
     *
     * @param upgradeTime
     *            the time
     */
    void upgrade(int upgradeTime);

    /**
     * Setter for the tower sellTime.
     * @param sellTime sellTime
     */
    void sell(int sellTime);

    /**
     * Setter for the tower changeStrategyTime and newStrategy.
     * @param sellTime sellTime
     * @param strategy newStrategy
     */
    public void changeStrategy(int updateRound, Strategy strategy);

    /**
     * getter.
     * @return context
     */
    GameContext getContext();

    /**
     * getter.
     * @return grid
     */
    Grid getGrid();

    /**
     * getter.
     * @return strategy
     */
    Strategy getStrategy();
}
