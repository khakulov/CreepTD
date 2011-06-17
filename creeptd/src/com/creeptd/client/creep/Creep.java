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
package com.creeptd.client.creep;

import java.awt.Graphics2D;

import com.creeptd.client.tower.Tower;
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
     * Calculates the damage done to the creep.
     * @param damage value of the damage
     * @param t the type of tower
     */
    void damageCreep(int damage, Tower t);

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
     * Getter for the speed of the creep.
     * @return the current creep speed
     */
    float getSpeed();

    /**
     * Gets the description of the creep.
     * @return string with the description
     */
    String getDescription();

    /**
     * Setter for the health value.
     * @param h health of creep
     */
    void setHealth(int h);

    /**
     * Getter the health of the creep.
     * @return the health
     */
    int getHealth();

    /**
     * sets the position of the creep.
     * @param x x-coordinate
     * @param y y-coordinate
     */
    void setPosition(float x, float y);

    /**
     * sets the angle value to rotate the creep.
     * @param r angle of rotation
     */
    void setRotate(double r);

    /**
     * Getter for angle value to rotate for the creep.
     * @return rotation
     */
    double getRotate();

    /**
     * Getter for the creep type.
     * @return the type of the creep
     */
    Constants.Creeps getType();

    /**
     * Tests if the Creep is a valid target.
     * @return true if the creep is activ and not dead, else false
     * @return
     */
    boolean isValidTarget();

    /**
     * Sets the build time for the creep. Used to correct the time
     * if the server sends a message.
     * @param buildTime the remaining build time in rounds
     */
    void setBuildTime(int buildTime);

    /**
     * Gets the build time for the creep.
     * @return buildTime the remaining build time in rounds
     */
    int getBuildTime();

    /**
     * Gets the id from the player who initially sent the creep.
     * @return the player id
     */
    int getSenderId();

    /**
     * Sets the sender id.
     * @param senderId the id from the player who sent the creep
     * @param senderId
     */
    void setSenderId(int senderId);

    /**
     * @return the slowImmune
     */
    boolean isSlowImmune();

    /**
     * @param slowImmune the slowImmune to set
     */
    void setSlowImmune(boolean slowImmune);

    /**
     *
     * @param id
     *            id
     */
    void setID(int id);

    /**
     *
     * @return id
     */
    int getID();

    /**
     * @return the playerID
     */
    int getPlayerID();

    /**
     * @param playerID the playerID to set
     */
    void setPlayerID(int playerID);

    /**
     * @return the total segment steps moved
     */
    int getTotalSegmentSteps();
}
