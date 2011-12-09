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
package com.creeptd.client.game.creeps;

import java.awt.Graphics2D;

import com.creeptd.common.Constants;

/**
 * Interface for all creeps.
 * @author Philipp
 *
 */
public interface Creep {

    /**
     * Updates the creep.
     * @param tickNumber the current tick number
     */
    void update(long tickNumber);

    /**
     * paints the creep.
     * @param g the graphics object
     */
    void paint(Graphics2D g);
    
    /**
     * Getter for the x position.
     * @return the x pos
     */
    float getX();

    /**
     * Getter for the y position.
     * @return the y pos
     */
    float getY();

    /**
     * Getter for the creep type.
     * @return the type of the creep
     */
    Constants.Creeps getType();

	/**
	 * Setter for the speed value.
	 * @param speed speed of creep
	 * @param slowTime slow time
	 */
	void slow(double speed, int slowTime);

    /**
     * Getter for the speed of the creep.
     * @return the current creep speed
     */
	double getSpeed();

    /**
     * Getter the health of the creep.
     * @return the health
     */
    int getHealth();

    /**
     * Setter for the health value.
     * @param h health of creep
     */
    void setHealth(int h);

    /**
     * Tests if the Creep is a valid target.
     * @return true if the creep is activ and not dead, else false
     * @return
     */
    boolean isActive();

    /**
     * Sets the build time for the creep. Used to correct the time
     * if the server sends a message.
     * @param buildTime the remaining build time in rounds
     */
    void setBuildTime(int buildTime);

    /**
     * @return the total segment steps moved
     */
    int getTotalSegmentSteps();
}
